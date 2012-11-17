/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.lyeeedar.Roguelike3D.Graphics.Models;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;

public class StillModelAttributes implements StillModelInstance {
	static final private float[] vec3 = {0, 0, 0};

	final public Vector3 origin = new Vector3();
	final public Vector3 transformedPosition = new Vector3();

	final public Matrix4 matrix = new Matrix4();
	public Material material;
	public float radius;

	public StillModelAttributes (Material material, float radius) {
		this.material = material;
		this.radius = radius;
	}

	@Override
	public Matrix4 getTransform () {
		return matrix;
	}

	@Override
	public Vector3 getSortCenter () {
		vec3[0] = origin.x;
		vec3[1] = origin.y;
		vec3[2] = origin.z;
		Matrix4.mulVec(matrix.val, vec3);
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
		
		final StillModelAttributes copy = new StillModelAttributes(copy_material, radius);
		
		copy.matrix.set(matrix.val);
		copy.origin.set(origin);
		copy.transformedPosition.set(transformedPosition);
		return copy;

	}
}
