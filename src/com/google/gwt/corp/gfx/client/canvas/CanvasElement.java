/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.corp.gfx.client.canvas;

import com.google.gwt.dom.client.Element;

/**
 * Canvas element.
 * 
 * @see <a href="http://www.w3.org/html/wg/html5/#canvas">W3C HTML 5
 *      Specification</a>
 */
public class CanvasElement extends Element {

  protected CanvasElement() {
  }

  /**
   * Gets the 2D rendering context that may be used to draw on this canvas.
   * 
   * @return the canvas rendering context
   */
  public final native CanvasRenderingContext2D getContext2D() /*-{
    return this.getContext("2d");
  }-*/;

  /**
   * Gets the height of the canvas.
   * 
   * @return the height, in pixels
   */
  public final native int getHeight() /*-{
    return this.height;
  }-*/;

  /**
   * Gets the width of the canvas.
   * 
   * @return the width, in pixels
   */
  public final native int getWidth() /*-{
    return this.width;
  }-*/;

  /**
   * Determines whether rendering is supported.
   * 
   * @return <code>true</code> if rendering is supported
   */
  public final native boolean isSupported() /*-{
    return typeof this.getContext != "undefined";
  }-*/;

  /**
   * Sets the height of the canvas.
   * 
   * @param height the height, in pixels
   */
  public final native void setHeight(int height) /*-{
    this.height = height;
  }-*/;

  /**
   * Sets the width of the canvas.
   * 
   * @param width the width, in pixels
   */
  public final native void setWidth(int width) /*-{
    this.width = width;
  }-*/;

  /**
   * Returns a data URL for the current content of the canvas element
   * 
   * @return a data URL for the current content of this element.
   */
  public final native String toDataUrl() /*-{
    return canvas.toDataUrl();
  }-*/;  
}
