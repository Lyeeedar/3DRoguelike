package com.lyeeedar.Roguelike3D.Graphics.ParticleEffects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Bag;

public class ParticleEffect implements Serializable {
	private transient Vector3 pos;

	private static final long serialVersionUID = -5746609278217754852L;
	
	public final String UID;
	
	private Bag<Emitter> emitters = new Bag<Emitter>();
	
	private float x, y, z;
	private float radius;

	public ParticleEffect(float radius) {
		
		this.UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		
		this.radius = radius;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public Vector3 getPos() {
		return pos.set(x, y, z);
	}
	
	public void setPosition(Vector3 pos) {
		this.x = pos.x;
		this.y = pos.y;
		this.z = pos.z;

		for (Emitter e : emitters)
		{
			e.emitter.setPosition(x+e.x, y+e.y, z+e.z);
		}
	}
	
	public void addEmitter(ParticleEmitter emitter,
			float x, float y, float z)
	{
		System.out.println("Adding emitter");
		Emitter e = new Emitter(emitter, x, y, z);
		emitters.add(e);
	}
	
	public void update(float delta, Camera cam)
	{
		for (Emitter e : emitters)
		{
			e.emitter.update(delta, cam);
		}
	}
	
	public void getEmitters(ArrayList<ParticleEmitter> visibleEmitters, Camera cam)
	{
		for (Emitter e : emitters)
		{
			if (!cam.frustum.sphereInFrustum(e.emitter.getPosition(), e.emitter.getRadius()*2)) continue;
	
			e.emitter.distance = cam.position.dst2(e.emitter.getPosition());
			
			visibleEmitters.add(e.emitter);
		}
	}
	
	public void render()
	{
		for (Emitter e : emitters)
		{
			e.emitter.render();
		}
	}
	
	public void fixReferences() {
		pos = new Vector3();
		for (Emitter e : emitters)
		{
			e.emitter.fixReferences();
		}
	}
	
	public void create()
	{
		for (Emitter e : emitters)
		{
			e.emitter.create();
		}
	}
	
	public void dispose()
	{
		for (Emitter e : emitters)
		{
			e.emitter.dispose();
		}
	}
	
	public void delete()
	{
		Iterator<Emitter> itr = emitters.iterator();
		
		while(itr.hasNext())
		{
			Emitter e = itr.next();
			e.emitter.dispose();
			e.emitter.delete();
			itr.remove();
		}
	}
	
	public ParticleEffect copy()
	{
		ParticleEffect effect = new ParticleEffect(radius);
		
		for (Emitter e : emitters)
		{
			effect.addEmitter(e.emitter.copy(), e.x, e.y, e.z);
		}
		
		return effect;
	}

	private class Emitter implements Serializable {

		private static final long serialVersionUID = 7076203259415104530L;
		ParticleEmitter emitter;
		float x;
		float y;
		float z;
		
		public Emitter(ParticleEmitter emitter,
				float x, float y, float z)
		{
			this.emitter = emitter;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
}
