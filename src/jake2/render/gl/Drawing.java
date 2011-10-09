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
import jake2.client.Window;
import jake2.qcommon.Com;
import jake2.qcommon.Defines;
import jake2.qcommon.QuakeImage;
import jake2.render.GlAdapter;
import jake2.render.ModelImage;
import jake2.util.Lib;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Draw
 * (gl_draw.c)
 * 
 * @author cwei
 */
public abstract class Drawing extends Images {

	/*
	===============
	Draw_InitLocal
	===============
	*/
	void Draw_InitLocal() {
		// load console characters
		draw_chars = GL_FindImage("pics/conchars.pcx", QuakeImage.it_pic);
	}

	/*
	================
	Draw_Char

	Draws one 8*8 graphics character with 0 being transparent.
	It can be clipped to the top of the screen to allow the console to be
	smoothly scrolled off.
	================
    */
	
	
	
	protected void DrawChar_(int x, int y, int num) {
		num &= 255;
	
		if ( (num&127) == 32 ) return; // space

		if (y <= -8) return; // totally off screen

		int row = num>>4;
		int col = num&15;

		float frow = row*0.0625f;
		float fcol = col*0.0625f;
		float size = 0.0625f;

		
		gl.glTexCoord2f (fcol, frow);
		gl.glVertex2f (x, y);
		gl.glTexCoord2f (fcol + size, frow);
		gl.glVertex2f (x+8, y);
		gl.glTexCoord2f (fcol + size, frow + size);
		gl.glVertex2f (x+8, y+8);
		gl.glTexCoord2f (fcol, frow + size);
		gl.glVertex2f (x, y+8);
	}


	/*
	=============
	Draw_FindPic
	=============
	*/
	protected ModelImage Draw_FindPic(String name) {
		ModelImage image = null;
		String fullname;

		if (!name.startsWith("/") && !name.startsWith("\\")) {
			fullname = "pics/" + name + ".pcx";
		} else {
			fullname = name.substring(1);
		}
		image = GL_FindImage(fullname, QuakeImage.it_pic);
		return image;
	}


	/*
	=============
	Draw_GetPicSize
	=============
	*/
	public final void DrawGetPicSize(Dimension dim, String pic)	{
		ModelImage image = Draw_FindPic(pic);
		dim.width = (image != null) ? image.width : -1;
		dim.height = (image != null) ? image.height : -1;
	}

	/*
	=============
	Draw_StretchPic
	=============
	*/
	public void DrawStretchPic (int x, int y, int w, int h, String pic) {
		
		ModelImage image;

		image = Draw_FindPic(pic);
		if (image == null)
		{
			Window.Printf (Defines.PRINT_ALL, "Can't find pic: " + pic +'\n');
			return;
		}

//		if (scrap_dirty)
//			Scrap_Upload();

//		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0) ) && !image.has_alpha)
//		  gl.glDisable(GLAdapter.GL_ALPHA_TEST);

		GL_Bind(image.texnum);
		gl.glBegin (GlAdapter.SIMPLE_TEXUTRED_QUAD);
		gl.glVertex2f (x, y);
		gl.glVertex2f (x+w, y);
		gl.glVertex2f (x+w, y+h);
		gl.glVertex2f (x, y+h);
		gl.glEnd ();

//		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) !=0 ) ) && !image.has_alpha)
//		  gl.glEnable(GLAdapter.GL_ALPHA_TEST);
	}


