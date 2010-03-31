// Copyright 2009 Google Inc.  All Rights Reserved
package com.google.gwt.corp.webgl.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *  The WebGLArrayBuffer interface describes the buffer used to store data for the WebGLArray 
 *  and its subclasses.
 *
 * @author haustein@google.com (Stefan Haustein)
 */
public class WebGLArrayBuffer extends JavaScriptObject {

  protected WebGLArrayBuffer() {
  }
  
  public static final native WebGLArrayBuffer create(int length) /*-{
    return new WebGLArrayBuffer(length);
  }-*/;
  
  public final native int getByteLength() /*-{
    return this.length;
  }-*/;
}
