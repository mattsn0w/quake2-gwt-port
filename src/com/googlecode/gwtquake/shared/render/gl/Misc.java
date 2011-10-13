/*
Copyright (C) 1997-2001 Id Software, Inc.

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
/* Modifications
   Copyright 2003-2004 Bytonic Software
   Copyright 2010 Google Inc.
*/
package com.googlecode.gwtquake.shared.render.gl;



import java.nio.FloatBuffer;

import com.googlecode.gwtquake.shared.client.Window;
import com.googlecode.gwtquake.shared.common.Constants;
import com.googlecode.gwtquake.shared.common.QuakeImage;
import com.googlecode.gwtquake.shared.render.GlAdapter;


/**
 * Misc
 *  
 * @author cwei
 */
public class Misc {

	static void R_InitParticleTexture()
	{
		int		x,y;
		byte[] data = new byte[8 * 8 * 4];

		//
		// particle texture
		//
		for (x=0 ; x<8 ; x++)
		{
			for (y=0 ; y<8 ; y++)
			{
				data[y * 32 + x * 4 + 0] = (byte)255;
				data[y * 32 + x * 4 + 1] = (byte)255;
				data[y * 32 + x * 4 + 2] = (byte)255;
				data[y * 32 + x * 4 + 3] = (byte)(GlConstants.dottexture[x][y]*255);

			}
		}
		GlState.r_particletexture = Images.GL_LoadPic("***particle***", data, 8, 8, QuakeImage.it_sprite, 32);

		//
		// also use this for bad textures, but without alpha
		//
		for (x=0 ; x<8 ; x++)
		{
			for (y=0 ; y<8 ; y++)
			{
				data[y * 32 + x * 4 + 0] = (byte)(GlConstants.dottexture[x&3][y&3]*255);
				data[y * 32 + x * 4 + 1] = 0; // dottexture[x&3][y&3]*255;
				data[y * 32 + x * 4 + 2] = 0; //dottexture[x&3][y&3]*255;
				data[y * 32 + x * 4 + 3] = (byte)255;
			}
		}
		GlState.r_notexture = Images.GL_LoadPic("***r_notexture***", data, 8, 8, QuakeImage.it_wall, 32);
	}

//	/* 
//	============================================================================== 
// 
//							SCREEN SHOTS 
// 
//	============================================================================== 
//	*/ 
//
//	typedef struct _TargaHeader {
//		unsigned char 	id_length, colormap_type, image_type;
//		unsigned short	colormap_index, colormap_length;
//		unsigned char	colormap_size;
//		unsigned short	x_origin, y_origin, width, height;
//		unsigned char	pixel_size, attributes;
//	} TargaHeader;

	/* 
	================== 
	GL_ScreenShot_f
	================== 
	*/  
	static void GL_ScreenShot_f() {
		throw new RuntimeException("ScreenShot currently unsupported");
//	    StringBuffer sb = new StringBuffer(FileSystem.Gamedir() + "/scrshot/jake00.tga");
//	    FileSystem.CreatePath(sb.toString());
//	    File file = new File(sb.toString());
//	    // find a valid file name
//	    int i = 0; int offset = sb.length() - 6;
//	    while (file.exists() && i++ < 100) {
//	        sb.setCharAt(offset, (char) ((i/10) + '0'));
//	        sb.setCharAt(offset + 1, (char) ((i%10) + '0'));
//	        file = new File(sb.toString());
//        }
//	    if (i == 100) {
//		    VID.Printf(Defines.PRINT_ALL, "Clean up your screenshots\n");
//		    return;
//	    }
//	    
//	    try {
//	        RandomAccessFile out = new RandomAccessFile(file, "rw");
//	        FileChannel ch = out.getChannel();
//	        int fileLength = TGA_HEADER_SIZE + vid.width * vid.height * 3;
//	        out.setLength(fileLength);
//	        MappedByteBuffer image = ch.map(FileChannel.MapMode.READ_WRITE, 0,
//	                fileLength);
//	        
//	        // write the TGA header
//	        image.put(0, (byte) 0).put(1, (byte) 0);
//	        image.put(2, (byte) 2); // uncompressed type
//	        image.put(12, (byte) (vid.width & 0xFF)); // vid.width
//	        image.put(13, (byte) (vid.width >> 8)); // vid.width
//	        image.put(14, (byte) (vid.height & 0xFF)); // vid.height
//	        image.put(15, (byte) (vid.height >> 8)); // vid.height
//	        image.put(16, (byte) 24); // pixel size
//	        
//	        // go to image data position
//	        image.position(TGA_HEADER_SIZE);
//	        
//	        
//	        // change pixel alignment for reading
//	        if (vid.width % 4 != 0) {
//	          gl.glPixelStorei(GLAdapter.GL_PACK_ALIGNMENT, 1); 
//	        }
//	        
//	        // OpenGL 1.2+ supports the GL_BGR color format
//	        // check the GL_VERSION to use the TARGA BGR order if possible
//	        // e.g.: 1.5.2 NVIDIA 66.29
////	        if (gl_config.getOpenGLVersion() >= 1.2f) {
////	            // read the BGR values into the image buffer
////	          gl.glReadPixels(0, 0, vid.width, vid.height, GL12.GL_BGR, GLAdapter.GL_UNSIGNED_BYTE, image);
////	        } else {
//	            // read the RGB values into the image buffer
//	            gl.glReadPixels(0, 0, vid.width, vid.height, GLAdapter.GL_RGB, GLAdapter.GL_UNSIGNED_BYTE, image);
//		        // flip RGB to BGR
//		        byte tmp;
//		        for (i = TGA_HEADER_SIZE; i < fileLength; i += 3) {
//		            tmp = image.get(i);
//		            image.put(i, image.get(i + 2));
//		            image.put(i + 2, tmp);
//		        }
////	        }
//	        // reset to default alignment
//	        gl.glPixelStorei(GLAdapter.GL_PACK_ALIGNMENT, 4); 
//	        // close the file channel
//	        ch.close();
//	    } catch (IOException e) {
//	        VID.Printf(Defines.PRINT_ALL, e.getMessage() + '\n');
//	    }
//
//	    VID.Printf(Defines.PRINT_ALL, "Wrote " + file + '\n');
 	} 

