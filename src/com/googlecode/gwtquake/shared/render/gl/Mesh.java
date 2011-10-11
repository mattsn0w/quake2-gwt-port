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
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import com.googlecode.gwtquake.shared.client.EntityType;
import com.googlecode.gwtquake.shared.client.Window;
import com.googlecode.gwtquake.shared.common.Constants;
import com.googlecode.gwtquake.shared.common.QuakeFiles;
import com.googlecode.gwtquake.shared.render.GlAdapter;
import com.googlecode.gwtquake.shared.render.ModelImage;
import com.googlecode.gwtquake.shared.util.Math3D;

/**
 * Mesh
 *  
 * @author cwei
 */
public abstract class Mesh extends Light {

  // g_mesh.c: triangle model functions
  /*
	=============================================================

	  ALIAS MODELS

	=============================================================
   */
  // precalculated dot products for quantized angles
  static final int SHADEDOT_QUANT = 16;

  static final int NUMVERTEXNORMALS =	162;

  float[][] r_avertexnormals = Anorms.VERTEXNORMALS;
  float[] shadevector = {0, 0, 0};
  float[] shadelight = {0, 0, 0};


  float[][]	r_avertexnormal_dots = Anorms.VERTEXNORMAL_DOTS;

  float[] shadedots = r_avertexnormal_dots[0];

  /**
   * GL_LerpVerts
   * @param nverts
   * @param ov
   * @param verts
   * @param move
   * @param frontv
   * @param backv
   */
  void GL_LerpVerts(int nverts, int[] ov, int[] v, float[] move, float[] frontv, float[] backv )
  {
    FloatBuffer lerp = vertexArrayBuf;
    lerp.limit((nverts << 2) - nverts); // nverts * 3

    int ovv, vv;
    //PMM -- added RF_SHELL_DOUBLE, RF_SHELL_HALF_DAM
    if ( (currententity.flags & ( Constants.RF_SHELL_RED | Constants.RF_SHELL_GREEN | Constants.RF_SHELL_BLUE | Constants.RF_SHELL_DOUBLE | Constants.RF_SHELL_HALF_DAM)) != 0 )
    {
      float[] normal;
      int j = 0;
      for (int i=0 ; i < nverts; i++/* , v++, ov++, lerp+=4 */)
      {
        vv = v[i];
        normal = r_avertexnormals[(vv >>> 24 ) & 0xFF];
        ovv = ov[i];
        lerp.put(j, move[0] + (ovv & 0xFF)* backv[0] + (vv & 0xFF) * frontv[0] + normal[0] * Constants.POWERSUIT_SCALE);
        lerp.put(j + 1, move[1] + ((ovv >>> 8) & 0xFF) * backv[1] + ((vv >>> 8) & 0xFF) * frontv[1] + normal[1] * Constants.POWERSUIT_SCALE);
        lerp.put(j + 2, move[2] + ((ovv >>> 16) & 0xFF) * backv[2] + ((vv >>> 16) & 0xFF) * frontv[2] + normal[2] * Constants.POWERSUIT_SCALE); 
        j += 3;
      }
    }
    else
    {
      int j = 0;
      for (int i=0 ; i < nverts; i++ /* , v++, ov++, lerp+=4 */)
      {
        ovv = ov[i];
        vv = v[i];

        lerp.put(j, move[0] + (ovv & 0xFF)* backv[0] + (vv & 0xFF)*frontv[0]);
        lerp.put(j + 1, move[1] + ((ovv >>> 8) & 0xFF)* backv[1] + ((vv >>> 8) & 0xFF)*frontv[1]);
        lerp.put(j + 2, move[2] + ((ovv >>> 16) & 0xFF)* backv[2] + ((vv >>> 16) & 0xFF)*frontv[2]);
        j += 3;
      }
    }
  }


  FloatBuffer colorArrayBuf;// = gl.createFloatBuffer(qfiles.MAX_VERTS * 4);
  FloatBuffer vertexArrayBuf;// = gl.createFloatBuffer(qfiles.MAX_VERTS * 3);
  FloatBuffer textureArrayBuf;// = gl.createFloatBuffer(qfiles.MAX_VERTS * 2);