	/*
	=============
	Draw_Pic
	=============
	*/
	public void DrawPic(int x, int y, String pic)
	{
		ModelImage image;

		image = Draw_FindPic(pic);
		if (image == null)
		{
			Window.Printf(Defines.PRINT_ALL, "Can't find pic: " +pic + '\n');
			return;
		}
//		if (scrap_dirty)
//			Scrap_Upload();

//		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) ) && !image.has_alpha)
//		  gl.glDisable (GLAdapter.GL_ALPHA_TEST);

		GL_Bind(image.texnum);

		gl.glBegin (GlAdapter.SIMPLE_TEXUTRED_QUAD);
		gl.glVertex2f (x, y);
		gl.glVertex2f (x+image.width, y);
		gl.glVertex2f (x+image.width, y+image.height);
		gl.glVertex2f (x, y+image.height);
		gl.glEnd ();

//		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) )  && !image.has_alpha)
//		  gl.glEnable (GLAdapter.GL_ALPHA_TEST);
	}

	/*
	=============
	Draw_TileClear

	This repeats a 64*64 tile graphic to fill the screen around a sized down
	refresh window.
	=============
	*/
	public final void DrawTileClear(int x, int y, int w, int h, String pic) {
		ModelImage	image;

		image = Draw_FindPic(pic);
		if (image == null)
		{
			Window.Printf(Defines.PRINT_ALL, "Can't find pic: " + pic + '\n');
			return;
		}

//		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) )  && !image.has_alpha)
//		  gl.glDisable(GLAdapter.GL_ALPHA_TEST);

		GL_Bind(image.texnum);
		gl.glBegin (GlAdapter._GL_QUADS);
		gl.glTexCoord2f(x/64.0f, y/64.0f);
		gl.glVertex2f (x, y);
		gl.glTexCoord2f( (x+w)/64.0f, y/64.0f);
		gl.glVertex2f(x+w, y);
		gl.glTexCoord2f( (x+w)/64.0f, (y+h)/64.0f);
		gl.glVertex2f(x+w, y+h);
		gl.glTexCoord2f( x/64.0f, (y+h)/64.0f );
		gl.glVertex2f (x, y+h);
		gl.glEnd ();

//		if ( ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) )  && !image.has_alpha)
//		  gl.glEnable(GLAdapter.GL_ALPHA_TEST);
	}


	/*
	=============
	Draw_Fill

	Fills a box of pixels with a single color
	=============
	*/
	/** 
	 * @see jake2.client.Renderer#DrawFill
	 */
	public void DrawFill(int x, int y, int w, int h, int colorIndex)	{

		if ( colorIndex > 255)
			Com.Error(Defines.ERR_FATAL, "Draw_Fill: bad color");

		gl.glDisable(GlAdapter.GL_TEXTURE_2D);

		int color = QuakeImage.PALETTE_ABGR[colorIndex]; 

		gl.glColor3ub(
			(byte)((color >> 0) & 0xff), // r
			(byte)((color >> 8) & 0xff), // g
			(byte)((color >> 16) & 0xff) // b
		);

		gl.glBegin (GlAdapter._GL_QUADS);

		gl.glVertex2f(x,y);
		gl.glVertex2f(x+w, y);
		gl.glVertex2f(x+w, y+h);
		gl.glVertex2f(x, y+h);

		gl.glEnd();
		gl.glColor3f(1,1,1);
		gl.glEnable(GlAdapter.GL_TEXTURE_2D);
	}

	//=============================================================================

	/*
	================
	Draw_FadeScreen
	================
	*/
	/** 
	 * @see jake2.client.Renderer#DrawFadeScreen()
	 */
	public void DrawFadeScreen()	{
	  gl.glEnable(GlAdapter.GL_BLEND);
	  gl.glDisable(GlAdapter.GL_TEXTURE_2D);
	  gl.glColor4f(0, 0, 0, 0.8f);
	  gl.glBegin(GlAdapter._GL_QUADS);

	  gl.glVertex2f(0,0);
	  gl.glVertex2f(vid.width, 0);
	  gl.glVertex2f(vid.width, vid.height);
	  gl.glVertex2f(0, vid.height);

	  gl.glEnd();
	  gl.glColor4f(1,1,1,1);
	  gl.glEnable(GlAdapter.GL_TEXTURE_2D);
	  gl.glDisable(GlAdapter.GL_BLEND);
	}

// ====================================================================

