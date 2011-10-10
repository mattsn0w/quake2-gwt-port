package com.googlecode.gwtquake.client;

import com.google.gwt.typedarrays.client.ArrayBufferView;

public interface HasArrayBufferView {

	public ArrayBufferView getTypedArray();
	public int getElementSize();
}
