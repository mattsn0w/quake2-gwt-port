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



import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import com.googlecode.gwtquake.shared.client.DynamicLightData;
import com.googlecode.gwtquake.shared.client.EntityType;
import com.googlecode.gwtquake.shared.client.Lightstyle;
import com.googlecode.gwtquake.shared.common.Com;
import com.googlecode.gwtquake.shared.common.Constants;
import com.googlecode.gwtquake.shared.game.Plane;
import com.googlecode.gwtquake.shared.render.GlAdapter;
import com.googlecode.gwtquake.shared.render.GlPolygon;
import com.googlecode.gwtquake.shared.render.ModelEdge;
import com.googlecode.gwtquake.shared.render.ModelImage;
import com.googlecode.gwtquake.shared.render.ModelLeaf;
import com.googlecode.gwtquake.shared.render.ModelNode;
import com.googlecode.gwtquake.shared.render.ModelSurface;
import com.googlecode.gwtquake.shared.render.ModelTextureInfo;
import com.googlecode.gwtquake.shared.render.RendererModel;
import com.googlecode.gwtquake.shared.util.Lib;
import com.googlecode.gwtquake.shared.util.Math3D;


/**
 * Surf
 *  
 * @author cwei
 */
public abstract class Surfaces  {

	// GL_RSURF.C: surface-related refresh code
	static float[] modelorg = {0, 0, 0};		// relative to viewpoint

	static ModelSurface	r_alpha_surfaces;

	static final int DYNAMIC_LIGHT_WIDTH = 128;
	static final int DYNAMIC_LIGHT_HEIGHT = 128;

	static final int LIGHTMAP_BYTES = 4;

	static final int BLOCK_WIDTH = 128;
	static final int BLOCK_HEIGHT = 128;

	static final int MAX_LIGHTMAPS = 128;

	static int c_visible_lightmaps;
	static int c_visible_textures;

    static int staticBufferId;
	
	static final int GL_LIGHTMAP_FORMAT = GlAdapter.GL_RGBA;

	static class gllightmapstate_t 
	{
		int internal_format;
		int current_lightmap_texture;

		ModelSurface[] lightmap_surfaces = new ModelSurface[MAX_LIGHTMAPS];
		int[] allocated = new int[BLOCK_WIDTH];

		// the lightmap texture data needs to be kept in
		// main memory so texsubimage can update properly
		//byte[] lightmap_buffer = new byte[4 * BLOCK_WIDTH * BLOCK_HEIGHT];
		IntBuffer lightmap_buffer = Lib.newIntBuffer(BLOCK_WIDTH * BLOCK_HEIGHT, ByteOrder.LITTLE_ENDIAN);
				
		public gllightmapstate_t() {
			for (int i = 0; i < MAX_LIGHTMAPS; i++)
				lightmap_surfaces[i] = new ModelSurface();
		}
		
		public void clearLightmapSurfaces() {
			for (int i = 0; i < MAX_LIGHTMAPS; i++)
				// TODO lightmap_surfaces[i].clear();
				lightmap_surfaces[i] = new ModelSurface();
		}
		
	} 

	static gllightmapstate_t gl_lms = new gllightmapstate_t();

		
	/*
	=============================================================

		BRUSH MODELS

	=============================================================
	*/

	/**
	 * R_TextureAnimation
	 * Returns the proper texture for a given time and base texture
	 */
	static ModelImage R_TextureAnimation(ModelTextureInfo tex)
	{
		if (tex.next == null)
			return tex.image;

		int c = GlState.currententity.frame % tex.numframes;
		while (c != 0)
		{
			tex = tex.next;
			c--;
		}

		return tex.image;
	}

	/**
	 * DrawGLPoly
	 */
	static void DrawGLPoly(GlPolygon p)
	{
	  GlState.gl.glDrawArrays(GlAdapter._GL_POLYGON, p.pos, p.numverts);
	}

	/**
	 * DrawGLFlowingPoly
	 * version that handles scrolling texture
	 */
	static void DrawGLFlowingPoly(GlPolygon p)
	{
		float scroll = -64 * ( (GlState.r_newrefdef.time / 40.0f) - (int)(GlState.r_newrefdef.time / 40.0f) );
		if(scroll == 0.0f)
			scroll = -64.0f;
		p.beginScrolling(scroll);
		GlState.gl.glDrawArrays(GlAdapter._GL_POLYGON, p.pos, p.numverts);
		p.endScrolling();
	}

	/**
	 * R_DrawTriangleOutlines
	*/
	static void R_DrawTriangleOutlines()
	{
        if (GlState.gl_showtris.value == 0)
            return;

        GlState.gl.glDisable(GlAdapter.GL_TEXTURE_2D);
        GlState.gl.glDisable(GlAdapter.GL_DEPTH_TEST);
        GlState.gl.glColor4f(1, 1, 1, 1);

        ModelSurface surf;
        GlPolygon p;
        int j;	
        for (int i = 0; i < MAX_LIGHTMAPS; i++) {
             for (surf = gl_lms.lightmap_surfaces[i]; surf != null; surf = surf.lightmapchain) {
                for (p = surf.polys; p != null; p = p.chain) {
                    for (j = 2; j < p.numverts; j++) {
                      GlState.gl.glBegin(GlAdapter.GL_LINE_STRIP);
                      GlState.gl.glVertex3f(p.x(0), p.y(0), p.z(0));
                      GlState.gl.glVertex3f(p.x(j-1), p.y(j-1), p.z(j-1));
                      GlState.gl.glVertex3f(p.x(j), p.y(j), p.z(j));
                      GlState.gl.glVertex3f(p.x(0), p.y(0), p.z(0));
                      GlState.gl.glEnd();
                    }
                }
            }
        }

        GlState.gl.glEnable(GlAdapter.GL_DEPTH_TEST);
        GlState.gl.glEnable(GlAdapter.GL_TEXTURE_2D);
	}

	private static final IntBuffer temp2 = Lib.newIntBuffer(34 * 34, ByteOrder.LITTLE_ENDIAN);

