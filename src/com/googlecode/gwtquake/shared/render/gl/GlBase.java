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
package com.googlecode.gwtquake.shared.render.gl;



import java.util.LinkedList;

import com.googlecode.gwtquake.shared.client.Dimension;
import com.googlecode.gwtquake.shared.client.Window;
import com.googlecode.gwtquake.shared.common.Constants;
import com.googlecode.gwtquake.shared.common.ExecutableCommand;
import com.googlecode.gwtquake.shared.render.DisplayMode;

/**
 * LWJGLBase
 * 
 * @author dsanders/cwei
 */
public  class GlBase {
	
	public static DisplayMode[] getModeList() {
		DisplayMode[] modes = GlState.gl.getAvailableDisplayModes();
		
		LinkedList<DisplayMode> l = new LinkedList<DisplayMode>();
		l.add(GlState.oldDisplayMode);
		
		for (int i = 0; i < modes.length; i++) {
			DisplayMode m = modes[i];
			
//			if (m.getBitsPerPixel() != oldDisplayMode.getBitsPerPixel()) continue;
////			if (m.getFrequency() > oldDisplayMode.getFrequency()) continue;
////			if (m.getHeight() < 240 || m.getWidth() < 320) continue;
			
			if (m.height != GlState.oldDisplayMode.height || 
					m.width != GlState.oldDisplayMode.width) {
				l.add(m);
			}
		}
		DisplayMode[] ma = new DisplayMode[l.size()];
		l.toArray(ma);
		return ma;
	}
	
	protected static DisplayMode findDisplayMode(Dimension dim) {
		DisplayMode mode = null;
		DisplayMode m = null;
		DisplayMode[] modes = getModeList();
		int w = dim.width;
		int h = dim.height;
		
		for (int i = 0; i < modes.length; i++) {
			m = modes[i];
			if (m.getWidth() == w && m.getHeight() == h) {
				mode = m;
				break;
			}
		}
		if (mode == null) mode = GlState.oldDisplayMode;
		return mode;		
	}
		
	static String getModeString(DisplayMode m) {
		StringBuffer sb = new StringBuffer();
		sb.append(m.getWidth());
		sb.append('x');
		sb.append(m.getHeight());
		sb.append('x');
		sb.append(m.getBitsPerPixel());
		sb.append('@');
		sb.append(m.getFrequency());
		sb.append("Hz");
		return sb.toString();
	}

	

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
