/*
 * Copyright 2009 Google Inc.
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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Object that holds image data and a size. 
 */
public class ImageData extends JavaScriptObject {
 
  protected ImageData() {
  }
  
  /**
   * Returns a canvas pixel array of the size width * height * 4.
   */
  public native final CanvasPixelArray getData() /*-{
    return this.data;
  }-*/;
  
  /**
   * Returns the height of this image data object.
   */
  public native final int getHeight() /*-{
    return this.height;
  }-*/;
  
  /**
   * Returns the width of this image data object.
   */
  public native final int getWidth() /*-{
    return this.width;
  }-*/;
}
