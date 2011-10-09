/*
 * Copyright (C) 1997-2001 Id Software, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 *  
 */
/* Modifications
   Copyright 2003-2004 Bytonic Software
   Copyright 2010 Google Inc.
*/
package jake2.qcommon;

import jake2.game.*;
import jake2.util.*;

public class Messages extends Globals {

    //
    // writing functions
    //

    //ok.
    public static void WriteChar(Buffer sb, int c) {
        sb.data[SZ.GetSpace(sb, 1)] = (byte) (c & 0xFF);
    }

    //ok.
    public static void WriteChar(Buffer sb, float c) {

        WriteChar(sb, (int) c);
    }

    //ok.
    public static void WriteByte(Buffer sb, int c) {
        sb.data[SZ.GetSpace(sb, 1)] = (byte) (c & 0xFF);
    }

    //ok.
    public static void WriteByte(Buffer sb, float c) {
        WriteByte(sb, (int) c);
    }

    public static void WriteShort(Buffer sb, int c) {
        int i = SZ.GetSpace(sb, 2);
        sb.data[i++] = (byte) (c & 0xff);
        sb.data[i] = (byte) ((c >>> 8) & 0xFF);
    }

    //ok.
    public static void WriteInt(Buffer sb, int c) {
        int i = SZ.GetSpace(sb, 4);
        sb.data[i++] = (byte) ((c & 0xff));
        sb.data[i++] = (byte) ((c >>> 8) & 0xff);
        sb.data[i++] = (byte) ((c >>> 16) & 0xff);
        sb.data[i++] = (byte) ((c >>> 24) & 0xff);
    }

    //ok.
    public static void WriteLong(Buffer sb, int c) {
        WriteInt(sb, c);
    }

    //ok.
    public static void WriteFloat(Buffer sb, float f) {
        WriteInt(sb, Compatibility.floatToIntBits(f));
    }

    // had a bug, now its ok.
    public static void WriteString(Buffer sb, String s) {
        String x = s;

        if (s == null)
            x = "";

        SZ.Write(sb, Lib.stringToBytes(x));
        WriteByte(sb, 0);
        //Com.dprintln("MSG.WriteString:" + s.replace('\0', '@'));
    }

    //ok.
    public static void WriteString(Buffer sb, byte s[]) {
        WriteString(sb, Compatibility.newString(s).trim());
    }

    public static void WriteCoord(Buffer sb, float f) {
        WriteShort(sb, (int) (f * 8));
    }

    public static void WritePos(Buffer sb, float[] pos) {
        assert (pos.length == 3) : "vec3_t bug";
        WriteShort(sb, (int) (pos[0] * 8));
        WriteShort(sb, (int) (pos[1] * 8));
        WriteShort(sb, (int) (pos[2] * 8));
    }

    public static void WriteAngle(Buffer sb, float f) {
        WriteByte(sb, (int) (f * 256 / 360) & 255);
    }

    public static void WriteAngle16(Buffer sb, float f) {
        WriteShort(sb, Math3D.ANGLE2SHORT(f));
    }

