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

import java.nio.FloatBuffer;

import com.googlecode.gwtquake.shared.util.Lib;

public class GlPolygon {
	public final static int STRIDE = 7;
	public final static int BYTE_STRIDE = 7 * Lib.SIZEOF_FLOAT;
	public final static int MAX_VERTICES = 64;
	
	private static final int MAX_POLYS = 20000;
	private static final int MAX_BUFFER_VERTICES = 120000;
	private static float[] s1_old = new float[MAX_VERTICES];
	
	



	private static FloatBuffer buffer = Lib.newFloatBuffer(MAX_BUFFER_VERTICES * STRIDE);
	private static int bufferIndex = 0;
	private static int polyCount = 0;
	protected static GlPolygon[] polyCache = new GlPolygon[MAX_POLYS];

	
	public GlPolygon next;
	public GlPolygon chain;
	public int numverts;
	public int flags; // for SURF_UNDERWATER (not needed anymore?)
	
	// the array position (glDrawArrays) 
	public int pos = 0;
	

	
	static {
	    for (int i = 0; i < polyCache.length; i++) {
            polyCache[i] = new GlPolygon();
        }
	}
	
	private GlPolygon() {
	}
	
	
	public static GlPolygon create(int numverts) {
	    GlPolygon poly = polyCache[polyCount++];
	    poly.clear();
	    poly.numverts = numverts;
	    poly.pos = bufferIndex;
	    bufferIndex += numverts;
	    return poly;
	}

	public static void reset() {
	    polyCount = 0;
	    bufferIndex = 0;
	}

	/** the interleaved buffer has the format:
	   textureCoord0 (index 0, 1)
	  vertex (index 2, 3, 4)
	  textureCoord1 (index 5, 6) */
	public static FloatBuffer getInterleavedBuffer() {
	    return (FloatBuffer)buffer.rewind();
	}


	private final void clear() {
	    next = null;
	    chain = null;
	    numverts = 0;
	    flags = 0;
	}

	public final float x(int index) {
	    return buffer.get((index + pos) * 7 + 2);
	}

	public final void x(int index, float value) {
	    buffer.put((index + pos) * 7 + 2, value);
	}

	public final float y(int index) {
	    return buffer.get((index + pos) * 7 + 3);
	}

	public final void y(int index, float value) {
	    buffer.put((index + pos) * 7 + 3, value);
	}

	public final float z(int index) {
	    return buffer.get((index + pos) * 7 + 4);
	}

	public final void z(int index, float value) {
	    buffer.put((index + pos) * 7 + 4, value);
	}

	public final float s1(int index) {
	    return buffer.get((index + pos) * 7 + 0);
	}

	public final void s1(int index, float value) {
	    buffer.put((index + pos) * 7 + 0, value);
	}

	public final float t1(int index) {
	    return buffer.get((index + pos) * 7 + 1);
	}

	public final void t1(int index, float value) {
	    buffer.put((index + pos) * 7 + 1, value);
	}

	public final float s2(int index) {
	    return buffer.get((index + pos) * 7 + 5);
	}

	public final void s2(int index, float value) {
	    buffer.put((index + pos) * 7 + 5, value);
	}

	public final float t2(int index) {
	    return buffer.get((index + pos) * 7 + 6);
	}

	public final void t2(int index, float value) {
	    buffer.put((index + pos) * 7 + 6, value);
	}

	public final void beginScrolling(float scroll) {
	    int index = pos * 7;
	    for (int i = 0; i < numverts; i++, index+=7) {
	        scroll += s1_old[i] = buffer.get(index);
	        buffer.put(index, scroll);
	    }
	}

	public final void endScrolling() {
	    int index = pos * 7;
	    for (int i = 0; i < numverts; i++, index+=7) {
	        buffer.put(index, s1_old[i]);
	    }
	}
}