	/**
	 * R_RenderBrushPoly
	 */
	static void R_RenderBrushPoly(ModelSurface fa)
	{
		GlState.c_brush_polys++;

		ModelImage image = R_TextureAnimation(fa.texinfo);

		if ((fa.flags & Constants.SURF_DRAWTURB) != 0)
		{	
			Images.GL_Bind( image.texnum );

			// warp texture, no lightmaps
			Images.GL_TexEnv( GlAdapter.GL_MODULATE );
			GlState.gl.glColor4f( GlState.gl_state.inverse_intensity, 
						GlState.gl_state.inverse_intensity,
						GlState.gl_state.inverse_intensity,
						1.0F );
			Warp.EmitWaterPolys (fa);
			Images.GL_TexEnv( GlAdapter.GL_REPLACE );

			return;
		}
		else
		{
		  Images.GL_Bind( image.texnum );
		  Images.GL_TexEnv( GlAdapter.GL_REPLACE );
		}

		//	  ======
		//	  PGM
		if((fa.texinfo.flags & Constants.SURF_FLOWING) != 0)
			DrawGLFlowingPoly(fa.polys);
		else
			DrawGLPoly (fa.polys);
		//	  PGM
		//	  ======

		// ersetzt goto
		boolean gotoDynamic = false;
		/*
		** check for lightmap modification
		*/
		int maps;
		for ( maps = 0; maps < Constants.MAXLIGHTMAPS && fa.styles[maps] != (byte)255; maps++ )
		{
			if ( GlState.r_newrefdef.lightstyles[fa.styles[maps] & 0xFF].white != fa.cached_light[maps] ) {
				gotoDynamic = true;
				break;
			}
		}
		
		// this is a hack from cwei
		if (maps == 4) maps--;

		// dynamic this frame or dynamic previously
		boolean is_dynamic = false;
		if ( gotoDynamic || ( fa.dlightframe == GlState.r_framecount ) )
		{
			//	label dynamic:
			if ( GlState.gl_dynamic.value != 0 )
			{
				if (( fa.texinfo.flags & (Constants.SURF_SKY | Constants.SURF_TRANS33 | Constants.SURF_TRANS66 | Constants.SURF_WARP ) ) == 0)
				{
					is_dynamic = true;
				}
			}
		}

		if ( is_dynamic )
		{
			if ( ( (fa.styles[maps] & 0xFF) >= 32 || fa.styles[maps] == 0 ) && ( fa.dlightframe != GlState.r_framecount ) )
			{
				// ist ersetzt durch temp2:	unsigned	temp[34*34];
				int smax, tmax;

				smax = (fa.extents[0]>>4)+1;
				tmax = (fa.extents[1]>>4)+1;

				Light.R_BuildLightMap( fa, temp2, smax);
				Light.R_SetCacheState( fa );

				Images.GL_Bind( GlState.gl_state.lightmap_textures + fa.lightmaptexturenum );

				GlState.gl.glTexSubImage2D( GlAdapter.GL_TEXTURE_2D, 0,
								  fa.light_s, fa.light_t, 
								  smax, tmax, 
								  GL_LIGHTMAP_FORMAT, 
								  GlAdapter.GL_UNSIGNED_BYTE, temp2 );

				fa.lightmapchain = gl_lms.lightmap_surfaces[fa.lightmaptexturenum];
				gl_lms.lightmap_surfaces[fa.lightmaptexturenum] = fa;
			}
			else
			{
				fa.lightmapchain = gl_lms.lightmap_surfaces[0];
				gl_lms.lightmap_surfaces[0] = fa;
			}
		}
		else
		{
			fa.lightmapchain = gl_lms.lightmap_surfaces[fa.lightmaptexturenum];
			gl_lms.lightmap_surfaces[fa.lightmaptexturenum] = fa;
		}
	}


	/**
	 * R_DrawAlphaSurfaces
	 * Draw water surfaces and windows.
	 * The BSP tree is waled front to back, so unwinding the chain
	 * of alpha_surfaces will draw back to front, giving proper ordering.
	 */
	static void R_DrawAlphaSurfaces()
	{
		GlState.r_world_matrix.clear();
		//
		// go back to the world matrix
		//
		GlState.gl.glLoadMatrix(GlState.r_world_matrix);

		GlState.gl.glEnable (GlAdapter.GL_BLEND);
		Images.GL_TexEnv(GlAdapter.GL_MODULATE );
		

		// the textures are prescaled up for a better lighting range,
		// so scale it back down
		float intens = GlState.gl_state.inverse_intensity;

		glInterleavedArraysT2F_V3F(GlPolygon.BYTE_STRIDE, globalPolygonInterleavedBuf, staticBufferId);

		for (ModelSurface s = r_alpha_surfaces ; s != null ; s=s.texturechain)
		{
		  Images.GL_Bind(s.texinfo.image.texnum);
			GlState.c_brush_polys++;
			if ((s.texinfo.flags & Constants.SURF_TRANS33) != 0)
			  GlState.gl.glColor4f (intens, intens, intens, 0.33f);
			else if ((s.texinfo.flags & Constants.SURF_TRANS66) != 0)
			  GlState.gl.glColor4f (intens, intens, intens, 0.66f);
			else
			  GlState.gl.glColor4f (intens,intens,intens,1);
			if ((s.flags & Constants.SURF_DRAWTURB) != 0)
				Warp.EmitWaterPolys(s);
			else if((s.texinfo.flags & Constants.SURF_FLOWING) != 0)			// PGM	9/16/98
				DrawGLFlowingPoly(s.polys);							// PGM
			else
				DrawGLPoly(s.polys);
		}

		Images.GL_TexEnv( GlAdapter.GL_REPLACE );
		GlState.gl.glColor4f (1,1,1,1);
		GlState.gl.glDisable (GlAdapter.GL_BLEND);

		r_alpha_surfaces = null;
	}

