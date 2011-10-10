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
package com.googlecode.gwtquake.shared.render;

import com.googlecode.gwtquake.shared.util.Lib;

public abstract class GlPolygon {
	public final static int STRIDE = 7;
	public final static int BYTE_STRIDE = 7 * Lib.SIZEOF_FLOAT;
	public final static int MAX_VERTICES = 64;
	
	public GlPolygon next;
	public GlPolygon chain;
	public int numverts;
	public int flags; // for SURF_UNDERWATER (not needed anymore?)
	
	// the array position (glDrawArrays) 
	public int pos = 0;
	
	protected GlPolygon() {
	}
	
	public abstract float x(int index);
	
	public abstract void x(int index, float value);

	public abstract float y(int index);
	
	public abstract void y(int index, float value);
	
	public abstract float z(int index);
	
	public abstract void z(int index, float value);

	public abstract float s1(int index);
	
	public abstract void s1(int index, float value);

	public abstract float t1(int index);
	
	public abstract void t1(int index, float value);

	public abstract float s2(int index);
	
	public abstract void s2(int index, float value);

	public abstract float t2(int index);
	
	public abstract void t2(int index, float value);
	
	public abstract void beginScrolling(float s1);
	
	public abstract void endScrolling();
}
