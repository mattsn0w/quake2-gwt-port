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

import jake2.client.Dimension;
import jake2.client.SCR;
import jake2.client.WebSocketFactoryImpl;
import jake2.client.refexport_t;
import jake2.qcommon.Compatibility;
import jake2.qcommon.Cvar;
import jake2.qcommon.Globals;
import jake2.qcommon.Qcommon;
import jake2.qcommon.ResourceLoader;
import jake2.sound.S;
import jake2.sys.NET;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.corp.gfx.client.canvas.CanvasElement;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;

public class GwtQuake implements EntryPoint {

	enum BrowserType {
		FIREFOX, CHROME, SAFARI, OTHER
	};
	
	private static final int INTER_FRAME_DELAY = 1;
	private static final int LOADING_DELAY = 500;

  static CanvasElement canvas;
  static Element video;
  static BrowserType browserType;

	private static final String NO_WEBGL_MESSAGE = 
	  "<div style='padding:20px;font-family: sans-serif;'>" +
	  "<h2>WebGL Support Required</h2>" +
	  "<p>For a list of compatible browsers and installation instructions, please refer to" +
	  "<ul><li>"+
	  "<a href='http://code.google.com/p/quake2-gwt-port/wiki/CompatibleBrowsers' " + 
	  "style='color:#888'>http://code.google.com/p/quake2-gwt-port/wiki/CompatibleBrowsers</a>" + 
	  "</li></ul>"+
    "<p>For a detailed error log, please refer to the JS console.<p>" +
    "</div>";

  Timer timer;
  int w;
  int h;
  
  public void onModuleLoad() {
  // Initialize drivers.
	Document doc = Document.get();
	doc.setTitle("GWT Quake II");
	BodyElement body = doc.getBody();
	Style style = body.getStyle();
	style.setPadding(0, Unit.PX);
	style.setMargin(0, Unit.PX);
	style.setBorderWidth(0, Unit.PX);
	style.setProperty("height", "100%");
	style.setBackgroundColor("#000");
	style.setColor("#888");
	
	String userAgent = Navigator.getUserAgent();
	System.out.println("UA: " + userAgent);
	if (userAgent == null) {
		browserType = BrowserType.OTHER;
	} else if (userAgent.indexOf("Chrome/") != -1) {
		browserType = BrowserType.CHROME;
	} else if (userAgent.indexOf("Safari/") != -1) {
		browserType = BrowserType.SAFARI;
	} else if (userAgent.indexOf("Firefox/") != -1 || userAgent.indexOf("Minefield/") != -1) {
		browserType = BrowserType.FIREFOX;
	} else {
		browserType = BrowserType.OTHER;
	}
	
	boolean wireframe = (""+Window.Location.getHash()).indexOf("wireframe") != -1;
	
	canvas = (CanvasElement) doc.createElement("canvas");
	video = doc.createElement("video");
	
	w = Window.getClientWidth();
	h = Window.getClientHeight();
	canvas.setWidth(w);
	canvas.setHeight(h);
	style = canvas.getStyle();
	style.setProperty("height", "100%");
	style.setProperty("width", "100%");
	
	style = video.getStyle();
	style.setProperty("height", "100%");
	style.setProperty("width", "100%");
	style.setProperty("display", "none");
	
	body.appendChild(canvas);
	body.appendChild(video);
	
    try {
      final refexport_t renderer = wireframe 
      	? new GwtWireframeGLRenderer(canvas) : new GwtWebGLRenderer(canvas, video);
      Globals.re = renderer;

      ResourceLoader.impl = new GwtResourceLoaderImpl();
      Compatibility.impl = new CompatibilityImpl();
      
      S.impl = new GwtSound();
      NET.socketFactory = new WebSocketFactoryImpl();
//      Sys.impl = new Sys.SysImpl() {
//        public void exit(int status) {
//          Window.alert("Something's rotten in Denmark");
//          Window.Location.assign("gameover.html");
//        }
//      };

      // Flags.
      Qcommon.Init(new String[] { "GQuake" });


      // Enable stdout.
      Globals.nostdout = Cvar.Get("nostdout", "0", 0);

      Window.addResizeHandler(new ResizeHandler() {

        public void onResize(ResizeEvent event) {
          if (Window.getClientWidth() == w && 
              Window.getClientHeight() == h) {
            return;
          } 

          w = Window.getClientWidth();
          h = Window.getClientHeight();

          renderer.GLimp_SetMode(new Dimension(w, h), 0, false);
        }
      });
      
//      QuakeServer.main(new String[0], new DummySNetImpl(), false);
      
      timer = new Timer() {
        double startTime = Duration.currentTimeMillis();

        @Override
        public void run() {
          try {
            double curTime = Duration.currentTimeMillis();
            boolean pumping = ResourceLoader.Pump();
            if (pumping) {
            	SCR.UpdateScreen2();
            } else {
            	int dt = (int)(curTime - startTime);
            	GwtKBD.Frame(dt);
            	Qcommon.Frame(dt);
            }
            startTime = curTime;
            timer.schedule(ResourceLoader.Pump() ? LOADING_DELAY : INTER_FRAME_DELAY);
          } catch(Exception e) {
            Compatibility.printStackTrace(e);
          }
        }
      };
      timer.schedule(INTER_FRAME_DELAY);
    } catch (Exception e) {
    	Compatibility.printStackTrace(e);
      body.setInnerHTML(NO_WEBGL_MESSAGE);
    }
  }
}