	/*
	** GL_Strings_f
	*/
	static void GL_Strings_f()	{
	  /*
		VID.Printf(Defines.PRINT_ALL, "GL_VENDOR: " + gl_config.vendor_string + '\n');
		VID.Printf(Defines.PRINT_ALL, "GL_RENDERER: " + gl_config.renderer_string + '\n');
		VID.Printf(Defines.PRINT_ALL, "GL_VERSION: " + gl_config.version_string + '\n');
		VID.Printf(Defines.PRINT_ALL, "GL_EXTENSIONS: " + gl_config.extensions_string + '\n');
		*/
	}

	/*
	** GL_SetDefaultState
	*/
	static void GL_SetDefaultState()
	{
	  GlState.gl.glClearColor(1f,0f, 0.5f , 0.5f); // original quake2
		//gl.glClearColor(0, 0, 0, 0); // replaced with black
	  GlState.gl.glCullFace(GlAdapter.GL_FRONT);
	  GlState.gl.glEnable(GlAdapter.GL_TEXTURE_2D);

//	  gl.glEnable(GLAdapter.GL_ALPHA_TEST);
	  GlState.gl.glAlphaFunc(GlAdapter.GL_GREATER, 0.666f);

	  GlState.gl.glDisable (GlAdapter.GL_DEPTH_TEST);
	  GlState.gl.glDisable (GlAdapter.GL_CULL_FACE);
	  GlState.gl.glDisable (GlAdapter.GL_BLEND);

	  GlState.gl.glColor4f (1,1,1,1);

	  System.out.println("   gl.glPolygonMode (GLAdapter.GL_FRONT_AND_BACK, GLAdapter.GL_FILL);");
	  GlState.gl.glShadeModel (GlAdapter.GL_FLAT);

		Images.GL_TextureMode( GlState.gl_texturemode.string );
		Images.GL_TextureAlphaMode( GlState.gl_texturealphamode.string );
		Images.GL_TextureSolidMode( GlState.gl_texturesolidmode.string );

		GlState.gl.glTexParameterf(GlAdapter.GL_TEXTURE_2D, GlAdapter.GL_TEXTURE_MIN_FILTER, Images.gl_filter_min);
		GlState.gl.glTexParameterf(GlAdapter.GL_TEXTURE_2D, GlAdapter.GL_TEXTURE_MAG_FILTER, Images.gl_filter_max);

		GlState.gl.glTexParameterf(GlAdapter.GL_TEXTURE_2D, GlAdapter.GL_TEXTURE_WRAP_S, GlAdapter.GL_REPEAT);
		GlState.gl.glTexParameterf(GlAdapter.GL_TEXTURE_2D, GlAdapter.GL_TEXTURE_WRAP_T, GlAdapter.GL_REPEAT);

		GlState.gl.glBlendFunc (GlAdapter.GL_SRC_ALPHA, GlAdapter.GL_ONE_MINUS_SRC_ALPHA);

		Images.GL_TexEnv( GlAdapter.GL_REPLACE );

		if ( GlState.qglPointParameterfEXT )
		{
			// float[] attenuations = { gl_particle_att_a.value, gl_particle_att_b.value, gl_particle_att_c.value };
			FloatBuffer att_buffer=GlState.gl.createFloatBuffer(4);
			att_buffer.put(0,GlState.gl_particle_att_a.value);
			att_buffer.put(1,GlState.gl_particle_att_b.value);
			att_buffer.put(2,GlState.gl_particle_att_c.value);
			
			GlState.gl.glEnable( GlAdapter.GL_POINT_SMOOTH );
			GlState.gl.glPointParameterf(GlAdapter.GL_POINT_SIZE_MIN, GlState.gl_particle_min_size.value );
			GlState.gl.glPointParameterf( GlAdapter.GL_POINT_SIZE_MAX, GlState.gl_particle_max_size.value );
			System.out.println("  gl.glPointParameter( GLAdapter.GL_DISTANCE_ATTENUATION, att_buffer );");
		}

//		if ( qglColorTableEXT && gl_ext_palettedtexture.value != 0.0f )
//		{
//		  gl.glEnable( EXTSharedTexturePalette.GL_SHARED_TEXTURE_PALETTE_EXT );
//
//			GL_SetTexturePalette( d_8to24table );
//		}

		GL_UpdateSwapInterval();
		
		/*
		 * vertex array extension
		 */
		GlState.gl.glEnableClientState(GlAdapter.GL_VERTEX_ARRAY);
		GlState.gl.glClientActiveTexture(GlState.GL_TEXTURE0);
		GlState.gl.glEnableClientState(GlAdapter.GL_TEXTURE_COORD_ARRAY);
	}

	static void GL_UpdateSwapInterval()
	{
		if ( GlState.gl_swapinterval.modified )
		{
			GlState.gl_swapinterval.modified = false;
		}
	}
}
