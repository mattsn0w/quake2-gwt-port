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

import static com.googlecode.gwtquake.shared.common.Constants.CVAR_ARCHIVE;
import static com.googlecode.gwtquake.shared.common.Constants.CVAR_USERINFO;


import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.googlecode.gwtquake.shared.client.Dimension;
import com.googlecode.gwtquake.shared.client.EntityType;
import com.googlecode.gwtquake.shared.client.Particles;
import com.googlecode.gwtquake.shared.client.RendererState;
import com.googlecode.gwtquake.shared.client.Window;
import com.googlecode.gwtquake.shared.common.Com;
import com.googlecode.gwtquake.shared.common.ConsoleVariables;
import com.googlecode.gwtquake.shared.common.Constants;
import com.googlecode.gwtquake.shared.common.ExecutableCommand;
import com.googlecode.gwtquake.shared.common.QuakeFiles;
import com.googlecode.gwtquake.shared.common.QuakeImage;
import com.googlecode.gwtquake.shared.game.Commands;
import com.googlecode.gwtquake.shared.game.ConsoleVariable;
import com.googlecode.gwtquake.shared.game.Plane;
import com.googlecode.gwtquake.shared.render.GlAdapter;
import com.googlecode.gwtquake.shared.render.GlState;
import com.googlecode.gwtquake.shared.render.ModelImage;
import com.googlecode.gwtquake.shared.render.ModelLeaf;
import com.googlecode.gwtquake.shared.render.RendererModel;
import com.googlecode.gwtquake.shared.util.Math3D;
import com.googlecode.gwtquake.shared.util.Vargs;


/**
 * Main
 * 
 * @author cwei
 */
public abstract class Main extends GlBase {

	int c_visible_lightmaps;
	int c_visible_textures;

	int registration_sequence;

	// this a hack for function pointer test
	// default disabled
	boolean qglActiveTextureARB = false;
	boolean qglPointParameterfEXT = false;

	//	=================
	//  abstract methods
	//	=================
	protected abstract void Draw_GetPalette();

	abstract void GL_ImageList_f();
	abstract void GL_ScreenShot_f();
	abstract void GL_SetTexturePalette(int[] palette);
	abstract void GL_Strings_f();

	abstract void Mod_Modellist_f();
	abstract ModelLeaf Mod_PointInLeaf(float[] point, RendererModel model);

	abstract void GL_SetDefaultState();

	abstract void GL_InitImages();
	abstract void Mod_Init(); // Model.java
	abstract void R_InitParticleTexture(); // MIsc.java
	abstract void R_DrawAliasModel(EntityType e); // Mesh.java
	abstract void R_DrawBrushModel(EntityType e); // Surf.java
	abstract void Draw_InitLocal();
	abstract void R_LightPoint(float[] p, float[] color);
	abstract void R_PushDlights();
	abstract void R_MarkLeaves();
	abstract void R_DrawWorld();
	abstract void R_RenderDlights();
	abstract void R_DrawAlphaSurfaces();

	abstract void Mod_FreeAll();

	abstract void GL_ShutdownImages();
	abstract void GL_Bind(int texnum);
	abstract void GL_TexEnv(int mode);
	abstract void GL_TextureMode(String string);
	abstract void GL_TextureAlphaMode(String string);
	abstract void GL_TextureSolidMode(String string);
	abstract void GL_UpdateSwapInterval();

	/*
	====================================================================
	
	from gl_rmain.c
	
	====================================================================
	*/

	int GL_TEXTURE0 = GlAdapter.GL_TEXTURE0;
	int GL_TEXTURE1 = GlAdapter.GL_TEXTURE1;

	RendererModel r_worldmodel;

	float gldepthmin, gldepthmax;

	GlState gl_state = new GlState();

	ModelImage r_notexture; // use for bad textures
	ModelImage r_particletexture; // little dot for particles

	EntityType currententity;
	RendererModel currentmodel;

	Plane frustum[] = { new Plane(), new Plane(), new Plane(), new Plane()};

	int r_visframecount; // bumped when going to a new PVS
	int r_framecount; // used for dlight push checking

	int c_brush_polys, c_alias_polys;

	float v_blend[] = { 0, 0, 0, 0 }; // final blending color

	//
	//	   view origin
	//
	float[] vup = { 0, 0, 0 };
	float[] vpn = { 0, 0, 0 };
	float[] vright = { 0, 0, 0 };
	float[] r_origin = { 0, 0, 0 };

	//float r_world_matrix[] = new float[16];
	FloatBuffer r_world_matrix;
	
	protected void init() {
		super.init();
		r_world_matrix =gl.createFloatBuffer(16);
	}
	
	float r_base_world_matrix[] = new float[16];

	//
	//	   screen size info
	//
	RendererState r_newrefdef = new RendererState();

	int r_viewcluster, r_viewcluster2, r_oldviewcluster, r_oldviewcluster2;

	ConsoleVariable r_norefresh;
	ConsoleVariable r_drawentities;
	ConsoleVariable r_drawworld;
	ConsoleVariable r_speeds;
	ConsoleVariable r_fullbright;
	ConsoleVariable r_novis;
	ConsoleVariable r_nocull;
	ConsoleVariable r_lerpmodels;
	ConsoleVariable r_lefthand;

	ConsoleVariable r_lightlevel;
	// FIXME: This is a HACK to get the client's light level

	ConsoleVariable gl_nosubimage;
	ConsoleVariable gl_allow_software;

	ConsoleVariable gl_vertex_arrays;

	ConsoleVariable gl_particle_min_size;
	ConsoleVariable gl_particle_max_size;
	ConsoleVariable gl_particle_size;
	ConsoleVariable gl_particle_att_a;
	ConsoleVariable gl_particle_att_b;
	ConsoleVariable gl_particle_att_c;

	ConsoleVariable gl_ext_swapinterval;
	ConsoleVariable gl_ext_palettedtexture;
	ConsoleVariable gl_ext_multitexture;
	ConsoleVariable gl_ext_pointparameters;
	ConsoleVariable gl_ext_compiled_vertex_array;

