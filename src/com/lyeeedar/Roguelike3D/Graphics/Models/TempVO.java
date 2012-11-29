package com.lyeeedar.Roguelike3D.Graphics.Models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;

public class TempVO {

	public TempMesh mesh; public int primitive_type; public Color colour; public String textureName;
	public float x; public float y; public float z;
	
	public TempVO(TempMesh mesh, int primitive_type, Color colour, String textureName, float x, float y, float z)
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
