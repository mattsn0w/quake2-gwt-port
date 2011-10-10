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

import com.googlecode.gwtquake.shared.game.*;

public class ModelNode {
	//	common with leaf
	public int contents; // -1, to differentiate from leafs
	public int visframe; // node needs to be traversed if current

	//public float minmaxs[] = new float[6]; // for bounding box culling
	public float mins[] = new float[3]; // for bounding box culling
	public float maxs[] = new float[3]; // for bounding box culling

	public ModelNode parent;

	//	node specific
	public Plane plane;
	public ModelNode children[] = new ModelNode[2];

	// unsigned short
	public int firstsurface;
	public int numsurfaces;

}
