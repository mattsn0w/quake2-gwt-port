/*
Copyright (C) 2010 Copyright 2010 Google Inc.

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

import java.io.File;
import java.io.IOException;

public class WAVConverter extends Converter {
	
	static final String[] ENCODER_DIRS = {
		"/usr/local/bin",
		"/opt/local/bin",
	};
	
	String lameLocation;
	String oggLocation;
	
	public WAVConverter() {
		super("wav", "wav");
		
		for (String s : ENCODER_DIRS) {
			if (findEncoder(s)) {
				return;
			}
		}

		String path = System.getenv("PATH");
		for (String s : path.split(File.pathSeparator)) {
			if (findEncoder(s)) {
				return;
			}
		}
	}

	private boolean findEncoder(String path) {
		File f;
		if (lameLocation == null) {
			f = new File(path, "lame");
			if (f.exists()) {
				lameLocation = f.getAbsolutePath();
			} else {
				f = new File(path, "lame.exe");
				if (f.exists()) {
					lameLocation = f.getAbsolutePath();
				}
			}
		} 
		if (oggLocation == null) {
			f = new File(path, "oggenc");
			if (f.exists()) {
				oggLocation = f.getAbsolutePath();
			} else {
				f = new File(path, "oggenc.exe");
				if (f.exists()) {
					oggLocation = f.getAbsolutePath();
				}
			}
		}
		return lameLocation != null && oggLocation != null;
	}
	
	@Override
	public void convert(byte[] raw, File outFile, int[] size) throws IOException {
	  String outPath = lowerFile(outFile);

		if (lameLocation == null) {
			System.out.println("lame not found");
		} else {
			Process p = Runtime.getRuntime().exec(
				lameLocation + " - " + outPath + ".mp3");
			p.getOutputStream().write(raw);
			p.getOutputStream().close();
		
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (oggLocation == null) {
			System.out.println("oggenc not found");
		} else {
			Process p = Runtime.getRuntime().exec(
				oggLocation + " - -o " + outPath + ".ogg");
			p.getOutputStream().write(raw);
			p.getOutputStream().close();
		
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

  private String lowerFile(File outFile) throws IOException {
    String lowerFileName = outFile.getCanonicalFile().getName().toLowerCase();
    String pathName = outFile.getCanonicalFile().getParent();
    return pathName + File.separator + lowerFileName;
  }
}
