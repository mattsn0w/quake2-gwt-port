/*
 * LWJGLRenderer.java
 * Copyright (C) 2004
 *
 * $Id: LWJGLRenderer.java,v 1.3 2004/12/20 21:49:14 cawe Exp $
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
package jake2.render.gl;

import com.google.gwt.user.client.Command;

import jake2.client.VID;
import jake2.client.refdef_t;
import jake2.qcommon.AsyncCallback;
import jake2.qcommon.Defines;
import jake2.render.GLAdapter;
import jake2.render.image_t;
import jake2.render.model_t;

/**
 * LWJGLRenderer
 * 
 * @author dsanders/cwei
 */
public abstract class GLRenderer extends Misc {

	// ============================================================================
	// public interface for Renderer implementations
	//
	// refexport_t (ref.h)
	// ============================================================================
	/** 
	 * @see jake2.client.refexport_t#Init()
	 */
	public boolean Init(int vid_xpos, int vid_ypos) {
		// pre init
		if (!R_Init(vid_xpos, vid_ypos)) return false;
		// post init		
		boolean ok = R_Init2();
		if (!ok) {
			VID.Printf(Defines.PRINT_ALL, "Missing multi-texturing for LWJGL renderer\n");
		}
		return ok;
	}

	/** 
	 * @see jake2.client.refexport_t#Shutdown()
	 */
	public void Shutdown() {
		R_Shutdown();
	}

	/** 
	 * @see jake2.client.refexport_t#BeginRegistration(java.lang.String)
	 */
	public final void BeginRegistration(String map, Command callback) {
		R_BeginRegistration(map, callback);
	}

	/** 
	 * @see jake2.client.refexport_t#RegisterModel(java.lang.String)
	 */
	public final void RegisterModel(String name, AsyncCallback<model_t> callback) {
		R_RegisterModel(name, callback);
	}

	/** 
	 * @see jake2.client.refexport_t#RegisterSkin(java.lang.String)
	 */
	public final image_t RegisterSkin(String name) {
		return R_RegisterSkin(name);
	}
	
	/** 
	 * @see jake2.client.refexport_t#RegisterPic(java.lang.String)
	 */
	public image_t RegisterPic(String name) {
		return Draw_FindPic(name);
	}
	/** 
	 * @see jake2.client.refexport_t#SetSky(java.lang.String, float, float[])
	 */
	public final void SetSky(String name, float rotate, float[] axis) {
		R_SetSky(name, rotate, axis);
	}

	/** 
	 * @see jake2.client.refexport_t#EndRegistration()
	 */
	public final void EndRegistration() {
		R_EndRegistration();
	}

	/** 
	 * @see jake2.client.refexport_t#RenderFrame(jake2.client.refdef_t)
	 */
	public final void RenderFrame(refdef_t fd) {
		R_RenderFrame(fd);
	}

  public void DrawChar(int x, int y, int ch) {
    GL_Bind(draw_chars.texnum);
    gl.glBegin (GLAdapter._GL_QUADS);
    DrawChar_(x, y, ch);
    gl.glEnd();
  }

  public void DrawString(int x, int y, String str) {
    DrawString(x, y, str, 0, str.length());
  }

  public void DrawString(int x, int y, String str, boolean alt) {
    DrawString(x, y, str, 0, str.length(), alt);
  }

  public final void DrawString(int x, int y, String str, int ofs, int len) {
    DrawString(x, y, str, ofs, len, false);
  }

  public void DrawString(int x, int y, String str, int ofs, int len, boolean alt) {
    GL_Bind(draw_chars.texnum);
    gl.glBegin (GLAdapter._GL_QUADS);
    for (int i = 0; i < len; ++i) {
      DrawChar_(x, y, str.charAt(ofs + i) + (alt ? 128 : 0));
      x += 8;
    }
    gl.glEnd();
  }

  public void DrawString(int x, int y, byte[] str, int ofs, int len) {
    GL_Bind(draw_chars.texnum);
    gl.glBegin (GLAdapter._GL_QUADS);
    for (int i = 0; i < len; ++i) {
      DrawChar_(x, y, str[ofs + i]);
      x += 8;
    }
    gl.glEnd();
  }

	/** 
	 * @see jake2.client.refexport_t#CinematicSetPalette(byte[])
	 */
	public void CinematicSetPalette(byte[] palette) {
		R_SetPalette(palette);
	}


	/** 
	 * @see jake2.client.refexport_t#EndFrame()
	 */
	public final void EndFrame() {
		GLimp_EndFrame();
	}

	/** 
	 * @see jake2.client.refexport_t#AppActivate(boolean)
	 */
	public final void AppActivate(boolean activate) {
		GLimp_AppActivate(activate);
	}

	public final int apiVersion() {
		return Defines.API_VERSION;
	}

	public boolean showVideo(String name) {
		return false;
	}
	
	public boolean updateVideo() {
		return false;
	}
}
