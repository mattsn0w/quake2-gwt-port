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

import jake2.render.gl.GLRenderer;
import jake2.sys.KBD;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.corp.compatibility.Numbers;


abstract class AbstractGwtGLRenderer extends GLRenderer {
	KBD kbd = new GwtKBD();
	
	
	public KBD getKeyboardHandler() {
		return kbd;
	}
	

	static native JsArrayInteger getImageSize(String name) /*-{
      return $wnd.__imageSizes[name];
    }-*/;
	

	@Override
	protected float intBitsToFloat(int i) {
		return Numbers.intBitsToFloat(i);
	}
}
