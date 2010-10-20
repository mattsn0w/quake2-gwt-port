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

import jake2.render.DisplayMode;
import jake2.render.GLAdapter;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;


public class LWJGLAdapter extends GLAdapter {

	public void glDisableClientState(int state) {
		GL11.glDisableClientState(state);
	}

	public void glEnableClientState(int state) {
		GL11.glEnableClientState(state);
	}

	public void glPushMatrix() {
		GL11.glPushMatrix();
	}

	public void glGetInteger(int pname, IntBuffer params) {
		GL11.glGetInteger(pname, params);
	}
	public void glColor4f(float r, float g, float b, float a) {
		GL11.glColor4f(r, g, b, a);
	}

	public void glDisable(int name) {
		GL11.glDisable(name);
	}

	public void glEnable(int name) {
		GL11.glEnable(name);
	}

	public void glDrawArrays(int mode, int first, int count) {
		GL11.glDrawArrays(mode, first, count);
	}

	public void glLoadMatrix(FloatBuffer m) {
		GL11.glLoadMatrix(m);
	}

	public void glTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int type,
			ByteBuffer pixels) {
		GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	public void glColor3f(float r, float g, float b) {
		GL11.glColor3f(r, g, b);
	}

	public void glPopMatrix() {
		GL11.glPopMatrix();
	}

	public void glTexCoordPointer(int size, int byteStride, FloatBuffer buf) {
		GL11.glTexCoordPointer(size, byteStride, buf);
	}

	public void glTexImage2D(int target, int level, int internalformat,
			int width, int height, int border, int format, int type,
			ByteBuffer pixels) {
			GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}

	public void glTexParameterf(int target, int pname, float param) {
		GL11.glTexParameterf(target, pname, param);
	}

	public void glVertexPointer(int size, int byteStride, FloatBuffer buf) {
		GL11.glVertexPointer(size, byteStride, buf);
	}

	public void glTexImage2D(int target, int level, int internalformat,
			int width, int height, int border, int format, int type,
			IntBuffer pixels) {
		GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}

	public void glTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int type,
			IntBuffer pixels) {
		GL11.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	@Override
	public void swapBuffers() {
		GL11.glFlush();
		Display.update();
	}

	@Override
	public void glAlphaFunc(int i, float j) {
		GL11.glAlphaFunc(i, j);
	}
//
//	@Override
//	public void glBegin(int mode) {
//		GL11.glBegin(mode);
//	}

	@Override
	public void glBindTexture(int target, int texture) {
		GL11.glBindTexture(target, texture);
	}

	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		GL11.glBlendFunc(sfactor, dfactor);
	}

	@Override
	public void glClear(int bits) {
		GL11.glClear(bits);
	}

	@Override
	public void glClearColor(float r, float g, float b, float a) {
		GL11.glClearColor(r, g, b, a);
	}

	@Override
	public void glColorPointer(int size, int stride, FloatBuffer pointer) {
		GL11.glColorPointer(size, stride, pointer);
	}

	@Override
	public void glColorPointer(int size, boolean b, int stride,
			ByteBuffer pointer) {
		GL11.glColorPointer(size, b, stride, pointer);
	}

	@Override
	public void glCullFace(int face) {
		GL11.glCullFace(face);
	}

	@Override
	public void glDeleteTextures(IntBuffer texnumBuffer) {
		GL11.glDeleteTextures(texnumBuffer);
	}

	@Override
	public void glDepthFunc(int func) {
		GL11.glDepthFunc(func);
	}

	@Override
	public void glDepthMask(boolean b) {
		GL11.glDepthMask(b);
	}

	@Override
	public void glDepthRange(float zNear, float zFar) {
		GL11.glDepthRange(zNear, zFar);
	}

	@Override
	public void glDrawBuffer(int mode) {
		GL11.glDrawBuffer(mode);
	}

	@Override
	public void glDrawElements(int mode, ShortBuffer indices) {
		GL11.glDrawElements(mode, indices);
	}

