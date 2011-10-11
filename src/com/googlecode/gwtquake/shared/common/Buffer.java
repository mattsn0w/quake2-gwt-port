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

import java.util.Arrays;

import com.googlecode.gwtquake.shared.util.Lib;
import com.googlecode.gwtquake.shared.util.Math3D;

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
	
	public void clear()
	{
		if (data!=null)		
			Arrays.fill(data,(byte)0);
		cursize = 0;
		overflowed = false;
	}

	//should be ok.
	public static void ReadDir(Buffer sb, float[] dir) {
	    int b;
	
	    b = ReadByte(sb);
	    if (b >= Defines.NUMVERTEXNORMALS)
	        Com.Error(Defines.ERR_DROP, "MSF_ReadDir: out of range");
	    Math3D.VectorCopy(Globals.bytedirs[b], dir);
	}

	//should be ok.
	public static void WriteDir(Buffer sb, float[] dir) {
	    int i, best;
	    float d, bestd;
	
	    if (dir == null) {
	        WriteByte(sb, 0);
	        return;
	    }
	
	    bestd = 0;
	    best = 0;
	    for (i = 0; i < Defines.NUMVERTEXNORMALS; i++) {
	        d = Math3D.DotProduct(dir, Globals.bytedirs[i]);
	        if (d > bestd) {
	            bestd = d;
	            best = i;
	        }
	    }
	    WriteByte(sb, best);
	}

	public static void BeginReading(Buffer msg) {
	    msg.readcount = 0;
	}

	// returns -1 if no more characters are available, but also [-128 , 127]
	public static int ReadChar(Buffer msg_read) {
	    int c;
	
	    if (msg_read.readcount + 1 > msg_read.cursize)
	        c = -1;
	    else
	        c = msg_read.data[msg_read.readcount];
	    msg_read.readcount++;
	    // kickangles bugfix (rst)
	    return c;
	}

	public static int ReadByte(Buffer msg_read) {
	    int c;
	
	    if (msg_read.readcount + 1 > msg_read.cursize)
	        c = -1;
	    else
	        c = msg_read.data[msg_read.readcount] & 0xff;
	    
	    msg_read.readcount++;
	
	    return c;
	}

	public static short ReadShort(Buffer msg_read) {
	    int c;
	
	    if (msg_read.readcount + 2 > msg_read.cursize)
	        c = -1;
	    else
	        c = (short) ((msg_read.data[msg_read.readcount] & 0xff) + (msg_read.data[msg_read.readcount + 1] << 8));
	
	    msg_read.readcount += 2;
	
	    return (short) c;
	}

	public static int ReadLong(Buffer msg_read) {
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

	public static float ReadFloat(Buffer msg_read) {
	    int n = Buffer.ReadLong(msg_read);
	    return Compatibility.intBitsToFloat(n);
	}

	public static String ReadString(Buffer msg_read) {
	
	    byte c;
	    int l = 0;
	    do {
	        c = (byte) Buffer.ReadByte(msg_read);
	        if (c == -1 || c == 0)
	            break;
	
	        Buffer.readbuf[l] = c;
	        l++;
	    } while (l < 2047);
	    
	    String ret = Compatibility.newString(Buffer.readbuf, 0, l);
	    // Com.dprintln("MSG.ReadString:[" + ret + "]");
	    return ret;
	}

	public static String ReadStringLine(Buffer msg_read) {
	
	    int l;
	    byte c;
	
	    l = 0;
	    do {
	        c = (byte) Buffer.ReadChar(msg_read);
	        if (c == -1 || c == 0 || c == 0x0a)
	            break;
	        Buffer.readbuf[l] = c;
	        l++;
	    } while (l < 2047);
	    
	    String ret = Compatibility.newString(Buffer.readbuf, 0, l).trim();
	    Com.dprintln("MSG.ReadStringLine:[" + ret.replace('\0', '@') + "]");
	    return ret;
	}

	public static float ReadCoord(Buffer msg_read) {
	    return Buffer.ReadShort(msg_read) * (1.0f / 8);
	}

	public static void ReadPos(Buffer msg_read, float pos[]) {
	    assert (pos.length == 3) : "vec3_t bug";
	    pos[0] = Buffer.ReadShort(msg_read) * (1.0f / 8);
	    pos[1] = Buffer.ReadShort(msg_read) * (1.0f / 8);
	    pos[2] = Buffer.ReadShort(msg_read) * (1.0f / 8);
	}

	public static float ReadAngle(Buffer msg_read) {
	    return Buffer.ReadChar(msg_read) * (360.0f / 256);
	}

	public static float ReadAngle16(Buffer msg_read) {
	    return Math3D.SHORT2ANGLE(Buffer.ReadShort(msg_read));
	}

	public static void ReadData(Buffer msg_read, byte data[], int len) {
	    for (int i = 0; i < len; i++)
	        data[i] = (byte) Buffer.ReadByte(msg_read);
	}

	public static void WriteAngle16(Buffer sb, float f) {
	    WriteShort(sb, Math3D.ANGLE2SHORT(f));
	}

	//ok.
	public static void WriteStringTrimmed(Buffer sb, byte s[]) {
	    WriteString(sb, Compatibility.newString(s).trim());
	}

	public static void WriteAngle(Buffer sb, float f) {
	    WriteByte(sb, (int) (f * 256 / 360) & 255);
	}

	public static void WritePos(Buffer sb, float[] pos) {
	    assert (pos.length == 3) : "vec3_t bug";
	    WriteShort(sb, (int) (pos[0] * 8));
	    WriteShort(sb, (int) (pos[1] * 8));
	    WriteShort(sb, (int) (pos[2] * 8));
	}

	public static void WriteCoord(Buffer sb, float f) {
	    WriteShort(sb, (int) (f * 8));
	}

	// had a bug, now its ok.
	public static void WriteString(Buffer sb, String s) {
	    String x = s;
	
	    if (s == null)
	        x = "";
	
	    Write(sb, Lib.stringToBytes(x));
	    WriteByte(sb, 0);
	    //Com.dprintln("MSG.WriteString:" + s.replace('\0', '@'));
	}

	//ok.
	public static void WriteFloat(Buffer sb, float f) {
	    WriteInt(sb, Compatibility.floatToIntBits(f));
	}

	//ok.
	public static void WriteLong(Buffer sb, int c) {
	    WriteInt(sb, c);
	}

	//ok.
	public static void WriteInt(Buffer sb, int c) {
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

	//ok.
	public static void WriteByte(Buffer sb, int c) {
	    sb.data[GetSpace(sb, 1)] = (byte) (c & 0xFF);
	}

	//ok.
	public static void WriteChar(Buffer sb, int c) {
	    sb.data[GetSpace(sb, 1)] = (byte) (c & 0xFF);
	}

	// 
	public static void Print(Buffer buf, String data) {
	    Com.dprintln("SZ.print():<" + data + ">" );
		int length = data.length();
		byte str[] = Lib.stringToBytes(data);
	
		if (buf.cursize != 0) {
	
			if (buf.data[buf.cursize - 1] != 0) {
				//memcpy( SZ_GetSpace(buf, len), data, len); // no trailing 0
				System.arraycopy(str, 0, buf.data, GetSpace(buf, length+1), length);
			} else {
				System.arraycopy(str, 0, buf.data, GetSpace(buf, length)-1, length);
				//memcpy(SZ_GetSpace(buf, len - 1) - 1, data, len); // write over trailing 0
			}
		} else
			// first print.
			System.arraycopy(str, 0, buf.data, GetSpace(buf, length), length);
		//memcpy(SZ_GetSpace(buf, len), data, len);
		
		buf.data[buf.cursize - 1]=0;
	}

	public static void Write(Buffer buf, byte data[]) {
		int length = data.length;
		//memcpy(SZ_GetSpace(buf, length), data, length);
		System.arraycopy(data, 0, buf.data, GetSpace(buf, length), length);
	}

	public static void Write(Buffer buf, byte data[], int offset, int length) {
		System.arraycopy(data, offset, buf.data, GetSpace(buf, length), length);
	}

	public static void Write(Buffer buf, byte data[], int length) {
		//memcpy(SZ_GetSpace(buf, length), data, length);
		System.arraycopy(data, 0, buf.data, GetSpace(buf, length), length);
	}

	/** Ask for the pointer using sizebuf_t.cursize (RST) */
	public static int GetSpace(Buffer buf, int length) {
		int oldsize;
	
		if (buf.cursize + length > buf.maxsize) {
			if (!buf.allowoverflow)
				Com.Error(Defines.ERR_FATAL, "SZ_GetSpace: overflow without allowoverflow set");
	
			if (length > buf.maxsize)
				Com.Error(Defines.ERR_FATAL, "SZ_GetSpace: " + length + " is > full buffer size");
	
			Com.Printf("SZ_GetSpace: overflow\n");
			buf.clear();
			buf.overflowed = true;
		}
	
		oldsize = buf.cursize;
		buf.cursize += length;
	
		return oldsize;
	}

	public static void Init(Buffer buf, byte data[], int length) {
	  // TODO check this. cwei
	  buf.readcount = 0;
	
	  buf.data = data;
		buf.maxsize = length;
		buf.cursize = 0;
		buf.allowoverflow = buf.overflowed = false;
	}
}
