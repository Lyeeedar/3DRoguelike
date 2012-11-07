package com.lyeeedar.Roguelike3D.Graphics;

import com.badlogic.gdx.math.Vector3;

public class Light {
	
	public Vector3 colour;
	public Vector3 position;
	public Vector3 direction;
	
	public float attenuation;
		
	public Light(Vector3 colour, Vector3 position, Vector3 direction, float attenuation)
	{
		this.colour = colour;
		this.position = position;
		this.direction = direction;
		this.attenuation = attenuation;
	}
	
	public boolean inDrawDistance(Vector3 pos, float offset)
	{
		if (pos.dst(position) + offset < attenuation) return true;
		
		return false;
	}

}