//	@Override
//	public void glEnd() {
//		GL11.glEnd();
//	}

	@Override
	public void glFinish() {
		GL11.glFinish();
	}

	@Override
	public void glFrustum(double left, double right, double bottom, double top,
			double zNear, double zFar) {
		GL11.glFrustum(left, right, bottom, top, zNear, zFar);
	}

	@Override
	public int glGetError() {
		return GL11.glGetError();
	}

	@Override
	public void glGetFloat(int pname, FloatBuffer params) {
		GL11.glGetFloat(pname, params);
	}

	@Override
	public String glGetString(int name) {
		return GL11.glGetString(name);
	}

	@Override
	public void glLoadIdentity() {
		GL11.glLoadIdentity();
	}

	@Override
	public void glMatrixMode(int mode) {
		GL11.glMatrixMode(mode);
	}

	@Override
	public void glOrtho(int left, int right, int bottom, int top, int zNear, int zFar) {
		GL11.glOrtho(left, right, bottom, top, zNear, zFar);
	}

	@Override
	public void glPixelStorei(int i, int j) {
		GL11.glPixelStorei(i, j);
	}

	@Override
	public void glPointSize(float value) {
		GL11.glPointSize(value);
	}

	@Override
	public void glPolygonMode(int i, int j) {
		GL11.glPolygonMode(i, j);
	}

	@Override
	public void glReadPixels(int x, int y, int width, int height, int format,
			int type, ByteBuffer pixels) {
		GL11.glReadPixels(x, y, width, height, format, type, pixels);
	}

	@Override
	public void glRotatef(float angle, float x, float y, float z) {
		GL11.glRotatef(angle, x, y, z);
	}

	@Override
	public void glScalef(float x, float y, float z) {
		GL11.glScalef(x, y, z);
	}

	@Override
	public void glScissor(int x, int y, int width, int height) {
		GL11.glScissor(x, y, width, height);
	}

	@Override
	public void glShadeModel(int sm) {
		GL11.glShadeModel(sm);
	}

//	@Override
//	public void glTexCoord2f(float x, float y) {
//		GL11.glTexCoord2f(x, y);
//	}

	@Override
	public void glTexEnvi(int target, int pname, int param) {
		GL11.glTexEnvi(target, pname, param);
	}

	@Override
	public void glTexParameteri(int target, int pname, int param) {
		GL11.glTexParameteri(target, pname, param);
	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		GL11.glTranslatef(x, y, z);
	}

//	@Override
//	public void glVertex3f(float x, float y, float z) {
//		GL11.glVertex3f(x, y, z);
//	}

	@Override
	public void glViewport(int x, int y, int w, int h) {
		GL11.glViewport(x, y, w, h);
	}

	@Override
	public FloatBuffer createFloatBuffer(int size) {
		return BufferUtils.createFloatBuffer(size);
	}

	@Override
	public ShortBuffer createShortBuffer(int size) {
		return BufferUtils.createShortBuffer(size);
	}

	@Override
	public DisplayMode getDisplayMode() {
		org.lwjgl.opengl.DisplayMode mode = org.lwjgl.opengl.Display.getDisplayMode();
		return new DisplayMode(mode.getWidth(), mode.getHeight(), mode.getBitsPerPixel(), mode.getFrequency());
	}

	@Override
	public DisplayMode[] getAvailableDisplayModes() {
		try {
			org.lwjgl.opengl.DisplayMode[] modes = org.lwjgl.opengl.Display.getAvailableDisplayModes();
			
			DisplayMode[] result = new DisplayMode[modes.length];

			for(int i = 0; i < result.length; i++) {
				org.lwjgl.opengl.DisplayMode mode = modes[i];
				result[i] = new DisplayMode(mode.getWidth(), mode.getHeight(), mode.getBitsPerPixel(), mode.getFrequency());
			}
			return result;
			
		} catch (LWJGLException e) {
			throw new RuntimeException("" + e);
		}
		
		
		
	}
	
	
	@Override
	public void setDisplayMode(DisplayMode target) {
		try {
			for (org.lwjgl.opengl.DisplayMode mode : org.lwjgl.opengl.Display.getAvailableDisplayModes()) {
				if (mode.getWidth() == target.getWidth() && mode.getHeight() == target.getHeight() && 
						mode.getFrequency() == target.getFrequency() && mode.getBitsPerPixel() == target.getBitsPerPixel()) {
					Display.setDisplayMode(mode);
					Display.setFullscreen(false);
					Display.create();
					return;
				}
			}
			throw new RuntimeException("mode not found: " + target);
		} catch (LWJGLException e) {
			throw new RuntimeException("" + e);
		}
	}

	@Override
	public void shutdow() {
		
		while (Display.isCreated()) {
			Display.destroy();
		} 

	}

	@Override
	public ByteBuffer createByteBuffer(int size) {
		return BufferUtils.createByteBuffer(size);
	}

	@Override
	public IntBuffer createIntBuffer(int size) {
		return BufferUtils.createIntBuffer(size);
	}

	@Override
	public void glActiveTexture(int texture) {
		GL13.glActiveTexture(texture);
	}

	@Override
	public void glClientActiveTexture(int texture) {
		GL13.glClientActiveTexture(texture);
	}

	@Override
	public void glPointParameterf(int id, float value) {
		GL14.glPointParameterf(id, value);
	}

}