  FloatBuffer colorArrayBuf2;// = gl.createFloatBuffer(qfiles.MAX_VERTS * 4);
  FloatBuffer vertexArrayBuf2;// = gl.createFloatBuffer(qfiles.MAX_VERTS * 3);
  FloatBuffer textureArrayBuf2;// = gl.createFloatBuffer(qfiles.MAX_VERTS * 2);


  @Override
  public void init() {
    super.init();
    colorArrayBuf = gl.createFloatBuffer(QuakeFiles.MAX_VERTS * 4);
    vertexArrayBuf = gl.createFloatBuffer(QuakeFiles.MAX_VERTS * 3);
    textureArrayBuf = gl.createFloatBuffer(QuakeFiles.MAX_VERTS * 2);


    int FACTOR = 4;

    colorArrayBuf2 = gl.createFloatBuffer(QuakeFiles.MAX_VERTS * 4 *FACTOR);
    vertexArrayBuf2 = gl.createFloatBuffer(QuakeFiles.MAX_VERTS * 3 * FACTOR);
    textureArrayBuf2 = gl.createFloatBuffer(QuakeFiles.MAX_VERTS * 2 * FACTOR);

  }

  boolean isFilled = false;
  float[] tmpVec = {0, 0, 0};
  float[][] vectors = {
      {0, 0, 0}, {0, 0, 0}, {0, 0, 0} // 3 mal vec3_t
  };

