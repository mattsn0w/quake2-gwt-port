// Copyright 2009 Google Inc.  All Rights Reserved
package com.google.gwt.corp.webgl.client;

import com.google.gwt.core.client.JsArrayNumber;

/**
 * GWT wrapper for the WebGL Int8Array (WARNING:
 * this is not a stable API). 
 * 
 * @author haustein@google.com (Stefan Haustein)
 */

public class Int8Array extends TypedArray<Int8Array> {
  
  protected Int8Array() {
  }
  
  /**
   * Create a new canvas array object of the given length with a new underlying 
   * ArrayBuffer large enough to hold length elements of the type of this buffer. 
   * Data in the buffer is initialized to 0. 
   */
  public static final native Int8Array create(int size) /*-{
    return new Int8Array(size);
  }-*/;

  /**
   * Create a new canvas array object with a new underlying ArrayBuffer large enough 
   * to hold the given data, then copy the passed data into the buffer. 
   */
  public static final native Int8Array create(JsArrayNumber data) /*-{
    return new Int8Array(data);
  }-*/;
  
  /**
   * Create a new canvas array object with a new underlying ArrayBuffer large enough 
   * to hold the given data, then copy the passed data into the buffer. 
   */
  public static final Int8Array create(byte[] data) {
// TODO(haustein) Uncomment when set works in FF    
//    Int8Array result = create(data.length);
//    result.set(data);
//    return result;
    return create(toJsArray(data));
  }

  /**
   * Create a new canvas array object with a new underlying ArrayBuffer large enough 
   * to hold the given data, then copy the passed data into the buffer. 
   */
  public static final native Int8Array create(Int8Array array) /*-{
    return new Int8Array(array);
  }-*/;
  
  /**
   * Create a new canvas array object using the passed ArrayBuffer for its storage. 
   */
  public static final native Int8Array create(ArrayBuffer buffer) /*-{
    return new Int8Array(buffer);
  }-*/;

  /**
   * Create a new canvas array object using the passed ArrayBuffer for its storage,
   * starting at the given byte offset and extending to the end of the underlying buffer.
   */
  public static final native Int8Array create(ArrayBuffer buffer, 
      int byteOffset) /*-{
    return new Int8Array(buffer, byteOffset);
  }-*/;

  /**
   * Create a new canvas array object using the passed ArrayBuffer for its storage. 
   * @param byteOffset indicates the offset in bytes from the start of the ArrayBuffer.
   * @param length the count of elements from the offset that this Int8Array will 
   *                reference. 
   * @return the new canvas array.
   */
  public static final native Int8Array create(ArrayBuffer buffer, int byteOffset, 
      int length) /*-{
    return new Int8Array(buffer, byteOffset, length);
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
  public native final void set(Int8Array array) /*-{
    this.set(array);
  }-*/;

  /**
   * Set multiple values, reading input values from the array.
   * 
   * @param array the array to read the values from
   * @param offset indicates the index in the current array where values are written. 
   */
  public native final void set(Int8Array array, int offset) /*-{
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
