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

import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;

public interface Model {

	/** Renders this model using the {@link GL20} shader pipeline.<br />
	 * <br />
	 * <strong>IMPORTANT:</strong> This model must have materials set before you can use this render function. Do that by using
	 * {@link Model#setMaterials(Material...)}.
	 * @param program The shader program that you will use to draw this object to the screen. It must be non-null. */
	public void render (ShaderProgram program, StillModelAttributes attributes);

	/** Returns a {@link Model} that is made up of the sub-meshes with the provided names.
	 * @param subMeshNames A list of names of each {@link SubMesh} that is to be extracted from this model.
	 * @return A new {@link Model} that is only made up of the parts you requested. */
	public Model getSubModel (String... subMeshNames);

	/** @param name The name of the {@link SubMesh} to be acquired.
	 * @return The {@link SubMesh} that matches that name; or null, if one does not exist. */
	public SubMesh getSubMesh (String name);

	/** @return An array of every {@link SubMesh} that makes up this model. */
	public SubMesh[] getSubMeshes ();

	/** Generates the bounding box for the Model.<br />
	 * <br />
	 * For every finite 3D object there exists a box that can enclose the object. This function sets the give {@link BoundingBox}
	 * to be one such enclosing box.<br />
	 * Bounding boxes are useful for very basic collision detection amongst other tasks.
	 * @param bbox The provided {@link BoundingBox} will have its internal values correctly set. (To allow Java Object reuse) */
	public void getBoundingBox (BoundingBox bbox);

	/** Sets the {@link Material} of every {@link SubMesh} in this Model to be the material provided.
	 * @param material The Material that you wish the whole object to be rendered with. */
	public void setMaterial (Material material);

	/** This function releases memory once you are done with the Model. Once you are finished with the Model you MUST call this
	 * function or else you will suffer memory leaks. */
	public void dispose ();
}