	ConsoleVariable gl_log;
	ConsoleVariable gl_bitdepth;
	ConsoleVariable gl_drawbuffer;
	ConsoleVariable gl_driver;
	ConsoleVariable gl_lightmap;
	ConsoleVariable gl_shadows;
	ConsoleVariable gl_mode;
	ConsoleVariable gl_dynamic;
	ConsoleVariable gl_monolightmap;
	ConsoleVariable gl_modulate;
	ConsoleVariable gl_nobind;
	ConsoleVariable gl_round_down;
	ConsoleVariable gl_picmip;
	ConsoleVariable gl_skymip;
	ConsoleVariable gl_showtris;
	ConsoleVariable gl_ztrick;
	ConsoleVariable gl_finish;
	ConsoleVariable gl_clear;
	ConsoleVariable gl_cull;
	ConsoleVariable gl_polyblend;
	ConsoleVariable gl_flashblend;
	ConsoleVariable gl_playermip;
	ConsoleVariable gl_saturatelighting;
	ConsoleVariable gl_swapinterval;
	ConsoleVariable gl_texturemode;
	ConsoleVariable gl_texturealphamode;
	ConsoleVariable gl_texturesolidmode;
	ConsoleVariable gl_lockpvs;

	ConsoleVariable gl_3dlabs_broken;

	ConsoleVariable vid_gamma;
	ConsoleVariable vid_ref;

	// ============================================================================
	// to port from gl_rmain.c, ...
	// ============================================================================

	/**
	 * R_CullBox
	 * Returns true if the box is completely outside the frustum
	 */
	final boolean R_CullBox(float[] mins, float[] maxs) {
		assert(mins.length == 3 && maxs.length == 3) : "vec3_t bug";

		if (r_nocull.value != 0)
			return false;

		for (int i = 0; i < 4; i++) {
			if (Math3D.BoxOnPlaneSide(mins, maxs, frustum[i]) == 2)
				return true;
		}
		return false;
	}

	/**
	 * R_RotateForEntity
	 */
	final void R_RotateForEntity(EntityType e) {
	  gl.glTranslatef(e.origin[0], e.origin[1], e.origin[2]);

	  gl.glRotatef(e.angles[1], 0, 0, 1);
	  gl.glRotatef(-e.angles[0], 0, 1, 0);
	  gl.glRotatef(-e.angles[2], 1, 0, 0);
	}

	/*
	=============================================================
	
	   SPRITE MODELS
	
	=============================================================
	*/

	// stack variable
	private final float[] point = { 0, 0, 0 };
	/**
	 * R_DrawSpriteModel
	 */
	void R_DrawSpriteModel(EntityType e) {
		float alpha = 1.0F;

		QuakeFiles.dsprframe_t frame;
		QuakeFiles.dsprite_t psprite;

		// don't even bother culling, because it's just a single
		// polygon without a surface cache

		psprite = (QuakeFiles.dsprite_t) currentmodel.extradata;

		e.frame %= psprite.numframes;

		frame = psprite.frames[e.frame];

		if ((e.flags & Constants.RF_TRANSLUCENT) != 0)
			alpha = e.alpha;

		if (alpha != 1.0F)
		  gl.glEnable(GlAdapter.GL_BLEND);

		gl.glColor4f(1, 1, 1, alpha);

		GL_Bind(currentmodel.skins[e.frame].texnum);

		GL_TexEnv(GlAdapter.GL_MODULATE);

//		if (alpha == 1.0)
//		  gl.glEnable(GLAdapter.GL_ALPHA_TEST);
//		else
//		  gl.glDisable(GLAdapter.GL_ALPHA_TEST);

		gl.glBegin(GlAdapter._GL_QUADS);

		gl.glTexCoord2f(0, 1);
		Math3D.VectorMA(e.origin, -frame.origin_y, vup, point);
		Math3D.VectorMA(point, -frame.origin_x, vright, point);
		gl.glVertex3f(point[0], point[1], point[2]);

		gl.glTexCoord2f(0, 0);
		Math3D.VectorMA(e.origin, frame.height - frame.origin_y, vup, point);
		Math3D.VectorMA(point, -frame.origin_x, vright, point);
		gl.glVertex3f(point[0], point[1], point[2]);

		gl.glTexCoord2f(1, 0);
		Math3D.VectorMA(e.origin, frame.height - frame.origin_y, vup, point);
		Math3D.VectorMA(point, frame.width - frame.origin_x, vright, point);
		gl.glVertex3f(point[0], point[1], point[2]);

		gl.glTexCoord2f(1, 1);
		Math3D.VectorMA(e.origin, -frame.origin_y, vup, point);
		Math3D.VectorMA(point, frame.width - frame.origin_x, vright, point);
		gl.glVertex3f(point[0], point[1], point[2]);

		gl.glEnd();

//		gl.glDisable(GLAdapter.GL_ALPHA_TEST);
		GL_TexEnv(GlAdapter.GL_REPLACE);

		if (alpha != 1.0F)
		  gl.glDisable(GlAdapter.GL_BLEND);

		gl.glColor4f(1, 1, 1, 1);
	}

	// ==================================================================================

	// stack variable
	private final float[] shadelight = { 0, 0, 0 };
	/**
	 * R_DrawNullModel
	*/
	void R_DrawNullModel() {
		if ((currententity.flags & Constants.RF_FULLBRIGHT) != 0) {
			// cwei wollte blau: shadelight[0] = shadelight[1] = shadelight[2] = 1.0F;
			shadelight[0] = shadelight[1] = shadelight[2] = 0.0F;
			shadelight[2] = 0.8F;
		}
		else {
			R_LightPoint(currententity.origin, shadelight);
		}

		gl.glPushMatrix();
		R_RotateForEntity(currententity);

		gl.glDisable(GlAdapter.GL_TEXTURE_2D);
		gl.glColor3f(shadelight[0], shadelight[1], shadelight[2]);

		// this replaces the TRIANGLE_FAN
		//glut.glutWireCube(gl, 20);

		gl.glBegin(GlAdapter.GL_TRIANGLE_FAN);
		gl.glVertex3f(0, 0, -16);
		int i;
		for (i=0 ; i<=4 ; i++) {
		  gl.glVertex3f((float)(16.0f * Math.cos(i * Math.PI / 2)), (float)(16.0f * Math.sin(i * Math.PI / 2)), 0.0f);
		}
		gl.glEnd();
		
		gl.glBegin(GlAdapter.GL_TRIANGLE_FAN);
		gl.glVertex3f (0, 0, 16);
		for (i=4 ; i>=0 ; i--) {
		  gl.glVertex3f((float)(16.0f * Math.cos(i * Math.PI / 2)), (float)(16.0f * Math.sin(i * Math.PI / 2)), 0.0f);
		}
		gl.glEnd();

		
		gl.glColor3f(1, 1, 1);
		gl.glPopMatrix();
		gl.glEnable(GlAdapter.GL_TEXTURE_2D);
	}

