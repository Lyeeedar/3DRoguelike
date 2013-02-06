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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Graphics.Colour;

public class TempVO {

	public TempMesh mesh; public int primitive_type; public Colour colour; public String textureName;
	public float x; public float y; public float z;
	
	public TempVO(TempMesh mesh, int primitive_type, Colour colour, String textureName, float x, float y, float z)
	{
		this.mesh = mesh;
		this.primitive_type = primitive_type;
		this.colour = colour;
		this.textureName = textureName;
		
		this.x = x;
		this.y = y;
		this.z = z;
	}
}
