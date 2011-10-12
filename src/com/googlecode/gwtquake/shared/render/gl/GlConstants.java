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

import com.googlecode.gwtquake.shared.render.GlAdapter;


/**
 * Base
 * 
 * @author dsanders/cwei
 */
public abstract class GlConstants  {
    
  //  static final int GL_COLOR_INDEX8_EXT = GLAdapter._GL_COLOR_INDEX;
    
    static final String REF_VERSION = "GL 0.01";
    
    // up / down
    static final int PITCH = 0;
    
    // left / right
    static final int YAW = 1;
    
    // fall over
    static final int ROLL = 2;
    
    // enum modtype_t
    static final int mod_bad = 0;
    
    static final int mod_brush = 1;
    
    static final int mod_sprite = 2;
    
    static final int mod_alias = 3;
    
    static final int TEXNUM_LIGHTMAPS = 1024;
    
    static final int TEXNUM_SCRAPS = 1152;
    
    static final int TEXNUM_IMAGES = 1154;
    
    static final int MAX_GLTEXTURES = 1024;
    
    static final int MAX_LBM_HEIGHT = 480;
    
    static final float BACKFACE_EPSILON = 0.01f;
    
    /*
     * * GL config stuff
     */
    static final int GL_RENDERER_VOODOO = 0x00000001;
    
    static final int GL_RENDERER_VOODOO2 = 0x00000002;
    
    static final int GL_RENDERER_VOODOO_RUSH = 0x00000004;
    
    static final int GL_RENDERER_BANSHEE = 0x00000008;
    
    static final int GL_RENDERER_3DFX = 0x0000000F;
    
    static final int GL_RENDERER_PCX1 = 0x00000010;
    
    static final int GL_RENDERER_PCX2 = 0x00000020;
    
    static final int GL_RENDERER_PMX = 0x00000040;
    
    static final int GL_RENDERER_POWERVR = 0x00000070;
    
    static final int GL_RENDERER_PERMEDIA2 = 0x00000100;
    
    static final int GL_RENDERER_GLINT_MX = 0x00000200;
    
    static final int GL_RENDERER_GLINT_TX = 0x00000400;
    
    static final int GL_RENDERER_3DLABS_MISC = 0x00000800;
    
    static final int GL_RENDERER_3DLABS = 0x00000F00;
    
    static final int GL_RENDERER_REALIZM = 0x00001000;
    
    static final int GL_RENDERER_REALIZM2 = 0x00002000;
    
    static final int GL_RENDERER_INTERGRAPH = 0x00003000;
    
    static final int GL_RENDERER_3DPRO = 0x00004000;
    
    static final int GL_RENDERER_REAL3D = 0x00008000;
    
    static final int GL_RENDERER_RIVA128 = 0x00010000;
    
    static final int GL_RENDERER_DYPIC = 0x00020000;
    
    static final int GL_RENDERER_V1000 = 0x00040000;
    
    static final int GL_RENDERER_V2100 = 0x00080000;
    
    static final int GL_RENDERER_V2200 = 0x00100000;
    
    static final int GL_RENDERER_RENDITION = 0x001C0000;
    
    static final int GL_RENDERER_O2 = 0x00100000;
    
    static final int GL_RENDERER_IMPACT = 0x00200000;
    
    static final int GL_RENDERER_RE = 0x00400000;
    
    static final int GL_RENDERER_IR = 0x00800000;
    
    static final int GL_RENDERER_SGI = 0x00F00000;
    
    static final int GL_RENDERER_MCD = 0x01000000;
    
    static final int GL_RENDERER_OTHER = 0x80000000;

    static final int DLIGHT_CUTOFF = 64;

    // enum rserr_t
    protected static final int rserr_ok = 0;

    protected static final int rserr_invalid_fullscreen = 1;

    protected static final int rserr_invalid_mode = 2;

    protected static final int rserr_unknown = 3;

    /*
    ==================
    R_InitParticleTexture
    ==================
    */
    static byte[][] dottexture =
    {
    	{0,0,0,0,0,0,0,0},
    	{0,0,1,1,0,0,0,0},
    	{0,1,1,1,1,0,0,0},
    	{0,1,1,1,1,0,0,0},
    	{0,0,1,1,0,0,0,0},
    	{0,0,0,0,0,0,0,0},
    	{0,0,0,0,0,0,0,0},
    	{0,0,0,0,0,0,0,0},
    };

    private final static int TGA_HEADER_SIZE = 18;

    static final int NUM_BEAM_SEGS = 6;

