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
package com.googlecode.gwtquake.shared.common;

import java.nio.ByteOrder;
import java.util.Arrays;


/**
 * sizebuf_t
 */
public final class Buffer {
	public boolean allowoverflow = false;
	public boolean overflowed = false;
	public byte[] data = null;
	public int maxsize = 0;
	public int cursize = 0;
	public int readcount = 0;
	// 2k read buffer.
	public static byte readbuf[] = new byte[2048];

	public static Buffer allocate(int size) {
	  return wrap(new byte[size]);
	}
	
	public void clear()
	{
		if (data!=null) {
			Arrays.fill(data,(byte)0);
		}
		cursize = 0;
		overflowed = false;
		
	}

	public static void reset(Buffer msg) {
	    msg.readcount = 0;
	}

	public static short getShort(Buffer msg_read) {
	    int c;
	
	    if (msg_read.readcount + 2 > msg_read.cursize)
	        c = -1;
	    else
	        c = (short) ((msg_read.data[msg_read.readcount] & 0xff) + (msg_read.data[msg_read.readcount + 1] << 8));
	
	    msg_read.readcount += 2;
	
	    return (short) c;
	}

	public static int getLong(Buffer msg_read) {
	    int c;
	
	    if (msg_read.readcount + 4 > msg_read.cursize) {
	        Com.Printf("buffer underrun in ReadLong!");
	        c = -1;
	    }
	
	    else
	        c = (msg_read.data[msg_read.readcount] & 0xff)
	                | ((msg_read.data[msg_read.readcount + 1] & 0xff) << 8)
	                | ((msg_read.data[msg_read.readcount + 2] & 0xff) << 16)
	                | ((msg_read.data[msg_read.readcount + 3] & 0xff) << 24);
	
	    msg_read.readcount += 4;
	
	    return c;
	}

	public static float getFloat(Buffer msg_read) {
	    int n = Buffer.getLong(msg_read);
	    return Compatibility.intBitsToFloat(n);
	}

	//ok.
	public static void putFloat(Buffer sb, float f) {
	    putInt(sb, Compatibility.floatToIntBits(f));
	}

	//ok.
	public static void putInt(Buffer sb, int c) {
	    int i = GetSpace(sb, 4);
	    sb.data[i++] = (byte) ((c & 0xff));
	    sb.data[i++] = (byte) ((c >>> 8) & 0xff);
	    sb.data[i++] = (byte) ((c >>> 16) & 0xff);
	    sb.data[i++] = (byte) ((c >>> 24) & 0xff);
	}

	public static void WriteShort(Buffer sb, int c) {
	    int i = GetSpace(sb, 2);
	    sb.data[i++] = (byte) (c & 0xff);
	    sb.data[i] = (byte) ((c >>> 8) & 0xFF);
	}

	/** Ask for the pointer using sizebuf_t.cursize (RST) */
	static int GetSpace(Buffer buf, int length) {
		int oldsize;
	
		if (buf.cursize + length > buf.maxsize) {
			if (!buf.allowoverflow)
				Com.Error(Constants.ERR_FATAL, "SZ_GetSpace: overflow without allowoverflow set");
	
			if (length > buf.maxsize)
				Com.Error(Constants.ERR_FATAL, "SZ_GetSpace: " + length + " is > full buffer size");
	
			Com.Printf("SZ_GetSpace: overflow\n");
			buf.clear();
			buf.overflowed = true;
		}
	
		oldsize = buf.cursize;
		buf.cursize += length;
	
		return oldsize;
	}

	public static Buffer wrap(byte[] data) {
	  Buffer buf = new Buffer();
	  Buffer.Init(buf, data, data.length);
	  return buf;
	}
	
	private static void Init(Buffer buf, byte data[], int length) {
	  // TODO check this. cwei
	  buf.readcount = 0;
	
	  buf.data = data;
	  buf.maxsize = length;
	  buf.cursize = 0;
	  buf.allowoverflow = buf.overflowed = false;
	}

  public Buffer order(ByteOrder order) {
    if (order != ByteOrder.LITTLE_ENDIAN) {
      throw new RuntimeException("BIG_ENDIAN not supported");
    }
    return this;
  }
}
