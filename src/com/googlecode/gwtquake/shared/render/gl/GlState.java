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

import com.googlecode.gwtquake.shared.client.Dimension;
import com.googlecode.gwtquake.shared.client.EntityType;
import com.googlecode.gwtquake.shared.client.RendererState;
import com.googlecode.gwtquake.shared.game.ConsoleVariable;
import com.googlecode.gwtquake.shared.game.Plane;
import com.googlecode.gwtquake.shared.render.DisplayMode;
import com.googlecode.gwtquake.shared.render.GlAdapter;
import com.googlecode.gwtquake.shared.render.ModelImage;
import com.googlecode.gwtquake.shared.render.ModelSurface;
import com.googlecode.gwtquake.shared.render.RendererModel;

public class GlState 
{
	public static float inverse_intensity;
	public static boolean fullscreen;

	public static int prev_mode;

	public static int lightmap_textures;

	public static int currenttextures[]= {0,0};
	public static int currenttmu;

	public static float camera_separation;
	public static boolean stereo_enabled;

	public static byte originalRedGammaTable[]= new byte [256];
	public static byte originalGreenGammaTable[]= new byte [256];
	public static byte originalBlueGammaTable[]= new byte [256];
  public static GlAdapter gl;
  // window position on the screen
  static int window_xpos;
  // IMPORTED FUNCTIONS
  static protected DisplayMode oldDisplayMode;
  static int window_ypos;
  static protected Dimension vid = new Dimension();
  static protected ConsoleVariable vid_fullscreen;
  static int c_visible_lightmaps;
  static	int c_visible_textures;
  static int registration_sequence;
  // this a hack for function pointer test
  // default disabled
  static boolean qglActiveTextureARB = false;
  static boolean qglPointParameterfEXT = false;
  static int GL_TEXTURE0 = GlAdapter.GL_TEXTURE0;
  static int GL_TEXTURE1 = GlAdapter.GL_TEXTURE1;
  static RendererModel r_worldmodel;
  static float gldepthmax;
  static Plane frustum[] = { new Plane(), new Plane(), new Plane(), new Plane()};
  static RendererModel currentmodel;
  static ModelImage r_notexture; // use for bad textures
  static float gldepthmin;
  static ModelImage r_particletexture; // little dot for particles
  static EntityType currententity;
  static int r_visframecount; // bumped when going to a new PVS
  static GlState gl_state = new GlState();
  static int r_framecount; // used for dlight push checking
  static int c_brush_polys;
  static int c_alias_polys;
  static float v_blend[] = { 0, 0, 0, 0 }; // final blending color
  //
  //	   view origin
  //
  static float[] vup = { 0, 0, 0 };
  static float[] vpn = { 0, 0, 0 };
  static float[] vright = { 0, 0, 0 };
  static float[] r_origin = { 0, 0, 0 };
  //float r_world_matrix[] = new float[16];
  static FloatBuffer r_world_matrix;
  static float r_base_world_matrix[] = new float[16];
  //
  //	   screen size info
  //
  static RendererState r_newrefdef = new RendererState();
  static ConsoleVariable gl_nosubimage;
  static ConsoleVariable gl_allow_software;
  static ConsoleVariable gl_vertex_arrays;
  static ConsoleVariable gl_particle_min_size;
  static ConsoleVariable gl_particle_max_size;
  static ConsoleVariable gl_particle_size;
  static ConsoleVariable gl_particle_att_a;
  static ConsoleVariable gl_particle_att_b;
  static ConsoleVariable gl_particle_att_c;
  static ConsoleVariable gl_ext_swapinterval;
  static ConsoleVariable gl_ext_palettedtexture;
  static ConsoleVariable gl_ext_multitexture;
  static ConsoleVariable gl_ext_pointparameters;
  static ConsoleVariable gl_ext_compiled_vertex_array;
  static ConsoleVariable gl_log;
  static ConsoleVariable gl_bitdepth;
  static ConsoleVariable gl_drawbuffer;
  static ConsoleVariable gl_driver;
  static ConsoleVariable gl_lightmap;
  static ConsoleVariable gl_shadows;
  static ConsoleVariable gl_mode;
  static ConsoleVariable gl_dynamic;
  static ConsoleVariable gl_monolightmap;
  static ConsoleVariable gl_modulate;
  static ConsoleVariable gl_nobind;
  static ConsoleVariable gl_round_down;
  static ConsoleVariable gl_picmip;
  static ConsoleVariable gl_skymip;
  static ConsoleVariable gl_showtris;
  static ConsoleVariable gl_ztrick;
  static ConsoleVariable gl_finish;
  static ConsoleVariable gl_clear;
  static ConsoleVariable gl_cull;
  static ConsoleVariable gl_polyblend;
  static ConsoleVariable gl_flashblend;
  static ConsoleVariable gl_playermip;
  static ConsoleVariable gl_saturatelighting;
  static ConsoleVariable gl_swapinterval;
  static ConsoleVariable gl_texturemode;
  static ConsoleVariable gl_texturealphamode;
  static ConsoleVariable gl_texturesolidmode;
  static ConsoleVariable gl_lockpvs;
  static ConsoleVariable gl_3dlabs_broken;
  static int r_viewcluster;
  static int r_viewcluster2;
  static int r_oldviewcluster;
  static int r_oldviewcluster2;
  static ConsoleVariable r_norefresh;
  static ConsoleVariable r_drawentities;
  static ConsoleVariable r_drawworld;
  static ConsoleVariable r_speeds;
  static ConsoleVariable r_fullbright;
  static ConsoleVariable r_novis;
  static ConsoleVariable r_nocull;
  static ConsoleVariable r_lerpmodels;
  static ConsoleVariable r_lefthand;
  static ConsoleVariable r_lightlevel;
  // FIXME: This is a HACK to get the client's light level
  static ConsoleVariable vid_gamma;
  static ConsoleVariable vid_ref;
  // stack variable
  static  float[] light = { 0, 0, 0 };
  // stack variable
  static  float[] point = { 0, 0, 0 };
  // stack variable
  static  float[] shadelight = { 0, 0, 0 };
  // stack variable 
  static  float[] up = { 0, 0, 0 };
  static float[] right = { 0, 0, 0 };
  // stack variable
  static float[] temp = {0, 0, 0};
  static int trickframe = 0;
  static float[] r_turbsin = new float[256];
  static int[] r_rawpalette = new int[256];
  static float[][] start_points = new float[GlConstants.NUM_BEAM_SEGS][3];
  // array of vec3_t
  static float[][] end_points = new float[GlConstants.NUM_BEAM_SEGS][3]; // array of vec3_t
  // stack variable
  static final float[] perpvec = { 0, 0, 0 }; // vec3_t
  static final float[] direction = { 0, 0, 0 }; // vec3_t
  static final float[] normalized_direction = { 0, 0, 0 }; // vec3_t
  static final float[] oldorigin = { 0, 0, 0 }; // vec3_t
  static final float[] origin = { 0, 0, 0 }; // vec3_t
  static int r_dlightframecount;
  static String skyname;
  static float	skyrotate;
  static float[] skyaxis = {0, 0, 0};
  static ModelImage[] sky_images = new ModelImage[6];
  static ModelSurface	warpface;
  static float[] dists = new float[GlConstants.MAX_CLIP_VERTS];

}