  // stack variable
  private final float[] move = {0, 0, 0}; // vec3_t		
  private final float[] frontv = {0, 0, 0}; // vec3_t
  private final float[] backv = {0, 0, 0}; // vec3_t
  /**
   * GL_DrawAliasFrameLerp
   * 
   * interpolates between two frames and origins
   * FIXME: batch lerp all vertexes
   */
  void GL_DrawAliasFrameLerp(QuakeFiles.dmdl_t paliashdr, float backlerp)
  {
    QuakeFiles.daliasframe_t frame = paliashdr.aliasFrames[currententity.frame];

    int[] verts = frame.verts;

    QuakeFiles.daliasframe_t oldframe = paliashdr.aliasFrames[currententity.oldframe];

    int[] ov = oldframe.verts;

    float	alpha;
    if ((currententity.flags & Constants.RF_TRANSLUCENT) != 0)
      alpha = currententity.alpha;
    else
      alpha = 1.0f;

    // PMM - added double shell
    if ( (currententity.flags & ( Constants.RF_SHELL_RED | Constants.RF_SHELL_GREEN | Constants.RF_SHELL_BLUE | Constants.RF_SHELL_DOUBLE | Constants.RF_SHELL_HALF_DAM)) != 0)
      gl.glDisable( GlAdapter.GL_TEXTURE_2D );

    float frontlerp = 1.0f - backlerp;

    // move should be the delta back to the previous frame * backlerp
    Math3D.VectorSubtract (currententity.oldorigin, currententity.origin, frontv);
    Math3D.AngleVectors (currententity.angles, vectors[0], vectors[1], vectors[2]);

    move[0] = Math3D.DotProduct (frontv, vectors[0]);	// forward
    move[1] = -Math3D.DotProduct (frontv, vectors[1]);	// left
    move[2] = Math3D.DotProduct (frontv, vectors[2]);	// up

    Math3D.VectorAdd (move, oldframe.translate, move);

    for (int i=0 ; i<3 ; i++)
    {
      move[i] = backlerp*move[i] + frontlerp*frame.translate[i];
      frontv[i] = frontlerp*frame.scale[i];
      backv[i] = backlerp*oldframe.scale[i];
    }

    // ab hier wird optimiert

    GL_LerpVerts( paliashdr.num_xyz, ov, verts, move, frontv, backv );

    int num_xyz = paliashdr.num_xyz;
    vertexArrayBuf.limit(num_xyz * 3);

    //gl.glEnableClientState( GLAdapter.GL_VERTEX_ARRAY );
    gl.glVertexPointer( 3, 0, vertexArrayBuf );

    // PMM - added double damage shell
    if ( (currententity.flags & ( Constants.RF_SHELL_RED | Constants.RF_SHELL_GREEN | Constants.RF_SHELL_BLUE | Constants.RF_SHELL_DOUBLE | Constants.RF_SHELL_HALF_DAM)) != 0)
    {
      gl.glColor4f( shadelight[0], shadelight[1], shadelight[2], alpha );
    }
    else
    {
      gl.glEnableClientState( GlAdapter.GL_COLOR_ARRAY );

      FloatBuffer color = colorArrayBuf;
      color.limit(num_xyz * 4);

      gl.glColorPointer( 4, 0, color );

      //
      // pre light everything
      //
      float l;
      int j = 0;
      for (int i = 0; i < num_xyz; i++ )
      {
        l = shadedots[(verts[i] >>> 24) & 0xFF];
        color.put(j,  l * shadelight[0]);
        color.put(j + 1, l * shadelight[1]);
        color.put(j + 2, l * shadelight[2]);
        color.put(j + 3, alpha);
        j += 4;
      }
    }

    gl.glClientActiveTexture(GL_TEXTURE0);
    FloatBuffer dstTextureCoords = textureArrayBuf;

    gl.glTexCoordPointer( 2, 0, dstTextureCoords);
    gl.glEnableClientState( GlAdapter.GL_TEXTURE_COORD_ARRAY);

    int pos = 0;
    int[] counts = paliashdr.counts;

    ShortBuffer srcIndexBuf = null;

    FloatBuffer srcTextureCoords = paliashdr.textureCoordBuf;

    int dstIndex = 0;
    int srcIndex = 0;
    int count;
    int mode;
    int size = counts.length;
    for (int j = 0; j < size; j++) {
      dstTextureCoords.limit(num_xyz * 2);

      // get the vertex count and primitive type
      count = counts[j];
      if (count == 0)
        break;		// done

      srcIndexBuf = paliashdr.indexElements[j];

      mode = GlAdapter.GL_TRIANGLE_STRIP;
      if (count < 0) {
        mode = GlAdapter.GL_TRIANGLE_FAN;
        count = -count;
      }
      srcIndex = pos << 1;
      srcIndex--;

      int minIdx = 99999; 
      int maxIdx = 0;

      for (int k = 0; k < count; k++) {
        dstIndex = srcIndexBuf.get(k) << 1;
        if (dstIndex < minIdx) {
          minIdx = dstIndex;
        } 
        if (dstIndex > maxIdx) {
          maxIdx = dstIndex;
        }
        dstTextureCoords.put(dstIndex, srcTextureCoords.get(++srcIndex));
        dstTextureCoords.put(++dstIndex, srcTextureCoords.get(++srcIndex));
      }

      //gl.updatTCBuffer(dstTextureCoords, minIdx, maxIdx - minIdx + 2);

      dstTextureCoords.limit(maxIdx + 2);
      gl.glTexCoordPointer( 2, 0, dstTextureCoords);

      gl.glDrawElements(mode, srcIndexBuf);
      pos += count;
    }

    // PMM - added double damage shell
    if ( (currententity.flags & ( Constants.RF_SHELL_RED | Constants.RF_SHELL_GREEN | Constants.RF_SHELL_BLUE | Constants.RF_SHELL_DOUBLE | Constants.RF_SHELL_HALF_DAM)) != 0 )
      gl.glEnable( GlAdapter.GL_TEXTURE_2D );

    gl.glDisableClientState( GlAdapter.GL_COLOR_ARRAY );
  }