	/**
	 * R_DrawEntitiesOnList
	 */
	void R_DrawEntitiesOnList() {
		if (r_drawentities.value == 0.0f)
			return;

		// draw non-transparent first
		int i;
		for (i = 0; i < r_newrefdef.num_entities; i++) {
			currententity = r_newrefdef.entities[i];
			if ((currententity.flags & Constants.RF_TRANSLUCENT) != 0)
				continue; // solid

			if ((currententity.flags & Constants.RF_BEAM) != 0) {
				R_DrawBeam(currententity);
			}
			else {
				currentmodel = currententity.model;
				if (currentmodel == null) {
					R_DrawNullModel();
					continue;
				}
				switch (currentmodel.type) {
					case GlConstants.mod_alias :
						R_DrawAliasModel(currententity);
						break;
					case GlConstants.mod_brush :
						R_DrawBrushModel(currententity);
						break;
					case GlConstants.mod_sprite :
						R_DrawSpriteModel(currententity);
						break;
					default :
						Com.Error(Constants.ERR_DROP, "Bad modeltype");
						break;
				}
			}
		}
		// draw transparent entities
		// we could sort these if it ever becomes a problem...
		gl.glDepthMask(false); // no z writes
		for (i = 0; i < r_newrefdef.num_entities; i++) {
			currententity = r_newrefdef.entities[i];
			if ((currententity.flags & Constants.RF_TRANSLUCENT) == 0)
				continue; // solid

			if ((currententity.flags & Constants.RF_BEAM) != 0) {
				R_DrawBeam(currententity);
			}
			else {
				currentmodel = currententity.model;

				if (currentmodel == null) {
					R_DrawNullModel();
					continue;
				}
				switch (currentmodel.type) {
					case GlConstants.mod_alias :
						R_DrawAliasModel(currententity);
						break;
					case GlConstants.mod_brush :
						R_DrawBrushModel(currententity);
						break;
					case GlConstants.mod_sprite :
						R_DrawSpriteModel(currententity);
						break;
					default :
						Com.Error(Constants.ERR_DROP, "Bad modeltype");
						break;
				}
			}
		}
		gl.glDepthMask(true); // back to writing
	}
	
	// stack variable 
	private final float[] up = { 0, 0, 0 };
	private final float[] right = { 0, 0, 0 };
	/**
	 * GL_DrawParticles
	 */
	void GL_DrawParticles(int num_particles) {
		float origin_x, origin_y, origin_z;

		Math3D.VectorScale(vup, 1.5f, up);
		Math3D.VectorScale(vright, 1.5f, right);
		
		GL_Bind(r_particletexture.texnum);
		gl.glDepthMask(false); // no z buffering
		gl.glEnable(GlAdapter.GL_BLEND);
		GL_TexEnv(GlAdapter.GL_MODULATE);
		
		gl.glBegin(GlAdapter.GL_TRIANGLES);

		FloatBuffer sourceVertices = Particles.vertexArray;
		IntBuffer sourceColors = Particles.colorArray;
		float scale;
		int color;
		for (int j = 0, i = 0; i < num_particles; i++) {
			origin_x = sourceVertices.get(j++);
			origin_y = sourceVertices.get(j++);
			origin_z = sourceVertices.get(j++);

			// hack a scale up to keep particles from disapearing
			scale =
				(origin_x - r_origin[0]) * vpn[0]
					+ (origin_y - r_origin[1]) * vpn[1]
					+ (origin_z - r_origin[2]) * vpn[2];

			scale = (scale < 20) ? 1 :  1 + scale * 0.004f;

			color = sourceColors.get(i);
		
			gl.glColor4ub(
				(byte)((color) & 0xFF),
				(byte)((color >> 8) & 0xFF),
				(byte)((color >> 16) & 0xFF),
				(byte)((color >>> 24))
			);
			// first vertex
			gl.glTexCoord2f(0.0625f, 0.0625f);
			gl.glVertex3f(origin_x, origin_y, origin_z);
			// second vertex
			gl.glTexCoord2f(1.0625f, 0.0625f);
			gl.glVertex3f(origin_x + up[0] * scale, origin_y + up[1] * scale, origin_z + up[2] * scale);
			// third vertex
			gl.glTexCoord2f(0.0625f, 1.0625f);
			gl.glVertex3f(origin_x + right[0] * scale, origin_y + right[1] * scale, origin_z + right[2] * scale);
		}
		gl.glEnd();
		
		gl.glDisable(GlAdapter.GL_BLEND);
		gl.glColor4f(1, 1, 1, 1);
		gl.glDepthMask(true); // back to normal Z buffering
		GL_TexEnv(GlAdapter.GL_REPLACE);
	}

	/**
	 * R_DrawParticles
	 */
	void R_DrawParticles() {

		if (gl_ext_pointparameters.value != 0.0f && qglPointParameterfEXT) {

			//gl.glEnableClientState(GLAdapter.GL_VERTEX_ARRAY);
		  gl.glVertexPointer(3, 0, Particles.vertexArray);
		  gl.glEnableClientState(GlAdapter.GL_COLOR_ARRAY);
		  gl.glColorPointer(4, true, 0, Particles.getColorAsByteBuffer());
			
		  gl.glDepthMask(false);
		  gl.glEnable(GlAdapter.GL_BLEND);
		  gl.glDisable(GlAdapter.GL_TEXTURE_2D);
		  gl.glPointSize(gl_particle_size.value);
			
		  gl.glDrawArrays(GlAdapter.GL_POINTS, 0, r_newrefdef.num_particles);
			
		  gl.glDisableClientState(GlAdapter.GL_COLOR_ARRAY);
			//gl.glDisableClientState(GLAdapter.GL_VERTEX_ARRAY);

		  gl.glDisable(GlAdapter.GL_BLEND);
		  gl.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		  gl.glDepthMask(true);
		  gl.glEnable(GlAdapter.GL_TEXTURE_2D);

		}
		else {
			GL_DrawParticles(r_newrefdef.num_particles);
		}
	}

