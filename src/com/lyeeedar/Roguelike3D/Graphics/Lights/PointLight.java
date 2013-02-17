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
package com.lyeeedar.Roguelike3D.Graphics.Lights;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Graphics.Colour;

public class PointLight implements Comparable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5237715137220677938L;

	final public String UID;

	final public Vector3 position;
	public Colour colour;

	public float attenuation;

	protected int priority;

	public static final int PRIORITY_DISCRETE_STEPS = 256;
	
	private final Vector3 tmpVec = new Vector3();
	
	public transient Mesh area;

	public PointLight(Vector3 position, Colour colour, float attentuation)
	{
		UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		this.position = position;
		this.colour = colour;
		this.attenuation = attentuation;
	}
	
	public void positionAbsolutely(float x, float y, float z)
	{
		positionAbsolutely(tmpVec.set(x, y, z));
	}
	
	public void positionAbsolutely(Vector3 pos)
	{
		position.set(pos);
	}
	
	public void transform(float x, float y, float z)
	{
		transform(tmpVec.set(x, y, z));
	}
	
	public void transform(Vector3 amount)
	{
		position.add(amount);
	}

	@Override
	public int compareTo (Object other) {
		return this.priority - ((PointLight)other).priority;
	}
	
	public Vector3 getColourRGB()
	{
		return new Vector3(colour.r, colour.g, colour.b);
	}
	
	public void fixReferences()
	{
		
	}

}
