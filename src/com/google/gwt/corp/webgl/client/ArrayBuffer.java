// Copyright 2009 Google Inc.  All Rights Reserved
package com.google.gwt.corp.webgl.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *  The ArrayBuffer interface describes the buffer used to store data for the TypedArray 
 *  and its subclasses.
 *
 * @author haustein@google.com (Stefan Haustein)
 */
public class ArrayBuffer extends JavaScriptObject {

  protected ArrayBuffer() {
  }
  
  public static final native ArrayBuffer create(int length) /*-{
    return new ArrayBuffer(length);
  }-*/;
  
  public final native int getByteLength() /*-{
    return this.length;
  }-*/;
}
