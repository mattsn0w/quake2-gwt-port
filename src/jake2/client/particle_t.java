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
package jake2.client;

import com.google.gwt.typedarrays.client.Float32Array;
import com.google.gwt.typedarrays.client.Int32Array;

import jake2.qcommon.Defines;

public class particle_t {

	public static Float32Array vertexArray = Float32Array.create(Defines.MAX_PARTICLES * 3);
	public static Int32Array colorArray = Int32Array.create(Defines.MAX_PARTICLES);
	public static int[] colorTable = new int[256];

	public static void setColorPalette(int[] palette) {
		for (int i=0; i < 256; i++) {
			colorTable[i] = palette[i] & 0x00FFFFFF;
		}
	}
}
