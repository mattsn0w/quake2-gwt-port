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
	
	static final String[] LAME_DIRS = {
		"/usr/local/bin",
	};
	
	static final String[] TARGET_EXT = {".ogg", ".mp3"};
	
	String lameLocation;
	
	public WAVConverter() {
		super("wav", "wav");
		
		for (String s : LAME_DIRS) {
			if (findLame(s)) {
				return;
			}
		}

		String path = System.getenv("PATH");
		for (String s : path.split(File.pathSeparator)) {
			if (findLame(s)) {
				return;
			}
		}
	}

	private boolean findLame(String path) {
		File f = new File(path, "lame");
		if (f.exists()) {
			lameLocation = f.getAbsolutePath();
			return true;
		} 
		f = new File(path, "lame.exe");
		if (f.exists()) {
			lameLocation = f.getAbsolutePath();
			return true;
		} 
		return false;
	}
	
	@Override
	public void convert(byte[] raw, File outFile) throws IOException {
		if (lameLocation == null) {
			System.out.println("lame not found");
		}
		
		for (String ext : TARGET_EXT) {
			Process p = Runtime.getRuntime().exec(lameLocation + " - " + outFile.getCanonicalPath() + ext);
			p.getOutputStream().write(raw);
			p.getOutputStream().close();
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
