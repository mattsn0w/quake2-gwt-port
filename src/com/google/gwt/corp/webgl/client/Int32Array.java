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

import com.google.gwt.core.client.JsArrayNumber;

/**
 * GWT wrapper for the WebGL Int32Array (WARNING:
 * this is not a stable API). 
 * 
 * @author Stefan Haustein
 */
public class Int32Array extends TypedArray<Int32Array> {
  
  protected Int32Array() {
  }
  
  /**
   * Create a new canvas array object of the given length with a new underlying 
   * ArrayBuffer large enough to hold length elements of the type of this buffer. 
   * Data in the buffer is initialized to 0. 
   */
  public static final native Int32Array create(int size) /*-{
    return new Int32Array(size);
  }-*/;

  /**
   * Create a new canvas array object with a new underlying ArrayBuffer large enough 
   * to hold the given data, then copy the passed data into the buffer. 
   */
  public static final native Int32Array create(JsArrayNumber data) /*-{
    return new Int32Array(data);
  }-*/;
  
  /**
   * Create a new canvas array object with a new underlying ArrayBuffer large enough 
   * to hold the given data, then copy the passed data into the buffer. 
   */
  public static final Int32Array create(int[] data) {
// TODO(haustein) Uncomment when set works in FF    
//    Int32Array result = create(data.length);
//    result.set(data);
//    return result;
    return create(toJsArray(data));
  }

  /**
   * Create a new canvas array object with a new underlying ArrayBuffer large enough 
   * to hold the given data, then copy the passed data into the buffer. 
   */
  public static final native Int32Array create(Int32Array array) /*-{
    return new Int32Array(array);
  }-*/;
  
  /**
   * Create a new canvas array object using the passed ArrayBuffer for its storage. 
   */
  public static final native Int32Array create(ArrayBuffer buffer) /*-{
    return new Int32Array(buffer);
  }-*/;

  /**
   * Create a new canvas array object using the passed ArrayBuffer for its storage,
   * starting at the given byte offset and extending to the end of the underlying buffer.
   */
  public static final native Int32Array create(ArrayBuffer buffer, 
      int byteOffset) /*-{
    return new Int32Array(buffer, byteOffset);
  }-*/;

  /**
   * Create a new canvas array object using the passed ArrayBuffer for its storage. 
   * @param byteOffset indicates the offset in bytes from the start of the ArrayBuffer.
   * @param length the count of elements from the offset that this WebGLByteArray will 
   *                reference. 
   * @return the new canvas array.
   */
  public static final native Int32Array create(ArrayBuffer buffer, int byteOffset, 
      int length) /*-{
    return new Int32Array(buffer, byteOffset, length);
  }-*/;
  
  /**
   * Return the element at the given index. If the index is out of range, an exception is raised. 
   */
  public native final int get(int index) /*-{
    return this[index];
  }-*/;
  
  /**
   * Sets the element at the given index to the given value. If the index is out of range, 
   * an exception is raised. 
   */
  public native final void set(int index, int value) /*-{
    this[index] = value;
  }-*/;

  
  /**
   * Set multiple values, reading input values from the array.
   */
  public native final void set(JsArrayNumber array) /*-{
    this.set(array);
  }-*/;
  
  /**
   * Set multiple values, reading input values from the array.
   * 
   * @param array the array to read the values from
   * @param offset indicates the index in the current array where values are written. 
   */
  public native final void set(JsArrayNumber array, int offset) /*-{
    this.set(array, offset);
  }-*/;
  
  /**
   * Set multiple values, reading input values from the array.
   */
  public native final void set(Int32Array array) /*-{
    this.set(array);
  }-*/;

  /**
   * Set multiple values, reading input values from the array.
   * 
   * @param array the array to read the values from
   * @param offset indicates the index in the current array where values are written. 
   */
  public native final void set(Int32Array array, int offset) /*-{
    this.set(array, offset);
  }-*/;

  /**
   * Set multiple values, reading input values from the array.
   */
  public final void set(int[] array) {
    set(array, 0);
  }
  
  /**
   * Set multiple values, reading input values from the array.
   * 
   * @param array the array to read the values from
   * @param offset indicates the index in the current array where values are written. 
   */
  public final void set(int[] array, int offset) {
    int len = array.length;
    for (int i = 0; i < len; i++) {
      set(i + offset, array[i]);
    }
  }
}