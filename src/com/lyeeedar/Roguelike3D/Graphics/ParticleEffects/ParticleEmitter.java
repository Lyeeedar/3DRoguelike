package com.lyeeedar.Roguelike3D.Graphics.ParticleEffects;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;

public class ParticleEmitter {
	
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
	}
	
	public void update(float delta)
	{
		time -= delta;
		if (time > 0) return;
		
		Particle p = new Particle(texture, velocity.cpy(), atime, start, end, width, height, x+ran.nextInt((int) vx), y+ran.nextInt((int) vy), z+ran.nextInt((int) vz));

		GameData.particles.add(p);
	}

}