	private static void glInterleavedArraysT2F_V3F(int byteStride, FloatBuffer buf) {
	  int pos = buf.position();
	  GlState.gl.glTexCoordPointer(2, byteStride, buf);
	  GlState.gl.glEnableClientState(GlAdapter.GL_TEXTURE_COORD_ARRAY);
	        
	  buf.position(pos + 2);
	  GlState.gl.glVertexPointer(3, byteStride, buf);
	  GlState.gl.glEnableClientState(GlAdapter.GL_VERTEX_ARRAY);
	        
	  buf.position(pos);
	}

	private static void glInterleavedArraysT2F_V3F(int byteStride, FloatBuffer buf, int staticDrawIdV) {
		GlState.gl.glEnableClientState(GlAdapter.GL_TEXTURE_COORD_ARRAY);
		GlState.gl.glVertexAttribPointer(GlAdapter.ARRAY_TEXCOORD_0, 2, GlAdapter.GL_FLOAT, 
		    false, byteStride, 0, buf, staticDrawIdV);
		
		GlState.gl.glEnableClientState(GlAdapter.GL_VERTEX_ARRAY);
//		 gl.glVertexPointer(3, byteStride, buf);
		GlState.gl.glVertexAttribPointer(GlAdapter.ARRAY_POSITION, 3, GlAdapter.GL_FLOAT,
		    false, byteStride, 8, buf, staticDrawIdV);
	}
	/**
	 * DrawTextureChains
	 */
	static void DrawTextureChains()
	{
		c_visible_textures = 0;

		ModelSurface	s;
		ModelImage image;
		int i;
		for (i = 0; i < Images.numgltextures ; i++)
		{
			image = Images.gltextures[i];

			if (image.registration_sequence == 0)
				continue;
			if (image.texturechain == null)
				continue;
			c_visible_textures++;

			for ( s = image.texturechain; s != null ; s=s.texturechain)
			{
				if ( ( s.flags & Constants.SURF_DRAWTURB) == 0 )
					R_RenderBrushPoly(s);
			}
		}

		Images.GL_EnableMultitexture( false );
		for (i = 0; i < Images.numgltextures ; i++)
		{
			image = Images.gltextures[i];

			if (image.registration_sequence == 0)
				continue;
			s = image.texturechain;
			if (s == null)
				continue;

			for ( ; s != null ; s=s.texturechain)
			{
				if ( (s.flags & Constants.SURF_DRAWTURB) != 0 )
					R_RenderBrushPoly(s);
			}

			image.texturechain = null;
		}

		Images.GL_TexEnv( GlAdapter.GL_REPLACE );
	}

	// direct buffer
	private static final IntBuffer temp = Lib.newIntBuffer(128 * 128, ByteOrder.LITTLE_ENDIAN);
	
	/**
	 * GL_RenderLightmappedPoly
	 * @param surf
	 */
	static void GL_RenderLightmappedPoly( ModelSurface surf )
	{

		// ersetzt goto
		boolean gotoDynamic = false;
		int map;
		for ( map = 0; map < Constants.MAXLIGHTMAPS && (surf.styles[map] != (byte)255); map++ )
		{
			if ( GlState.r_newrefdef.lightstyles[surf.styles[map] & 0xFF].white != surf.cached_light[map] ) {
				gotoDynamic = true;
				break;
			}
		}

		// this is a hack from cwei
		if (map == 4) map--;

		// dynamic this frame or dynamic previously
		boolean is_dynamic = false;
		if ( gotoDynamic || ( surf.dlightframe == GlState.r_framecount ) )
		{
			//	label dynamic:
			if ( GlState.gl_dynamic.value != 0 )
			{
				if ( (surf.texinfo.flags & (Constants.SURF_SKY | Constants.SURF_TRANS33 | Constants.SURF_TRANS66 | Constants.SURF_WARP )) == 0 )
				{
					is_dynamic = true;
				}
			}
		}

		GlPolygon p;
		ModelImage image = R_TextureAnimation( surf.texinfo );
		int lmtex = surf.lightmaptexturenum;

		if ( is_dynamic )
		{
			// ist raus gezogen worden int[] temp = new int[128*128];
			int smax = (surf.extents[0]>>4)+1;
			int tmax = (surf.extents[1]>>4)+1;

			Light.R_BuildLightMap( surf, temp, smax);
			if (( (surf.styles[map] & 0xFF) >= 32 || surf.styles[map] == 0 ) && ( surf.dlightframe != GlState.r_framecount ) )
			{
				Light.R_SetCacheState( surf );
				lmtex = surf.lightmaptexturenum;
			}
			else
			{
				lmtex = 0;
			}
			Images.GL_MBind( GlState.GL_TEXTURE1, GlState.gl_state.lightmap_textures + lmtex );
			GlState.gl.glTexSubImage2D( GlAdapter.GL_TEXTURE_2D, 0,
					  surf.light_s, surf.light_t, 
					  smax, tmax, 
					  GL_LIGHTMAP_FORMAT, 
					  GlAdapter.GL_UNSIGNED_BYTE, temp );

		}
			GlState.c_brush_polys++;

			Images.GL_MBind( GlState.GL_TEXTURE0, image.texnum );
			Images.GL_MBind( GlState.GL_TEXTURE1, GlState.gl_state.lightmap_textures + lmtex );

			// ==========
			//	  PGM
			if ((surf.texinfo.flags & Constants.SURF_FLOWING) != 0)
			{
				float scroll;
		
				scroll = -64 * ( (GlState.r_newrefdef.time / 40.0f) - (int)(GlState.r_newrefdef.time / 40.0f) );
				if(scroll == 0.0f)
					scroll = -64.0f;

				for ( p = surf.polys; p != null; p = p.chain )
				{
				    p.beginScrolling(scroll);
				    GlState.gl.glDrawArrays(GlAdapter._GL_POLYGON, p.pos, p.numverts);
				    p.endScrolling();
				}
			}
			else
			{
				for ( p = surf.polys; p != null; p = p.chain )
				{
				  GlState.gl.glDrawArrays(GlAdapter._GL_POLYGON, p.pos, p.numverts);
				}
			}
			// PGM
			// ==========
//		}
//		else
//		{
//			c_brush_polys++;
//
//			GL_MBind( GL_TEXTURE0, image.texnum );
//			GL_MBind( GL_TEXTURE1, gl_state.lightmap_textures + lmtex);
//			
//			// ==========
//			//	  PGM
//			if ((surf.texinfo.flags & Defines.SURF_FLOWING) != 0)
//			{
//				float scroll;
//		
//				scroll = -64 * ( (r_newrefdef.time / 40.0f) - (int)(r_newrefdef.time / 40.0f) );
//				if(scroll == 0.0)
//					scroll = -64.0f;
//
//				for ( p = surf.polys; p != null; p = p.chain )
//				{
//				    p.beginScrolling(scroll);
//				    gl.glDrawArrays(GLAdapter._GL_POLYGON, p.pos, p.numverts);
//				    p.endScrolling();
//				}
//			}
//			else
//			{
//			// PGM
//			//  ==========
//				for ( p = surf.polys; p != null; p = p.chain )
//				{
//				  gl.glDrawArrays(GLAdapter._GL_POLYGON, p.pos, p.numverts);
//				}
//				
//			// ==========
//			// PGM
//			}
//			// PGM
//			// ==========
//		}
	}

