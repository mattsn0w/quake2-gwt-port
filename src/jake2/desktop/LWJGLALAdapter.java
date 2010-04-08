/*
Copyright (C) 2010 Copyright 2010 Google Inc.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package jake2.desktop;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import jake2.sound.ALAdapter;

/**
 * Implementation based on delegation to LWJGL's OpenAL API.
 */
public class LWJGLALAdapter extends ALAdapter {

  public void alBufferData(int buffer, int format, ByteBuffer data, int freq) {
    AL10.alBufferData(buffer, format, data, freq);
  }

  public void alBufferData(int buffer, int format, IntBuffer data, int freq) {
    AL10.alBufferData(buffer, format, data, freq);
  }

  public void alBufferData(int buffer, int format, ShortBuffer data, int freq) {
    AL10.alBufferData(buffer, format, data, freq);
  }

  public void alDeleteBuffers(IntBuffer buffers) {
    AL10.alDeleteBuffers(buffers);
  }

  public void alDeleteSources(IntBuffer sources) {
    AL10.alDeleteSources(sources);
  }

  public void alDisable(int capability) {
    AL10.alDisable(capability);
  }

  public void alDistanceModel(int value) {
    AL10.alDistanceModel(value);
  }

  public void alDopplerFactor(float value) {
    AL10.alDopplerFactor(value);
  }

  public void alDopplerVelocity(float value) {
    AL10.alDopplerVelocity(value);
  }

  public void alEnable(int capability) {
    AL10.alEnable(capability);
  }

  public void alGenBuffers(IntBuffer buffers) {
    AL10.alGenBuffers(buffers);
  }

  public void alGenSources(IntBuffer sources) {
    AL10.alGenSources(sources);
  }

  public boolean alGetBoolean(int pname) {
    return AL10.alGetBoolean(pname);
  }

  public float alGetBufferf(int buffer, int pname) {
    return AL10.alGetBufferf(buffer, pname);
  }

  public int alGetBufferi(int buffer, int pname) {
    return AL10.alGetBufferi(buffer, pname);
  }

  public double alGetDouble(int pname) {
    return AL10.alGetDouble(pname);
  }

  public void alGetDouble(int pname, DoubleBuffer data) {
    AL10.alGetDouble(pname, data);
  }

  public int alGetEnumValue(String ename) {
    return AL10.alGetEnumValue(ename);
  }

  public int alGetError() {
    return AL10.alGetError();
  }

  public float alGetFloat(int pname) {
    return AL10.alGetFloat(pname);
  }

  public void alGetFloat(int pname, FloatBuffer data) {
    AL10.alGetFloat(pname, data);
  }

  public int alGetInteger(int pname) {
    return AL10.alGetInteger(pname);
  }

  public void alGetInteger(int pname, IntBuffer data) {
    AL10.alGetInteger(pname, data);
  }

  public void alGetListener(int pname, FloatBuffer floatdata) {
    AL10.alGetListener(pname, floatdata);
  }

  public float alGetListenerf(int pname) {
    return AL10.alGetListenerf(pname);
  }

  public int alGetListeneri(int pname) {
    return AL10.alGetListeneri(pname);
  }

  public void alGetSource(int source, int pname, FloatBuffer floatdata) {
    AL10.alGetSource(source, pname, floatdata);
  }

  public float alGetSourcef(int source, int pname) {
    return AL10.alGetSourcef(source, pname);
  }

  public int alGetSourcei(int source, int pname) {
    return AL10.alGetSourcei(source, pname);
  }

  public String alGetString(int i) {
    return AL10.alGetString(i);
  }

  public boolean alIsBuffer(int buffer) {
    return AL10.alIsBuffer(buffer);
  }

  public boolean alIsEnabled(int capability) {
    return AL10.alIsEnabled(capability);
  }

  public boolean alIsExtensionPresent(String fname) {
    return AL10.alIsExtensionPresent(fname);
  }

  public boolean alIsSource(int id) {
    return AL10.alIsSource(id);
  }

  public void alListener(int pname, FloatBuffer value) {
    AL10.alListener(pname, value);
  }

  public void alListener3f(int pname, float v1, float v2, float v3) {
    AL10.alListener3f(pname, v1, v2, v3);
  }

  public void alListenerf(int pname, float value) {
    AL10.alListenerf(pname, value);
  }

  public void alListeneri(int pname, int value) {
    AL10.alListeneri(pname, value);
  }

  public void alSource(int source, int pname, FloatBuffer value) {
    AL10.alSource(source, pname, value);
  }

  public void alSource3f(int source, int pname, float v1, float v2, float v3) {
    AL10.alSource3f(source, pname, v1, v2, v3);
  }

  public void alSourcef(int source, int pname, float value) {
    AL10.alSourcef(source, pname, value);
  }

  public void alSourcei(int source, int pname, int value) {
    AL10.alSourcei(source, pname, value);
  }

  public void alSourcePause(int source) {
    AL10.alSourcePause(source);
  }

  public void alSourcePause(IntBuffer sources) {
    AL10.alSourcePause(sources);
  }

  public void alSourcePlay(int source) {
    AL10.alSourcePlay(source);
  }

  public void alSourcePlay(IntBuffer sources) {
    AL10.alSourcePlay(sources);
  }

  public void alSourceQueueBuffers(int source, IntBuffer buffers) {
    AL10.alSourceQueueBuffers(source, buffers);
  }

  public void alSourceRewind(int source) {
    AL10.alSourceRewind(source);
  }

  public void alSourceRewind(IntBuffer sources) {
    AL10.alSourceRewind(sources);
  }

  public void alSourceStop(int source) {
    AL10.alSourceStop(source);
  }

  public void alSourceStop(IntBuffer sources) {
    AL10.alSourceStop(sources);
  }

  public void alSourceUnqueueBuffers(int source, IntBuffer buffers) {
    AL10.alSourceUnqueueBuffers(source, buffers);
  }

  @Override
  public void create(String deviceArguments, int contextFrequency,
      int contextRefresh, boolean contextSynchronized) {
    try {
      AL.create(deviceArguments, contextFrequency, contextRefresh,
          contextSynchronized);
    } catch (LWJGLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void create(String deviceArguments, int contextFrequency,
      int contextRefresh, boolean contextSynchronized, boolean openDevice) {
    try {
      AL.create(deviceArguments, contextFrequency, contextRefresh,
          contextSynchronized, openDevice);
    } catch (LWJGLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void create() {
    try {
      AL.create();
      super.create();
    } catch (LWJGLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void destroy() {

    try {
      AL.create();
      super.destroy();
    } catch (LWJGLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isCreated() {
    return AL.getDevice().isValid() && super.isCreated();
  }
}
