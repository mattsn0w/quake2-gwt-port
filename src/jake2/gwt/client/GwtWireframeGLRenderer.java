/*
Copyright (C) 2010 Copyright 2010 Google Inc.

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
package jake2.gwt.client;

import jake2.client.Renderer;
import jake2.render.ModelImage;
import jake2.render.gl.WireframeRenderer;
import jake2.sys.KBD;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.corp.compatibility.Numbers;
import com.google.gwt.html5.client.CanvasElement;
import com.google.gwt.html5.client.CanvasRenderingContext2D;

public class GwtWireframeGLRenderer extends AbstractGwtGLRenderer implements Renderer {
	KBD kbd = new GwtKBD();
	private CanvasRenderingContext2D ctx;

	public GwtWireframeGLRenderer(final CanvasElement canvas) {
		this.gl = new WireframeRenderer(canvas, canvas.getWidth(), canvas.getHeight());
		init();
	}

	public KBD getKeyboardHandler() {
		return kbd;
	}

	@Override
	public void DrawChar_(int x, int y, int num) {
		ctx.setGlobalAlpha(1);
		num &= 255;
		
		if ( (num&127) == 32 ) return; // space

		if (y <= -8) return; // totally off screen

		switch(num) {
		case 11: num = '_'; break;
		case 13: num = '>'; break;
		default:
			if (num < 32) {
				num = '+';
			}
		}
		ctx.fillText("" + (char) num, x, y + 10); 
	}
	
	public void DrawStretchPic (int x, int y, int w, int h, String pic) {
		super.DrawStretchPic(x, y, w, h, pic);
		ctx.setGlobalAlpha(1);
		ctx.fillText(pic.substring(pic.lastIndexOf('_') + 1), x, y + 10);
	}

	public void DrawPic (int x, int y, String pic) {
		super.DrawPic(x, y, pic);
		ctx.setGlobalAlpha(1);
		ctx.fillText(pic.substring(pic.lastIndexOf('_') + 1), x, y + 10);
	}

	
	@Override
	protected void GL_ResampleTexture(int[] in, int inwidth, int inheight,
			int[] out, int outwidth, int outheight) {
		// TODO(haustein) Auto-generated method stub
		// Should be simple with canvas and is not needed for wireframe
	}
  
  static native JsArrayInteger getImageSize(String name) /*-{
    return $wnd.__imageSizes[name];
  }-*/;
  
  protected ModelImage GL_LoadNewImage(final String name, int type) {
		final ModelImage image = GL_Find_free_image_t(name, type);

		JsArrayInteger d = getImageSize(name);
		if (d == null) {
			gl.log("Size not found for " + name);
			image.width = 128;
			image.height = 128;
		} else {
			image.width = d.get(0);
			image.height = d.get(1);
		}
		
		return image;
	}
  


	@Override
	protected float intBitsToFloat(int i) {
		return Numbers.intBitsToFloat(i);
	}

}