	/**
	 * R_DrawInlineBModel
	 */
	static void R_DrawInlineBModel()
	{
		// calculate dynamic lighting for bmodel
		if ( GlState.gl_flashblend.value == 0 )
		{
			DynamicLightData	lt;
			for (int k=0 ; k<GlState.r_newrefdef.num_dlights ; k++)
			{
				lt = GlState.r_newrefdef.dlights[k];
				Light.R_MarkLights(lt, 1<<k, GlState.currentmodel.nodes[GlState.currentmodel.firstnode]);
			}
		}

		// psurf = &currentmodel->surfaces[currentmodel->firstmodelsurface];
		int psurfp = GlState.currentmodel.firstmodelsurface;
		ModelSurface[] surfaces = GlState.currentmodel.surfaces;
		//psurf = surfaces[psurfp];

		if ( (GlState.currententity.flags & Constants.RF_TRANSLUCENT) != 0 )
		{
		  GlState.gl.glEnable (GlAdapter.GL_BLEND);
		  GlState.gl.glColor4f (1,1,1,0.25f);
			Images.GL_TexEnv( GlAdapter.GL_MODULATE );
		}

		//
		// draw texture
		//
		ModelSurface psurf;
		Plane pplane;
		float dot;
		for (int i=0 ; i<GlState.currentmodel.nummodelsurfaces ; i++)
		{
			psurf = surfaces[psurfp++];
			// find which side of the node we are on
			pplane = psurf.plane;

			dot = Math3D.DotProduct(modelorg, pplane.normal) - pplane.dist;

			// draw the polygon
			if (((psurf.flags & Constants.SURF_PLANEBACK) != 0 && (dot < -GlConstants.BACKFACE_EPSILON)) ||
				((psurf.flags & Constants.SURF_PLANEBACK) == 0 && (dot > GlConstants.BACKFACE_EPSILON)))
			{
				if ((psurf.texinfo.flags & (Constants.SURF_TRANS33 | Constants.SURF_TRANS66)) != 0 )
				{	// add to the translucent chain
					psurf.texturechain = r_alpha_surfaces;
					r_alpha_surfaces = psurf;
				}
				else if ( (psurf.flags & Constants.SURF_DRAWTURB) == 0 )
				{
					GL_RenderLightmappedPoly( psurf );
				}
				else
				{
					Images.GL_EnableMultitexture( false );
					R_RenderBrushPoly( psurf );
					Images.GL_EnableMultitexture( true );
				}
			}
		}
		
		if ( (GlState.currententity.flags & Constants.RF_TRANSLUCENT) != 0 ) {
		  GlState.gl.glDisable (GlAdapter.GL_BLEND);
		  GlState.gl.glColor4f (1,1,1,1);
			Images.GL_TexEnv( GlAdapter.GL_REPLACE );
		}
	}

	// stack variable
	private static final float[] mins = {0, 0, 0};
	private static final float[] maxs = {0, 0, 0};
	private static final float[] org = {0, 0, 0};
	private static final float[] forward = {0, 0, 0};
	private static final float[] right = {0, 0, 0};
	private static final float[] up = {0, 0, 0};
	/**
	 * R_DrawBrushModel
	 */
	static void R_DrawBrushModel(EntityType e)
	{
		if (GlState.currentmodel.nummodelsurfaces == 0)
			return;

		GlState.currententity = e;
		GlState.gl_state.currenttextures[0] = GlState.gl_state.currenttextures[1] = -1;

		boolean rotated;
		if (e.angles[0] != 0 || e.angles[1] != 0 || e.angles[2] != 0)
		{
			rotated = true;
			for (int i=0 ; i<3 ; i++)
			{
				mins[i] = e.origin[i] - GlState.currentmodel.radius;
				maxs[i] = e.origin[i] + GlState.currentmodel.radius;
			}
		}
		else
		{
			rotated = false;
			Math3D.VectorAdd(e.origin, GlState.currentmodel.mins, mins);
			Math3D.VectorAdd(e.origin, GlState.currentmodel.maxs, maxs);
		}

		if (Main.R_CullBox(mins, maxs)) return;

		GlState.gl.glColor3f (1,1,1);
		
		// memset (gl_lms.lightmap_surfaces, 0, sizeof(gl_lms.lightmap_surfaces));
		
		// TODO wird beim multitexturing nicht gebraucht
		//gl_lms.clearLightmapSurfaces();
		
		Math3D.VectorSubtract (GlState.r_newrefdef.vieworg, e.origin, modelorg);
		if (rotated)
		{
			Math3D.VectorCopy (modelorg, org);
			Math3D.AngleVectors (e.angles, forward, right, up);
			modelorg[0] = Math3D.DotProduct (org, forward);
			modelorg[1] = -Math3D.DotProduct (org, right);
			modelorg[2] = Math3D.DotProduct (org, up);
		}

		GlState.gl.glPushMatrix();
		
		e.angles[0] = -e.angles[0];	// stupid quake bug
		e.angles[2] = -e.angles[2];	// stupid quake bug
		Main.R_RotateForEntity(e);
		e.angles[0] = -e.angles[0];	// stupid quake bug
		e.angles[2] = -e.angles[2];	// stupid quake bug

		Images.GL_EnableMultitexture( true );
		Images.GL_SelectTexture(GlState.GL_TEXTURE0);
		Images.GL_TexEnv( GlAdapter.GL_REPLACE );
		glInterleavedArraysT2F_V3F(GlPolygon.BYTE_STRIDE, globalPolygonInterleavedBuf, staticBufferId);
		Images.GL_SelectTexture(GlState.GL_TEXTURE1);
		Images.GL_TexEnv( GlAdapter.GL_MODULATE );
//		gl.glTexCoordPointer(2, Polygon.BYTE_STRIDE, globalPolygonTexCoord1Buf);
		GlState.gl.glEnableClientState(GlAdapter.GL_TEXTURE_COORD_ARRAY);
		GlState.gl.glVertexAttribPointer(GlAdapter.ARRAY_TEXCOORD_1, 2, GlAdapter.GL_FLOAT, false, 
				GlPolygon.BYTE_STRIDE, 20, globalPolygonInterleavedBuf, staticBufferId);

		R_DrawInlineBModel();

		GlState.gl.glClientActiveTexture(GlState.GL_TEXTURE1);
		GlState.gl.glDisableClientState(GlAdapter.GL_TEXTURE_COORD_ARRAY);

		Images.GL_EnableMultitexture( false );

		GlState.gl.glPopMatrix();
	}

