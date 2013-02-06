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

public class StillSubMesh extends SubMesh {

	public StillSubMesh (String name, Mesh mesh, int primitiveType) {
		super(name, mesh, primitiveType);
	}

	@Override
	public void getBoundingBox (BoundingBox bbox) {
		mesh.calculateBoundingBox(bbox);
	}

}