	/**
	 * R_PolyBlend
	 */
	void R_PolyBlend() {
		if (gl_polyblend.value == 0.0f)
			return;

		if (v_blend[3] == 0.0f)
			return;

//		gl.glDisable(GLAdapter.GL_ALPHA_TEST);
		gl.glEnable(GlAdapter.GL_BLEND);
		gl.glDisable(GlAdapter.GL_DEPTH_TEST);
		gl.glDisable(GlAdapter.GL_TEXTURE_2D);

		gl.glLoadIdentity();

		// FIXME: get rid of these
		gl.glRotatef(-90, 1, 0, 0); // put Z going up
		gl.glRotatef(90, 0, 0, 1); // put Z going up

		gl.glColor4f(v_blend[0], v_blend[1], v_blend[2], v_blend[3]);

		gl.glBegin(GlAdapter._GL_QUADS);

		gl.glVertex3f(10, 100, 100);
		gl.glVertex3f(10, -100, 100);
		gl.glVertex3f(10, -100, -100);
		gl.glVertex3f(10, 100, -100);
		gl.glEnd();

		gl.glDisable(GlAdapter.GL_BLEND);
		gl.glEnable(GlAdapter.GL_TEXTURE_2D);
//		gl.glEnable(GLAdapter.GL_ALPHA_TEST);

		gl.glColor4f(1, 1, 1, 1);
	}

	// =======================================================================

	/**
	 * SignbitsForPlane
	 */
	int SignbitsForPlane(Plane out) {
		// for fast box on planeside test
		int bits = 0;
		for (int j = 0; j < 3; j++) {
			if (out.normal[j] < 0)
				bits |= (1 << j);
		}
		return bits;
	}

	/**
	 * R_SetFrustum
	 */
	void R_SetFrustum() {
		// rotate VPN right by FOV_X/2 degrees
		Math3D.RotatePointAroundVector(frustum[0].normal, vup, vpn, - (90f - r_newrefdef.fov_x / 2f));
		// rotate VPN left by FOV_X/2 degrees
		Math3D.RotatePointAroundVector(frustum[1].normal, vup, vpn, 90f - r_newrefdef.fov_x / 2f);
		// rotate VPN up by FOV_X/2 degrees
		Math3D.RotatePointAroundVector(frustum[2].normal, vright, vpn, 90f - r_newrefdef.fov_y / 2f);
		// rotate VPN down by FOV_X/2 degrees
		Math3D.RotatePointAroundVector(frustum[3].normal, vright, vpn, - (90f - r_newrefdef.fov_y / 2f));

		for (int i = 0; i < 4; i++) {
			frustum[i].type = Constants.PLANE_ANYZ;
			frustum[i].dist = Math3D.DotProduct(r_origin, frustum[i].normal);
			frustum[i].signbits = (byte) SignbitsForPlane(frustum[i]);
		}
	}

	// =======================================================================

	// stack variable
	private final float[] temp = {0, 0, 0};
	/**
	 * R_SetupFrame
	 */
	void R_SetupFrame() {
		r_framecount++;

		//	build the transformation matrix for the given view angles
		Math3D.VectorCopy(r_newrefdef.vieworg, r_origin);

		Math3D.AngleVectors(r_newrefdef.viewangles, vpn, vright, vup);

		//	current viewcluster
		ModelLeaf leaf;
		if ((r_newrefdef.rdflags & Constants.RDF_NOWORLDMODEL) == 0) {
			r_oldviewcluster = r_viewcluster;
			r_oldviewcluster2 = r_viewcluster2;
			leaf = Mod_PointInLeaf(r_origin, r_worldmodel);
			r_viewcluster = r_viewcluster2 = leaf.cluster;

			// check above and below so crossing solid water doesn't draw wrong
			if (leaf.contents == 0) { // look down a bit
				Math3D.VectorCopy(r_origin, temp);
				temp[2] -= 16;
				leaf = Mod_PointInLeaf(temp, r_worldmodel);
				if ((leaf.contents & Constants.CONTENTS_SOLID) == 0 && (leaf.cluster != r_viewcluster2))
					r_viewcluster2 = leaf.cluster;
			}
			else { // look up a bit
				Math3D.VectorCopy(r_origin, temp);
				temp[2] += 16;
				leaf = Mod_PointInLeaf(temp, r_worldmodel);
				if ((leaf.contents & Constants.CONTENTS_SOLID) == 0 && (leaf.cluster != r_viewcluster2))
					r_viewcluster2 = leaf.cluster;
			}
		}

		for (int i = 0; i < 4; i++)
			v_blend[i] = r_newrefdef.blend[i];

		c_brush_polys = 0;
		c_alias_polys = 0;

		// clear out the portion of the screen that the NOWORLDMODEL defines
		if ((r_newrefdef.rdflags & Constants.RDF_NOWORLDMODEL) != 0) {
		  gl.glEnable(GlAdapter.GL_SCISSOR_TEST);
		  gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
		  gl.glScissor(
				r_newrefdef.x,
				vid.height - r_newrefdef.height - r_newrefdef.y,
				r_newrefdef.width,
				r_newrefdef.height);
		  gl.glClear(GlAdapter.GL_COLOR_BUFFER_BIT | GlAdapter.GL_DEPTH_BUFFER_BIT);
		  gl.glClearColor(1.0f, 0.0f, 0.5f, 0.5f);
		  gl.glDisable(GlAdapter.GL_SCISSOR_TEST);
		}
	}