  /**
   * GL_DrawAliasFrameLerp with drawArrays
   */
  void GL_DrawAliasFrameLerpDA(QuakeFiles.dmdl_t paliashdr, float backlerp)
  {
    QuakeFiles.daliasframe_t frame = paliashdr.aliasFrames[currententity.frame];

    int[] verts = frame.verts;

    QuakeFiles.daliasframe_t oldframe = paliashdr.aliasFrames[currententity.oldframe];

    int[] ov = oldframe.verts;

    float	alpha;
    if ((currententity.flags & Constants.RF_TRANSLUCENT) != 0)
      alpha = currententity.alpha;
    else
      alpha = 1.0f;

    // PMM - added double shell
    if ( (currententity.flags & ( Constants.RF_SHELL_RED | Constants.RF_SHELL_GREEN | Constants.RF_SHELL_BLUE | Constants.RF_SHELL_DOUBLE | Constants.RF_SHELL_HALF_DAM)) != 0)
      gl.glDisable( GlAdapter.GL_TEXTURE_2D );

    float frontlerp = 1.0f - backlerp;

    // move should be the delta back to the previous frame * backlerp
    Math3D.VectorSubtract (currententity.oldorigin, currententity.origin, frontv);
    Math3D.AngleVectors (currententity.angles, vectors[0], vectors[1], vectors[2]);

    move[0] = Math3D.DotProduct (frontv, vectors[0]);	// forward
    move[1] = -Math3D.DotProduct (frontv, vectors[1]);	// left
    move[2] = Math3D.DotProduct (frontv, vectors[2]);	// up

    Math3D.VectorAdd (move, oldframe.translate, move);

    for (int i=0 ; i<3 ; i++)
    {
      move[i] = backlerp*move[i] + frontlerp*frame.translate[i];
      frontv[i] = frontlerp*frame.scale[i];
      backv[i] = backlerp*oldframe.scale[i];
    }

    // ab hier wird optimiert

    GL_LerpVerts( paliashdr.num_xyz, ov, verts, move, frontv, backv );

    int num_xyz = paliashdr.num_xyz;
    FloatBuffer vertices = vertexArrayBuf;

    // PMM - added double damage shell
    boolean hasColorArray;
    FloatBuffer color = colorArrayBuf;
    hasColorArray = (currententity.flags & ( Constants.RF_SHELL_RED | Constants.RF_SHELL_GREEN | Constants.RF_SHELL_BLUE | Constants.RF_SHELL_DOUBLE | Constants.RF_SHELL_HALF_DAM)) == 0;
    if (hasColorArray) {
      //
      // pre light everything
      //
      float l;
      int j = 0;
      for (int i = 0; i < num_xyz; i++ )
      {
        l = shadedots[(verts[i] >>> 24) & 0xFF];
        color.put(j,  l * shadelight[0]);
        color.put(j + 1, l * shadelight[1]);
        color.put(j + 2, l * shadelight[2]);
        color.put(j + 3, alpha);
        j += 4;
      } 
    } else {
      gl.glColor4f( shadelight[0], shadelight[1], shadelight[2], alpha );
    }

    int pos = 0;
    int[] counts = paliashdr.counts;

    FloatBuffer dstVertexCoords = vertexArrayBuf2;
    FloatBuffer dstColors = colorArrayBuf2;

    dstVertexCoords.clear();
    dstColors.clear();

    int count;
    int mode;
    int size = counts.length;
    for (int j = 0; j < size; j++) {
      // get the vertex count and primitive type
      count = counts[j];
      if (count == 0)
        break;		// done

      ShortBuffer srcIndexBuf = paliashdr.indexElements[j];

      if (count < 0) {
        count = -count;
      }

      for (int k = 0; k < count; k++) {
        int srcIndex = srcIndexBuf.get(k);
        if (hasColorArray) {
          int cSrcIndex = srcIndex * 4;
          dstColors.put(color.get(cSrcIndex));
          dstColors.put(color.get(cSrcIndex+1));
          dstColors.put(color.get(cSrcIndex+2));
          dstColors.put(color.get(cSrcIndex+3));
        }
        int vSrcIndex = srcIndex * 3;
        dstVertexCoords.put(vertices.get(vSrcIndex));
        dstVertexCoords.put(vertices.get(vSrcIndex+1));
        dstVertexCoords.put(vertices.get(vSrcIndex+2));
      }

      //gl.updatTCBuffer(dstTextureCoords, minIdx, maxIdx - minIdx + 2);
      pos += count;
    }

    if (hasColorArray) {
      gl.glEnableClientState( GlAdapter.GL_COLOR_ARRAY );
      dstColors.flip();
      gl.glColorPointer(4, 0, dstColors);
    }

    gl.glClientActiveTexture(GL_TEXTURE0);	
    gl.glEnableClientState( GlAdapter.GL_TEXTURE_COORD_ARRAY);

    FloatBuffer tc0 = paliashdr.textureCoordBuf;
    int limit = tc0.limit();
    tc0.limit(tc0.position() + pos * 2);
    gl.glVertexAttribPointer(GlAdapter.ARRAY_TEXCOORD_0, 2, GlAdapter.GL_FLOAT, false, 0, 0,
        paliashdr.textureCoordBuf, paliashdr.staticTextureBufId);
    tc0.limit(limit);

    dstVertexCoords.flip();
    gl.glVertexPointer(3, 0, dstVertexCoords);

    pos = 0;
    for (int j = 0; j < size; j++) {
      // get the vertex count and primitive type
      count = counts[j];
      if (count == 0)
        break;		// done

      mode = GlAdapter.GL_TRIANGLE_STRIP;
      if (count < 0) {
        mode = GlAdapter.GL_TRIANGLE_FAN;
        count = -count;
      }
      gl.glDrawArrays(mode, pos, count);
      pos += count;
    }


    // PMM - added double damage shell
    if ( (currententity.flags & ( Constants.RF_SHELL_RED | Constants.RF_SHELL_GREEN | Constants.RF_SHELL_BLUE | Constants.RF_SHELL_DOUBLE | Constants.RF_SHELL_HALF_DAM)) != 0 )
      gl.glEnable( GlAdapter.GL_TEXTURE_2D );

    gl.glDisableClientState( GlAdapter.GL_COLOR_ARRAY );
  }