    // warpsin.h
    public static final float[] SIN = {
    	0f, 0.19633f, 0.392541f, 0.588517f, 0.784137f, 0.979285f, 1.17384f, 1.3677f,
    	 1.56072f, 1.75281f, 1.94384f, 2.1337f, 2.32228f, 2.50945f, 2.69512f, 2.87916f,
    	 3.06147f, 3.24193f, 3.42044f, 3.59689f, 3.77117f, 3.94319f, 4.11282f, 4.27998f,
    	 4.44456f, 4.60647f, 4.76559f, 4.92185f, 5.07515f, 5.22538f, 5.37247f, 5.51632f,
    	 5.65685f, 5.79398f, 5.92761f, 6.05767f, 6.18408f, 6.30677f, 6.42566f, 6.54068f,
    	 6.65176f, 6.75883f, 6.86183f, 6.9607f, 7.05537f, 7.14579f, 7.23191f, 7.31368f,
    	 7.39104f, 7.46394f, 7.53235f, 7.59623f, 7.65552f, 7.71021f, 7.76025f, 7.80562f,
    	 7.84628f, 7.88222f, 7.91341f, 7.93984f, 7.96148f, 7.97832f, 7.99036f, 7.99759f,
    	 8f, 7.99759f, 7.99036f, 7.97832f, 7.96148f, 7.93984f, 7.91341f, 7.88222f,
    	 7.84628f, 7.80562f, 7.76025f, 7.71021f, 7.65552f, 7.59623f, 7.53235f, 7.46394f,
    	 7.39104f, 7.31368f, 7.23191f, 7.14579f, 7.05537f, 6.9607f, 6.86183f, 6.75883f,
    	 6.65176f, 6.54068f, 6.42566f, 6.30677f, 6.18408f, 6.05767f, 5.92761f, 5.79398f,
    	 5.65685f, 5.51632f, 5.37247f, 5.22538f, 5.07515f, 4.92185f, 4.76559f, 4.60647f,
    	 4.44456f, 4.27998f, 4.11282f, 3.94319f, 3.77117f, 3.59689f, 3.42044f, 3.24193f,
    	 3.06147f, 2.87916f, 2.69512f, 2.50945f, 2.32228f, 2.1337f, 1.94384f, 1.75281f,
    	 1.56072f, 1.3677f, 1.17384f, 0.979285f, 0.784137f, 0.588517f, 0.392541f, 0.19633f,
    	 9.79717e-16f, -0.19633f, -0.392541f, -0.588517f, -0.784137f, -0.979285f, -1.17384f, -1.3677f,
    	 -1.56072f, -1.75281f, -1.94384f, -2.1337f, -2.32228f, -2.50945f, -2.69512f, -2.87916f,
    	 -3.06147f, -3.24193f, -3.42044f, -3.59689f, -3.77117f, -3.94319f, -4.11282f, -4.27998f,
    	 -4.44456f, -4.60647f, -4.76559f, -4.92185f, -5.07515f, -5.22538f, -5.37247f, -5.51632f,
    	 -5.65685f, -5.79398f, -5.92761f, -6.05767f, -6.18408f, -6.30677f, -6.42566f, -6.54068f,
    	 -6.65176f, -6.75883f, -6.86183f, -6.9607f, -7.05537f, -7.14579f, -7.23191f, -7.31368f,
    	 -7.39104f, -7.46394f, -7.53235f, -7.59623f, -7.65552f, -7.71021f, -7.76025f, -7.80562f,
    	 -7.84628f, -7.88222f, -7.91341f, -7.93984f, -7.96148f, -7.97832f, -7.99036f, -7.99759f,
    	 -8f, -7.99759f, -7.99036f, -7.97832f, -7.96148f, -7.93984f, -7.91341f, -7.88222f,
    	 -7.84628f, -7.80562f, -7.76025f, -7.71021f, -7.65552f, -7.59623f, -7.53235f, -7.46394f,
    	 -7.39104f, -7.31368f, -7.23191f, -7.14579f, -7.05537f, -6.9607f, -6.86183f, -6.75883f,
    	 -6.65176f, -6.54068f, -6.42566f, -6.30677f, -6.18408f, -6.05767f, -5.92761f, -5.79398f,
    	 -5.65685f, -5.51632f, -5.37247f, -5.22538f, -5.07515f, -4.92185f, -4.76559f, -4.60647f,
    	 -4.44456f, -4.27998f, -4.11282f, -3.94319f, -3.77117f, -3.59689f, -3.42044f, -3.24193f,
    	 -3.06147f, -2.87916f, -2.69512f, -2.50945f, -2.32228f, -2.1337f, -1.94384f, -1.75281f,
    	 -1.56072f, -1.3677f, -1.17384f, -0.979285f, -0.784137f, -0.588517f, -0.392541f, -0.19633f
    };

    static final int SUBDIVIDE_SIZE = 64;

    // =========================================================
    static final float TURBSCALE = (float)(256.0f / (2 * Math.PI));

    static final float ON_EPSILON = 0.1f; // point on plane side epsilon

    static final int MAX_CLIP_VERTS = 64;

    // g_mesh.c: triangle model functions
    /*
      =============================================================
    
        ALIAS MODELS
    
      =============================================================
     */
    // precalculated dot products for quantized angles
    static final int SHADEDOT_QUANT = 16;

    static final int NUMVERTEXNORMALS =	162;
}
