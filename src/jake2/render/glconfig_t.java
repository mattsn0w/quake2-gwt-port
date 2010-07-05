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
/* Modifications
   Copyright 2003-2004 Bytonic Software
   Copyright 2010 Google Inc.
*/
package jake2.render;

public class glconfig_t {
    
	public int renderer;
	public String renderer_string;
	public String vendor_string;
	public String version_string;
	public String extensions_string;

	public boolean allow_cds;
	
	private float version = 1.1f;

	public void parseOpenGLVersion() {
		StringBuilder digits = new StringBuilder();
		boolean dot = false;
		for (int i = 0; i < version_string.length(); i++) {
			char c = version_string.charAt(i);
			if (c >= '0' && c <= '9') {
				digits.append(c);
			} else if (c == '.' && !dot) {
				dot = true;
				digits.append(c);
			}
		}

		version = Float.parseFloat(digits.toString());
	}
	
	public float getOpenGLVersion() {
	    return version;
	}
}
