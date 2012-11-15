package com.lyeeedar.Roguelike3D.Graphics;

import com.badlogic.gdx.math.Vector3;

public class Light {
	
	public Vector3 colour;
	public Vector3 position;
	public Vector3 direction;
	
	public float attenuation;
	
	public String UID;
		
	public Light(Vector3 colour, Vector3 position, Vector3 direction, float attenuation)
	{
		this.colour = colour;
		this.position = position;
		this.direction = direction;
		this.attenuation = attenuation;
		
		UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
	}
	
	public boolean inDrawDistance(Vector3 pos)
	{
		if (1 < (pos.dst(position) * attenuation)*0.001f) return true;
		
		return false;
	}

}