  private final float[] point = {0, 0, 0};
  /**
   * GL_DrawAliasShadow
   */
  void GL_DrawAliasShadow(QuakeFiles.dmdl_t paliashdr, int posenum)
  {
    float lheight = currententity.origin[2] - lightspot[2];
    //		qfiles.daliasframe_t frame = paliashdr.aliasFrames[currententity.frame];
    int[] order = paliashdr.glCmds;
    float height = -lheight + 1.0f;

    int orderIndex = 0;
    int index = 0;

    // TODO shadow drawing with vertex arrays

    int count;
    while (true)
    {
      // get the vertex count and primitive type
      count = order[orderIndex++];
      if (count == 0)
        break;		// done
      if (count < 0)
      {
        count = -count;
        gl.glBegin (GlAdapter.GL_TRIANGLE_FAN);
      }
      else
        gl.glBegin (GlAdapter.GL_TRIANGLE_STRIP);

      do
      {
        index = order[orderIndex + 2] * 3;
        point[0] = vertexArrayBuf.get(index);
        point[1] = vertexArrayBuf.get(index + 1);
        point[2] = vertexArrayBuf.get(index + 2);

        point[0] -= shadevector[0]*(point[2]+lheight);
        point[1] -= shadevector[1]*(point[2]+lheight);
        point[2] = height;
        gl.glVertex3f(point[0], point[1], point[2]);

        orderIndex += 3;

      } while (--count != 0);

      gl.glEnd ();
    }	
  }

