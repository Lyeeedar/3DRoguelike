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

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;

public class ParticleEmitter {
	
	public String UID;
	
	ArrayDeque<Particle> active;
	ArrayDeque<Particle> inactive;
	
	PointLight boundLight;
	
	Random ran = new Random();
	
	float time = 0;
	
	float x; float y; float z; float vx; float vy; float vz; float speed;
	
	float radius;
	
	public int particles;
	
	public ParticleEmitter(float x, float y, float z, float vx, float vy, float vz, float speed, int particles)
	{
		this.UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		this.particles = particles;
		this.x = x;
		this.y = y;
		this.z = z;
		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
		this.speed = speed;
		
		active = new ArrayDeque<Particle>(particles);
		inactive = new ArrayDeque<Particle>(particles);
		
		for (int i = 0; i < particles; i++)
		{
			Particle p = new Particle(false);
			inactive.add(p);
		}
		
		radius = vx + vz;
	}
	
	String texture; Vector3 velocity; float atime; Color start; Color end; float width; float height;
	
	public void setDecal(String texture, Vector3 velocity, float atime, Color start, Color end, float width, float height, boolean light)
	{
		this.texture = texture;
		this.velocity = velocity;
		this.atime = atime;
		this.start = start;
		this.end = end;
		this.width = width;
		this.height = height;
		
		if (light)
		{
			float intensity = 1-(speed/10);
			float attenuation = (vx+vy+vz)/1000;
			
			Color lightCol = new Color((start.r+end.r)/2f, (start.g+end.g)/2f, (start.b+end.b)/2f, 1.0f);
			
			if (boundLight == null)
			{
				boundLight = new PointLight(new Vector3(x+(vx/2f), y+vy, z+(vz/2)), lightCol, attenuation, intensity);
				GameData.lightManager.addDynamicLight(boundLight);
			}
			else
			{
				boundLight.colour = lightCol;
				boundLight.attenuation = attenuation;
				boundLight.intensity = intensity;
			}
		}
		else
		{
			if (boundLight != null)
			{
				GameData.lightManager.removeDynamicLight(boundLight.UID);
				boundLight = null;
			}
		}
	}
	
	public void render(DecalBatch batch, Camera cam)
	{
		for (Particle p : active)
		{
			p.lookAt(cam);
			batch.add(p.decal);
		}
	}
	
	public void update(float delta)
	{
		Iterator<Particle> pItr = active.iterator();
		
		while (pItr.hasNext())
		{
			Particle p = pItr.next();
			p.update(delta);
			
			if (!p.alive)
			{
				pItr.remove();
				inactive.add(p);
			}
		}
		
		time -= delta;
		if (time > 0 || inactive.size() == 0) return;
		time = speed;
		
		Particle p = inactive.pop();
		p.set(texture, velocity.cpy(), atime, start, end, width, height, x+(vx*ran.nextFloat()), y+(vy*ran.nextFloat()), z+(vz*ran.nextFloat()));

		active.add(p);
	}
	
	final Vector3 tmpVec = new Vector3();
	public Vector3 getPos()
	{
		return tmpVec.set(x, y, z);
	}
	
	public float getRadius()
	{
		return radius;
	}
	
	public void dispose()
	{
		GameData.lightManager.removeDynamicLight(boundLight.UID);
	}

}
