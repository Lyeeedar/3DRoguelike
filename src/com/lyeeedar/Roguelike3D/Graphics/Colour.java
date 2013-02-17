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
package com.lyeeedar.Roguelike3D.Graphics;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Colour implements Serializable {

	public float r;
	public float g;
	public float b;
	public float a;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1849249567986215838L;

	public Colour() {
		r = 1.0f;
		g = 1.0f;
		b = 1.0f;
		a = 1.0f;
	}

	/** Constructor, sets the components of the color
	 * 
	 * @param r the red component
	 * @param g the green component
	 * @param b the blue component
	 * @param a the alpha component */
	public Colour (float r, float g, float b, float a) {
		super();
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	/** Constructs a new color using the given color
	 * 
	 * @param color the color */
	public Colour (Color color) {
		super();
		this.r = color.r;
		this.g = color.g;
		this.b = color.b;
		this.a = color.a;
	}

	public void set(Colour c) {
		this.r = c.r;
		this.g = c.g;
		this.b = c.b;
		this.a = c.a;
	}
	
	public Color getColor()
	{
		return new Color(r, g, b, a);
	}
	
	public final Vector3 tmpVec = new Vector3();
	public Vector3 getColour()
	{
		return tmpVec.set(r, g, b);
	}

	public void set(float r, float g, float b, float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	@Override
	public String toString()
	{
		return r+","+g+","+b+","+a;
	}
}