    // allocate a 256 * 256 texture buffer
    private ByteBuffer image8 = Lib.newByteBuffer(256 * 256 * Defines.SIZE_OF_INT);
    // share the buffer
    private IntBuffer image32 = image8.asIntBuffer();

	/*
	=============
	Draw_StretchRaw
	=============
	*/
	public final void DrawStretchRaw (int x, int y, int w, int h, int cols, int rows, byte[] data)
	{
		int i, j, trows;
		int sourceIndex;
		int frac, fracstep;
		float hscale;
		int row;
		float t;

		GL_Bind(0);

		if (rows<=256)
		{
			hscale = 1;
			trows = rows;
		}
		else
		{
			hscale = rows/256.0f;
			trows = 256;
		}
		t = rows*hscale / 256;

//		if ( !qglColorTableEXT )
//		{
			//int[] image32 = new int[256*256];
			image32.clear();
			int destIndex = 0;

			for (i=0 ; i<trows ; i++)
			{
				row = (int)(i*hscale);
				if (row > rows)
					break;
				sourceIndex = cols*row;
				destIndex = i*256;
				fracstep = cols*0x10000/256;
				frac = fracstep >> 1;
				for (j=0 ; j<256 ; j++)
				{
					image32.put(destIndex + j, r_rawpalette[data[sourceIndex + (frac>>16)] & 0xff]);
					frac += fracstep;
				}
			}
			gl.glTexImage2D (GlAdapter.GL_TEXTURE_2D, 0, GlAdapter.GL_RGBA/*gl_tex_solid_format*/, 256, 256, 0, GlAdapter.GL_RGBA, GlAdapter.GL_UNSIGNED_BYTE, image32);
//		}
//		else
//		{
//			//byte[] image8 = new byte[256*256];
//			image8.clear();
//			int destIndex = 0;;
//
//			for (i=0 ; i<trows ; i++)
//			{
//				row = (int)(i*hscale);
//				if (row > rows)
//					break;
//				sourceIndex = cols*row;
//				destIndex = i*256;
//				fracstep = cols*0x10000/256;
//				frac = fracstep >> 1;
//				for (j=0 ; j<256 ; j++)
//				{
//					image8.put(destIndex  + j, data[sourceIndex + (frac>>16)]);
//					frac += fracstep;
//				}
//			}
//
//			gl.glTexImage2D( GLAdapter.GL_TEXTURE_2D, 
//						   0, 
//						   GL_COLOR_INDEX8_EXT, 
//						   256, 256, 
//						   0, 
//						   GLAdapter._GL_COLOR_INDEX, 
//						   GLAdapter.GL_UNSIGNED_BYTE, 
//						   image8 );
//		}
		gl.glTexParameterf(GlAdapter.GL_TEXTURE_2D, GlAdapter.GL_TEXTURE_MIN_FILTER, GlAdapter.GL_LINEAR);
		gl.glTexParameterf(GlAdapter.GL_TEXTURE_2D, GlAdapter.GL_TEXTURE_MAG_FILTER, GlAdapter.GL_LINEAR);

//		if ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) ) 
//		  gl.glDisable (GLAdapter.GL_ALPHA_TEST);

		gl.glBegin (GlAdapter._GL_QUADS);
		gl.glTexCoord2f (0, 0);
		gl.glVertex2f (x, y);
		gl.glTexCoord2f (1, 0);
		gl.glVertex2f (x+w, y);
		gl.glTexCoord2f (1, t);
		gl.glVertex2f (x+w, y+h);
		gl.glTexCoord2f (0, t);
		gl.glVertex2f (x, y+h);
		gl.glEnd ();

//		if ( ( gl_config.renderer == GL_RENDERER_MCD ) || ( (gl_config.renderer & GL_RENDERER_RENDITION) != 0 ) ) 
//		  gl.glEnable (GLAdapter.GL_ALPHA_TEST);
	}
}
