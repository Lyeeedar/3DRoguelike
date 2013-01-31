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
package com.lyeeedar.Roguelike3D.Graphics.ParticleEffects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

public class Particle {
	
	Vector3 velocity;
	float remainingTime;

	Color colour = new Color();
	
	float rstep;
	float gstep;
	float bstep;
	
	public boolean alive = true;
	
	final String UID;
	
	Vector3 position = new Vector3();
	
	public Particle(boolean alive)
	{
		UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		alive = false;
	}
	
	public Particle(Vector3 velocity, float time, Color start, Color end, float x, float y, float z)
	{
		UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		this.velocity.set(velocity);
		this.remainingTime = time;
		this.colour.set(start);
		this.position.set(x, y, z);
		
		float rdiff = end.r-start.r;
		rstep = rdiff/time;
		
		float gdiff = end.g-start.g;
		gstep = gdiff/time;
		
		float bdiff = end.b-start.b;
		bstep = bdiff/time;
	}
	
	public void set(Vector3 velocity, float time, Color start, Color end, float x, float y, float z)
	{		
		alive = true;
		
		this.velocity = velocity;
		this.remainingTime = time;
		this.colour.set(start);
		this.position.set(x, y, z);
		
		float rdiff = end.r-start.r;
		rstep = rdiff/time;
		
		float gdiff = end.g-start.g;
		gstep = gdiff/time;
		
		float bdiff = end.b-start.b;
		bstep = bdiff/time;
	}
	
	private final Vector3 tmpVec = new Vector3();
	public void update(float delta)
	{
		remainingTime -= delta;
		if (remainingTime < 0) alive = false;
		if (!alive) return;
		
		position.add(velocity.tmp().mul(delta));
		
		colour.r += rstep*delta;
		colour.g += gstep*delta;
		colour.b += bstep*delta;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Particle)) return false;
		Particle p = (Particle)o;
		
		if (p.alive != alive) return false;
		
		if (p.UID.equals(UID)) return true;
		else return false;
	}
}