  //	TODO sync with jogl renderer. hoz
  // stack variable
  private final float[] mins = { 0, 0, 0 };
  private final float[] maxs = { 0, 0, 0 };
  /**
   * R_CullAliasModel
   */
  boolean R_CullAliasModel(EntityType e) {
    QuakeFiles.dmdl_t paliashdr = (QuakeFiles.dmdl_t) currentmodel.extradata;

    if ((e.frame >= paliashdr.num_frames) || (e.frame < 0)) {
      Window.Printf(Constants.PRINT_ALL, "R_CullAliasModel " + currentmodel.name + ": no such frame " + e.frame + '\n');
      e.frame = 0;
    }
    if ((e.oldframe >= paliashdr.num_frames) || (e.oldframe < 0)) {
      Window.Printf(Constants.PRINT_ALL, "R_CullAliasModel " + currentmodel.name + ": no such oldframe " + e.oldframe + '\n');
      e.oldframe = 0;
    }

    QuakeFiles.daliasframe_t pframe = paliashdr.aliasFrames[e.frame];
    QuakeFiles.daliasframe_t poldframe = paliashdr.aliasFrames[e.oldframe];

    /*
     ** compute axially aligned mins and maxs
     */
    if (pframe == poldframe) {
      for (int i = 0; i < 3; i++) {
        mins[i] = pframe.translate[i];
        maxs[i] = mins[i] + pframe.scale[i] * 255;
      }
    } else {
      float thismaxs, oldmaxs;
      for (int i = 0; i < 3; i++) {
        thismaxs = pframe.translate[i] + pframe.scale[i] * 255;

        oldmaxs = poldframe.translate[i] + poldframe.scale[i] * 255;

        if (pframe.translate[i] < poldframe.translate[i])
          mins[i] = pframe.translate[i];
        else
          mins[i] = poldframe.translate[i];

        if (thismaxs > oldmaxs)
          maxs[i] = thismaxs;
        else
          maxs[i] = oldmaxs;
      }
    }

    /*
     ** compute a full bounding box
     */
    float[] tmp;
    for (int i = 0; i < 8; i++) {
      tmp = bbox[i];
      if ((i & 1) != 0)
        tmp[0] = mins[0];
      else
        tmp[0] = maxs[0];

      if ((i & 2) != 0)
        tmp[1] = mins[1];
      else
        tmp[1] = maxs[1];

      if ((i & 4) != 0)
        tmp[2] = mins[2];
      else
        tmp[2] = maxs[2];
    }

    /*
     ** rotate the bounding box
     */
    tmp = mins;
    Math3D.VectorCopy(e.angles, tmp);
    tmp[GlConstants.YAW] = -tmp[GlConstants.YAW];
    Math3D.AngleVectors(tmp, vectors[0], vectors[1], vectors[2]);

    for (int i = 0; i < 8; i++) {
      Math3D.VectorCopy(bbox[i], tmp);

      bbox[i][0] = Math3D.DotProduct(vectors[0], tmp);
      bbox[i][1] = -Math3D.DotProduct(vectors[1], tmp);
      bbox[i][2] = Math3D.DotProduct(vectors[2], tmp);

      Math3D.VectorAdd(e.origin, bbox[i], bbox[i]);
    }

    int f, mask;
    int aggregatemask = ~0; // 0xFFFFFFFF

    for (int p = 0; p < 8; p++) {
      mask = 0;

      for (f = 0; f < 4; f++) {
        float dp = Math3D.DotProduct(frustum[f].normal, bbox[p]);

        if ((dp - frustum[f].dist) < 0) {
          mask |= (1 << f);
        }
      }

      aggregatemask &= mask;
    }

    if (aggregatemask != 0) {
      return true;
    }

    return false;
  }


  // bounding box
  float[][] bbox = {
      {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0},
      {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0, 0}
  };