	/*
	=============================================================

		WORLD MODEL

	=============================================================
	*/

	/**
	 * R_RecursiveWorldNode
	 */
	static void R_RecursiveWorldNode (ModelNode node)
	{
		if (node.contents == Constants.CONTENTS_SOLID)
			return;		// solid
		
		if (node.visframe != GlState.r_visframecount)
			return;
			
		if (Main.R_CullBox(node.mins, node.maxs))
			return;
	
		int c;
		ModelSurface mark;
		// if a leaf node, draw stuff
		if (node.contents != -1)
		{
			ModelLeaf pleaf = (ModelLeaf)node;

			// check for door connected areas
			if (GlState.r_newrefdef.areabits != null)
			{
				if ( ((GlState.r_newrefdef.areabits[pleaf.area >> 3] & 0xFF) & (1 << (pleaf.area & 7)) ) == 0 )
					return;		// not visible
			}

			int markp = 0;

			mark = pleaf.getMarkSurface(markp); // first marked surface
			c = pleaf.nummarksurfaces;

			if (c != 0)
			{
				do
				{
					mark.visframe = GlState.r_framecount;
					mark = pleaf.getMarkSurface(++markp); // next surface
				} while (--c != 0);
			}

			return;
		}

		// node is just a decision point, so go down the apropriate sides

		// find which side of the node we are on
		Plane plane = node.plane;
		float dot;
		switch (plane.type)
		{
		case Constants.PLANE_X:
			dot = modelorg[0] - plane.dist;
			break;
		case Constants.PLANE_Y:
			dot = modelorg[1] - plane.dist;
			break;
		case Constants.PLANE_Z:
			dot = modelorg[2] - plane.dist;
			break;
		default:
			dot = Math3D.DotProduct(modelorg, plane.normal) - plane.dist;
			break;
		}

		int side, sidebit;
		if (dot >= 0.0f)
		{
			side = 0;
			sidebit = 0;
		}
		else
		{
			side = 1;
			sidebit = Constants.SURF_PLANEBACK;
		}

		// recurse down the children, front side first
		R_RecursiveWorldNode(node.children[side]);

		// draw stuff
		ModelSurface surf;
		ModelImage image;
		//for ( c = node.numsurfaces, surf = r_worldmodel.surfaces[node.firstsurface]; c != 0 ; c--, surf++)
		for ( c = 0; c < node.numsurfaces; c++)
		{
			surf = GlState.r_worldmodel.surfaces[node.firstsurface + c];
			if (surf.visframe != GlState.r_framecount)
				continue;

			if ( (surf.flags & Constants.SURF_PLANEBACK) != sidebit )
				continue;		// wrong side

			if ((surf.texinfo.flags & Constants.SURF_SKY) != 0)
			{	// just adds to visible sky bounds
				Warp.R_AddSkySurface(surf);
			}
			else if ((surf.texinfo.flags & (Constants.SURF_TRANS33 | Constants.SURF_TRANS66)) != 0)
			{	// add to the translucent chain
				surf.texturechain = r_alpha_surfaces;
				r_alpha_surfaces = surf;
			}
			else
			{
				if (  ( surf.flags & Constants.SURF_DRAWTURB) == 0 )
				{
					Surfaces.GL_RenderLightmappedPoly( surf );
				}
				else
				{
					// the polygon is visible, so add it to the texture
					// sorted chain
					// FIXME: this is a hack for animation
					image = R_TextureAnimation(surf.texinfo);
					surf.texturechain = image.texturechain;
					image.texturechain = surf;
				}
			}
		}
		// recurse down the back side
		R_RecursiveWorldNode(node.children[1 - side]);
	}

	private static final EntityType worldEntity = new EntityType();
	
