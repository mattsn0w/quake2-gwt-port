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
 * Array-like object holding the actual image data for an ImageData object. For each pixel, 
 * this object contains a red, green, blue and alpha value between 0 and 255 (in this order).
 * Note that we use ints here to represent the data to avoid complexities stemming from
 * bytes being signed in Java.
 */
public class CanvasPixelArray extends JavaScriptObject {
  
  protected CanvasPixelArray() {
  }
  
  /**
   * Returns the data value at position i.
   */
  public final native int get(int i) /*-{
    return this[i];
  }-*/;
  
  /**
   * Sets the data value at position i to the given value. The value will be clamped to 
   * the range 0..255.
   */
  public final native void set(int i, int value) /*-{
    this[i] = value;
  }-*/;
}
