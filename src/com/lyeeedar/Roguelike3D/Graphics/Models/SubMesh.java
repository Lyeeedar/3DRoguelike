/*******************************************************************************
 * Copyright (c) 2012 Philip Collin.
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
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;

public abstract class SubMesh {
	public String name;
	public int primitiveType;
	public Mesh mesh;


	public SubMesh (String name, Mesh mesh, int primitiveType) {
		this.name = name;
		this.setMesh(mesh);
		this.primitiveType = primitiveType;
	}

	/** Obtain the {@link BoundingBox} of this {@link SubMesh}.
	 * 
	 * @param bbox This {@link BoundingBox} will be modified so that its contain values that are the bounding box for this SubMesh. */
	public abstract void getBoundingBox (BoundingBox bbox);

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
}