	/**
	 * R_DrawWorld
	 */
	static void R_DrawWorld()
	{
		if (GlState.r_drawworld.value == 0)
			return;

		if ( (GlState.r_newrefdef.rdflags & Constants.RDF_NOWORLDMODEL) != 0 )
			return;

		GlState.currentmodel = GlState.r_worldmodel;

		Math3D.VectorCopy(GlState.r_newrefdef.vieworg, modelorg);

		EntityType ent = worldEntity;
		// auto cycle the world frame for texture animation
		ent.clear();
		ent.frame = (int)(GlState.r_newrefdef.time*2);
		GlState.currententity = ent;

		GlState.gl_state.currenttextures[0] = GlState.gl_state.currenttextures[1] = -1;

		GlState.gl.glColor3f (1,1,1);
		// memset (gl_lms.lightmap_surfaces, 0, sizeof(gl_lms.lightmap_surfaces));
		// TODO wird bei multitexture nicht gebraucht
		//gl_lms.clearLightmapSurfaces();
		
		Warp.R_ClearSkyBox();

		Images.GL_EnableMultitexture( true );

		Images.GL_SelectTexture( GlState.GL_TEXTURE0);
		Images.GL_TexEnv( GlAdapter.GL_REPLACE );
		
		
//		glInterleavedArraysT2F_V3F(Polygon.BYTE_STRIDE, globalPolygonInterleavedBuf);
        glInterleavedArraysT2F_V3F(GlPolygon.BYTE_STRIDE, globalPolygonInterleavedBuf, staticBufferId);
        
		Images.GL_SelectTexture( GlState.GL_TEXTURE1);
		GlState.gl.glEnableClientState(GlAdapter.GL_TEXTURE_COORD_ARRAY);
		GlState.gl.glVertexAttribPointer(GlAdapter.ARRAY_TEXCOORD_1, 2, GlAdapter.GL_FLOAT, 
		    false, GlPolygon.BYTE_STRIDE, 20, globalPolygonInterleavedBuf, staticBufferId);
//		gl.glTexCoordPointer(2, Polygon.BYTE_STRIDE, globalPolygonTexCoord1Buf);

		if ( GlState.gl_lightmap.value != 0)
			Images.GL_TexEnv( GlAdapter.GL_REPLACE );
		else 
			Images.GL_TexEnv( GlAdapter.GL_MODULATE );
				
		R_RecursiveWorldNode(GlState.r_worldmodel.nodes[0]); // root node

		GlState.gl.glClientActiveTexture(GlState.GL_TEXTURE1);
		GlState.gl.glDisableClientState(GlAdapter.GL_TEXTURE_COORD_ARRAY);

		Images.GL_EnableMultitexture( false );

		DrawTextureChains();
		Warp.R_DrawSkyBox();
		R_DrawTriangleOutlines();
	}

	final static byte[] fatvis = new byte[Constants.MAX_MAP_LEAFS / 8];

	/**
	 * R_MarkLeaves
	 * Mark the leaves and nodes that are in the PVS for the current
	 * cluster
	 */
	static void R_MarkLeaves()
	{
		if (GlState.r_oldviewcluster == GlState.r_viewcluster && GlState.r_oldviewcluster2 == GlState.r_viewcluster2 && GlState.r_novis.value == 0 && GlState.r_viewcluster != -1)
			return;

		// development aid to let you run around and see exactly where
		// the pvs ends
		if (GlState.gl_lockpvs.value != 0)
			return;

		GlState.r_visframecount++;
		GlState.r_oldviewcluster = GlState.r_viewcluster;
		GlState.r_oldviewcluster2 = GlState.r_viewcluster2;

		int i;
		if (GlState.r_novis.value != 0 || GlState.r_viewcluster == -1 || GlState.r_worldmodel.vis == null)
		{
			// mark everything
			for (i=0 ; i<GlState.r_worldmodel.numleafs ; i++)
				GlState.r_worldmodel.leafs[i].visframe = GlState.r_visframecount;
			for (i=0 ; i<GlState.r_worldmodel.numnodes ; i++)
				GlState.r_worldmodel.nodes[i].visframe = GlState.r_visframecount;
			return;
		}

		byte[] vis = Models.Mod_ClusterPVS(GlState.r_viewcluster, GlState.r_worldmodel);
		int c;
		// may have to combine two clusters because of solid water boundaries
		if (GlState.r_viewcluster2 != GlState.r_viewcluster)
		{
			// memcpy (fatvis, vis, (r_worldmodel.numleafs+7)/8);
			System.arraycopy(vis, 0, fatvis, 0, (GlState.r_worldmodel.numleafs+7) >> 3);
			vis = Models.Mod_ClusterPVS(GlState.r_viewcluster2, GlState.r_worldmodel);
			c = (GlState.r_worldmodel.numleafs + 31) >> 5;
			c <<= 2;
			for (int k=0 ; k<c ; k+=4) {
				fatvis[k] |= vis[k];
				fatvis[k + 1] |= vis[k + 1];
				fatvis[k + 2] |= vis[k + 2];
				fatvis[k + 3] |= vis[k + 3];
			}

			vis = fatvis;
		}

		ModelNode node;
		ModelLeaf leaf;
		int cluster;
		for ( i=0; i < GlState.r_worldmodel.numleafs; i++)
		{
			leaf = GlState.r_worldmodel.leafs[i];
			cluster = leaf.cluster;
			if (cluster == -1)
				continue;
			if (((vis[cluster>>3] & 0xFF) & (1 << (cluster & 7))) != 0)
			{
				node = (ModelNode)leaf;
				do
				{
					if (node.visframe == GlState.r_visframecount)
						break;
					node.visframe = GlState.r_visframecount;
					node = node.parent;
				} while (node != null);
			}
		}
	}

	/*
	=============================================================================

	  LIGHTMAP ALLOCATION

	=============================================================================
	*/

	/**
	 * LM_InitBlock
	 */
	static void LM_InitBlock()
	{
		Arrays.fill(gl_lms.allocated, 0);
	}

