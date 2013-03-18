/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Graphics.Models;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.collision.BoundingBox;

public class StillModel {
	final public StillSubMesh[] subMeshes;

	public StillModel (SubMesh[] subMeshes) {
		this.subMeshes = new StillSubMesh[subMeshes.length];
		for (int i = 0; i < subMeshes.length ; ++i) {
			this.subMeshes[i] = (StillSubMesh)subMeshes[i];
		}	
	}
	
	public StillModel (Mesh mesh, int primitive_type) {
		this.subMeshes = new StillSubMesh[]{new StillSubMesh("basic", mesh, primitive_type)};	
	}

	private final static BoundingBox tmpBox = new BoundingBox();

	public void getBoundingBox (BoundingBox bbox) {
		bbox.inf();
		for (int i = 0; i < subMeshes.length; i++) {
			subMeshes[i].mesh.calculateBoundingBox(tmpBox);
			bbox.ext(tmpBox);
		}
	}

	public void dispose () {
		for (int i = 0; i < subMeshes.length; i++) {
			subMeshes[i].mesh.dispose();
		}
	}
}
