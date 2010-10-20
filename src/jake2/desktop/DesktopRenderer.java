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
import jake2.render.gl.GLRenderer;
import jake2.sys.KBD;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;


public class DesktopRenderer extends GLRenderer implements refexport_t {
 
	  private DesktopKBD kbd = new DesktopKBD();

		public static final String DRIVER_NAME = "lwjgl";



		// ============================================================================
		// Ref interface
		// ============================================================================

		
		public final String getName() {
			return DRIVER_NAME;
		}

		public final String toString() {
			return DRIVER_NAME;
		}

		public final refexport_t GetRefAPI() {
			return this;
		}
		
		public final KBD getKeyboardHandler() { return kbd; }
		
	public DesktopRenderer() {
		this.gl = new LWJGLAdapter();
		init();
	}

	@Override
	protected void GL_ResampleTexture(int[] in, int inwidth, int inheight,
			int[] out, int outwidth, int outheight) {
		//		int		i, j;
		//		unsigned	*inrow, *inrow2;
		//		int frac, fracstep;
		//		int[] p1 = new int[1024];
		//		int[] p2 = new int[1024];
		//		

		// *** this source do the same ***
		BufferedImage image = new BufferedImage(inwidth, inheight, BufferedImage.TYPE_INT_ARGB);

		image.setRGB(0, 0, inwidth, inheight, in, 0, inwidth);

		AffineTransformOp op =
			new AffineTransformOp(
					AffineTransform.getScaleInstance(outwidth * 1.0 / inwidth, outheight * 1.0 / inheight),
					AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		BufferedImage tmp = op.filter(image, null);

		tmp.getRGB(0, 0, outwidth, outheight, out, 0, outwidth);

		// *** end ***

		//		byte		*pix1, *pix2, *pix3, *pix4;
		//
		//		fracstep = inwidth*0x10000/outwidth;
		//
		//		frac = fracstep>>2;
		//		for (i=0 ; i<outwidth ; i++)
		//		{
		//			p1[i] = 4*(frac>>16);
		//			frac += fracstep;
		//		}
		//		frac = 3*(fracstep>>2);
		//		for (i=0 ; i<outwidth ; i++)
		//		{
		//			p2[i] = 4*(frac>>16);
		//			frac += fracstep;
		//		}
		//
		//		for (i=0 ; i<outheight ; i++, out += outwidth)
		//		{
		//			inrow = in + inwidth*(int)((i+0.25)*inheight/outheight);
		//			inrow2 = in + inwidth*(int)((i+0.75)*inheight/outheight);
		//			frac = fracstep >> 1;
		//			for (j=0 ; j<outwidth ; j++)
		//			{
		//				pix1 = (byte *)inrow + p1[j];
		//				pix2 = (byte *)inrow + p2[j];
		//				pix3 = (byte *)inrow2 + p1[j];
		//				pix4 = (byte *)inrow2 + p2[j];
		//				((byte *)(out+j))[0] = (pix1[0] + pix2[0] + pix3[0] + pix4[0])>>2;
		//				((byte *)(out+j))[1] = (pix1[1] + pix2[1] + pix3[1] + pix4[1])>>2;
		//				((byte *)(out+j))[2] = (pix1[2] + pix2[2] + pix3[2] + pix4[2])>>2;
		//				((byte *)(out+j))[3] = (pix1[3] + pix2[3] + pix3[3] + pix4[3])>>2;
		//			}
		//		}
	}	



	@Override
	protected float intBitsToFloat(int i) {
		return Float.intBitsToFloat(i);
	}

	

}