	/**
	 * LM_UploadBlock
	 * @param dynamic
	 */
	static void LM_UploadBlock( boolean dynamic )
	{
		int texture = ( dynamic ) ? 0 : gl_lms.current_lightmap_texture;

		Images.GL_Bind( GlState.gl_state.lightmap_textures + texture );
		GlState.gl.glTexParameterf(GlAdapter.GL_TEXTURE_2D, GlAdapter.GL_TEXTURE_MIN_FILTER, GlAdapter.GL_LINEAR);
		GlState.gl.glTexParameterf(GlAdapter.GL_TEXTURE_2D, GlAdapter.GL_TEXTURE_MAG_FILTER, GlAdapter.GL_LINEAR);

		gl_lms.lightmap_buffer.rewind();
		if ( dynamic )
		{
			int height = 0;
			for (int i = 0; i < BLOCK_WIDTH; i++ )
			{
				if ( gl_lms.allocated[i] > height )
					height = gl_lms.allocated[i];
			}

			GlState.gl.glTexSubImage2D( GlAdapter.GL_TEXTURE_2D, 
							  0,
							  0, 0,
							  BLOCK_WIDTH, height,
							  GL_LIGHTMAP_FORMAT,
							  GlAdapter.GL_UNSIGNED_BYTE,
							  gl_lms.lightmap_buffer );
		}
		else
		{
		  GlState.gl.glTexImage2D( GlAdapter.GL_TEXTURE_2D, 
						   0, 
						   GL_LIGHTMAP_FORMAT/*gl_lms.internal_format*/,
						   BLOCK_WIDTH, BLOCK_HEIGHT, 
						   0, 
						   GL_LIGHTMAP_FORMAT, 
						   GlAdapter.GL_UNSIGNED_BYTE, 
						   gl_lms.lightmap_buffer );
			if ( ++gl_lms.current_lightmap_texture == MAX_LIGHTMAPS )
				Com.Error( Constants.ERR_DROP, "LM_UploadBlock() - MAX_LIGHTMAPS exceeded\n" );
				
//			debugLightmap(gl_lms.lightmap_buffer, 128, 128, 4);
		}
	}

	/**
	 * LM_AllocBlock
	 * @param w
	 * @param h
	 * @param pos
	 * @return a texture number and the position inside it
	 */
	static boolean LM_AllocBlock (int w, int h, Images.pos_t pos)
	{
		int best = BLOCK_HEIGHT;
		int x = pos.x; 

		int best2;
		int i, j;
		for (i=0 ; i<BLOCK_WIDTH-w ; i++)
		{
			best2 = 0;

			for (j=0 ; j<w ; j++)
			{
				if (gl_lms.allocated[i+j] >= best)
					break;
				if (gl_lms.allocated[i+j] > best2)
					best2 = gl_lms.allocated[i+j];
			}
			if (j == w)
			{	// this is a valid spot
				pos.x = x = i;
				pos.y = best = best2;
			}
		}

		if (best + h > BLOCK_HEIGHT)
			return false;

		for (i=0 ; i<w ; i++)
			gl_lms.allocated[x + i] = best + h;

		return true;
	}

	/**
	 * GL_BuildPolygonFromSurface
	 */
	static void GL_BuildPolygonFromSurface(ModelSurface fa)
	{
		// reconstruct the polygon
		ModelEdge[] pedges = GlState.currentmodel.edges;
		int lnumverts = fa.numedges;
		//
		// draw texture
		//
		// poly = Hunk_Alloc (sizeof(glpoly_t) + (lnumverts-4) * VERTEXSIZE*sizeof(float));
		GlPolygon poly = GlPolygon.create(lnumverts);

		poly.next = fa.polys;
		poly.flags = fa.flags;
		fa.polys = poly;

		int lindex;
		float[] vec;
		ModelEdge r_pedge;
		float s, t;
		for (int i=0 ; i<lnumverts ; i++)
		{
			lindex = GlState.currentmodel.surfedges[fa.firstedge + i];

			if (lindex > 0)
			{
				r_pedge = pedges[lindex];
				vec = GlState.currentmodel.vertexes[r_pedge.v[0]].position;
			}
			else
			{
				r_pedge = pedges[-lindex];
				vec = GlState.currentmodel.vertexes[r_pedge.v[1]].position;
			}
			
//			if(!fa.texinfo.image.complete) {
//				gl.log("building surface with bad texture coordinates");
//			}
			
			s = Math3D.DotProduct (vec, fa.texinfo.vecs[0]) + fa.texinfo.vecs[0][3];
			s /= fa.texinfo.image.width;

			t = Math3D.DotProduct (vec, fa.texinfo.vecs[1]) + fa.texinfo.vecs[1][3];
			t /= fa.texinfo.image.height;

			poly.x(i, vec[0]);
			poly.y(i, vec[1]);
			poly.z(i, vec[2]);
			
			poly.s1(i, s);
			poly.t1(i, t);

			//
			// lightmap texture coordinates
			//
			s = Math3D.DotProduct (vec, fa.texinfo.vecs[0]) + fa.texinfo.vecs[0][3];
			s -= fa.texturemins[0];
			s += fa.light_s*16;
			s += 8;
			s /= BLOCK_WIDTH*16; //fa.texinfo.texture.width;

			t = Math3D.DotProduct (vec, fa.texinfo.vecs[1]) + fa.texinfo.vecs[1][3];
			t -= fa.texturemins[1];
			t += fa.light_t*16;
			t += 8;
			t /= BLOCK_HEIGHT*16; //fa.texinfo.texture.height;

			poly.s2(i, s);
			poly.t2(i, t);
		}
	}

	/**
	 * GL_CreateSurfaceLightmap
	 */
	static void GL_CreateSurfaceLightmap(ModelSurface surf)
	{
		if ( (surf.flags & (Constants.SURF_DRAWSKY | Constants.SURF_DRAWTURB)) != 0)
			return;

		int smax = (surf.extents[0]>>4)+1;
		int tmax = (surf.extents[1]>>4)+1;
		
		Images.pos_t lightPos = new Images.pos_t(surf.light_s, surf.light_t);

		if ( !LM_AllocBlock( smax, tmax, lightPos ) )
		{
			LM_UploadBlock( false );
			LM_InitBlock();
			lightPos = new Images.pos_t(surf.light_s, surf.light_t);
			if ( !LM_AllocBlock( smax, tmax, lightPos ) )
			{
				Com.Error( Constants.ERR_FATAL, "Consecutive calls to LM_AllocBlock(" + smax +"," + tmax +") failed\n");
			}
		}
		
		// kopiere die koordinaten zurueck
		surf.light_s = lightPos.x;
		surf.light_t = lightPos.y;

		surf.lightmaptexturenum = gl_lms.current_lightmap_texture;
		
		IntBuffer base = gl_lms.lightmap_buffer;
		base.position(surf.light_t * BLOCK_WIDTH + surf.light_s);

		Light.R_SetCacheState( surf );
		Light.R_BuildLightMap(surf, base.slice(), BLOCK_WIDTH);
	}

