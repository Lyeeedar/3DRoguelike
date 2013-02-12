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

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;

public class StillModelAttributes implements StillModelInstance {
	static final private float[] vec3 = {0, 0, 0};
	final private Matrix4 tempMat = new Matrix4();

	final public Vector3 origin = new Vector3();
	final public Vector3 transformedPosition = new Vector3();

	final public Matrix4 position = new Matrix4();
	final public Matrix4 rotation = new Matrix4().idt();
	public Material material;
	public float radius;
	
	public final float scale;
	
	public Vector3 box;
	
	public static final Matrix4 composed = new Matrix4();

	public StillModelAttributes (Material material, float radius, float scale, Vector3 box) {
		this.material = material;
		this.box = box;
		
		this.radius = radius/2 * scale;
		
		this.scale = scale;
	}

	@Override
	public Matrix4 getTransform () {
		return position;
	}
	
	public Matrix4 getRotation () {
		return rotation;
	}

	@Override
	public Vector3 getSortCenter () {
		vec3[0] = origin.x;
		vec3[1] = origin.y;
		vec3[2] = origin.z;
		Matrix4.mulVec(tempMat.set(position).mul(rotation).val, vec3);
		transformedPosition.x = vec3[0];
		transformedPosition.y = vec3[1];
		transformedPosition.z = vec3[2];
		return transformedPosition;
	}

	@Override
	public Material getMaterial() {
		return material;
	}

	@Override
	public float getBoundingSphereRadius () {
		return radius;
	}

	public StillModelAttributes copy () {
		
		Material copy_material = material.copy();
		
		final StillModelAttributes copy = new StillModelAttributes(copy_material, radius, scale, box);
		
		copy.position.set(position.val);
		copy.rotation.set(rotation.val);
		copy.origin.set(origin);
		copy.transformedPosition.set(transformedPosition);
		return copy;

	}
	
	public void dispose()
	{
		material.dispose();
	}
	
	public Matrix4 getComposedMatrix()
	{
		return composed.set(position).scale(scale, scale, scale).mul(rotation);
	}
}
