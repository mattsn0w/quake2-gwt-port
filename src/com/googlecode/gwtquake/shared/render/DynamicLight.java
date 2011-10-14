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
package com.googlecode.gwtquake.shared.render;

import com.googlecode.gwtquake.shared.common.Constants;
import com.googlecode.gwtquake.shared.game.Plane;
import com.googlecode.gwtquake.shared.util.Math3D;

public class DynamicLight {
	public float origin[] = { 0, 0, 0 };
	public float color[] = { 0, 0, 0 };
	public float intensity;
	/**
	 * R_RenderDlight
	 */
	public static void R_RenderDlight(DynamicLight light)
	{
		float rad = light.intensity * 0.35f;
	
		Math3D.VectorSubtract (light.origin, GlState.r_origin, DynamicLights.v);
	
		GlState.gl.glBegin (Gl1Context.GL_TRIANGLE_FAN);
		GlState.gl.glColor3f (light.color[0]*0.2f, light.color[1]*0.2f, light.color[2]*0.2f);
		int i;
		for (i=0 ; i<3 ; i++)
			DynamicLights.v[i] = light.origin[i] - GlState.vpn[i]*rad;
		
		GlState.gl.glVertex3f(DynamicLights.v[0], DynamicLights.v[1], DynamicLights.v[2]);
		GlState.gl.glColor3f (0,0,0);
	
		int j;
		float a;
		for (i=16 ; i>=0 ; i--)
		{
			a = (float)(i/16.0f * Math.PI*2);
			for (j=0 ; j<3 ; j++)
				DynamicLights.v[j] = (float)(light.origin[j] + GlState.vright[j]*Math.cos(a)*rad
					+ GlState.vup[j]*Math.sin(a)*rad);
			GlState.gl.glVertex3f(DynamicLights.v[0], DynamicLights.v[1], DynamicLights.v[2]);
		}
		GlState.gl.glEnd ();
	}
	/**
	 * R_MarkLights
	 */
	public static void R_MarkLights (DynamicLight light, int bit, Node node)
	{
		if (node.contents != -1)
			return;
	
		Plane 	splitplane = node.plane;
		float dist = Math3D.DotProduct (light.origin, splitplane.normal) - splitplane.dist;
	
		if (dist > light.intensity - GlConstants.DLIGHT_CUTOFF)
		{
			R_MarkLights (light, bit, node.children[0]);
			return;
		}
		if (dist < -light.intensity + GlConstants.DLIGHT_CUTOFF)
		{
			R_MarkLights (light, bit, node.children[1]);
			return;
		}
	
		// mark the polygons
		Surface	surf;
		int sidebit;
		for (int i=0 ; i<node.numsurfaces ; i++)
		{
	
			surf = GlState.r_worldmodel.surfaces[node.firstsurface + i];
	
			/*
			 * cwei
			 * bugfix for dlight behind the walls
			 */			
			dist = Math3D.DotProduct (light.origin, surf.plane.normal) - surf.plane.dist;
			sidebit = (dist >= 0) ? 0 : Constants.SURF_PLANEBACK;
			if ( (surf.flags & Constants.SURF_PLANEBACK) != sidebit )
				continue;
			/*
			 * cwei
			 * bugfix end
			 */			
	
			if (surf.dlightframe != GlState.r_dlightframecount)
			{
				surf.dlightbits = 0;
				surf.dlightframe = GlState.r_dlightframecount;
			}
			surf.dlightbits |= bit;
		}
	
		R_MarkLights (light, bit, node.children[0]);
		R_MarkLights (light, bit, node.children[1]);
	}
}
