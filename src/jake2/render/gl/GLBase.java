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
package jake2.render.gl;

import jake2.client.Dimension;
import jake2.client.VID;
import jake2.client.viddef_t;
import jake2.game.cvar_t;
import jake2.qcommon.xcommand_t;
import jake2.render.DisplayMode;
import jake2.render.GLAdapter;

import java.util.LinkedList;

/**
 * LWJGLBase
 * 
 * @author dsanders/cwei
 */
public abstract class GLBase {
	// IMPORTED FUNCTIONS
	protected DisplayMode oldDisplayMode; 

	// window position on the screen
	int window_xpos, window_ypos;
	protected viddef_t vid = new viddef_t();

	// handles the post initialization with LWJGLRenderer
	protected abstract boolean R_Init2();
	
	protected cvar_t vid_fullscreen;

	// enum rserr_t
	protected static final int rserr_ok = 0;
	protected static final int rserr_invalid_fullscreen = 1;
	protected static final int rserr_invalid_mode = 2;
	protected static final int rserr_unknown = 3;
	
	protected GLAdapter gl;
	
	protected void init() {
	}
	
	public DisplayMode[] getModeList() {
		DisplayMode[] modes = gl.getAvailableDisplayModes();
		
		LinkedList<DisplayMode> l = new LinkedList<DisplayMode>();
		l.add(oldDisplayMode);
		
		for (int i = 0; i < modes.length; i++) {
			DisplayMode m = modes[i];
			
//			if (m.getBitsPerPixel() != oldDisplayMode.getBitsPerPixel()) continue;
////			if (m.getFrequency() > oldDisplayMode.getFrequency()) continue;
////			if (m.getHeight() < 240 || m.getWidth() < 320) continue;
			
			if (m.height != oldDisplayMode.height || 
					m.width != oldDisplayMode.width) {
				l.add(m);
			}
		}
		DisplayMode[] ma = new DisplayMode[l.size()];
		l.toArray(ma);
		return ma;
	}
	
	private DisplayMode findDisplayMode(Dimension dim) {
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
		if (mode == null) mode = oldDisplayMode;
		return mode;		
	}
		
	String getModeString(DisplayMode m) {
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

	/**
	 * @param dim
	 * @param mode
	 * @param fullscreen
	 * @return enum rserr_t
	 */
	public int GLimp_SetMode(Dimension dim, int mode, boolean fullscreen) {

		
		
	  // TODO: jgw
	  fullscreen = false;
	  
	  gl.log("GLimp_SetMode");

	  Dimension newDim = new Dimension(dim.width, dim.height);

		
		/*
		 * fullscreen handling
		 */
	  
	  gl.log("determining old display mode");
		if (oldDisplayMode == null) {
			oldDisplayMode = gl.getDisplayMode();
		}

		// destroy the existing window
		GLimp_Shutdown();


		  gl.log("searching new display mode");
		DisplayMode displayMode = findDisplayMode(newDim);
		  gl.log("copying w/h");
		newDim.width = displayMode.getWidth();
		newDim.height = displayMode.getHeight();
		
		  gl.log("setting mode: " + displayMode);

		gl.setDisplayMode(displayMode);

		  gl.log("storing mode");
		vid.width = newDim.width;
		vid.height = newDim.height;
		
		// let the sound and input subsystems know about the new window
		  gl.log("newWindow notification");
		VID.NewWindow(vid.width, vid.height);
		return rserr_ok;
	}

	protected void GLimp_Shutdown() {
		gl.shutdow();
	}

	/**
	 * @return true
	 */
	protected boolean GLimp_Init(int xpos, int ypos) {
		// do nothing
		window_xpos = xpos;
		window_ypos = ypos;
		return true;
	}

	protected void GLimp_EndFrame() {
		gl.swapBuffers();
		// swap buffers

	}

	protected void GLimp_BeginFrame(float camera_separation) {
		// do nothing
	}

	protected void GLimp_AppActivate(boolean activate) {
		// do nothing
	}

	protected void GLimp_EnableLogging(boolean enable) {
		// do nothing
	}

	protected void GLimp_LogNewFrame() {
		// do nothing
	}

	/**
	 * this is a hack for jogl renderers.
	 * @param callback
	 */
	public final void updateScreen(xcommand_t callback) {
		callback.execute();
	}	
}
