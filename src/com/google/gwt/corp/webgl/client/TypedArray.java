/*
 * Copyright 2010 Google Inc.
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
package com.google.gwt.corp.webgl.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;

/**
 * Artificial TypedArray super class for type safety in WebGL calls with canvas array parameters.
 * Note that the slice method is moved to 
 * 
 * @author Stefan Haustein
 */
public abstract class TypedArray<T extends TypedArray<?>> extends JavaScriptObject {

  protected TypedArray() {
  }
  
  /**
   * The ArrayBuffer holding the data for this array. 
   */
  public final native ArrayBuffer getBuffer() /*-{
    return this.buffer;
  }-*/;
  
  /**
   * The offset of this data, in bytes, from the start of this TypedArray's ArrayBuffer. 
   */
  public final native int getByteOffset() /*-{
    return this.byteOffset;
  }-*/;
  
  /**
   * The length of this data in bytes. 
   */
  public final native int getByteLength() /*-{
    return this.byteLength;
  }-*/;
  
  /**
   * The length of this data in elements. 
   */
  public final native int getLength() /*-{
    return this.length;
  }-*/;
  
  /**
   * Returns a new TypedArray  view of the ArrayBuffer  store for this TypedArray, 
   * referencing the element at offset in the current view, and containing length elements. 
   */
  public final native T slice(int offset, int length) /*-{
    return this.slice(offset, length);
  }-*/;
  
  
  static JsArrayNumber toJsArray(int[] data) {
    JsArrayNumber jsan = (JsArrayNumber) JsArrayNumber.createArray();
    int len = data.length;
    for(int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i]);
    }
    return jsan;
  }
  
  // TODO(haustein) remove when set works in FF
  static JsArrayNumber toJsArray(float[] data) {
    JsArrayNumber jsan = (JsArrayNumber) JsArrayNumber.createArray();
    int len = data.length;
    for(int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i]);
    }
    return jsan;
  }
  
  static JsArrayNumber toJsArray(byte[] data) {
    JsArrayNumber jsan = (JsArrayNumber) JsArrayNumber.createArray();
    int len = data.length;
    for(int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i]);
    }
    return jsan;
  }

  static JsArrayNumber toJsArray(short[] data) {
    JsArrayNumber jsan = (JsArrayNumber) JsArrayNumber.createArray();
    int len = data.length;
    for(int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i]);
    }
    return jsan;
  }

  static JsArrayNumber toJsArrayUnsigned(byte[] data) {
    JsArrayNumber jsan = (JsArrayNumber) JsArrayNumber.createArray();
    int len = data.length;
    for(int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i] & 255);
    }
    return jsan;
  }

  static JsArrayNumber toJsArrayUnsigned(short[] data) {
    JsArrayNumber jsan = (JsArrayNumber) JsArrayNumber.createArray();
    int len = data.length;
    for(int i = len - 1; i >= 0; i--) {
      jsan.set(i, data[i] & 65535);
    }
    return jsan;
  }

}
