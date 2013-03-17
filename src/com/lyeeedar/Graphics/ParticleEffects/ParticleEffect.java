package com.lyeeedar.Graphics.ParticleEffects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.lyeeedar.Roguelike3D.Bag;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;

public class ParticleEffect implements Serializable {
	private transient Vector3 pos;

	private static final long serialVersionUID = -5746609278217754852L;
	
	public final String UID;
	
	private Bag<Emitter> emitters = new Bag<Emitter>();
	
	private float x, y, z;
	private float radius;
	
	public ParticleEffect() {
		this.UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		
	}

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
	
	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;

		for (Emitter e : emitters)
		{
			e.emitter.setPosition(x+e.x, y+e.y, z+e.z);
		}
	}
	
	public void deleteEmitter(int index)
	{
		Emitter emitter = emitters.remove(index);
		emitter.emitter.dispose();
	}
	
	public void deleteEmitter(String name)
	{
		Iterator<Emitter> itr = emitters.iterator();
		
		while (itr.hasNext())
		{
			Emitter e = itr.next();
			
			if (e.emitter.name.equals(name))
			{
				itr.remove();
				
				e.emitter.dispose();
			}
		}
	}
	
	public ParticleEmitter getEmitter(int index)
	{
		return emitters.get(index).emitter;
	}
	
	public ParticleEmitter getEmitter(String name)
	{
		for (Emitter e : emitters) if (e.emitter.name.equals(name)) return e.emitter;
		
		return null;
	}
	
	public void getEmitters(List<ParticleEmitter> list)
	{
		for (Emitter e : emitters) list.add(e.emitter);
	}
	
	public ParticleEmitter getFirstEmitter()
	{
		return emitters.get(0).emitter;
	}
	
	public void addEmitter(ParticleEmitter emitter,
			float x, float y, float z)
	{
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
	
	public Vector3 getEmitterPosition(int index, Vector3 position)
	{
		Emitter e = emitters.get(index);
		return position.set(e.x, e.y, e.z);
	}
	
	public void setEmitterPosition(int index, Vector3 position)
	{
		Emitter e = emitters.get(index);
		e.x = position.x;
		e.y = position.y;
		e.z = position.z;
		
		setPosition(x, y, z);
	}
	
	public void getVisibleEmitters(ArrayList<ParticleEmitter> visibleEmitters, Camera cam)
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
	
	public void getLight(LightManager lightManager)
	{
		for (Emitter e : emitters)
		{
			e.emitter.getLight(lightManager);
		}
	}
	
	public void fixReferences() {
		pos = new Vector3();
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

	private static class Emitter implements Serializable, Json.Serializable {

		private static final long serialVersionUID = 7076203259415104530L;
		ParticleEmitter emitter;
		float x;
		float y;
		float z;
		
		@SuppressWarnings("unused")
		public Emitter(){}
		
		public Emitter(ParticleEmitter emitter,
				float x, float y, float z)
		{
			this.emitter = emitter;
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public void write(Json json) {
			ParticleEmitter.getJson(json);
			json.writeValue("emitter", emitter);
			json.writeValue("x", x);
			json.writeValue("y", y);
			json.writeValue("z", z);
		}

		@Override
		public void read(Json json, OrderedMap<String, Object> jsonData) {
			ParticleEmitter.getJson(json);
			
			Iterator<Entry<String, Object>> itr = jsonData.entries().iterator();
			
			while (itr.hasNext())
			{
				Entry<String, Object> entry = itr.next();
				
				if (entry.key.equals("emitter"))
				{
					emitter = json.readValue(ParticleEmitter.class, entry.value);
				}
				else if (entry.key.equals("x"))
				{
					x = (Float) entry.value;
				}
				else if (entry.key.equals("y"))
				{
					y = (Float) entry.value;
				}
				else if (entry.key.equals("z"))
				{
					z = (Float) entry.value;
				}
			}
		}
	}

	public int getActiveParticles() {
		int active = 0;
		
		for (Emitter e : emitters) active += e.emitter.getActiveParticles();
		
		return active;
	}
}
