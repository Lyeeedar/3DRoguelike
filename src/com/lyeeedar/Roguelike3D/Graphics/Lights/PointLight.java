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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;

public class PointLight implements Comparable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5237715137220677938L;

	final public String UID;

	final public Vector3 position;
	private final Color colour = new Color();
	public float attenuation;
	public float power;

	protected int priority;

	public static final int PRIORITY_DISCRETE_STEPS = 256;
	
	private final Vector3 tmpVec = new Vector3();
	
	public transient Mesh area;
	public transient float radius;

	public PointLight(Vector3 position, Color colour, float attentuation, float power)
	{
		UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		this.position = position;
		this.colour.set(colour);
		this.attenuation = attentuation;
		this.power = power;
		
		computeMesh();
	}
	
	public void computeMesh()
	{
		Vector3 intensity = new Vector3(getColour().r, getColour().g, getColour().b);
		float dist = 1;
		while (intensity.len2() > 0.5f)
		{
			intensity.set(getColour().r, getColour().g, getColour().b).div((attenuation + (attenuation/5)*dist)*dist);
			dist++;
		}
		
		dist *= 4;
		
		if (area != null) area.dispose();
		area = Shapes.genIcosahedronMesh(dist, dist);
		area.setVertices(Shapes.genIcosahedronVertices(dist, dist));
		
		radius = dist;
	}
	
	public void positionAbsolutely(float x, float y, float z)
	{
		position.set(x, y, z);
	}

	@Override
	public int compareTo (Object other) {
		return this.priority - ((PointLight)other).priority;
	}
	
	public void fixReferences()
	{
		computeMesh();
	}
	
	public void bind(ShaderProgram shader)
	{
		shader.setUniformf("u_model", position);
		shader.setUniformf("u_colour", getColour().r, getColour().g, getColour().b);
		shader.setUniformf("u_attenuation", attenuation);
		shader.setUniformf("u_power", power);
	}

	public Color getColour() {
		return colour;
	}

}
