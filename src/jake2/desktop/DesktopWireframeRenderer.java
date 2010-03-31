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


import jake2.client.refexport_t;
import jake2.render.LineDrawing;
import jake2.render.gl.GLRenderer;
import jake2.render.gl.WireframeRenderer;
import jake2.sys.KBD;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;




public class DesktopWireframeRenderer extends GLRenderer implements refexport_t {
	static final int WIDTH = 800;
	static final int HEIGHT = 600;

	AwtKBD kbd = new AwtKBD();	
	BufferedImage[] buffers = new BufferedImage[2];
	int currentBuffer;
	
	Canvas canvas = new Canvas() {
		@Override
		public void paint(Graphics g) {
			g.drawImage(buffers[1-currentBuffer], 0, 0, null);
		}
	};
	
	
	public DesktopWireframeRenderer() {
		for (int i = 0; i < 2; i++) {
			buffers[i] = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		}

		gl = new WireframeRenderer(new LineDrawing.SwapBuffersCallback() {
			public LineDrawing glSwapBuffers() {
				currentBuffer = 1-currentBuffer;
				canvas.repaint();
				return new DesktopLineDrawing((Graphics2D) buffers[currentBuffer].getGraphics());
			}
		}, WIDTH, HEIGHT);
		
		Frame frame = new Frame("Wireframe");

		canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		canvas.setSize(WIDTH, HEIGHT);
		
		canvas.addKeyListener(kbd);
		frame.add("Center", canvas);
		frame.show();
		frame.pack();
		frame.addWindowListener(new WindowAdapter() {
			
			
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		canvas.requestFocus();
		init();
	}


	public KBD getKeyboardHandler() {
		// TODO Auto-generated method stub
		return kbd;
	}


	@Override
	protected void GL_ResampleTexture(int[] in, int inwidth, int inheight,
			int[] out, int outwidth, int outheight) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	protected float intBitsToFloat(int i) {
		return Float.intBitsToFloat(i);
	}

}