    public static void WriteDeltaUsercmd(Buffer buf, UserCommand from,
            UserCommand cmd) {
        int bits;

        //
        // send the movement message
        //
        bits = 0;
        if (cmd.angles[0] != from.angles[0])
            bits |= Defines.CM_ANGLE1;
        if (cmd.angles[1] != from.angles[1])
            bits |= Defines.CM_ANGLE2;
        if (cmd.angles[2] != from.angles[2])
            bits |= Defines.CM_ANGLE3;
        if (cmd.forwardmove != from.forwardmove)
            bits |= Defines.CM_FORWARD;
        if (cmd.sidemove != from.sidemove)
            bits |= Defines.CM_SIDE;
        if (cmd.upmove != from.upmove)
            bits |= Defines.CM_UP;
        if (cmd.buttons != from.buttons)
            bits |= Defines.CM_BUTTONS;
        if (cmd.impulse != from.impulse)
            bits |= Defines.CM_IMPULSE;

        WriteByte(buf, bits);

        if ((bits & Defines.CM_ANGLE1) != 0)
            WriteShort(buf, cmd.angles[0]);
        if ((bits & Defines.CM_ANGLE2) != 0)
            WriteShort(buf, cmd.angles[1]);
        if ((bits & Defines.CM_ANGLE3) != 0)
            WriteShort(buf, cmd.angles[2]);

        if ((bits & Defines.CM_FORWARD) != 0)
            WriteShort(buf, cmd.forwardmove);
        if ((bits & Defines.CM_SIDE) != 0)
            WriteShort(buf, cmd.sidemove);
        if ((bits & Defines.CM_UP) != 0)
            WriteShort(buf, cmd.upmove);

        if ((bits & Defines.CM_BUTTONS) != 0)
            WriteByte(buf, cmd.buttons);
        if ((bits & Defines.CM_IMPULSE) != 0)
            WriteByte(buf, cmd.impulse);

        WriteByte(buf, cmd.msec);
        WriteByte(buf, cmd.lightlevel);
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
            d = Math3D.DotProduct(dir, bytedirs[i]);
            if (d > bestd) {
                bestd = d;
                best = i;
            }
        }
        WriteByte(sb, best);
    }

    //should be ok.
    public static void ReadDir(Buffer sb, float[] dir) {
        int b;

        b = ReadByte(sb);
        if (b >= Defines.NUMVERTEXNORMALS)
            Com.Error(Defines.ERR_DROP, "MSF_ReadDir: out of range");
        Math3D.VectorCopy(bytedirs[b], dir);
    }

    /*
     * ================== WriteDeltaEntity
     * 
     * Writes part of a packetentities message. Can delta from either a baseline
     * or a previous packet_entity ==================
     */
    public static void WriteDeltaEntity(EntityState from, EntityState to,
            Buffer msg, boolean force, boolean newentity) {
        int bits;

        if (0 == to.number)
            Com.Error(Defines.ERR_FATAL, "Unset entity number");
        if (to.number >= Defines.MAX_EDICTS)
            Com.Error(Defines.ERR_FATAL, "Entity number >= MAX_EDICTS");

        // send an update
        bits = 0;

        if (to.number >= 256)
            bits |= Defines.U_NUMBER16; // number8 is implicit otherwise

        if (to.origin[0] != from.origin[0])
            bits |= Defines.U_ORIGIN1;
        if (to.origin[1] != from.origin[1])
            bits |= Defines.U_ORIGIN2;
        if (to.origin[2] != from.origin[2])
            bits |= Defines.U_ORIGIN3;

        if (to.angles[0] != from.angles[0])
            bits |= Defines.U_ANGLE1;
        if (to.angles[1] != from.angles[1])
            bits |= Defines.U_ANGLE2;
        if (to.angles[2] != from.angles[2])
            bits |= Defines.U_ANGLE3;

        if (to.skinnum != from.skinnum) {
            if (to.skinnum < 256)
                bits |= Defines.U_SKIN8;
            else if (to.skinnum < 0x10000)
                bits |= Defines.U_SKIN16;
            else
                bits |= (Defines.U_SKIN8 | Defines.U_SKIN16);
        }

        if (to.frame != from.frame) {
            if (to.frame < 256)
                bits |= Defines.U_FRAME8;
            else
                bits |= Defines.U_FRAME16;
        }

        if (to.effects != from.effects) {
            if (to.effects < 256)
                bits |= Defines.U_EFFECTS8;
            else if (to.effects < 0x8000)
                bits |= Defines.U_EFFECTS16;
            else
                bits |= Defines.U_EFFECTS8 | Defines.U_EFFECTS16;
        }

        if (to.renderfx != from.renderfx) {
            if (to.renderfx < 256)
                bits |= Defines.U_RENDERFX8;
            else if (to.renderfx < 0x8000)
                bits |= Defines.U_RENDERFX16;
            else
                bits |= Defines.U_RENDERFX8 | Defines.U_RENDERFX16;
        }

        if (to.solid != from.solid)
            bits |= Defines.U_SOLID;

        // event is not delta compressed, just 0 compressed
        if (to.event != 0)
            bits |= Defines.U_EVENT;

        if (to.modelindex != from.modelindex)
            bits |= Defines.U_MODEL;
        if (to.modelindex2 != from.modelindex2)
            bits |= Defines.U_MODEL2;
        if (to.modelindex3 != from.modelindex3)
            bits |= Defines.U_MODEL3;
        if (to.modelindex4 != from.modelindex4)
            bits |= Defines.U_MODEL4;

        if (to.sound != from.sound)
            bits |= Defines.U_SOUND;

        if (newentity || (to.renderfx & Defines.RF_BEAM) != 0)
            bits |= Defines.U_OLDORIGIN;

        //
        // write the message
        //
        if (bits == 0 && !force)
            return; // nothing to send!

        //----------

        if ((bits & 0xff000000) != 0)
            bits |= Defines.U_MOREBITS3 | Defines.U_MOREBITS2 | Defines.U_MOREBITS1;
        else if ((bits & 0x00ff0000) != 0)
            bits |= Defines.U_MOREBITS2 | Defines.U_MOREBITS1;
        else if ((bits & 0x0000ff00) != 0)
            bits |= Defines.U_MOREBITS1;

        WriteByte(msg, bits & 255);

        if ((bits & 0xff000000) != 0) {
            WriteByte(msg, (bits >>> 8) & 255);
            WriteByte(msg, (bits >>> 16) & 255);
            WriteByte(msg, (bits >>> 24) & 255);
        } else if ((bits & 0x00ff0000) != 0) {
            WriteByte(msg, (bits >>> 8) & 255);
            WriteByte(msg, (bits >>> 16) & 255);
        } else if ((bits & 0x0000ff00) != 0) {
            WriteByte(msg, (bits >>> 8) & 255);
        }

        //----------

        if ((bits & Defines.U_NUMBER16) != 0)
            WriteShort(msg, to.number);
        else
            WriteByte(msg, to.number);

        if ((bits & Defines.U_MODEL) != 0)
            WriteByte(msg, to.modelindex);
        if ((bits & Defines.U_MODEL2) != 0)
            WriteByte(msg, to.modelindex2);
        if ((bits & Defines.U_MODEL3) != 0)
            WriteByte(msg, to.modelindex3);
        if ((bits & Defines.U_MODEL4) != 0)
            WriteByte(msg, to.modelindex4);

        if ((bits & Defines.U_FRAME8) != 0)
            WriteByte(msg, to.frame);
        if ((bits & Defines.U_FRAME16) != 0)
            WriteShort(msg, to.frame);

        if ((bits & Defines.U_SKIN8) != 0 && (bits & Defines.U_SKIN16) != 0) //used for laser
                                                             // colors
            WriteInt(msg, to.skinnum);
        else if ((bits & Defines.U_SKIN8) != 0)
            WriteByte(msg, to.skinnum);
        else if ((bits & Defines.U_SKIN16) != 0)
            WriteShort(msg, to.skinnum);

        if ((bits & (Defines.U_EFFECTS8 | Defines.U_EFFECTS16)) == (Defines.U_EFFECTS8 | Defines.U_EFFECTS16))
            WriteInt(msg, to.effects);
        else if ((bits & Defines.U_EFFECTS8) != 0)
            WriteByte(msg, to.effects);
        else if ((bits & Defines.U_EFFECTS16) != 0)
            WriteShort(msg, to.effects);

        if ((bits & (Defines.U_RENDERFX8 | Defines.U_RENDERFX16)) == (Defines.U_RENDERFX8 | Defines.U_RENDERFX16))
            WriteInt(msg, to.renderfx);
        else if ((bits & Defines.U_RENDERFX8) != 0)
            WriteByte(msg, to.renderfx);
        else if ((bits & Defines.U_RENDERFX16) != 0)
            WriteShort(msg, to.renderfx);

        if ((bits & Defines.U_ORIGIN1) != 0)
            WriteCoord(msg, to.origin[0]);
        if ((bits & Defines.U_ORIGIN2) != 0)
            WriteCoord(msg, to.origin[1]);
        if ((bits & Defines.U_ORIGIN3) != 0)
            WriteCoord(msg, to.origin[2]);

        if ((bits & Defines.U_ANGLE1) != 0)
            WriteAngle(msg, to.angles[0]);
        if ((bits & Defines.U_ANGLE2) != 0)
            WriteAngle(msg, to.angles[1]);
        if ((bits & Defines.U_ANGLE3) != 0)
            WriteAngle(msg, to.angles[2]);

        if ((bits & Defines.U_OLDORIGIN) != 0) {
            WriteCoord(msg, to.old_origin[0]);
            WriteCoord(msg, to.old_origin[1]);
            WriteCoord(msg, to.old_origin[2]);
        }

        if ((bits & Defines.U_SOUND) != 0)
            WriteByte(msg, to.sound);
        if ((bits & Defines.U_EVENT) != 0)
            WriteByte(msg, to.event);
        if ((bits & Defines.U_SOLID) != 0)
            WriteShort(msg, to.solid);
    }

    //============================================================

    //
    // reading functions
    //

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
        int n = ReadLong(msg_read);
        return Compatibility.intBitsToFloat(n);
    }

    // 2k read buffer.
    public static byte readbuf[] = new byte[2048];

    public static String ReadString(Buffer msg_read) {

        byte c;
        int l = 0;
        do {
            c = (byte) ReadByte(msg_read);
            if (c == -1 || c == 0)
                break;

            readbuf[l] = c;
            l++;
        } while (l < 2047);
        
        String ret = Compatibility.newString(readbuf, 0, l);
        // Com.dprintln("MSG.ReadString:[" + ret + "]");
        return ret;
    }

    public static String ReadStringLine(Buffer msg_read) {

        int l;
        byte c;

        l = 0;
        do {
            c = (byte) ReadChar(msg_read);
            if (c == -1 || c == 0 || c == 0x0a)
                break;
            readbuf[l] = c;
            l++;
        } while (l < 2047);
        
        String ret = Compatibility.newString(readbuf, 0, l).trim();
        Com.dprintln("MSG.ReadStringLine:[" + ret.replace('\0', '@') + "]");
        return ret;
    }

    public static float ReadCoord(Buffer msg_read) {
        return ReadShort(msg_read) * (1.0f / 8);
    }

    public static void ReadPos(Buffer msg_read, float pos[]) {
        assert (pos.length == 3) : "vec3_t bug";
        pos[0] = ReadShort(msg_read) * (1.0f / 8);
        pos[1] = ReadShort(msg_read) * (1.0f / 8);
        pos[2] = ReadShort(msg_read) * (1.0f / 8);
    }

    public static float ReadAngle(Buffer msg_read) {
        return ReadChar(msg_read) * (360.0f / 256);
    }

    public static float ReadAngle16(Buffer msg_read) {
        return Math3D.SHORT2ANGLE(ReadShort(msg_read));
    }

    public static void ReadDeltaUsercmd(Buffer msg_read, UserCommand from,
            UserCommand move) {
        int bits;

        //memcpy(move, from, sizeof(* move));
        // IMPORTANT!! copy without new
        move.set(from);
        bits = ReadByte(msg_read);

        // read current angles
        if ((bits & Defines.CM_ANGLE1) != 0)
            move.angles[0] = ReadShort(msg_read);
        if ((bits & Defines.CM_ANGLE2) != 0)
            move.angles[1] = ReadShort(msg_read);
        if ((bits & Defines.CM_ANGLE3) != 0)
            move.angles[2] = ReadShort(msg_read);

        // read movement
        if ((bits & Defines.CM_FORWARD) != 0)
            move.forwardmove = ReadShort(msg_read);
        if ((bits & Defines.CM_SIDE) != 0)
            move.sidemove = ReadShort(msg_read);
        if ((bits & Defines.CM_UP) != 0)
            move.upmove = ReadShort(msg_read);

        // read buttons
        if ((bits & Defines.CM_BUTTONS) != 0)
            move.buttons = (byte) ReadByte(msg_read);

        if ((bits & Defines.CM_IMPULSE) != 0)
            move.impulse = (byte) ReadByte(msg_read);

        // read time to run command
        move.msec = (byte) ReadByte(msg_read);

        // read the light level
        move.lightlevel = (byte) ReadByte(msg_read);

    }

    public static void ReadData(Buffer msg_read, byte data[], int len) {
        for (int i = 0; i < len; i++)
            data[i] = (byte) ReadByte(msg_read);
    }    
            
}
