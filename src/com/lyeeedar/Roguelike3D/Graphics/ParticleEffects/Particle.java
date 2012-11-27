package com.lyeeedar.Roguelike3D.Graphics.ParticleEffects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.math.Vector3;

public class Particle {
	
	public Decal decal;
	Vector3 velocity;
	float remainingTime;

	Color start;
	Color end;
	
	float rstep;
	float gstep;
	float bstep;
	
	public boolean alive = true;
	
	public Particle(String texture, Vector3 velocity, float time, Color start, Color end, float width, float height, float x, float y, float z)
	{
		this.velocity = velocity;
		this.remainingTime = time;
		this.start = start;
		
		if (end != null) this.end = end;
		else this.end = start;
		
		float rdiff = end.r-start.r;
		rstep = rdiff/time;
		
		float gdiff = end.g-start.g;
		gstep = gdiff/time;
		
		float bdiff = end.b-start.b;
		bstep = bdiff/time;
		
		TextureRegion tex = new TextureRegion(new Texture(Gdx.files.internal(texture)));
		
		decal = Decal.newDecal(width, height, tex, true);
		decal.setColor(start.r, start.g, start.b, start.a);
		
		decal.getPosition().set(x, y, z);
	}
	
	private final Vector3 tmpVec = new Vector3();
	public void update(float delta)
	{
		remainingTime -= delta;
		if (remainingTime < 0) alive = false;
		
		if (!alive) return;
		
		decal.getPosition().add(tmpVec.set(velocity).mul(delta));
		
		decal.setColor(end.r-(rstep*remainingTime), end.g-(gstep*remainingTime), end.b-(bstep*remainingTime), 1.0f);
	}
	
	public void lookAt(Camera cam)
	{
		decal.lookAt(cam.position, cam.up);
	}
}