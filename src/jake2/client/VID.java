/*
 * VID.java
 * Copyright (C) 2003
 *
 * $Id: VID.java,v 1.17 2005/11/13 13:36:00 cawe Exp $
 */
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
package jake2.client;

import static jake2.qcommon.Defines.CVAR_ARCHIVE;
import jake2.game.cvar_t;
import jake2.qcommon.Com;
import jake2.qcommon.Cvar;
import jake2.qcommon.Defines;
import jake2.qcommon.Globals;
import jake2.sys.IN;
import jake2.util.Vargs;

/**
 * VID is a video driver.
 * 
 * source: client/vid.h linux/vid_so.c
 * 
 * @author cwei
 */
public class VID {

  // Console variables that we need to access from this module
	static cvar_t vid_gamma;
	static cvar_t vid_xpos;			// X coordinate of window position
	static cvar_t vid_ypos;			// Y coordinate of window position
	static cvar_t vid_width;
	static cvar_t vid_height;
	static cvar_t vid_fullscreen;

	// Global variables used internally by this module
	static boolean reflib_active = false;

	/*
	==========================================================================
	DLL GLUE
	==========================================================================
	*/
	public static void Printf(int print_level, String fmt) {
		Printf(print_level, fmt, null);	
	}

	public static void Printf(int print_level, String fmt, Vargs vargs) {
		// static qboolean inupdate;
		if (print_level == Defines.PRINT_ALL)
			Com.Printf(fmt, vargs);
		else
			Com.DPrintf(fmt, vargs);
	}

	/*
	** VID_NewWindow
	*/
	public static void NewWindow(int width, int height) {
		Globals.viddef.width = width;
		Globals.viddef.height = height;
	}

	static void FreeReflib() {
		if (Globals.re != null) {
			Globals.re.getKeyboardHandler().Close();
			IN.Shutdown();
		}

		Globals.re = null;
		reflib_active = false;
	}

	/*
	==============
	VID_LoadRefresh
	==============
	*/
	public static boolean LoadRefresh() {
		if ( reflib_active ) {
			Globals.re.getKeyboardHandler().Close();
			IN.Shutdown();

			Globals.re.Shutdown();
			FreeReflib();
		}

		Com.Printf( "------- Loading VID -------\n");

		if (Globals.re.apiVersion() != Defines.API_VERSION) {
			FreeReflib();
			Com.Error(Defines.ERR_FATAL, "VID has incompatible api_version");
		}

		IN.Real_IN_Init();

		if ( !Globals.re.Init((int)vid_xpos.value, (int)vid_ypos.value) ) {
			Globals.re.Shutdown();
			FreeReflib();
			return false;
		}

		/* Init KBD */
		Globals.re.getKeyboardHandler().Init();

		Com.Printf( "------------------------------------\n");
		reflib_active = true;
		return true;
	}

	/*
	============
	VID_Init
	============
	*/
	public static void Init() {
		/* Create the video variables so we know how to start the graphics drivers */
		vid_xpos = Cvar.Get("vid_xpos", "3", CVAR_ARCHIVE);
		vid_ypos = Cvar.Get("vid_ypos", "22", CVAR_ARCHIVE);
		vid_width = Cvar.Get("vid_width", "800", CVAR_ARCHIVE);
		vid_height = Cvar.Get("vid_height", "600", CVAR_ARCHIVE);
		vid_fullscreen = Cvar.Get("vid_fullscreen", "0", CVAR_ARCHIVE);
		vid_gamma = Cvar.Get( "vid_gamma", "1", CVAR_ARCHIVE );

		/* Start the graphics mode and load refresh DLL */
		LoadRefresh();
	}

	/*
	============
	VID_Shutdown
	============
	*/
	public static void Shutdown() {
		if ( reflib_active )
		{
			Globals.re.getKeyboardHandler().Close();
			IN.Shutdown();

			Globals.re.Shutdown();
			FreeReflib();
		}
	}

	public static void CheckChanges() {
		// TODO Auto-generated method stub
		
	}
}
