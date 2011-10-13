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

	//	=================
	//  abstract methods
	//	=================
	protected abstract void Draw_GetPalette();


	
	abstract void GL_SetTexturePalette(int[] palette);


	abstract void Mod_Modellist_f();
	abstract ModelLeaf Mod_PointInLeaf(float[] point, RendererModel model);


	abstract void GL_InitImages();
	abstract void Mod_Init(); // Model.java
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


	/*
	====================================================================
	
	from gl_rmain.c
	
	====================================================================
	*/

	protected void init() {
		super.init();
		GlState.r_world_matrix =GlState.gl.createFloatBuffer(16);
	}
	
	

	// ============================================================================
	// to port from gl_rmain.c, ...
	// ============================================================================

	/**
	 * R_CullBox
	 * Returns true if the box is completely outside the frustum
	 */
	final boolean R_CullBox(float[] mins, float[] maxs) {
		assert(mins.length == 3 && maxs.length == 3) : "vec3_t bug";

		if (GlState.r_nocull.value != 0)
			return false;

		for (int i = 0; i < 4; i++) {
			if (Math3D.BoxOnPlaneSide(mins, maxs, GlState.frustum[i]) == 2)
				return true;
		}
		return false;
	}

	/**
	 * R_RotateForEntity
	 */
	final void R_RotateForEntity(EntityType e) {
	  GlState.gl.glTranslatef(e.origin[0], e.origin[1], e.origin[2]);

	  GlState.gl.glRotatef(e.angles[1], 0, 0, 1);
	  GlState.gl.glRotatef(-e.angles[0], 0, 1, 0);
	  GlState.gl.glRotatef(-e.angles[2], 1, 0, 0);
	}

	/*
	=============================================================
	
	   SPRITE MODELS
	
	=============================================================
	*/

	/**
	 * R_DrawSpriteModel
	 */
	void R_DrawSpriteModel(EntityType e) {
		float alpha = 1.0F;

		QuakeFiles.dsprframe_t frame;
		QuakeFiles.dsprite_t psprite;

		// don't even bother culling, because it's just a single
		// polygon without a surface cache

		psprite = (QuakeFiles.dsprite_t) GlState.currentmodel.extradata;

		e.frame %= psprite.numframes;

		frame = psprite.frames[e.frame];

		if ((e.flags & Constants.RF_TRANSLUCENT) != 0)
			alpha = e.alpha;

		if (alpha != 1.0F)
		  GlState.gl.glEnable(GlAdapter.GL_BLEND);

		GlState.gl.glColor4f(1, 1, 1, alpha);

		Images.GL_Bind(GlState.currentmodel.skins[e.frame].texnum);

		Images.GL_TexEnv(GlAdapter.GL_MODULATE);

//		if (alpha == 1.0)
//		  gl.glEnable(GLAdapter.GL_ALPHA_TEST);
//		else
//		  gl.glDisable(GLAdapter.GL_ALPHA_TEST);

		GlState.gl.glBegin(GlAdapter._GL_QUADS);

		GlState.gl.glTexCoord2f(0, 1);
		Math3D.VectorMA(e.origin, -frame.origin_y, GlState.vup, GlState.point);
		Math3D.VectorMA(GlState.point, -frame.origin_x, GlState.vright, GlState.point);
		GlState.gl.glVertex3f(GlState.point[0], GlState.point[1], GlState.point[2]);

		GlState.gl.glTexCoord2f(0, 0);
		Math3D.VectorMA(e.origin, frame.height - frame.origin_y, GlState.vup, GlState.point);
		Math3D.VectorMA(GlState.point, -frame.origin_x, GlState.vright, GlState.point);
		GlState.gl.glVertex3f(GlState.point[0], GlState.point[1], GlState.point[2]);

		GlState.gl.glTexCoord2f(1, 0);
		Math3D.VectorMA(e.origin, frame.height - frame.origin_y, GlState.vup, GlState.point);
		Math3D.VectorMA(GlState.point, frame.width - frame.origin_x, GlState.vright, GlState.point);
		GlState.gl.glVertex3f(GlState.point[0], GlState.point[1], GlState.point[2]);

		GlState.gl.glTexCoord2f(1, 1);
		Math3D.VectorMA(e.origin, -frame.origin_y, GlState.vup, GlState.point);
		Math3D.VectorMA(GlState.point, frame.width - frame.origin_x, GlState.vright, GlState.point);
		GlState.gl.glVertex3f(GlState.point[0], GlState.point[1], GlState.point[2]);

		GlState.gl.glEnd();

//		gl.glDisable(GLAdapter.GL_ALPHA_TEST);
		Images.GL_TexEnv(GlAdapter.GL_REPLACE);

		if (alpha != 1.0F)
		  GlState.gl.glDisable(GlAdapter.GL_BLEND);

		GlState.gl.glColor4f(1, 1, 1, 1);
	}

	// ==================================================================================

	/**
	 * R_DrawNullModel
	*/
	void R_DrawNullModel() {
		if ((GlState.currententity.flags & Constants.RF_FULLBRIGHT) != 0) {
			// cwei wollte blau: shadelight[0] = shadelight[1] = shadelight[2] = 1.0F;
			GlState.shadelight[0] = GlState.shadelight[1] = GlState.shadelight[2] = 0.0F;
			GlState.shadelight[2] = 0.8F;
		}
		else {
			R_LightPoint(GlState.currententity.origin, GlState.shadelight);
		}

		GlState.gl.glPushMatrix();
		R_RotateForEntity(GlState.currententity);

		GlState.gl.glDisable(GlAdapter.GL_TEXTURE_2D);
		GlState.gl.glColor3f(GlState.shadelight[0], GlState.shadelight[1], GlState.shadelight[2]);

		// this replaces the TRIANGLE_FAN
		//glut.glutWireCube(gl, 20);

		GlState.gl.glBegin(GlAdapter.GL_TRIANGLE_FAN);
		GlState.gl.glVertex3f(0, 0, -16);
		int i;
		for (i=0 ; i<=4 ; i++) {
		  GlState.gl.glVertex3f((float)(16.0f * Math.cos(i * Math.PI / 2)), (float)(16.0f * Math.sin(i * Math.PI / 2)), 0.0f);
		}
		GlState.gl.glEnd();
		
		GlState.gl.glBegin(GlAdapter.GL_TRIANGLE_FAN);
		GlState.gl.glVertex3f (0, 0, 16);
		for (i=4 ; i>=0 ; i--) {
		  GlState.gl.glVertex3f((float)(16.0f * Math.cos(i * Math.PI / 2)), (float)(16.0f * Math.sin(i * Math.PI / 2)), 0.0f);
		}
		GlState.gl.glEnd();

		
		GlState.gl.glColor3f(1, 1, 1);
		GlState.gl.glPopMatrix();
		GlState.gl.glEnable(GlAdapter.GL_TEXTURE_2D);
	}

	/**
	 * R_DrawEntitiesOnList
	 */
	void R_DrawEntitiesOnList() {
		if (GlState.r_drawentities.value == 0.0f)
			return;

		// draw non-transparent first
		int i;
		for (i = 0; i < GlState.r_newrefdef.num_entities; i++) {
			GlState.currententity = GlState.r_newrefdef.entities[i];
			if ((GlState.currententity.flags & Constants.RF_TRANSLUCENT) != 0)
				continue; // solid

			if ((GlState.currententity.flags & Constants.RF_BEAM) != 0) {
				R_DrawBeam(GlState.currententity);
			}
			else {
				GlState.currentmodel = GlState.currententity.model;
				if (GlState.currentmodel == null) {
					R_DrawNullModel();
					continue;
				}
				switch (GlState.currentmodel.type) {
					case GlConstants.mod_alias :
						R_DrawAliasModel(GlState.currententity);
						break;
					case GlConstants.mod_brush :
						R_DrawBrushModel(GlState.currententity);
						break;
					case GlConstants.mod_sprite :
						R_DrawSpriteModel(GlState.currententity);
						break;
					default :
						Com.Error(Constants.ERR_DROP, "Bad modeltype");
						break;
				}
			}
		}
		// draw transparent entities
		// we could sort these if it ever becomes a problem...
		GlState.gl.glDepthMask(false); // no z writes
		for (i = 0; i < GlState.r_newrefdef.num_entities; i++) {
			GlState.currententity = GlState.r_newrefdef.entities[i];
			if ((GlState.currententity.flags & Constants.RF_TRANSLUCENT) == 0)
				continue; // solid

			if ((GlState.currententity.flags & Constants.RF_BEAM) != 0) {
				R_DrawBeam(GlState.currententity);
			}
			else {
				GlState.currentmodel = GlState.currententity.model;

				if (GlState.currentmodel == null) {
					R_DrawNullModel();
					continue;
				}
				switch (GlState.currentmodel.type) {
					case GlConstants.mod_alias :
						R_DrawAliasModel(GlState.currententity);
						break;
					case GlConstants.mod_brush :
						R_DrawBrushModel(GlState.currententity);
						break;
					case GlConstants.mod_sprite :
						R_DrawSpriteModel(GlState.currententity);
						break;
					default :
						Com.Error(Constants.ERR_DROP, "Bad modeltype");
						break;
				}
			}
		}
		GlState.gl.glDepthMask(true); // back to writing
	}
	
	/**
	 * GL_DrawParticles
	 */
	void GL_DrawParticles(int num_particles) {
		float origin_x, origin_y, origin_z;

		Math3D.VectorScale(GlState.vup, 1.5f, GlState.up);
		Math3D.VectorScale(GlState.vright, 1.5f, GlState.right);
		
		Images.GL_Bind(GlState.r_particletexture.texnum);
		GlState.gl.glDepthMask(false); // no z buffering
		GlState.gl.glEnable(GlAdapter.GL_BLEND);
		Images.GL_TexEnv(GlAdapter.GL_MODULATE);
		
		GlState.gl.glBegin(GlAdapter.GL_TRIANGLES);

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
				(origin_x - GlState.r_origin[0]) * GlState.vpn[0]
					+ (origin_y - GlState.r_origin[1]) * GlState.vpn[1]
					+ (origin_z - GlState.r_origin[2]) * GlState.vpn[2];

			scale = (scale < 20) ? 1 :  1 + scale * 0.004f;

			color = sourceColors.get(i);
		
			GlState.gl.glColor4ub(
				(byte)((color) & 0xFF),
				(byte)((color >> 8) & 0xFF),
				(byte)((color >> 16) & 0xFF),
				(byte)((color >>> 24))
			);
			// first vertex
			GlState.gl.glTexCoord2f(0.0625f, 0.0625f);
			GlState.gl.glVertex3f(origin_x, origin_y, origin_z);
			// second vertex
			GlState.gl.glTexCoord2f(1.0625f, 0.0625f);
			GlState.gl.glVertex3f(origin_x + GlState.up[0] * scale, origin_y + GlState.up[1] * scale, origin_z + GlState.up[2] * scale);
			// third vertex
			GlState.gl.glTexCoord2f(0.0625f, 1.0625f);
			GlState.gl.glVertex3f(origin_x + GlState.right[0] * scale, origin_y + GlState.right[1] * scale, origin_z + GlState.right[2] * scale);
		}
		GlState.gl.glEnd();
		
		GlState.gl.glDisable(GlAdapter.GL_BLEND);
		GlState.gl.glColor4f(1, 1, 1, 1);
		GlState.gl.glDepthMask(true); // back to normal Z buffering
		Images.GL_TexEnv(GlAdapter.GL_REPLACE);
	}

	/**
	 * R_DrawParticles
	 */
	void R_DrawParticles() {

		if (GlState.gl_ext_pointparameters.value != 0.0f && GlState.qglPointParameterfEXT) {

			//gl.glEnableClientState(GLAdapter.GL_VERTEX_ARRAY);
		  GlState.gl.glVertexPointer(3, 0, Particles.vertexArray);
		  GlState.gl.glEnableClientState(GlAdapter.GL_COLOR_ARRAY);
		  GlState.gl.glColorPointer(4, true, 0, Particles.getColorAsByteBuffer());
			
		  GlState.gl.glDepthMask(false);
		  GlState.gl.glEnable(GlAdapter.GL_BLEND);
		  GlState.gl.glDisable(GlAdapter.GL_TEXTURE_2D);
		  GlState.gl.glPointSize(GlState.gl_particle_size.value);
			
		  GlState.gl.glDrawArrays(GlAdapter.GL_POINTS, 0, GlState.r_newrefdef.num_particles);
			
		  GlState.gl.glDisableClientState(GlAdapter.GL_COLOR_ARRAY);
			//gl.glDisableClientState(GLAdapter.GL_VERTEX_ARRAY);

		  GlState.gl.glDisable(GlAdapter.GL_BLEND);
		  GlState.gl.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		  GlState.gl.glDepthMask(true);
		  GlState.gl.glEnable(GlAdapter.GL_TEXTURE_2D);

		}
		else {
			GL_DrawParticles(GlState.r_newrefdef.num_particles);
		}
	}

	/**
	 * R_PolyBlend
	 */
	void R_PolyBlend() {
		if (GlState.gl_polyblend.value == 0.0f)
			return;

		if (GlState.v_blend[3] == 0.0f)
			return;

//		gl.glDisable(GLAdapter.GL_ALPHA_TEST);
		GlState.gl.glEnable(GlAdapter.GL_BLEND);
		GlState.gl.glDisable(GlAdapter.GL_DEPTH_TEST);
		GlState.gl.glDisable(GlAdapter.GL_TEXTURE_2D);

		GlState.gl.glLoadIdentity();

		// FIXME: get rid of these
		GlState.gl.glRotatef(-90, 1, 0, 0); // put Z going up
		GlState.gl.glRotatef(90, 0, 0, 1); // put Z going up

		GlState.gl.glColor4f(GlState.v_blend[0], GlState.v_blend[1], GlState.v_blend[2], GlState.v_blend[3]);

		GlState.gl.glBegin(GlAdapter._GL_QUADS);

		GlState.gl.glVertex3f(10, 100, 100);
		GlState.gl.glVertex3f(10, -100, 100);
		GlState.gl.glVertex3f(10, -100, -100);
		GlState.gl.glVertex3f(10, 100, -100);
		GlState.gl.glEnd();

		GlState.gl.glDisable(GlAdapter.GL_BLEND);
		GlState.gl.glEnable(GlAdapter.GL_TEXTURE_2D);
//		gl.glEnable(GLAdapter.GL_ALPHA_TEST);

		GlState.gl.glColor4f(1, 1, 1, 1);
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
		Math3D.RotatePointAroundVector(GlState.frustum[0].normal, GlState.vup, GlState.vpn, - (90f - GlState.r_newrefdef.fov_x / 2f));
		// rotate VPN left by FOV_X/2 degrees
		Math3D.RotatePointAroundVector(GlState.frustum[1].normal, GlState.vup, GlState.vpn, 90f - GlState.r_newrefdef.fov_x / 2f);
		// rotate VPN up by FOV_X/2 degrees
		Math3D.RotatePointAroundVector(GlState.frustum[2].normal, GlState.vright, GlState.vpn, 90f - GlState.r_newrefdef.fov_y / 2f);
		// rotate VPN down by FOV_X/2 degrees
		Math3D.RotatePointAroundVector(GlState.frustum[3].normal, GlState.vright, GlState.vpn, - (90f - GlState.r_newrefdef.fov_y / 2f));

		for (int i = 0; i < 4; i++) {
			GlState.frustum[i].type = Constants.PLANE_ANYZ;
			GlState.frustum[i].dist = Math3D.DotProduct(GlState.r_origin, GlState.frustum[i].normal);
			GlState.frustum[i].signbits = (byte) SignbitsForPlane(GlState.frustum[i]);
		}
	}

	// =======================================================================

	/**
	 * R_SetupFrame
	 */
	void R_SetupFrame() {
		GlState.r_framecount++;

		//	build the transformation matrix for the given view angles
		Math3D.VectorCopy(GlState.r_newrefdef.vieworg, GlState.r_origin);

		Math3D.AngleVectors(GlState.r_newrefdef.viewangles, GlState.vpn, GlState.vright, GlState.vup);

		//	current viewcluster
		ModelLeaf leaf;
		if ((GlState.r_newrefdef.rdflags & Constants.RDF_NOWORLDMODEL) == 0) {
			GlState.r_oldviewcluster = GlState.r_viewcluster;
			GlState.r_oldviewcluster2 = GlState.r_viewcluster2;
			leaf = Mod_PointInLeaf(GlState.r_origin, GlState.r_worldmodel);
			GlState.r_viewcluster = GlState.r_viewcluster2 = leaf.cluster;

			// check above and below so crossing solid water doesn't draw wrong
			if (leaf.contents == 0) { // look down a bit
				Math3D.VectorCopy(GlState.r_origin, GlState.temp);
				GlState.temp[2] -= 16;
				leaf = Mod_PointInLeaf(GlState.temp, GlState.r_worldmodel);
				if ((leaf.contents & Constants.CONTENTS_SOLID) == 0 && (leaf.cluster != GlState.r_viewcluster2))
					GlState.r_viewcluster2 = leaf.cluster;
			}
			else { // look up a bit
				Math3D.VectorCopy(GlState.r_origin, GlState.temp);
				GlState.temp[2] += 16;
				leaf = Mod_PointInLeaf(GlState.temp, GlState.r_worldmodel);
				if ((leaf.contents & Constants.CONTENTS_SOLID) == 0 && (leaf.cluster != GlState.r_viewcluster2))
					GlState.r_viewcluster2 = leaf.cluster;
			}
		}

		for (int i = 0; i < 4; i++)
			GlState.v_blend[i] = GlState.r_newrefdef.blend[i];

		GlState.c_brush_polys = 0;
		GlState.c_alias_polys = 0;

		// clear out the portion of the screen that the NOWORLDMODEL defines
		if ((GlState.r_newrefdef.rdflags & Constants.RDF_NOWORLDMODEL) != 0) {
		  GlState.gl.glEnable(GlAdapter.GL_SCISSOR_TEST);
		  GlState.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
		  GlState.gl.glScissor(
				GlState.r_newrefdef.x,
				GlState.vid.height - GlState.r_newrefdef.height - GlState.r_newrefdef.y,
				GlState.r_newrefdef.width,
				GlState.r_newrefdef.height);
		  GlState.gl.glClear(GlAdapter.GL_COLOR_BUFFER_BIT | GlAdapter.GL_DEPTH_BUFFER_BIT);
		  GlState.gl.glClearColor(1.0f, 0.0f, 0.5f, 0.5f);
		  GlState.gl.glDisable(GlAdapter.GL_SCISSOR_TEST);
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

		xmin += - (2 * GlState.gl_state.camera_separation) / zNear;
		xmax += - (2 * GlState.gl_state.camera_separation) / zNear;

		GlState.gl.glFrustum(xmin, xmax, ymin, ymax, zNear, zFar);
	}

	/**
	 * R_SetupGL
	 */
	void R_SetupGL() {

		//
		// set up viewport
		//
		//int x = (int) Math.floor(r_newrefdef.x * vid.width / vid.width);
		int x = GlState.r_newrefdef.x;
		//int x2 = (int) Math.ceil((r_newrefdef.x + r_newrefdef.width) * vid.width / vid.width);
		int x2 = GlState.r_newrefdef.x + GlState.r_newrefdef.width;
		//int y = (int) Math.floor(vid.height - r_newrefdef.y * vid.height / vid.height);
		int y = GlState.vid.height - GlState.r_newrefdef.y;
		//int y2 = (int) Math.ceil(vid.height - (r_newrefdef.y + r_newrefdef.height) * vid.height / vid.height);
		int y2 = GlState.vid.height - (GlState.r_newrefdef.y + GlState.r_newrefdef.height);

		int w = x2 - x;
		int h = y - y2;

		GlState.gl.glViewport(x, y2, w, h);

		//
		// set up projection matrix
		//
		float screenaspect = (float) GlState.r_newrefdef.width / GlState.r_newrefdef.height;
		GlState.gl.glMatrixMode(GlAdapter.GL_PROJECTION);
		GlState.gl.glLoadIdentity();
		MYgluPerspective(GlState.r_newrefdef.fov_y, screenaspect, 4, 4096);

		GlState.gl.glCullFace(GlAdapter.GL_FRONT);

		GlState.gl.glMatrixMode(GlAdapter.GL_MODELVIEW);
		GlState.gl.glLoadIdentity();

		GlState.gl.glRotatef(-90, 1, 0, 0); // put Z going up
		GlState.gl.glRotatef(90, 0, 0, 1); // put Z going up
		GlState.gl.glRotatef(-GlState.r_newrefdef.viewangles[2], 1, 0, 0);
		GlState.gl.glRotatef(-GlState.r_newrefdef.viewangles[0], 0, 1, 0);
		GlState.gl.glRotatef(-GlState.r_newrefdef.viewangles[1], 0, 0, 1);
		GlState.gl.glTranslatef(-GlState.r_newrefdef.vieworg[0], -GlState.r_newrefdef.vieworg[1], -GlState.r_newrefdef.vieworg[2]);

		GlState.gl.glGetFloat(GlAdapter._GL_MODELVIEW_MATRIX, GlState.r_world_matrix);
		GlState.r_world_matrix.clear();

		//
		// set drawing parms
		//
		if (GlState.gl_cull.value != 0.0f)
		  GlState.gl.glEnable(GlAdapter.GL_CULL_FACE);
		else
		  GlState.gl.glDisable(GlAdapter.GL_CULL_FACE);

		GlState.gl.glDisable(GlAdapter.GL_BLEND);
//		gl.glDisable(GLAdapter.GL_ALPHA_TEST);
		GlState.gl.glEnable(GlAdapter.GL_DEPTH_TEST);
	}

	/**
	 * R_Clear
	 */
	void R_Clear() {
		if (GlState.gl_ztrick.value != 0.0f) {

			if (GlState.gl_clear.value != 0.0f) {
			  GlState.gl.glClear(GlAdapter.GL_COLOR_BUFFER_BIT);
			}

			GlState.trickframe++;
			if ((GlState.trickframe & 1) != 0) {
				GlState.gldepthmin = 0;
				GlState.gldepthmax = 0.49999f;
				GlState.gl.glDepthFunc(GlAdapter.GL_LEQUAL);
			}
			else {
				GlState.gldepthmin = 1;
				GlState.gldepthmax = 0.5f;
				GlState.gl.glDepthFunc(GlAdapter.GL_GEQUAL);
			}
		}
		else {
			if (GlState.gl_clear.value != 0.0f)
			  GlState.gl.glClear(GlAdapter.GL_COLOR_BUFFER_BIT | GlAdapter.GL_DEPTH_BUFFER_BIT);
			else
			  GlState.gl.glClear(GlAdapter.GL_DEPTH_BUFFER_BIT);

			GlState.gldepthmin = 0;
			GlState.gldepthmax = 1;
			GlState.gl.glDepthFunc(GlAdapter.GL_LEQUAL);
		}
		GlState.gl.glDepthRange(GlState.gldepthmin, GlState.gldepthmax);
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

		if (GlState.r_norefresh.value != 0.0f)
			return;

		GlState.r_newrefdef = fd;

		// included by cwei
		if (GlState.r_newrefdef == null) {
			Com.Error(Constants.ERR_DROP, "R_RenderView: refdef_t fd is null");
		}

		if (GlState.r_worldmodel == null && (GlState.r_newrefdef.rdflags & Constants.RDF_NOWORLDMODEL) == 0)
			Com.Error(Constants.ERR_DROP, "R_RenderView: NULL worldmodel");

		if (GlState.r_speeds.value != 0.0f) {
			GlState.c_brush_polys = 0;
			GlState.c_alias_polys = 0;
		}

		R_PushDlights();

		if (GlState.gl_finish.value != 0.0f)
		  GlState.gl.glFinish();

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

		if (GlState.r_speeds.value != 0.0f) {
			Window.Printf(
				Constants.PRINT_ALL,
				"%4i wpoly %4i epoly %i tex %i lmaps\n",
				new Vargs(4).add(GlState.c_brush_polys).add(GlState.c_alias_polys).add(GlState.c_visible_textures).add(GlState.c_visible_lightmaps));
		}
	}

	/**
	 * R_SetGL2D
	 */
	void R_SetGL2D() {
		// set 2D virtual screen size
	  GlState.gl.glViewport(0, 0, GlState.vid.width, GlState.vid.height);
	  GlState.gl.glMatrixMode(GlAdapter.GL_PROJECTION);
	  GlState.gl.glLoadIdentity();
	  GlState.gl.glOrtho(0, GlState.vid.width, GlState.vid.height, 0, -99999, 99999);
	  GlState.gl.glMatrixMode(GlAdapter.GL_MODELVIEW);
	  GlState.gl.glLoadIdentity();
	  GlState.gl.glDisable(GlAdapter.GL_DEPTH_TEST);
	  GlState.gl.glDisable(GlAdapter.GL_CULL_FACE);
	  GlState.gl.glDisable(GlAdapter.GL_BLEND);
//	  gl.glEnable(GLAdapter.GL_ALPHA_TEST);
	  GlState.gl.glColor4f(1, 1, 1, 1);
	}

	/**
	 *	R_SetLightLevel
	 */
	void R_SetLightLevel() {
		if ((GlState.r_newrefdef.rdflags & Constants.RDF_NOWORLDMODEL) != 0)
			return;

		// save off light value for server to look at (BIG HACK!)

		R_LightPoint(GlState.r_newrefdef.vieworg, GlState.light);

		// pick the greatest component, which should be the same
		// as the mono value returned by software
		if (GlState.light[0] > GlState.light[1]) {
			if (GlState.light[0] > GlState.light[2])
				GlState.r_lightlevel.value = 150 * GlState.light[0];
			else
				GlState.r_lightlevel.value = 150 * GlState.light[2];
		}
		else {
			if (GlState.light[1] > GlState.light[2])
				GlState.r_lightlevel.value = 150 * GlState.light[1];
			else
				GlState.r_lightlevel.value = 150 * GlState.light[2];
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
		GlState.r_lefthand = ConsoleVariables.Get("hand", "0", CVAR_USERINFO | CVAR_ARCHIVE);
		GlState.r_norefresh = ConsoleVariables.Get("r_norefresh", "0", 0);
		GlState.r_fullbright = ConsoleVariables.Get("r_fullbright", "0", 0);
		GlState.r_drawentities = ConsoleVariables.Get("r_drawentities", "1", 0);
		GlState.r_drawworld = ConsoleVariables.Get("r_drawworld", "1", 0);
		GlState.r_novis = ConsoleVariables.Get("r_novis", "0", 0);
		GlState.r_nocull = ConsoleVariables.Get("r_nocull", "0", 0);
		GlState.r_lerpmodels = ConsoleVariables.Get("r_lerpmodels", "1", 0);
		GlState.r_speeds = ConsoleVariables.Get("r_speeds", "0", 0);

		GlState.r_lightlevel = ConsoleVariables.Get("r_lightlevel", "1", 0);

		GlState.gl_nosubimage = ConsoleVariables.Get("gl_nosubimage", "0", 0);
		GlState.gl_allow_software = ConsoleVariables.Get("gl_allow_software", "0", 0);

		GlState.gl_particle_min_size = ConsoleVariables.Get("gl_particle_min_size", "2", CVAR_ARCHIVE);
		GlState.gl_particle_max_size = ConsoleVariables.Get("gl_particle_max_size", "40", CVAR_ARCHIVE);
		GlState.gl_particle_size = ConsoleVariables.Get("gl_particle_size", "40", CVAR_ARCHIVE);
		GlState.gl_particle_att_a = ConsoleVariables.Get("gl_particle_att_a", "0.01", CVAR_ARCHIVE);
		GlState.gl_particle_att_b = ConsoleVariables.Get("gl_particle_att_b", "0.0", CVAR_ARCHIVE);
		GlState.gl_particle_att_c = ConsoleVariables.Get("gl_particle_att_c", "0.01", CVAR_ARCHIVE);

		GlState.gl_modulate = ConsoleVariables.Get("gl_modulate", "1.5", CVAR_ARCHIVE);
		GlState.gl_log = ConsoleVariables.Get("gl_log", "0", 0);
		GlState.gl_bitdepth = ConsoleVariables.Get("gl_bitdepth", "0", 0);
		GlState.gl_mode = ConsoleVariables.Get("gl_mode", "3", CVAR_ARCHIVE); // 640x480
		GlState.gl_lightmap = ConsoleVariables.Get("gl_lightmap", "0", 0);
		GlState.gl_shadows = ConsoleVariables.Get("gl_shadows", "0", CVAR_ARCHIVE);
		GlState.gl_dynamic = ConsoleVariables.Get("gl_dynamic", "1", 0);
		GlState.gl_nobind = ConsoleVariables.Get("gl_nobind", "0", 0);
		GlState.gl_round_down = ConsoleVariables.Get("gl_round_down", "1", 0);
		GlState.gl_picmip = ConsoleVariables.Get("gl_picmip", "0", 0);
		GlState.gl_skymip = ConsoleVariables.Get("gl_skymip", "0", 0);
		GlState.gl_showtris = ConsoleVariables.Get("gl_showtris", "0", 0);
		GlState.gl_ztrick = ConsoleVariables.Get("gl_ztrick", "0", 0);
		GlState.gl_finish = ConsoleVariables.Get("gl_finish", "0", CVAR_ARCHIVE);
		GlState.gl_clear = ConsoleVariables.Get("gl_clear", "0", 0);
		GlState.gl_cull = ConsoleVariables.Get("gl_cull", "1", 0);
		GlState.gl_polyblend = ConsoleVariables.Get("gl_polyblend", "1", 0);
		GlState.gl_flashblend = ConsoleVariables.Get("gl_flashblend", "0", 0);
		GlState.gl_playermip = ConsoleVariables.Get("gl_playermip", "0", 0);
		GlState.gl_monolightmap = ConsoleVariables.Get("gl_monolightmap", "0", 0);
		GlState.gl_driver = ConsoleVariables.Get("gl_driver", "opengl32", CVAR_ARCHIVE);
		GlState.gl_texturemode = ConsoleVariables.Get("gl_texturemode", "GL_LINEAR_MIPMAP_NEAREST", CVAR_ARCHIVE);
		GlState.gl_texturealphamode = ConsoleVariables.Get("gl_texturealphamode", "default", CVAR_ARCHIVE);
		GlState.gl_texturesolidmode = ConsoleVariables.Get("gl_texturesolidmode", "default", CVAR_ARCHIVE);
		GlState.gl_lockpvs = ConsoleVariables.Get("gl_lockpvs", "0", 0);

		GlState.gl_vertex_arrays = ConsoleVariables.Get("gl_vertex_arrays", "1", CVAR_ARCHIVE);

		GlState.gl_ext_swapinterval = ConsoleVariables.Get("gl_ext_swapinterval", "1", CVAR_ARCHIVE);
		GlState.gl_ext_palettedtexture = ConsoleVariables.Get("gl_ext_palettedtexture", "0", CVAR_ARCHIVE);
		GlState.gl_ext_multitexture = ConsoleVariables.Get("gl_ext_multitexture", "1", CVAR_ARCHIVE);
		GlState.gl_ext_pointparameters = ConsoleVariables.Get("gl_ext_pointparameters", "1", CVAR_ARCHIVE);
		GlState.gl_ext_compiled_vertex_array = ConsoleVariables.Get("gl_ext_compiled_vertex_array", "1", CVAR_ARCHIVE);

		GlState.gl_drawbuffer = ConsoleVariables.Get("gl_drawbuffer", "GL_BACK", 0);
		GlState.gl_swapinterval = ConsoleVariables.Get("gl_swapinterval", "0", CVAR_ARCHIVE);

		GlState.gl_saturatelighting = ConsoleVariables.Get("gl_saturatelighting", "0", 0);

		GlState.gl_3dlabs_broken = ConsoleVariables.Get("gl_3dlabs_broken", "1", CVAR_ARCHIVE);

		GlState.vid_fullscreen = ConsoleVariables.Get("vid_fullscreen", "0", CVAR_ARCHIVE);
		GlState.vid_gamma = ConsoleVariables.Get("vid_gamma", "1.0", CVAR_ARCHIVE);
		GlState.vid_ref = ConsoleVariables.Get("vid_ref", "lwjgl", CVAR_ARCHIVE);

		Commands.addCommand("imagelist", new ExecutableCommand() {
			public void execute() {
				Images.GL_ImageList_f();
			}
		});

		Commands.addCommand("screenshot", new ExecutableCommand() {
			public void execute() {
				Misc.GL_ScreenShot_f();
			}
		});
		Commands.addCommand("modellist", new ExecutableCommand() {
			public void execute() {
				Mod_Modellist_f();
			}
		});
		Commands.addCommand("gl_strings", new ExecutableCommand() {
			public void execute() {
				Misc.GL_Strings_f();
			}
		});
	}

	/**
	 * R_SetMode
	 */
	protected boolean R_SetMode() {
		boolean fullscreen = (GlState.vid_fullscreen.value > 0.0f);

		GlState.vid_fullscreen.modified = false;
		GlState.gl_mode.modified = false;

		Dimension dim = new Dimension(GlState.vid.width, GlState.vid.height);

		int err; //  enum rserr_t
		if ((err = GLimp_SetMode(dim, (int) GlState.gl_mode.value, fullscreen)) == GlConstants.rserr_ok) {
			GlState.gl_state.prev_mode = (int) GlState.gl_mode.value;
		}
		else {
			if (err == GlConstants.rserr_invalid_fullscreen) {
				ConsoleVariables.SetValue("vid_fullscreen", 0);
				GlState.vid_fullscreen.modified = false;
				Window.Printf(Constants.PRINT_ALL, "ref_gl::R_SetMode() - fullscreen unavailable in this mode\n");
				if ((err = GLimp_SetMode(dim, (int) GlState.gl_mode.value, false)) == GlConstants.rserr_ok)
					return true;
			}
			else if (err == GlConstants.rserr_invalid_mode) {
				ConsoleVariables.SetValue("gl_mode", GlState.gl_state.prev_mode);
				GlState.gl_mode.modified = false;
				Window.Printf(Constants.PRINT_ALL, "ref_gl::R_SetMode() - invalid mode\n");
			}

			// try setting it back to something safe
			if ((err = GLimp_SetMode(dim, GlState.gl_state.prev_mode, false)) != GlConstants.rserr_ok) {
				Window.Printf(Constants.PRINT_ALL, "ref_gl::R_SetMode() - could not revert to safe mode\n");
				return false;
			}
		}
		return true;
	}

	/**
	 * R_Init
	 */
	protected boolean R_Init(int vid_xpos, int vid_ypos) {

		assert(GlConstants.SIN.length == 256) : "warpsin table bug";

		// fill r_turbsin
		for (int j = 0; j < 256; j++) {
			GlState.r_turbsin[j] = GlConstants.SIN[j] * 0.5f;
		}

		Window.Printf(Constants.PRINT_ALL, "ref_gl version: " + GlConstants.REF_VERSION + '\n');

		Draw_GetPalette();

		R_Register();

		// set our "safe" modes
		GlState.gl_state.prev_mode = 3;

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
		GlState.qglPointParameterfEXT = true;
		GlState.qglActiveTextureARB = true;
		GlState.GL_TEXTURE0 = GlAdapter.GL_TEXTURE0;
		GlState.GL_TEXTURE1 = GlAdapter.GL_TEXTURE1;

		if (!(GlState.qglActiveTextureARB))
			return false;

		Misc.GL_SetDefaultState();

		GL_InitImages();
		Mod_Init();
		Misc.R_InitParticleTexture();
		Draw_InitLocal();

		int err = GlState.gl.glGetError();
		if (err != GlAdapter.GL_NO_ERROR)
			Window.Printf(
				Constants.PRINT_ALL,
				"glGetError() = 0x%x\n\t%s\n",
				new Vargs(2).add(err).add("" + GlState.gl.glGetString(err)));

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

		GlState.gl_state.camera_separation = camera_separation;

		/*
		** change modes if necessary
		*/
		if (GlState.gl_mode.modified || GlState.vid_fullscreen.modified) {
			// FIXME: only restart if CDS is required
			ConsoleVariable ref;

			ref = ConsoleVariables.Get("vid_ref", "lwjgl", 0);
			ref.modified = true;
		}

		if (GlState.gl_log.modified) {
			GLimp_EnableLogging((GlState.gl_log.value != 0.0f));
			GlState.gl_log.modified = false;
		}

		if (GlState.gl_log.value != 0.0f) {
			GLimp_LogNewFrame();
		}

		/*
		** update 3Dfx gamma -- it is expected that a user will do a vid_restart
		** after tweaking this value
		*/
		if (GlState.vid_gamma.modified) {
			GlState.vid_gamma.modified = false;
		}

		GLimp_BeginFrame(camera_separation);

		/*
		** go into 2D mode
		*/
		GlState.gl.glViewport(0, 0, GlState.vid.width, GlState.vid.height);
		GlState.gl.glMatrixMode(GlAdapter.GL_PROJECTION);
		GlState.gl.glLoadIdentity();
		GlState.gl.glOrtho(0, GlState.vid.width, GlState.vid.height, 0, -99999, 99999);
		GlState.gl.glMatrixMode(GlAdapter.GL_MODELVIEW);
		GlState.gl.glLoadIdentity();
		GlState.gl.glDisable(GlAdapter.GL_DEPTH_TEST);
		GlState.gl.glDisable(GlAdapter.GL_CULL_FACE);
		GlState.gl.glDisable(GlAdapter.GL_BLEND);
//		gl.glEnable(GLAdapter.GL_ALPHA_TEST);
		GlState.gl.glColor4f(1, 1, 1, 1);

		/*
		** draw buffer stuff
		*/
		if (GlState.gl_drawbuffer.modified) {
			GlState.gl_drawbuffer.modified = false;

			if (GlState.camera_separation == 0 || !GlState.stereo_enabled) {
				if (GlState.gl_drawbuffer.string.equalsIgnoreCase("GL_FRONT"))
				  GlState.gl.glDrawBuffer(GlAdapter.GL_FRONT);
				else
				  GlState.gl.glDrawBuffer(GlAdapter.GL_BACK);
			}
		}

		/*
		** texturemode stuff
		*/
		if (GlState.gl_texturemode.modified) {
			Images.GL_TextureMode(GlState.gl_texturemode.string);
			GlState.gl_texturemode.modified = false;
		}

		if (GlState.gl_texturealphamode.modified) {
			Images.GL_TextureAlphaMode(GlState.gl_texturealphamode.string);
			GlState.gl_texturealphamode.modified = false;
		}

		if (GlState.gl_texturesolidmode.modified) {
			Images.GL_TextureSolidMode(GlState.gl_texturesolidmode.string);
			GlState.gl_texturesolidmode.modified = false;
		}

		/*
		** swapinterval stuff
		*/
		Misc.GL_UpdateSwapInterval();

		//
		// clear screen if desired
		//
		R_Clear();
	}

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
				GlState.r_rawpalette[i] = color;
			}
		}
		else {
			for (i = 0; i < 256; i++) {
				GlState.r_rawpalette[i] = QuakeImage.PALETTE_ABGR[i] | 0xff000000;
			}
		}
		GL_SetTexturePalette(GlState.r_rawpalette);

		GlState.gl.glClearColor(0, 0, 0, 0);
		GlState.gl.glClear(GlAdapter.GL_COLOR_BUFFER_BIT);
		GlState.gl.glClearColor(1f, 0f, 0.5f, 0.5f);
	}

	/**
	 * R_DrawBeam
	 */
	void R_DrawBeam(EntityType e) {
		GlState.oldorigin[0] = e.oldorigin[0];
		GlState.oldorigin[1] = e.oldorigin[1];
		GlState.oldorigin[2] = e.oldorigin[2];

		GlState.origin[0] = e.origin[0];
		GlState.origin[1] = e.origin[1];
		GlState.origin[2] = e.origin[2];

		GlState.normalized_direction[0] = GlState.direction[0] = GlState.oldorigin[0] - GlState.origin[0];
		GlState.normalized_direction[1] = GlState.direction[1] = GlState.oldorigin[1] - GlState.origin[1];
		GlState.normalized_direction[2] = GlState.direction[2] = GlState.oldorigin[2] - GlState.origin[2];

		if (Math3D.VectorNormalize(GlState.normalized_direction) == 0.0f)
			return;

		Math3D.PerpendicularVector(GlState.perpvec, GlState.normalized_direction);
		Math3D.VectorScale(GlState.perpvec, e.frame / 2, GlState.perpvec);

		for (int i = 0; i < 6; i++) {
			Math3D.RotatePointAroundVector(
				GlState.start_points[i],
				GlState.normalized_direction,
				GlState.perpvec,
				(360.0f / GlConstants.NUM_BEAM_SEGS) * i);

			Math3D.VectorAdd(GlState.start_points[i], GlState.origin, GlState.start_points[i]);
			Math3D.VectorAdd(GlState.start_points[i], GlState.direction, GlState.end_points[i]);
		}

		GlState.gl.glDisable(GlAdapter.GL_TEXTURE_2D);
		GlState.gl.glEnable(GlAdapter.GL_BLEND);
		GlState.gl.glDepthMask(false);

		float r = (QuakeImage.PALETTE_ABGR[e.skinnum & 0xFF]) & 0xFF;
		float g = (QuakeImage.PALETTE_ABGR[e.skinnum & 0xFF] >> 8) & 0xFF;
		float b = (QuakeImage.PALETTE_ABGR[e.skinnum & 0xFF] >> 16) & 0xFF;

		r *= 1 / 255.0f;
		g *= 1 / 255.0f;
		b *= 1 / 255.0f;

		GlState.gl.glColor4f(r, g, b, e.alpha);

		GlState.gl.glBegin(GlAdapter.GL_TRIANGLE_STRIP);
		
		float[] v;
		
		for (int i = 0; i < GlConstants.NUM_BEAM_SEGS; i++) {
			v = GlState.start_points[i];
			GlState.gl.glVertex3f(v[0], v[1], v[2]);
			v = GlState.end_points[i];
			GlState.gl.glVertex3f(v[0], v[1], v[2]);
			v = GlState.start_points[(i + 1) % GlConstants.NUM_BEAM_SEGS];
			GlState.gl.glVertex3f(v[0], v[1], v[2]);
			v = GlState.end_points[(i + 1) % GlConstants.NUM_BEAM_SEGS];
			GlState.gl.glVertex3f(v[0], v[1], v[2]);
		}
		GlState.gl.glEnd();

		GlState.gl.glEnable(GlAdapter.GL_TEXTURE_2D);
		GlState.gl.glDisable(GlAdapter.GL_BLEND);
		GlState.gl.glDepthMask(true);
	}
}
