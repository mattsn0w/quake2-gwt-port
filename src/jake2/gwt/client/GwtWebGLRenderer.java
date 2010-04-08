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

import jake2.client.refexport_t;
import jake2.qcommon.Com;
import jake2.qcommon.ResourceLoader;
import jake2.render.GLAdapter;
import jake2.render.image_t;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;


import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.corp.gfx.client.canvas.CanvasElement;
import com.google.gwt.corp.gfx.client.canvas.CanvasPixelArray;
import com.google.gwt.corp.gfx.client.canvas.CanvasRenderingContext2D;
import com.google.gwt.corp.gfx.client.canvas.ImageData;
import com.google.gwt.corp.webgl.client.WebGL;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class GwtWebGLRenderer extends AbstractGwtGLRenderer implements refexport_t {
	
	static class VideoElement extends Element {
		protected VideoElement() {
		}
		
		native final JavaScriptObject getError() /*-{
		  return this.error;
	    }-*/;

		public final native void pause() /*-{
			this.pause();
		}-*/;

		public final native void play() /*-{
			this.play();
		}-*/;

		public final native double getDuration() /*-{
			return this.duration;
		}-*/;

		public final native double getCurrentTime() /*-{
			return this.currentTime;
		}-*/;

		public final native void setCurrentTime(double s) /*-{
			return this.currentTime = s;
		}-*/;
		
		public final boolean ended() {
//			System.out.println("video.error: " + getError() + 
//					" duration: " + getDuration() + 
//					" currentTime: " + getCurrentTime());
//			
			return getError() != null || !(Double.isNaN(getDuration()) || 
					getCurrentTime() < getDuration());
		}
	}
	
	static final int IMAGE_CHECK_TIME = 250;
	static final int MAX_IMAGE_REQUEST_COUNT = 24;
	static int HOLODECK_TEXTURE_SIZE = 128;
	static int MASK = 15;
	static int HIT = MASK/2;
	ByteBuffer holoDeckTexture = ByteBuffer.allocateDirect(HOLODECK_TEXTURE_SIZE * 
			HOLODECK_TEXTURE_SIZE * 4);

	WebGLAdapter webGL;
	CanvasElement canvas1;
	CanvasElement canvas2;
	ArrayList<image_t> imageQueue = new ArrayList<image_t>();
	int waitingForImages;
	VideoElement video;
	CanvasElement canvas;
	
	public GwtWebGLRenderer(CanvasElement canvas, Element video) {
		this.gl = this.webGL = new WebGLAdapter(canvas);
		this.canvas = canvas;
		this.video = (VideoElement) video;
		
		for (int y = 0; y < HOLODECK_TEXTURE_SIZE; y++) {
			for (int x = 0; x < HOLODECK_TEXTURE_SIZE; x++) {
				holoDeckTexture.put((byte) 0);
				holoDeckTexture.put((byte) (((x & MASK) == HIT) || ((y & MASK) == HIT) ? 255 : 0));
				holoDeckTexture.put((byte) 0);
				holoDeckTexture.put((byte) 0xff);
			}
		}
		holoDeckTexture.rewind();
		
		canvas1 = (CanvasElement) Document.get().createElement("canvas");
		canvas1.getStyle().setDisplay(Display.NONE);
		canvas1.setWidth(128);
		canvas1.setHeight(128);
		Document.get().getBody().appendChild(canvas1);
		
		canvas2 = (CanvasElement) Document.get().createElement("canvas");
		canvas2.setWidth(128);
		canvas2.setHeight(128);
		canvas2.getStyle().setDisplay(Display.NONE);
		Document.get().getBody().appendChild(canvas2);
		
		init();
	}
	

	

	@Override
	protected void GL_ResampleTexture(int[] in, int inwidth, int inheight,
			int[] out, int outwidth, int outheight) {
		
		if (canvas1.getWidth() < inwidth) {
			canvas1.setWidth(inwidth);
		}
		if (canvas1.getHeight() < inheight) {
			canvas1.setHeight(inheight);
		}

		CanvasRenderingContext2D inCtx = canvas1.getContext2D();
		ImageData data = inCtx.createImageData(inwidth, inheight);
		CanvasPixelArray pixels = data.getData();
		
		int len = inwidth * inheight;
		int p = 0;
			
		for(int i = 0; i < len; i++) {
			int abgr = in[i];
			pixels.set(p, (abgr & 255));
			pixels.set(p + 1, (abgr >> 8) & 255);
			pixels.set(p + 2, (abgr >> 16) & 255);
			pixels.set(p + 3, (abgr >> 24) & 255);
			p += 4;
		}
		inCtx.putImageData(data, 0, 0);
		
		if (canvas2.getWidth() < outwidth) {
			canvas2.setWidth(outwidth);
		}
		if (canvas2.getHeight() < outheight) {
			canvas2.setHeight(outheight);
		}

		CanvasRenderingContext2D outCtx = canvas2.getContext2D();
		outCtx.drawImage(canvas1, 0, 0, inwidth, inheight, 0, 0, outwidth, outheight);
		
		data = outCtx.getImageData(0, 0, outwidth, outheight);
		pixels = data.getData();
		
		len = outwidth * outheight;
		p = 0;
			
		for(int i = 0; i < len; i++) {
			int r = pixels.get(p) & 255;
			int g = pixels.get(p + 1) & 255;
			int b = pixels.get(p + 2) & 255;
			int a = pixels.get(p + 3) & 255;
			p += 4;
			out[i] = (a << 24) | (b << 16) | (g << 8) | r;
		}
	}
	
	protected image_t GL_LoadNewImage(final String name, int type) {
		final image_t image = GL_Find_free_image_t(name, type);

		JsArrayInteger d = getImageSize(name);
		if (d == null) {
			gl.log("Size not found for " + name);
			image.width = 128;
			image.height = 128;
		} else {
			image.width = d.get(0);
			image.height = d.get(1);
		}
		
		if (type != jake2.qcommon.QuakeImage.it_pic) {
			gl.glTexImage2D(GLAdapter.GL_TEXTURE_2D, 0, 4, HOLODECK_TEXTURE_SIZE, HOLODECK_TEXTURE_SIZE, 0, GLAdapter.GL_RGBA, 
			    GLAdapter.GL_UNSIGNED_BYTE, holoDeckTexture);
			gl.glTexParameterf(GLAdapter.GL_TEXTURE_2D, GLAdapter.GL_TEXTURE_MIN_FILTER, GLAdapter.GL_LINEAR);
			gl.glTexParameterf(GLAdapter.GL_TEXTURE_2D, GLAdapter.GL_TEXTURE_MAG_FILTER, GLAdapter.GL_LINEAR);
		}

		imageQueue.add(image);
		if(imageQueue.size() == 1) {
		  new ImageLoader().schedule();
		}
		
		return image;
	}
	
	class ImageLoader extends Timer {

	  @Override
	  public void run() {
	    Document doc = Document.get();

	    while(!ResourceLoader.Pump() && waitingForImages < MAX_IMAGE_REQUEST_COUNT && imageQueue.size() > 0) {
	      final image_t image = imageQueue.remove(0);
	      final ImageElement img = doc.createImageElement();
	      img.setSrc(convertPicName(image.name, image.type));
	      img.getStyle().setDisplay(Display.NONE);
	      doc.getBody().appendChild(img);
	      
	      if (img.getPropertyBoolean("complete")) {
	        loaded(image, img);
	      } else {
	    	waitingForImages(+1);
	        Image imgWidget = Image.wrap(img);
	        final ImageElement finalImg = img;
	        imgWidget.addLoadHandler(new LoadHandler() {
	          public void onLoad(LoadEvent event) {
	        	waitingForImages(-1);
	            loaded(image, finalImg);
	          }
	        });
	        imgWidget.addErrorHandler(new ErrorHandler() {
	          public void onError(ErrorEvent event) {
	            String src = finalImg.getSrc();
	            if (src.endsWith("&rt&rt&rt&rt")) {
	              gl.log("too many load errors for " + finalImg.getSrc() + "; giving up!");
	              waitingForImages(-1);
	              image.complete = true;
	            } else {
	            	finalImg.setSrc(finalImg.getSrc() + "&rt");
	            }
	          }
	        });	
	      } // else
	    } // while
	    if (imageQueue.size() > 0) {
		  schedule();
	    }
	  }
	  
	protected void waitingForImages(int i) {
		waitingForImages += i;
		if (waitingForImages > 0) {
		  Com.Printf("Waiting for " + waitingForImages + " images\r");
		}
	}

	public void schedule() {
		  schedule(IMAGE_CHECK_TIME);
	  }
	}
	
	public void loaded(image_t image, ImageElement img) {
		setPicDataHighLevel(image, img);
	//	setPicDataLowLevel(image, img);
	}
		
	ByteBuffer bb = ByteBuffer.allocateDirect(128*128*4);
	
	public void setPicDataHighLevel(image_t image, ImageElement img) {
		image.has_alpha = true;
		image.complete = true;
		image.height = img.getHeight();
		image.width = img.getWidth();
		
		boolean mipMap = image.type != jake2.qcommon.QuakeImage.it_pic && 
			image.type != jake2.qcommon.QuakeImage.it_sky;
		
		GL_Bind(image.texnum);


		// canvas does not seem to work for texImage2d
		// GenertateMipmap does not seem to work at all.....

		if (mipMap) {
			int p2w = 1 << ((int) Math.ceil(Math.log(image.width) / Math.log(2))); 
			int p2h = 1 << ((int) Math.ceil(Math.log(image.height) / Math.log(2))); 

			if (canvas1.getWidth() < p2w) {
				canvas1.setWidth(p2w);
			}
			if (canvas1.getHeight() < p2h) {
				canvas1.setHeight(p2h);
			}
			
			image.width = image.upload_width = p2w;
			image.height = image.upload_height = p2h;
			
//			int level = 0;
//
//			if (bb.capacity() < p2h * p2w * 4) {
//				bb = ByteBuffer.allocateDirect(p2h * p2w * 4);
//			}
//
//			do {
//				canvas1.getContext2D().clearRect(0, 0, p2w, p2h);
//				canvas1.getContext2D().drawImage(img, 0, 0, p2w, p2h);
//				ImageData data = canvas1.getContext2D().getImageData(0, 0, p2w, p2h);
//			
//				CanvasPixelArray pixels = data.getData();
//			
//				int len = p2w * p2h * 4;
//				ByteBuffer bb = ByteBuffer.allocateDirect(len);
//				
//				for(int i = 0; i < len; i++) {
//					bb.put(i, (byte) (pixels.get(i) ));
//				}
//			
//				webGL.glTexImage2D(WebGL.GL_TEXTURE_2D, level++, GLAdapter.GL_RGBA, p2w, p2h, 0, 
//						GLAdapter.GL_RGBA, GLAdapter.GL_UNSIGNED_BYTE, bb);
//				
//				p2w = Math.max(p2w / 2, 1);
//				p2h = Math.max(p2h / 2, 1);
//			}
//			while(p2w >1 || p2h > 1);
//			// TODO: remove when mipMaps work in Webkit
//			mipMap = false;
//			}
			canvas1.setWidth(p2w);
			canvas1.setHeight(p2h);
			canvas1.getContext2D().drawImage(img, 0, 0, p2w, p2h);
			webGL.glTexImage2d(WebGL.GL_TEXTURE_2D, 0, canvas1);
//			webGL.glGenerateMipmap(WebGL.GL_TEXTURE_2D);
		} else {
			image.upload_height = image.height;
			image.upload_width = image.width;
			webGL.glTexImage2d(WebGL.GL_TEXTURE_2D, 0, img);
		}
		gl.glTexParameterf(GLAdapter.GL_TEXTURE_2D, GLAdapter.GL_TEXTURE_MIN_FILTER, 
				/*mipMap ? GLAdapter.GL_LINEAR_MIPMAP_NEAREST :*/ GLAdapter.GL_LINEAR);
		gl.glTexParameterf(GLAdapter.GL_TEXTURE_2D, GLAdapter.GL_TEXTURE_MAG_FILTER, GLAdapter.GL_LINEAR);
	}

  public void __setPicDataHighLevel(image_t image, ImageElement img) {
    image.has_alpha = true;
    image.complete = true;
    image.height = img.getHeight();
    image.width = img.getWidth();
    image.upload_height = image.height;
    image.upload_width = image.width;
    GL_Bind(image.texnum);
    webGL.glTexImage2d(WebGL.GL_TEXTURE_2D, 0, img);
    gl.glTexParameterf(GLAdapter.GL_TEXTURE_2D, GLAdapter.GL_TEXTURE_MIN_FILTER, GLAdapter.GL_LINEAR);
    gl.glTexParameterf(GLAdapter.GL_TEXTURE_2D, GLAdapter.GL_TEXTURE_MAG_FILTER, GLAdapter.GL_LINEAR);
  }

	public void setPicDataLowLevel(image_t image, ImageElement img) {
		CanvasElement canvas = (CanvasElement) Document.get().createElement("canvas");
		int w = img.getWidth();
		int h = img.getHeight();
		canvas.setWidth(w);
		canvas.setHeight(h);
//		canvas.getStyle().setProperty("border", "solid 1px green");
		canvas.getStyle().setDisplay(Display.NONE);
		Document.get().getBody().appendChild(canvas);
		CanvasRenderingContext2D ctx = canvas.getContext2D();
		ctx.drawImage(img, 0, 0);
		ImageData data = ctx.getImageData(0, 0, w, h);
		CanvasPixelArray pixels = data.getData();
		
		int count = w * h * 4;
		byte[] pic = new byte[count];
		for (int i = 0; i < count; i += 4) {
			pic[i + 3] = (byte) pixels.get(i + 3); // alpha, then bgr
			pic[i + 2] = (byte) pixels.get(i + 2);
			pic[i + 1] = (byte) pixels.get(i + 1);
			pic[i] = (byte) pixels.get(i);
		}
		
		GL_SetPicData(image, pic, w, h, 32);
	}
	
	 protected void debugLightmap(IntBuffer lightmapBuffer, int w, int h, float scale) {
		 CanvasElement canvas = (CanvasElement) Document.get().createElement("canvas");
		 canvas.setWidth(w);
		 canvas.setHeight(h);
		 Document.get().getBody().appendChild(canvas);
		 ImageData id = canvas.getContext2D().createImageData(w, h);
		 CanvasPixelArray pd = id.getData();
		 for (int i = 0; i < w*h; i++) {
			 int abgr = lightmapBuffer.get(i);
			 pd.set(i*4, abgr & 255);
			 pd.set(i*4+1, abgr & 255);
			 pd.set(i*4+2, abgr & 255);
			 pd.set(i*4+3, abgr & 255);
		 }
		 canvas.getContext2D().putImageData(id, 0, 0);
	 }
	 
	private static String convertPicName(String name, int type) {
	  int dotIdx = name.indexOf('.');
	  assert dotIdx != -1;
	  return "baseq2/" + name.substring(0, dotIdx) + ".png";
	}
	
	public boolean updateVideo() {
		
		return !video.ended();
	}
	
	
	public void CinematicSetPalette(byte[] palette) {
		setVideoVisible(palette != null);
	}
	
	
	boolean videoVisible = false;
	
	private void setVideoVisible(boolean show) {
		if (videoVisible == show) {
			return;
		}
		System.out.println("setVideoVisible(" + show + ")");
		videoVisible = show;
		if (show) {
			canvas.getStyle().setProperty("display", "none");
			video.getStyle().setProperty("display", "");
			if (video.getAttribute("src") != null  && !video.ended()) {
				video.play();
			}
		} else {
			canvas.getStyle().setProperty("display", "");
			video.getStyle().setProperty("display", "none");
			if (video.getAttribute("src") != null && !video.ended()) {
				video.pause();
			}
		}
	}
	
	public boolean showVideo(String name) {
		if (name == null) {
			setVideoVisible(false);
			return true;
		}

//		String src = GWT.getModuleBaseURL();
//		int cut = src.indexOf("/", 8);
//		if (cut == -1) {
//			cut = src.length();
//		}
		String src = "baseq2/video/" + name + ".mp4";

		System.out.println("trying to play video: " + src);

		video.setAttribute("class", "video-stream");
		video.setAttribute("src", src);
		if (!Double.isNaN(video.getDuration())) {
			video.setCurrentTime(0);
		}
		
		setVideoVisible(true);
		return true;
	}

	
}
