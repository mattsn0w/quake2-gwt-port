// Copyright 2009 Google Inc.  All Rights Reserved
package com.google.gwt.corp.webgl.client;

import com.google.gwt.core.client.JsArrayNumber;

/**
 * GWT wrapper for the WebGL WebGLUnsignedByteArray (WARNING:
 * this is not a stable API). 
 * 
 * @author haustein@google.com (Stefan Haustein)
 */

public class WebGLByteArray extends WebGLArray<WebGLByteArray> {
  
  protected WebGLByteArray() {
  }
  
  /**
   * Create a new canvas array object of the given length with a new underlying 
   * WebGLArrayBuffer large enough to hold length elements of the type of this buffer. 
   * Data in the buffer is initialized to 0. 
   */
  public static final native WebGLByteArray create(int size) /*-{
    return new WebGLByteArray(size);
  }-*/;

  /**
   * Create a new canvas array object with a new underlying WebGLArrayBuffer large enough 
   * to hold the given data, then copy the passed data into the buffer. 
   */
  public static final native WebGLByteArray create(JsArrayNumber data) /*-{
    return new WebGLByteArray(data);
  }-*/;
  
  /**
   * Create a new canvas array object with a new underlying WebGLArrayBuffer large enough 
   * to hold the given data, then copy the passed data into the buffer. 
   */
  public static final WebGLByteArray create(byte[] data) {
// TODO(haustein) Uncomment when set works in FF    
//    WebGLByteArray result = create(data.length);
//    result.set(data);
//    return result;
    return create(toJsArray(data));
  }

  /**
   * Create a new canvas array object with a new underlying WebGLArrayBuffer large enough 
   * to hold the given data, then copy the passed data into the buffer. 
   */
  public static final native WebGLByteArray create(WebGLByteArray array) /*-{
    return new WebGLByteArray(array);
  }-*/;
  
  /**
   * Create a new canvas array object using the passed WebGLArrayBuffer for its storage. 
   */
  public static final native WebGLByteArray create(WebGLArrayBuffer buffer) /*-{
    return new WebGLByteArray(buffer);
  }-*/;

  /**
   * Create a new canvas array object using the passed WebGLArrayBuffer for its storage,
   * starting at the given byte offset and extending to the end of the underlying buffer.
   */
  public static final native WebGLByteArray create(WebGLArrayBuffer buffer, 
      int byteOffset) /*-{
    return new WebGLByteArray(buffer, byteOffset);
  }-*/;

  /**
   * Create a new canvas array object using the passed WebGLArrayBuffer for its storage. 
   * @param byteOffset indicates the offset in bytes from the start of the WebGLArrayBuffer.
   * @param length the count of elements from the offset that this WebGLByteArray will 
   *                reference. 
   * @return the new canvas array.
   */
  public static final native WebGLByteArray create(WebGLArrayBuffer buffer, int byteOffset, 
      int length) /*-{
    return new WebGLByteArray(buffer, byteOffset, length);
  }-*/;
  
  /**
   * Return the element at the given index. If the index is out of range, an exception is raised. 
   */
  public native final byte get(int index) /*-{
    return this[index];
  }-*/;
  
  /**
   * Sets the element at the given index to the given value. If the index is out of range, 
   * an exception is raised. 
   */
  public native final void set(int index, byte value) /*-{
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
  public native final void set(WebGLByteArray array) /*-{
    this.set(array);
  }-*/;

  /**
   * Set multiple values, reading input values from the array.
   * 
   * @param array the array to read the values from
   * @param offset indicates the index in the current array where values are written. 
   */
  public native final void set(WebGLByteArray array, int offset) /*-{
    this.set(array, offset);
  }-*/;

  /**
   * Set multiple values, reading input values from the array.
   */
  public final void set(byte[] array) {
    set(array, 0);
  }
  
  /**
   * Set multiple values, reading input values from the array.
   * 
   * @param array the array to read the values from
   * @param offset indicates the index in the current array where values are written. 
   */
  public final void set(byte[] array, int offset) {
    int len = array.length;
    for (int i = 0; i < len; i++) {
      set(i + offset, array[i]);
    }
  }
}