	/**
	 * MYgluPerspective
	 * 
	 * @param fovy
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 */
	void MYgluPerspective(double fovy, double aspect, double zNear, double zFar) {
		double ymax = zNear * Math.tan(fovy * Math.PI / 360.0);
		double ymin = -ymax;

		double xmin = ymin * aspect;
		double xmax = ymax * aspect;

		xmin += - (2 * gl_state.camera_separation) / zNear;
		xmax += - (2 * gl_state.camera_separation) / zNear;

		gl.glFrustum(xmin, xmax, ymin, ymax, zNear, zFar);
	}

	/**
	 * R_SetupGL
	 */
	void R_SetupGL() {

		//
		// set up viewport
		//
		//int x = (int) Math.floor(r_newrefdef.x * vid.width / vid.width);
		int x = r_newrefdef.x;
		//int x2 = (int) Math.ceil((r_newrefdef.x + r_newrefdef.width) * vid.width / vid.width);
		int x2 = r_newrefdef.x + r_newrefdef.width;
		//int y = (int) Math.floor(vid.height - r_newrefdef.y * vid.height / vid.height);
		int y = vid.height - r_newrefdef.y;
		//int y2 = (int) Math.ceil(vid.height - (r_newrefdef.y + r_newrefdef.height) * vid.height / vid.height);
		int y2 = vid.height - (r_newrefdef.y + r_newrefdef.height);

		int w = x2 - x;
		int h = y - y2;

		gl.glViewport(x, y2, w, h);

		//
		// set up projection matrix
		//
		float screenaspect = (float) r_newrefdef.width / r_newrefdef.height;
		gl.glMatrixMode(GlAdapter.GL_PROJECTION);
		gl.glLoadIdentity();
		MYgluPerspective(r_newrefdef.fov_y, screenaspect, 4, 4096);

		gl.glCullFace(GlAdapter.GL_FRONT);

		gl.glMatrixMode(GlAdapter.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glRotatef(-90, 1, 0, 0); // put Z going up
		gl.glRotatef(90, 0, 0, 1); // put Z going up
		gl.glRotatef(-r_newrefdef.viewangles[2], 1, 0, 0);
		gl.glRotatef(-r_newrefdef.viewangles[0], 0, 1, 0);
		gl.glRotatef(-r_newrefdef.viewangles[1], 0, 0, 1);
		gl.glTranslatef(-r_newrefdef.vieworg[0], -r_newrefdef.vieworg[1], -r_newrefdef.vieworg[2]);

		gl.glGetFloat(GlAdapter._GL_MODELVIEW_MATRIX, r_world_matrix);
		r_world_matrix.clear();

		//
		// set drawing parms
		//
		if (gl_cull.value != 0.0f)
		  gl.glEnable(GlAdapter.GL_CULL_FACE);
		else
		  gl.glDisable(GlAdapter.GL_CULL_FACE);

		gl.glDisable(GlAdapter.GL_BLEND);
//		gl.glDisable(GLAdapter.GL_ALPHA_TEST);
		gl.glEnable(GlAdapter.GL_DEPTH_TEST);
	}

	int trickframe = 0;

	/**
	 * R_Clear
	 */
	void R_Clear() {
		if (gl_ztrick.value != 0.0f) {

			if (gl_clear.value != 0.0f) {
			  gl.glClear(GlAdapter.GL_COLOR_BUFFER_BIT);
			}

			trickframe++;
			if ((trickframe & 1) != 0) {
				gldepthmin = 0;
				gldepthmax = 0.49999f;
				gl.glDepthFunc(GlAdapter.GL_LEQUAL);
			}
			else {
				gldepthmin = 1;
				gldepthmax = 0.5f;
				gl.glDepthFunc(GlAdapter.GL_GEQUAL);
			}
		}
		else {
			if (gl_clear.value != 0.0f)
			  gl.glClear(GlAdapter.GL_COLOR_BUFFER_BIT | GlAdapter.GL_DEPTH_BUFFER_BIT);
			else
			  gl.glClear(GlAdapter.GL_DEPTH_BUFFER_BIT);

			gldepthmin = 0;
			gldepthmax = 1;
			gl.glDepthFunc(GlAdapter.GL_LEQUAL);
		}
		gl.glDepthRange(gldepthmin, gldepthmax);
	}

	/**
	 * R_Flash
	 */
	void R_Flash() {
		R_PolyBlend();
	}

	/**
	 * R_RenderView
	 * r_newrefdef must be set before the first call
	 */
	void R_RenderView(RendererState fd) {

		if (r_norefresh.value != 0.0f)
			return;

		r_newrefdef = fd;

		// included by cwei
		if (r_newrefdef == null) {
			Com.Error(Constants.ERR_DROP, "R_RenderView: refdef_t fd is null");
		}

		if (r_worldmodel == null && (r_newrefdef.rdflags & Constants.RDF_NOWORLDMODEL) == 0)
			Com.Error(Constants.ERR_DROP, "R_RenderView: NULL worldmodel");

		if (r_speeds.value != 0.0f) {
			c_brush_polys = 0;
			c_alias_polys = 0;
		}

		R_PushDlights();

		if (gl_finish.value != 0.0f)
		  gl.glFinish();

		R_SetupFrame();

		R_SetFrustum();

		R_SetupGL();

		R_MarkLeaves(); // done here so we know if we're in water

		R_DrawWorld();

		R_DrawEntitiesOnList();

		R_RenderDlights();

		R_DrawParticles();

		R_DrawAlphaSurfaces();

		R_Flash();

		if (r_speeds.value != 0.0f) {
			Window.Printf(
				Constants.PRINT_ALL,
				"%4i wpoly %4i epoly %i tex %i lmaps\n",
				new Vargs(4).add(c_brush_polys).add(c_alias_polys).add(c_visible_textures).add(c_visible_lightmaps));
		}
	}

	/**
	 * R_SetGL2D
	 */
	void R_SetGL2D() {
		// set 2D virtual screen size
	  gl.glViewport(0, 0, vid.width, vid.height);
	  gl.glMatrixMode(GlAdapter.GL_PROJECTION);
	  gl.glLoadIdentity();
	  gl.glOrtho(0, vid.width, vid.height, 0, -99999, 99999);
	  gl.glMatrixMode(GlAdapter.GL_MODELVIEW);
	  gl.glLoadIdentity();
	  gl.glDisable(GlAdapter.GL_DEPTH_TEST);
	  gl.glDisable(GlAdapter.GL_CULL_FACE);
	  gl.glDisable(GlAdapter.GL_BLEND);
//	  gl.glEnable(GLAdapter.GL_ALPHA_TEST);
	  gl.glColor4f(1, 1, 1, 1);
	}

	// stack variable
	private final float[] light = { 0, 0, 0 };
	/**
	 *	R_SetLightLevel
	 */
	void R_SetLightLevel() {
		if ((r_newrefdef.rdflags & Constants.RDF_NOWORLDMODEL) != 0)
			return;

		// save off light value for server to look at (BIG HACK!)

		R_LightPoint(r_newrefdef.vieworg, light);

		// pick the greatest component, which should be the same
		// as the mono value returned by software
		if (light[0] > light[1]) {
			if (light[0] > light[2])
				r_lightlevel.value = 150 * light[0];
			else
				r_lightlevel.value = 150 * light[2];
		}
		else {
			if (light[1] > light[2])
				r_lightlevel.value = 150 * light[1];
			else
				r_lightlevel.value = 150 * light[2];
		}
	}

	/**
	 * R_RenderFrame
	 */
	protected void R_RenderFrame(RendererState fd) {
		R_RenderView(fd);
		R_SetLightLevel();
		R_SetGL2D();
	}

	/**
	 * R_Register
	 */
	protected void R_Register() {
		r_lefthand = ConsoleVariables.Get("hand", "0", CVAR_USERINFO | CVAR_ARCHIVE);
		r_norefresh = ConsoleVariables.Get("r_norefresh", "0", 0);
		r_fullbright = ConsoleVariables.Get("r_fullbright", "0", 0);
		r_drawentities = ConsoleVariables.Get("r_drawentities", "1", 0);
		r_drawworld = ConsoleVariables.Get("r_drawworld", "1", 0);
		r_novis = ConsoleVariables.Get("r_novis", "0", 0);
		r_nocull = ConsoleVariables.Get("r_nocull", "0", 0);
		r_lerpmodels = ConsoleVariables.Get("r_lerpmodels", "1", 0);
		r_speeds = ConsoleVariables.Get("r_speeds", "0", 0);

		r_lightlevel = ConsoleVariables.Get("r_lightlevel", "1", 0);

		gl_nosubimage = ConsoleVariables.Get("gl_nosubimage", "0", 0);
		gl_allow_software = ConsoleVariables.Get("gl_allow_software", "0", 0);

		gl_particle_min_size = ConsoleVariables.Get("gl_particle_min_size", "2", CVAR_ARCHIVE);
		gl_particle_max_size = ConsoleVariables.Get("gl_particle_max_size", "40", CVAR_ARCHIVE);
		gl_particle_size = ConsoleVariables.Get("gl_particle_size", "40", CVAR_ARCHIVE);
		gl_particle_att_a = ConsoleVariables.Get("gl_particle_att_a", "0.01", CVAR_ARCHIVE);
		gl_particle_att_b = ConsoleVariables.Get("gl_particle_att_b", "0.0", CVAR_ARCHIVE);
		gl_particle_att_c = ConsoleVariables.Get("gl_particle_att_c", "0.01", CVAR_ARCHIVE);

		gl_modulate = ConsoleVariables.Get("gl_modulate", "1.5", CVAR_ARCHIVE);
		gl_log = ConsoleVariables.Get("gl_log", "0", 0);
		gl_bitdepth = ConsoleVariables.Get("gl_bitdepth", "0", 0);
		gl_mode = ConsoleVariables.Get("gl_mode", "3", CVAR_ARCHIVE); // 640x480
		gl_lightmap = ConsoleVariables.Get("gl_lightmap", "0", 0);
		gl_shadows = ConsoleVariables.Get("gl_shadows", "0", CVAR_ARCHIVE);
		gl_dynamic = ConsoleVariables.Get("gl_dynamic", "1", 0);
		gl_nobind = ConsoleVariables.Get("gl_nobind", "0", 0);
		gl_round_down = ConsoleVariables.Get("gl_round_down", "1", 0);
		gl_picmip = ConsoleVariables.Get("gl_picmip", "0", 0);
		gl_skymip = ConsoleVariables.Get("gl_skymip", "0", 0);
		gl_showtris = ConsoleVariables.Get("gl_showtris", "0", 0);
		gl_ztrick = ConsoleVariables.Get("gl_ztrick", "0", 0);
		gl_finish = ConsoleVariables.Get("gl_finish", "0", CVAR_ARCHIVE);
		gl_clear = ConsoleVariables.Get("gl_clear", "0", 0);
		gl_cull = ConsoleVariables.Get("gl_cull", "1", 0);
		gl_polyblend = ConsoleVariables.Get("gl_polyblend", "1", 0);
		gl_flashblend = ConsoleVariables.Get("gl_flashblend", "0", 0);
		gl_playermip = ConsoleVariables.Get("gl_playermip", "0", 0);
		gl_monolightmap = ConsoleVariables.Get("gl_monolightmap", "0", 0);
		gl_driver = ConsoleVariables.Get("gl_driver", "opengl32", CVAR_ARCHIVE);
		gl_texturemode = ConsoleVariables.Get("gl_texturemode", "GL_LINEAR_MIPMAP_NEAREST", CVAR_ARCHIVE);
		gl_texturealphamode = ConsoleVariables.Get("gl_texturealphamode", "default", CVAR_ARCHIVE);
		gl_texturesolidmode = ConsoleVariables.Get("gl_texturesolidmode", "default", CVAR_ARCHIVE);
		gl_lockpvs = ConsoleVariables.Get("gl_lockpvs", "0", 0);

		gl_vertex_arrays = ConsoleVariables.Get("gl_vertex_arrays", "1", CVAR_ARCHIVE);

		gl_ext_swapinterval = ConsoleVariables.Get("gl_ext_swapinterval", "1", CVAR_ARCHIVE);
		gl_ext_palettedtexture = ConsoleVariables.Get("gl_ext_palettedtexture", "0", CVAR_ARCHIVE);
		gl_ext_multitexture = ConsoleVariables.Get("gl_ext_multitexture", "1", CVAR_ARCHIVE);
		gl_ext_pointparameters = ConsoleVariables.Get("gl_ext_pointparameters", "1", CVAR_ARCHIVE);
		gl_ext_compiled_vertex_array = ConsoleVariables.Get("gl_ext_compiled_vertex_array", "1", CVAR_ARCHIVE);

		gl_drawbuffer = ConsoleVariables.Get("gl_drawbuffer", "GL_BACK", 0);
		gl_swapinterval = ConsoleVariables.Get("gl_swapinterval", "0", CVAR_ARCHIVE);

		gl_saturatelighting = ConsoleVariables.Get("gl_saturatelighting", "0", 0);

		gl_3dlabs_broken = ConsoleVariables.Get("gl_3dlabs_broken", "1", CVAR_ARCHIVE);

		vid_fullscreen = ConsoleVariables.Get("vid_fullscreen", "0", CVAR_ARCHIVE);
		vid_gamma = ConsoleVariables.Get("vid_gamma", "1.0", CVAR_ARCHIVE);
		vid_ref = ConsoleVariables.Get("vid_ref", "lwjgl", CVAR_ARCHIVE);

		Commands.addCommand("imagelist", new ExecutableCommand() {
			public void execute() {
				GL_ImageList_f();
			}
		});

		Commands.addCommand("screenshot", new ExecutableCommand() {
			public void execute() {
				GL_ScreenShot_f();
			}
		});
		Commands.addCommand("modellist", new ExecutableCommand() {
			public void execute() {
				Mod_Modellist_f();
			}
		});
		Commands.addCommand("gl_strings", new ExecutableCommand() {
			public void execute() {
				GL_Strings_f();
			}
		});
	}

	/**
	 * R_SetMode
	 */
	protected boolean R_SetMode() {
		boolean fullscreen = (vid_fullscreen.value > 0.0f);

		vid_fullscreen.modified = false;
		gl_mode.modified = false;

		Dimension dim = new Dimension(vid.width, vid.height);

		int err; //  enum rserr_t
		if ((err = GLimp_SetMode(dim, (int) gl_mode.value, fullscreen)) == rserr_ok) {
			gl_state.prev_mode = (int) gl_mode.value;
		}
		else {
			if (err == rserr_invalid_fullscreen) {
				ConsoleVariables.SetValue("vid_fullscreen", 0);
				vid_fullscreen.modified = false;
				Window.Printf(Constants.PRINT_ALL, "ref_gl::R_SetMode() - fullscreen unavailable in this mode\n");
				if ((err = GLimp_SetMode(dim, (int) gl_mode.value, false)) == rserr_ok)
					return true;
			}
			else if (err == rserr_invalid_mode) {
				ConsoleVariables.SetValue("gl_mode", gl_state.prev_mode);
				gl_mode.modified = false;
				Window.Printf(Constants.PRINT_ALL, "ref_gl::R_SetMode() - invalid mode\n");
			}

			// try setting it back to something safe
			if ((err = GLimp_SetMode(dim, gl_state.prev_mode, false)) != rserr_ok) {
				Window.Printf(Constants.PRINT_ALL, "ref_gl::R_SetMode() - could not revert to safe mode\n");
				return false;
			}
		}
		return true;
	}

	float[] r_turbsin = new float[256];

	/**
	 * R_Init
	 */
	protected boolean R_Init(int vid_xpos, int vid_ypos) {

		assert(Warp.SIN.length == 256) : "warpsin table bug";

		// fill r_turbsin
		for (int j = 0; j < 256; j++) {
			r_turbsin[j] = Warp.SIN[j] * 0.5f;
		}

		Window.Printf(Constants.PRINT_ALL, "ref_gl version: " + GlConstants.REF_VERSION + '\n');

		Draw_GetPalette();

		R_Register();

		// set our "safe" modes
		gl_state.prev_mode = 3;

		// create the window and set up the context
		if (!R_SetMode()) {
			Window.Printf(Constants.PRINT_ALL, "ref_gl::R_Init() - could not R_SetMode()\n");
			return false;
		}
		return true;
	}

	/**
	 * R_Init2
	 */
	protected boolean R_Init2() {
		qglPointParameterfEXT = true;
		qglActiveTextureARB = true;
		GL_TEXTURE0 = GlAdapter.GL_TEXTURE0;
		GL_TEXTURE1 = GlAdapter.GL_TEXTURE1;

		if (!(qglActiveTextureARB))
			return false;

		GL_SetDefaultState();

		GL_InitImages();
		Mod_Init();
		R_InitParticleTexture();
		Draw_InitLocal();

		int err = gl.glGetError();
		if (err != GlAdapter.GL_NO_ERROR)
			Window.Printf(
				Constants.PRINT_ALL,
				"glGetError() = 0x%x\n\t%s\n",
				new Vargs(2).add(err).add("" + gl.glGetString(err)));

		return true;
	}

	/**
	 * R_Shutdown
	 */
	protected void R_Shutdown() {
		Commands.RemoveCommand("modellist");
		Commands.RemoveCommand("screenshot");
		Commands.RemoveCommand("imagelist");
		Commands.RemoveCommand("gl_strings");

		Mod_FreeAll();

		GL_ShutdownImages();

		/*
		 * shut down OS specific OpenGL stuff like contexts, etc.
		 */
		GLimp_Shutdown();
	}

	/**
	 * R_BeginFrame
	 */
	public final void BeginFrame(float camera_separation) {

		gl_state.camera_separation = camera_separation;

		/*
		** change modes if necessary
		*/
		if (gl_mode.modified || vid_fullscreen.modified) {
			// FIXME: only restart if CDS is required
			ConsoleVariable ref;

			ref = ConsoleVariables.Get("vid_ref", "lwjgl", 0);
			ref.modified = true;
		}

		if (gl_log.modified) {
			GLimp_EnableLogging((gl_log.value != 0.0f));
			gl_log.modified = false;
		}

		if (gl_log.value != 0.0f) {
			GLimp_LogNewFrame();
		}

		/*
		** update 3Dfx gamma -- it is expected that a user will do a vid_restart
		** after tweaking this value
		*/
		if (vid_gamma.modified) {
			vid_gamma.modified = false;
		}

		GLimp_BeginFrame(camera_separation);

		/*
		** go into 2D mode
		*/
		gl.glViewport(0, 0, vid.width, vid.height);
		gl.glMatrixMode(GlAdapter.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrtho(0, vid.width, vid.height, 0, -99999, 99999);
		gl.glMatrixMode(GlAdapter.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glDisable(GlAdapter.GL_DEPTH_TEST);
		gl.glDisable(GlAdapter.GL_CULL_FACE);
		gl.glDisable(GlAdapter.GL_BLEND);
//		gl.glEnable(GLAdapter.GL_ALPHA_TEST);
		gl.glColor4f(1, 1, 1, 1);

		/*
		** draw buffer stuff
		*/
		if (gl_drawbuffer.modified) {
			gl_drawbuffer.modified = false;

			if (gl_state.camera_separation == 0 || !gl_state.stereo_enabled) {
				if (gl_drawbuffer.string.equalsIgnoreCase("GL_FRONT"))
				  gl.glDrawBuffer(GlAdapter.GL_FRONT);
				else
				  gl.glDrawBuffer(GlAdapter.GL_BACK);
			}
		}

		/*
		** texturemode stuff
		*/
		if (gl_texturemode.modified) {
			GL_TextureMode(gl_texturemode.string);
			gl_texturemode.modified = false;
		}

		if (gl_texturealphamode.modified) {
			GL_TextureAlphaMode(gl_texturealphamode.string);
			gl_texturealphamode.modified = false;
		}

		if (gl_texturesolidmode.modified) {
			GL_TextureSolidMode(gl_texturesolidmode.string);
			gl_texturesolidmode.modified = false;
		}

		/*
		** swapinterval stuff
		*/
		GL_UpdateSwapInterval();

		//
		// clear screen if desired
		//
		R_Clear();
	}

	int[] r_rawpalette = new int[256];

	/**
	 * R_SetPalette
	 */
	protected void R_SetPalette(byte[] palette) {
		// 256 RGB values (768 bytes)
		// or null
		int i;
		int color = 0;

		if (palette != null) {
			int j =0;
			for (i = 0; i < 256; i++) {
				color = (palette[j++] & 0xFF) << 0;
				color |= (palette[j++] & 0xFF) << 8;
				color |= (palette[j++] & 0xFF) << 16;
				color |= 0xFF000000;
				r_rawpalette[i] = color;
			}
		}
		else {
			for (i = 0; i < 256; i++) {
				r_rawpalette[i] = QuakeImage.PALETTE_ABGR[i] | 0xff000000;
			}
		}
		GL_SetTexturePalette(r_rawpalette);

		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GlAdapter.GL_COLOR_BUFFER_BIT);
		gl.glClearColor(1f, 0f, 0.5f, 0.5f);
	}

	static final int NUM_BEAM_SEGS = 6;
	float[][] start_points = new float[NUM_BEAM_SEGS][3];
	// array of vec3_t
	float[][] end_points = new float[NUM_BEAM_SEGS][3]; // array of vec3_t

	// stack variable
	private final float[] perpvec = { 0, 0, 0 }; // vec3_t
	private final float[] direction = { 0, 0, 0 }; // vec3_t
	private final float[] normalized_direction = { 0, 0, 0 }; // vec3_t
	private final float[] oldorigin = { 0, 0, 0 }; // vec3_t
	private final float[] origin = { 0, 0, 0 }; // vec3_t
	/**
	 * R_DrawBeam
	 */
	void R_DrawBeam(EntityType e) {
		oldorigin[0] = e.oldorigin[0];
		oldorigin[1] = e.oldorigin[1];
		oldorigin[2] = e.oldorigin[2];

		origin[0] = e.origin[0];
		origin[1] = e.origin[1];
		origin[2] = e.origin[2];

		normalized_direction[0] = direction[0] = oldorigin[0] - origin[0];
		normalized_direction[1] = direction[1] = oldorigin[1] - origin[1];
		normalized_direction[2] = direction[2] = oldorigin[2] - origin[2];

		if (Math3D.VectorNormalize(normalized_direction) == 0.0f)
			return;

		Math3D.PerpendicularVector(perpvec, normalized_direction);
		Math3D.VectorScale(perpvec, e.frame / 2, perpvec);

		for (int i = 0; i < 6; i++) {
			Math3D.RotatePointAroundVector(
				start_points[i],
				normalized_direction,
				perpvec,
				(360.0f / NUM_BEAM_SEGS) * i);

			Math3D.VectorAdd(start_points[i], origin, start_points[i]);
			Math3D.VectorAdd(start_points[i], direction, end_points[i]);
		}

		gl.glDisable(GlAdapter.GL_TEXTURE_2D);
		gl.glEnable(GlAdapter.GL_BLEND);
		gl.glDepthMask(false);

		float r = (QuakeImage.PALETTE_ABGR[e.skinnum & 0xFF]) & 0xFF;
		float g = (QuakeImage.PALETTE_ABGR[e.skinnum & 0xFF] >> 8) & 0xFF;
		float b = (QuakeImage.PALETTE_ABGR[e.skinnum & 0xFF] >> 16) & 0xFF;

		r *= 1 / 255.0f;
		g *= 1 / 255.0f;
		b *= 1 / 255.0f;

		gl.glColor4f(r, g, b, e.alpha);

		gl.glBegin(GlAdapter.GL_TRIANGLE_STRIP);
		
		float[] v;
		
		for (int i = 0; i < NUM_BEAM_SEGS; i++) {
			v = start_points[i];
			gl.glVertex3f(v[0], v[1], v[2]);
			v = end_points[i];
			gl.glVertex3f(v[0], v[1], v[2]);
			v = start_points[(i + 1) % NUM_BEAM_SEGS];
			gl.glVertex3f(v[0], v[1], v[2]);
			v = end_points[(i + 1) % NUM_BEAM_SEGS];
			gl.glVertex3f(v[0], v[1], v[2]);
		}
		gl.glEnd();

		gl.glEnable(GlAdapter.GL_TEXTURE_2D);
		gl.glDisable(GlAdapter.GL_BLEND);
		gl.glDepthMask(true);
	}
}
