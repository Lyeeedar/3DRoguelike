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

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;

public class ParticleEmitter {
	
	PointLight boundLight;
	
	Random ran = new Random();
	
	float time = 0;
	
	float x; float y; float z; float vx; float vy; float vz; float speed;
	
	public ParticleEmitter(float x, float y, float z, float vx, float vy, float vz, float speed)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.vx = vx;
		this.vy = vy;
		this.vz = vz;
		this.speed = speed;
	}
	
	String texture; Vector3 velocity; float atime; Color start; Color end; float width; float height;
	
	public void setDecal(String texture, Vector3 velocity, float atime, Color start, Color end, float width, float height)
	{
		this.texture = texture;
		this.velocity = velocity;
		this.atime = atime;
		this.start = start;
		this.end = end;
		this.width = width;
		this.height = height;
		
		float intensity = 1-(speed/10);
		float attenuation = (vx+vy+vz)/1000;
		
		if (boundLight == null)
		{
			boundLight = new PointLight(new Vector3(x, y, z), start, attenuation, intensity);
			GameData.lightManager.addLight(boundLight);
		}
		else
		{
			boundLight.colour = start;
			boundLight.attenuation = attenuation;
			boundLight.intensity = intensity;
		}
	}
	
	public void update(float delta)
	{
		time -= delta;
		if (time > 0) return;
		
		Particle p = new Particle(texture, velocity.cpy(), atime, start, end, width, height, x+(vx*ran.nextFloat()), y+(vy*ran.nextFloat()), z+(vz*ran.nextFloat()));

		GameData.particles.add(p);
	}
	
	public void dispose()
	{
		GameData.lightManager.removeLight(boundLight.UID);
	}

}
