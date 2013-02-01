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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;

public class ParticleEmitter {
	
	public float distance = 0;
	
	public String UID;
	
	public ArrayDeque<Particle> active;
	public ArrayDeque<Particle> inactive;
	
	PointLight boundLight;
	
	Random ran = new Random();
	
	float time = 0;
	
	float x; float y; float z; public float vx; public float vy; public float vz; float speed;
	
	float radius;
	
	public int particles;
	
	Texture texture;
	
	final ShaderProgram particleShader;
	final Mesh mesh;
	
	GameObject bound;
	
	float ox; float oy; float oz;
	
	public ParticleEmitter(float x, float y, float z, float vx, float vy, float vz, float speed, int particles, GameObject bound)
	{
		this.bound = bound;
		this.UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		this.particles = particles;
		this.ox = x;
		this.oy = y;
		this.oz = z;
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
		
		mesh = new Mesh(true, particles, 0, 
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Generic, 3, "a_colour"));
		
		final String vertexShader = Gdx.files.internal("data/shaders/model/particle.vertex.glsl").readString();
		final String fragmentShader = Gdx.files.internal("data/shaders/model/particle.fragment.glsl").readString();
		
		particleShader = new ShaderProgram(vertexShader, fragmentShader);
		
		vertices = new float[particles*6];
	}
	
	Vector3 velocity; float atime; Color start; Color end; float width; float height;
	
	float intensity; float attenuation;
	
	public void setTexture(String texture, Vector3 velocity, float atime, Color start, Color end, boolean light, float intensity, float attenuation)
	{
		this.texture = new Texture(Gdx.files.internal(texture));
		this.velocity = velocity;
		this.atime = atime;
		this.start = start;
		this.end = end;
		this.width = this.texture.getWidth();
		this.height = this.texture.getHeight();
		
		if (light)
		{
			this.intensity = intensity;
			this.attenuation = attenuation;
			
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
	
	final float[] vertices;
	public void render(Camera cam)
	{
		Gdx.gl.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
		Gdx.gl.glEnable(GL11.GL_POINT_SPRITE_OES);
		Gdx.gl.glEnable(GL20.GL_BLEND); 
		//Gdx.gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_SRC_ALPHA);
		Gdx.gl.glDepthMask(false);
		
		particleShader.begin();
		
		particleShader.setUniformMatrix("u_mv", cam.combined);
		particleShader.setUniformf("u_cam", cam.position);
		texture.bind(0);
		particleShader.setUniformi("u_texture", 0);
		mesh.setVertices(vertices);
		mesh.render(particleShader, GL20.GL_POINTS);
		
		particleShader.end();
	}
	
	private int signx;
	private int signy;
	private int signz;
	public void update(float delta)
	{
		x = bound.getPosition().x+ox;
		y = bound.getPosition().y+oy;
		z = bound.getPosition().z+oz;
		
		boundLight.position.set(x, y, z);
		
		if (boundLight != null)
		{
			boundLight.intensity = (float) (intensity * 
					(1-((1-((float)active.size() / (float)inactive.size())))/2));
			boundLight.attenuation = (float) (attenuation * 
					(1-((1-((float)inactive.size() / (float)active.size())))/2));
		}
		
		Iterator<Particle> pItr = active.iterator();
		
		int i = 0;
		while (pItr.hasNext())
		{
			Particle p = pItr.next();
			p.update(delta);
			
			if (!p.alive)
			{
				pItr.remove();
				inactive.add(p);
			}
			else
			{
				vertices[(i*6)] = p.position.x;
				vertices[(i*6)+1] = p.position.y;
				vertices[(i*6)+2] = p.position.z;
				
				vertices[(i*6)+3] = p.colour.r;
				vertices[(i*6)+4] = p.colour.g;
				vertices[(i*6)+5] = p.colour.b;
			}
			i++;
		}
		
		for (; i < vertices.length/6; i++)
		{
			vertices[(i*6)] = 0;
			vertices[(i*6)+1] = 0;
			vertices[(i*6)+2] = 0;
			
			vertices[(i*6)+3] = 0;
			vertices[(i*6)+4] = 0;
			vertices[(i*6)+5] = 0;
		}
		
		time -= delta;
		if (inactive.size() == 0) return;
		
		while (time < 0 && inactive.size() > 0)
		{
			Particle p = inactive.pop();
			
			signx = (ran.nextInt(2) == 0) ? 1 : -1;
			signy = (ran.nextInt(2) == 0) ? 1 : -1;
			signz = (ran.nextInt(2) == 0) ? 1 : -1;
			p.set(velocity, atime*ran.nextFloat(), start, end, x+(vx*ran.nextFloat()*signx), y+(vy*ran.nextFloat()*signy), z+(vz*ran.nextFloat()*signz));
	
			active.add(p);
			
			time += speed;
		}
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
