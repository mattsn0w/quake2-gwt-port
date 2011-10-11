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
package com.googlecode.gwtquake.shared.common;

import com.googlecode.gwtquake.shared.game.*;
import com.googlecode.gwtquake.shared.util.*;


public class Delta {

    //
    // writing functions
    //

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

        Buffer.WriteByte(buf, bits);

        if ((bits & Defines.CM_ANGLE1) != 0)
            Buffer.WriteShort(buf, cmd.angles[0]);
        if ((bits & Defines.CM_ANGLE2) != 0)
            Buffer.WriteShort(buf, cmd.angles[1]);
        if ((bits & Defines.CM_ANGLE3) != 0)
            Buffer.WriteShort(buf, cmd.angles[2]);

        if ((bits & Defines.CM_FORWARD) != 0)
            Buffer.WriteShort(buf, cmd.forwardmove);
        if ((bits & Defines.CM_SIDE) != 0)
            Buffer.WriteShort(buf, cmd.sidemove);
        if ((bits & Defines.CM_UP) != 0)
            Buffer.WriteShort(buf, cmd.upmove);

        if ((bits & Defines.CM_BUTTONS) != 0)
            Buffer.WriteByte(buf, cmd.buttons);
        if ((bits & Defines.CM_IMPULSE) != 0)
            Buffer.WriteByte(buf, cmd.impulse);

        Buffer.WriteByte(buf, cmd.msec);
        Buffer.WriteByte(buf, cmd.lightlevel);
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

        Buffer.WriteByte(msg, bits & 255);

        if ((bits & 0xff000000) != 0) {
            Buffer.WriteByte(msg, (bits >>> 8) & 255);
            Buffer.WriteByte(msg, (bits >>> 16) & 255);
            Buffer.WriteByte(msg, (bits >>> 24) & 255);
        } else if ((bits & 0x00ff0000) != 0) {
            Buffer.WriteByte(msg, (bits >>> 8) & 255);
            Buffer.WriteByte(msg, (bits >>> 16) & 255);
        } else if ((bits & 0x0000ff00) != 0) {
            Buffer.WriteByte(msg, (bits >>> 8) & 255);
        }

        //----------

        if ((bits & Defines.U_NUMBER16) != 0)
            Buffer.WriteShort(msg, to.number);
        else
            Buffer.WriteByte(msg, to.number);

        if ((bits & Defines.U_MODEL) != 0)
            Buffer.WriteByte(msg, to.modelindex);
        if ((bits & Defines.U_MODEL2) != 0)
            Buffer.WriteByte(msg, to.modelindex2);
        if ((bits & Defines.U_MODEL3) != 0)
            Buffer.WriteByte(msg, to.modelindex3);
        if ((bits & Defines.U_MODEL4) != 0)
            Buffer.WriteByte(msg, to.modelindex4);

        if ((bits & Defines.U_FRAME8) != 0)
            Buffer.WriteByte(msg, to.frame);
        if ((bits & Defines.U_FRAME16) != 0)
            Buffer.WriteShort(msg, to.frame);

        if ((bits & Defines.U_SKIN8) != 0 && (bits & Defines.U_SKIN16) != 0) //used for laser
                                                             // colors
            Buffer.WriteInt(msg, to.skinnum);
        else if ((bits & Defines.U_SKIN8) != 0)
            Buffer.WriteByte(msg, to.skinnum);
        else if ((bits & Defines.U_SKIN16) != 0)
            Buffer.WriteShort(msg, to.skinnum);

        if ((bits & (Defines.U_EFFECTS8 | Defines.U_EFFECTS16)) == (Defines.U_EFFECTS8 | Defines.U_EFFECTS16))
            Buffer.WriteInt(msg, to.effects);
        else if ((bits & Defines.U_EFFECTS8) != 0)
            Buffer.WriteByte(msg, to.effects);
        else if ((bits & Defines.U_EFFECTS16) != 0)
            Buffer.WriteShort(msg, to.effects);

        if ((bits & (Defines.U_RENDERFX8 | Defines.U_RENDERFX16)) == (Defines.U_RENDERFX8 | Defines.U_RENDERFX16))
            Buffer.WriteInt(msg, to.renderfx);
        else if ((bits & Defines.U_RENDERFX8) != 0)
            Buffer.WriteByte(msg, to.renderfx);
        else if ((bits & Defines.U_RENDERFX16) != 0)
            Buffer.WriteShort(msg, to.renderfx);

        if ((bits & Defines.U_ORIGIN1) != 0)
            Buffer.WriteCoord(msg, to.origin[0]);
        if ((bits & Defines.U_ORIGIN2) != 0)
            Buffer.WriteCoord(msg, to.origin[1]);
        if ((bits & Defines.U_ORIGIN3) != 0)
            Buffer.WriteCoord(msg, to.origin[2]);

        if ((bits & Defines.U_ANGLE1) != 0)
            Buffer.WriteAngle(msg, to.angles[0]);
        if ((bits & Defines.U_ANGLE2) != 0)
            Buffer.WriteAngle(msg, to.angles[1]);
        if ((bits & Defines.U_ANGLE3) != 0)
            Buffer.WriteAngle(msg, to.angles[2]);

        if ((bits & Defines.U_OLDORIGIN) != 0) {
            Buffer.WriteCoord(msg, to.old_origin[0]);
            Buffer.WriteCoord(msg, to.old_origin[1]);
            Buffer.WriteCoord(msg, to.old_origin[2]);
        }

        if ((bits & Defines.U_SOUND) != 0)
            Buffer.WriteByte(msg, to.sound);
        if ((bits & Defines.U_EVENT) != 0)
            Buffer.WriteByte(msg, to.event);
        if ((bits & Defines.U_SOLID) != 0)
            Buffer.WriteShort(msg, to.solid);
    }

    //============================================================

    //
    // reading functions
    //

    public static void ReadDeltaUsercmd(Buffer msg_read, UserCommand from,
            UserCommand move) {
        int bits;

        //memcpy(move, from, sizeof(* move));
        // IMPORTANT!! copy without new
        move.set(from);
        bits = Buffer.ReadByte(msg_read);

        // read current angles
        if ((bits & Defines.CM_ANGLE1) != 0)
            move.angles[0] = Buffer.ReadShort(msg_read);
        if ((bits & Defines.CM_ANGLE2) != 0)
            move.angles[1] = Buffer.ReadShort(msg_read);
        if ((bits & Defines.CM_ANGLE3) != 0)
            move.angles[2] = Buffer.ReadShort(msg_read);

        // read movement
        if ((bits & Defines.CM_FORWARD) != 0)
            move.forwardmove = Buffer.ReadShort(msg_read);
        if ((bits & Defines.CM_SIDE) != 0)
            move.sidemove = Buffer.ReadShort(msg_read);
        if ((bits & Defines.CM_UP) != 0)
            move.upmove = Buffer.ReadShort(msg_read);

        // read buttons
        if ((bits & Defines.CM_BUTTONS) != 0)
            move.buttons = (byte) Buffer.ReadByte(msg_read);

        if ((bits & Defines.CM_IMPULSE) != 0)
            move.impulse = (byte) Buffer.ReadByte(msg_read);

        // read time to run command
        move.msec = (byte) Buffer.ReadByte(msg_read);

        // read the light level
        move.lightlevel = (byte) Buffer.ReadByte(msg_read);

    }    
            
}
