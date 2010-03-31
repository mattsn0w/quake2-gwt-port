/*
 Copyright (C) 1997-2001 Id Software, Inc.

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package jake2.tools;

import jake2.desktop.CompatibilityImpl;
import jake2.qcommon.Compatibility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Hashtable;

public class Unpak {

  public static class pack_t {
    String filename;
    RandomAccessFile handle;
    ByteBuffer backbuffer;
    int numfiles;
    Hashtable<String, packfile_t> files;
  }

  public static class packfile_t {
    static final int SIZE = 64;
    static final int NAME_SIZE = 56;

    String name;
    int filepos, filelen;
  }

  static class dpackheader_t {
    int ident;
    int dirofs;
    int dirlen;
  }

  static {
    Compatibility.impl = new CompatibilityImpl();
    new WALConverter();
    new PCXConverter();
    new TGAConverter();
    new WAVConverter();
  }

  private static final int IDPAKHEADER = (('K' << 24) + ('C' << 16)
      + ('A' << 8) + 'P');
  private static final int MAX_FILES_IN_PACK = 4096;
  private static byte[] tmpText = new byte[packfile_t.NAME_SIZE];
  private static File indir, outdir;

  public static void main(String[] args) throws Throwable {
    if (args.length != 2) {
      System.err.println("Usage: Unpak [indir] [outdir]");
      System.err
          .println("  [indir] should be the game's data directory, containing pak0.pak, et al.");
      System.exit(-1);
    }

    indir = new File(args[0]);
    outdir = new File(args[1]);

    if (outdir.exists()) {
      System.out.println(outdir + " already exists; no need to unpak");
      return;
    }

    if (!indir.exists() || !indir.isDirectory()) {
      System.err.println("Couldn't find directory " + indir);
      System.exit(-1);
    }

    convertDir(indir, "");
  }

  private static void convertDir(File file, String prefix) throws IOException {
    for (String child : file.list()) {
      File childFile = new File(file, child);
      if (childFile.isDirectory()) {
        convertDir(childFile, prefix + child + File.separator);
      } else {
        // See if it's a .pak file.
        if (childFile.getName().endsWith(".pak")) {
          // Unpak it.
          unpak(childFile.getAbsolutePath());
        } else {
          // Convert the file.
          RandomAccessFile rafile = new RandomAccessFile(childFile, "r");
          FileChannel fc = rafile.getChannel();
          convertFile(prefix + childFile.getName(), fc, (int) fc.size());
          fc.close();
        }
      }
    }
  }

  private static void convertFile(String filename, FileChannel inChannel,
      int len) throws IOException {
    System.out.println(filename);

    Converter converter = Converter.get(filename);
    if (converter != null) {
      filename = replaceExtension(filename, converter.getOutExt());
    }

    String canonicalPath = new File(outdir, filename).getCanonicalPath();
    File outFile = new File(canonicalPath);
    createPath(outFile.getAbsolutePath());
 //   outFile.createNewFile();

    ByteBuffer buf = ByteBuffer.allocateDirect(len);
    if (converter != null) {
      // Convert the file.
      inChannel.read(buf);
      buf.flip();
      byte[] raw = new byte[len];
      buf.get(raw);
      converter.convert(raw, outFile);
    } else {
      // Just copy it directly.
      FileOutputStream outStream = new FileOutputStream(outFile);
      FileChannel outChannel = outStream.getChannel();

      inChannel.read(buf);
      buf.flip();
      outChannel.write(buf);

      outStream.close();
    }
  }

  private static void createPath(String path) {
    int index = path.lastIndexOf('/');
    // -1 if not found and 0 means write to root
    if (index > 0) {
      File f = new File(path.substring(0, index));
      if (!f.mkdirs() && !f.isDirectory()) {
        System.err.println("can't create path \"" + path + '"' + "\n");
        System.exit(-1);
      }
    }
  }

  private static pack_t loadPackFile(String packfile) {
    dpackheader_t header;
    Hashtable<String, packfile_t> newfiles;
    RandomAccessFile file;
    int numpackfiles = 0;
    pack_t pack = null;

    try {
      file = new RandomAccessFile(packfile, "r");
      FileChannel fc = file.getChannel();
      ByteBuffer packhandle = fc.map(FileChannel.MapMode.READ_ONLY, 0, file
          .length());
      packhandle.order(ByteOrder.LITTLE_ENDIAN);

      fc.close();

      if (packhandle == null || packhandle.limit() < 1)
        return null;

      header = new dpackheader_t();
      header.ident = packhandle.getInt();
      header.dirofs = packhandle.getInt();
      header.dirlen = packhandle.getInt();

      if (header.ident != IDPAKHEADER)
        System.err.println(packfile + " is not a packfile");

      numpackfiles = header.dirlen / packfile_t.SIZE;

      if (numpackfiles > MAX_FILES_IN_PACK)
        System.err.println(packfile + " has " + numpackfiles + " files");

      newfiles = new Hashtable<String, packfile_t>(numpackfiles);

      packhandle.position(header.dirofs);

      // parse the directory
      packfile_t entry = null;

      for (int i = 0; i < numpackfiles; i++) {
        packhandle.get(tmpText);

        entry = new packfile_t();
        entry.name = new String(tmpText).trim();
        entry.filepos = packhandle.getInt();
        entry.filelen = packhandle.getInt();

        newfiles.put(entry.name.toLowerCase(), entry);
      }
    } catch (IOException e) {
      System.out.println(e.getMessage() + '\n');
      return null;
    }

    pack = new pack_t();
    pack.filename = new String(packfile);
    pack.handle = file;
    pack.numfiles = numpackfiles;
    pack.files = newfiles;

    System.out.println("Opened packfile " + packfile + " (" + numpackfiles
        + " files)\n");
    return pack;
  }

  private static String replaceExtension(String filename, String outExt) {
    int idx = filename.lastIndexOf('.');
    assert idx != -1;
    return filename.substring(0, idx) + "." + outExt;
  }

  private static void unpak(String pakname) throws IOException {
    pack_t pak = loadPackFile(pakname);
    pak.handle = new RandomAccessFile(pak.filename, "r");
    FileChannel inChannel = pak.handle.getChannel();

    for (packfile_t entry : pak.files.values()) {
      pak.handle.seek(entry.filepos);
      convertFile(entry.name, inChannel, entry.filelen);
    }

    pak.handle.close();
  }
}
