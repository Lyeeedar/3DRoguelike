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
package com.lyeeedar.Roguelike3D.Game.Actor;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class CollisionBox {
	public final Vector3 position = new Vector3();
	public final Vector3 dimensions = new Vector3();
	
	public CollisionBox()
	{

	}
	
	public CollisionBox(BoundingBox box)
	{
		position.set(box.min);
		dimensions.set(box.getDimensions());
	}
	
	public CollisionBox(Vector3 dimensions)
	{
		position.set(0, 0, 0);
		this.dimensions.set(dimensions);
	}
	
	public CollisionBox(Vector3 position, Vector3 dimensions)
	{
		this.position.set(position);
		this.dimensions.set(dimensions);
	}
	
	public void translate(float x, float y, float z)
	{
		translate(new Vector3(x, y, z));
	}
	
	public void translate(Vector3 movement)
	{
		position.add(movement);
	}
	
	public boolean intersectBoxes(CollisionBox box)
	{
		if (position.x >= box.position.x+box.dimensions.x) {
			return false;
		}
		else if (position.z >= box.position.z+box.dimensions.z) {
			return false;
		}
		else if (position.x+dimensions.x <= box.position.x) {
			return false;
		}
		else if (position.z+dimensions.z <= box.position.z) {
			return false;
		}
		
		else {
			
			System.out.println("Collide!");
			System.out.println(position + "     " + dimensions);
			System.out.println(box.position + "     " + box.dimensions);
			
			Vector3 diff = new Vector3();
			diff.set(box.position);
			diff.add(box.dimensions);
			diff.sub(position);
			
			System.out.println(diff);
			
			diff.set(position);
			diff.add(dimensions);
			diff.sub(box.position);
			
			System.out.println(diff);
			
			return true;
		}
	}
	
	public boolean intersectRay (Ray ray) {
		float a, b;
		float min, max;
		float divX = 1 / ray.direction.x;
		float divY = 1 / ray.direction.y;
		float divZ = 1 / ray.direction.z;

		a = (position.x - ray.origin.x) * divX;
		b = (position.x+dimensions.x - ray.origin.x) * divX;
		if (a < b) {
			min = a;
			max = b;
		} else {
			min = b;
			max = a;
		}

		a = (position.y - ray.origin.y) * divY;
		b = (position.y+dimensions.y - ray.origin.y) * divY;
		if (a > b) {
			float t = a;
			a = b;
			b = t;
		}

		if (a > min) min = a;
		if (b < max) max = b;

		a = (position.z - ray.origin.z) * divZ;
		b = (position.z+dimensions.z - ray.origin.z) * divZ;
		if (a > b) {
			float t = a;
			a = b;
			b = t;
		}

		if (a > min) min = a;
		if (b < max) max = b;

		return (max >= 0) && (max >= min);
	}
	
	/**
	 * Copy the values from this collision box into the given collision box
	 * @param box
	 */
	public void cpy(CollisionBox box)
	{
		box.position.set(position);
		box.dimensions.set(dimensions);
	}

}