	static Lightstyle[] lightstyles;
	static private IntBuffer dummy;

	/**
	 * GL_BeginBuildingLightmaps
	 */
	static void GL_BeginBuildingLightmaps(RendererModel m)
	{
		GlState.gl.log("BeginBuildingLightmaps!");
		
		// static lightstyle_t	lightstyles[MAX_LIGHTSTYLES];

		// init lightstyles
		if ( lightstyles == null ) {
			lightstyles = new Lightstyle[Constants.MAX_LIGHTSTYLES];
			for (int i = 0; i < lightstyles.length; i++)
			{
				lightstyles[i] = new Lightstyle();				
			}
		}

		// memset( gl_lms.allocated, 0, sizeof(gl_lms.allocated) );
		Arrays.fill(gl_lms.allocated, 0);

		GlState.r_framecount = 1;		// no dlightcache

		Images.GL_EnableMultitexture( true );
		Images.GL_SelectTexture( GlState.GL_TEXTURE1);

		/*
		** setup the base lightstyles so the lightmaps won't have to be regenerated
		** the first time they're seen
		*/
		for (int i=0 ; i < Constants.MAX_LIGHTSTYLES ; i++)
		{
			lightstyles[i].rgb[0] = 1;
			lightstyles[i].rgb[1] = 1;
			lightstyles[i].rgb[2] = 1;
			lightstyles[i].white = 3;
		}
		GlState.r_newrefdef.lightstyles = lightstyles;

		if (GlState.lightmap_textures == 0)
		{
			GlState.lightmap_textures = GlConstants.TEXNUM_LIGHTMAPS;
		}

		gl_lms.current_lightmap_texture = 1;

		/*
		** if mono lightmaps are enabled and we want to use alpha
		** blending (a,1-a) then we're likely running on a 3DLabs
		** Permedia2.  In a perfect world we'd use a GL_ALPHA lightmap
		** in order to conserve space and maximize bandwidth, however 
		** this isn't a perfect world.
		**
		** So we have to use alpha lightmaps, but stored in GL_RGBA format,
		** which means we only get 1/16th the color resolution we should when
		** using alpha lightmaps.  If we find another board that supports
		** only alpha lightmaps but that can at least support the GL_ALPHA
		** format then we should change this code to use real alpha maps.
		*/
		
		char format = GlState.gl_monolightmap.string.toUpperCase().charAt(0);
		
		if ( format == 'A' )
		{
			gl_lms.internal_format = Images.gl_tex_alpha_format;
		}
		/*
		** try to do hacked colored lighting with a blended texture
		*/
		else if ( format == 'C' )
		{
			gl_lms.internal_format = Images.gl_tex_alpha_format;
		}
		else if ( format == 'I' )
		{
			GlState.gl.log("INTENSITY");
			gl_lms.internal_format = 1;
		}
		else if ( format == 'L' ) 
		{
			GlState.gl.log("LUMINANCE");
			gl_lms.internal_format = GlAdapter.GL_LUMINANCE;
		}
		else
		{
			gl_lms.internal_format = Images.gl_tex_solid_format;
		}

		if (dummy == null) {
			dummy = GlState.gl.createIntBuffer(128*128);
			for (int p = 0; p < 128 * 128; p++) {
				dummy.put(p, 0x0ffffffff);
			}
		}
		
		/*
		** initialize the dynamic lightmap texture
		*/
		Images.GL_Bind( GlState.gl_state.lightmap_textures + 0 );
		GlState.gl.glTexParameterf(GlAdapter.GL_TEXTURE_2D, GlAdapter.GL_TEXTURE_MIN_FILTER, GlAdapter.GL_LINEAR);
		GlState.gl.glTexParameterf(GlAdapter.GL_TEXTURE_2D, GlAdapter.GL_TEXTURE_MAG_FILTER, GlAdapter.GL_LINEAR);
		GlState.gl.glTexImage2D( GlAdapter.GL_TEXTURE_2D, 
					   0, 
					   GL_LIGHTMAP_FORMAT/*gl_lms.internal_format*/,
					   BLOCK_WIDTH, BLOCK_HEIGHT, 
					   0, 
					   GL_LIGHTMAP_FORMAT, 
					   GlAdapter.GL_UNSIGNED_BYTE, 
					   dummy );
	}

	/**
	 * GL_EndBuildingLightmaps
	 */
	static void GL_EndBuildingLightmaps()
	{
		LM_UploadBlock( false );
		Images.GL_EnableMultitexture( false );
	}
	
	/*
	 * new buffers for vertex array handling
	 */
	static FloatBuffer globalPolygonInterleavedBuf = GlPolygon.getInterleavedBuffer();
	static FloatBuffer globalPolygonTexCoord1Buf = null;

	static {
	 	globalPolygonInterleavedBuf.position(GlPolygon.STRIDE - 2);
	 	globalPolygonTexCoord1Buf = globalPolygonInterleavedBuf.slice();
		globalPolygonInterleavedBuf.position(0);
	 };

	//ImageFrame frame;
	
//	void debugLightmap(byte[] buf, int w, int h, float scale) {
//		IntBuffer pix = ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
//		
//		int[] pixel = new int[w * h];
//		
//		pix.get(pixel);
//		
//		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
//		image.setRGB(0,  0, w, h, pixel, 0, w);
//		AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(scale, scale), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
//		BufferedImage tmp = op.filter(image, null);
//		
//		if (frame == null) {
//			frame = new ImageFrame(null);
//			frame.show();
//		} 
//		frame.showImage(tmp);
//		
//	}

	protected static void debugLightmap(IntBuffer lightmapBuffer, int w, int h, float scale) {
		GlState.gl.log("debuglightmap");
	 }
	 
}
