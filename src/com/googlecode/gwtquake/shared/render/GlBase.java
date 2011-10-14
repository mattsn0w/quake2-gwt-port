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




import com.googlecode.gwtquake.shared.client.Window;
import com.googlecode.gwtquake.shared.common.Constants;
import com.googlecode.gwtquake.shared.common.ExecutableCommand;

/**
 * LWJGLBase
 * 
 * @author dsanders/cwei
 */
public  class GlBase {
	
	protected static void GLimp_Shutdown() {
		GlState.gl.shutdow();
	}

	/**
	 * @return true
	 */
	protected static boolean GLimp_Init(int xpos, int ypos) {
		// do nothing
		GlState.window_xpos = xpos;
		GlState.window_ypos = ypos;
		return true;
	}

	

	static void GLimp_BeginFrame(float camera_separation) {
		// do nothing
	}

	static void GLimp_AppActivate(boolean activate) {
		// do nothing
	}

	static void GLimp_EnableLogging(boolean enable) {
		// do nothing
	}

	static void GLimp_LogNewFrame() {
		// do nothing
	}

	
	
	
	

}