  //	TODO sync with jogl renderer. hoz
  /**
   * R_DrawAliasModel
   */
  void R_DrawAliasModel(EntityType e)
  {
    if ( ( e.flags & Constants.RF_WEAPONMODEL ) == 0)
    {
      if ( R_CullAliasModel(e) )
        return;
    }

    if ( (e.flags & Constants.RF_WEAPONMODEL) != 0 )
    {
      if ( r_lefthand.value == 2.0f )
        return;
    }

    QuakeFiles.dmdl_t paliashdr = (QuakeFiles.dmdl_t)currentmodel.extradata;

    //
    // get lighting information
    //
    // PMM - rewrote, reordered to handle new shells & mixing
    // PMM - 3.20 code .. replaced with original way of doing it to keep mod authors happy
    //
    int i;
    if ( (currententity.flags & ( Constants.RF_SHELL_HALF_DAM | Constants.RF_SHELL_GREEN | Constants.RF_SHELL_RED | Constants.RF_SHELL_BLUE | Constants.RF_SHELL_DOUBLE )) != 0 )
    {
      Math3D.VectorClear(shadelight);
      if ((currententity.flags & Constants.RF_SHELL_HALF_DAM) != 0)
      {
        shadelight[0] = 0.56f;
        shadelight[1] = 0.59f;
        shadelight[2] = 0.45f;
      }
      if ( (currententity.flags & Constants.RF_SHELL_DOUBLE) != 0 )
      {
        shadelight[0] = 0.9f;
        shadelight[1] = 0.7f;
      }
      if ( (currententity.flags & Constants.RF_SHELL_RED) != 0 )
        shadelight[0] = 1.0f;
      if ( (currententity.flags & Constants.RF_SHELL_GREEN) != 0 )
        shadelight[1] = 1.0f;
      if ( (currententity.flags & Constants.RF_SHELL_BLUE) != 0 )
        shadelight[2] = 1.0f;
    }

    else if ( (currententity.flags & Constants.RF_FULLBRIGHT) != 0 )
    {
      for (i=0 ; i<3 ; i++)
        shadelight[i] = 1.0f;
    }
    else
    {
      R_LightPoint (currententity.origin, shadelight);

      // player lighting hack for communication back to server
      // big hack!
      if ( (currententity.flags & Constants.RF_WEAPONMODEL) != 0 )
      {
        // pick the greatest component, which should be the same
        // as the mono value returned by software
        if (shadelight[0] > shadelight[1])
        {
          if (shadelight[0] > shadelight[2])
            r_lightlevel.value = 150*shadelight[0];
          else
            r_lightlevel.value = 150*shadelight[2];
        }
        else
        {
          if (shadelight[1] > shadelight[2])
            r_lightlevel.value = 150*shadelight[1];
          else
            r_lightlevel.value = 150*shadelight[2];
        }
      }

      if ( gl_monolightmap.string.charAt(0) != '0' )
      {
        float s = shadelight[0];

        if ( s < shadelight[1] )
          s = shadelight[1];
        if ( s < shadelight[2] )
          s = shadelight[2];

        shadelight[0] = s;
        shadelight[1] = s;
        shadelight[2] = s;
      }
    }

    if ( (currententity.flags & Constants.RF_MINLIGHT) != 0 )
    {
      for (i=0 ; i<3 ; i++)
        if (shadelight[i] > 0.1f)
          break;
      if (i == 3)
      {
        shadelight[0] = 0.1f;
        shadelight[1] = 0.1f;
        shadelight[2] = 0.1f;
      }
    }

    if ( (currententity.flags & Constants.RF_GLOW) != 0 )
    {	// bonus items will pulse with time
      float	scale;
      float	min;

      scale = (float)(0.1f * Math.sin(r_newrefdef.time*7));
      for (i=0 ; i<3 ; i++)
      {
        min = shadelight[i] * 0.8f;
        shadelight[i] += scale;
        if (shadelight[i] < min)
          shadelight[i] = min;
      }
    }

    // =================
    // PGM	ir goggles color override
    if ( (r_newrefdef.rdflags & Constants.RDF_IRGOGGLES) != 0 && (currententity.flags & Constants.RF_IR_VISIBLE) != 0)
    {
      shadelight[0] = 1.0f;
      shadelight[1] = 0.0f;
      shadelight[2] = 0.0f;
    }
    // PGM	
    // =================

    shadedots = r_avertexnormal_dots[((int)(currententity.angles[1] * (SHADEDOT_QUANT / 360.0))) & (SHADEDOT_QUANT - 1)];

    float an = (float)(currententity.angles[1]/180*Math.PI);
    shadevector[0] = (float)Math.cos(-an);
    shadevector[1] = (float)Math.sin(-an);
    shadevector[2] = 1;
    Math3D.VectorNormalize(shadevector);

    //
    // locate the proper data
    //

    c_alias_polys += paliashdr.num_tris;

    //
    // draw all the triangles
    //
    if ( (currententity.flags & Constants.RF_DEPTHHACK) != 0) // hack the depth range to prevent view model from poking into walls
      gl.glDepthRange(gldepthmin, (float) (gldepthmin + 0.3*(gldepthmax-gldepthmin)));

    if ( (currententity.flags & Constants.RF_WEAPONMODEL) != 0 && (r_lefthand.value == 1.0f) )
    {
      gl.glMatrixMode( GlAdapter.GL_PROJECTION );
      gl.glPushMatrix();
      gl.glLoadIdentity();
      gl.glScalef( -1, 1, 1 );
      MYgluPerspective( r_newrefdef.fov_y, ( float ) r_newrefdef.width / r_newrefdef.height,  4,  4096);
      gl.glMatrixMode( GlAdapter.GL_MODELVIEW );

      gl.glCullFace( GlAdapter.GL_BACK );
    }

    gl.glPushMatrix ();
    e.angles[GlConstants.PITCH] = -e.angles[GlConstants.PITCH];	// sigh.
    R_RotateForEntity (e);
    e.angles[GlConstants.PITCH] = -e.angles[GlConstants.PITCH];	// sigh.



    ModelImage		skin;
    // select skin
    if (currententity.skin != null)
      skin = currententity.skin;	// custom player skin
    else
    {
      if (currententity.skinnum >= QuakeFiles.MAX_MD2SKINS)
        skin = currentmodel.skins[0];
      else
      {
        skin = currentmodel.skins[currententity.skinnum];
        if (skin == null)
          skin = currentmodel.skins[0];
      }
    }
    if (skin == null)
      skin = r_notexture;	// fallback...
    GL_Bind(skin.texnum);

    // draw it

    gl.glShadeModel (GlAdapter.GL_SMOOTH);

    GL_TexEnv( GlAdapter.GL_MODULATE );
    if ( (currententity.flags & Constants.RF_TRANSLUCENT) != 0 )
    {
      gl.glEnable (GlAdapter.GL_BLEND);
    }


    if ( (currententity.frame >= paliashdr.num_frames) 
        || (currententity.frame < 0) )
    {
      Window.Printf (Constants.PRINT_ALL, "R_DrawAliasModel " + currentmodel.name +": no such frame " + currententity.frame + '\n');
      currententity.frame = 0;
      currententity.oldframe = 0;
    }

    if ( (currententity.oldframe >= paliashdr.num_frames)
        || (currententity.oldframe < 0))
    {
      Window.Printf (Constants.PRINT_ALL, "R_DrawAliasModel " + currentmodel.name +": no such oldframe " + currententity.oldframe + '\n');
      currententity.frame = 0;
      currententity.oldframe = 0;
    }

    if ( r_lerpmodels.value == 0.0f)
      currententity.backlerp = 0;

    GL_DrawAliasFrameLerpDA(paliashdr, currententity.backlerp);

    GL_TexEnv( GlAdapter.GL_REPLACE );
    gl.glShadeModel (GlAdapter.GL_FLAT);

    gl.glPopMatrix ();

    if ( ( currententity.flags & Constants.RF_WEAPONMODEL ) != 0 && ( r_lefthand.value == 1.0F ) )
    {
      gl.glMatrixMode( GlAdapter.GL_PROJECTION );
      gl.glPopMatrix();
      gl.glMatrixMode( GlAdapter.GL_MODELVIEW );
      gl.glCullFace( GlAdapter.GL_FRONT );
    }

    if ( (currententity.flags & Constants.RF_TRANSLUCENT) != 0 )
    {
      gl.glDisable (GlAdapter.GL_BLEND);
    }

    if ( (currententity.flags & Constants.RF_DEPTHHACK) != 0)
      gl.glDepthRange (gldepthmin, gldepthmax);

    if ( gl_shadows.value != 0.0f && (currententity.flags & (Constants.RF_TRANSLUCENT | Constants.RF_WEAPONMODEL)) == 0)
    {
      gl.glPushMatrix ();
      R_RotateForEntity (e);
      gl.glDisable (GlAdapter.GL_TEXTURE_2D);
      gl.glEnable (GlAdapter.GL_BLEND);
      gl.glColor4f (0,0,0,0.5f);
      GL_DrawAliasShadow (paliashdr, currententity.frame );
      gl.glEnable (GlAdapter.GL_TEXTURE_2D);
      gl.glDisable (GlAdapter.GL_BLEND);
      gl.glPopMatrix ();
    }
    gl.glColor4f (1,1,1,1);
  }
}
