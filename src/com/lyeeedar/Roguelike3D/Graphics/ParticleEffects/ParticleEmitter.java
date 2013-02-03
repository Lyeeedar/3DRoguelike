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

import java.nio.FloatBuffer;
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
import com.badlogic.gdx.utils.BufferUtils;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;

public class ParticleEmitter {
	
	public static float POINT_SIZE_MAX = 0;
	
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
	
	final static ShaderProgram pointShader = new ShaderProgram(
			Gdx.files.internal("data/shaders/model/particlePoint.vertex.glsl").readString(),
			Gdx.files.internal("data/shaders/model/particlePoint.fragment.glsl").readString());
	final static ShaderProgram quadShader = new ShaderProgram(
			Gdx.files.internal("data/shaders/model/particleQuad.vertex.glsl").readString(),
			Gdx.files.internal("data/shaders/model/particleQuad.fragment.glsl").readString());
	
	final Mesh meshPoint;
	final Mesh meshQuad;
	
	GameObject bound;
	
	float ox; float oy; float oz;
	
	private boolean pointMode = true;
	
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
		
		meshPoint = new Mesh(false, particles, 0, 
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Generic, 3, "a_colour"));
		
		meshQuad = new Mesh(false, particles*4, 0, 
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Generic, 3, "a_colour"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));

		verticesPoint = new float[particles*6];
		verticesQuad = new float[particles*32];
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
	
	final float[] verticesPoint;
	final float[] verticesQuad;
	public void render(Camera cam)
	{
		if (pointMode) {
			Gdx.gl.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
			Gdx.gl.glEnable(GL11.GL_POINT_SPRITE_OES);
			Gdx.gl.glEnable(GL20.GL_BLEND); 
			//Gdx.gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_SRC_ALPHA);
			Gdx.gl.glDepthMask(false);
			
			pointShader.begin();
			
			pointShader.setUniformMatrix("u_mv", cam.combined);
			pointShader.setUniformf("u_cam", cam.position);
			texture.bind(0);
			pointShader.setUniformi("u_texture", 0);
			meshPoint.setVertices(verticesPoint);
			meshPoint.render(pointShader, GL20.GL_POINTS);
			
			pointShader.end();
			
			Gdx.gl.glDepthMask(true);
		}
		else
		{
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glDepthMask(false);
			
			quadShader.begin();
			
			quadShader.setUniformMatrix("u_mv", cam.combined);
			texture.bind(0);
			quadShader.setUniformi("u_texture", 0);
			
			meshQuad.setVertices(verticesQuad);
			meshQuad.render(quadShader, GL20.GL_TRIANGLES);
			
			quadShader.end();
			
			Gdx.gl.glDepthMask(true);
		}
	}
	
	private int signx;
	private int signy;
	private int signz;
	private final Vector3 plane = new Vector3();
	private final Vector3 up = new Vector3(0, 1, 0);
	public void update(float delta, Camera cam)
	{
		plane.set(cam.direction).crs(up).nor();
		
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
			else if (pointMode)
			{
				verticesPoint[(i*6)] = p.position.x;
				verticesPoint[(i*6)+1] = p.position.y;
				verticesPoint[(i*6)+2] = p.position.z;
				
				verticesPoint[(i*6)+3] = p.colour.r;
				verticesPoint[(i*6)+4] = p.colour.g;
				verticesPoint[(i*6)+5] = p.colour.b;
			}
			else
			{
				verticesQuad[(i*32)] = p.position.x;
				verticesQuad[(i*32)+1] = p.position.y;
				verticesQuad[(i*32)+2] = p.position.z;
				
				verticesPoint[(i*32)+3] = p.colour.r;
				verticesPoint[(i*32)+4] = p.colour.g;
				verticesPoint[(i*32)+5] = p.colour.b;
				
				verticesPoint[(i*32)+6] = 0.0f;
				verticesPoint[(i*32)+7] = 0.0f;
				
				Vector3 nPos1 = plane.tmp().mul(width).add(p.position);
				
				verticesQuad[(i*32)+8] = nPos1.x;
				verticesQuad[(i*32)+9] = nPos1.y;
				verticesQuad[(i*32)+10] = nPos1.z;
				
				verticesPoint[(i*32)+11] = p.colour.r;
				verticesPoint[(i*32)+12] = p.colour.g;
				verticesPoint[(i*32)+13] = p.colour.b;
				
				verticesPoint[(i*32)+14] = width;
				verticesPoint[(i*32)+15] = 0.0f;
				
				
				verticesQuad[(i*32)+16] = p.position.x;
				verticesQuad[(i*32)+17] = p.position.y-height;
				verticesQuad[(i*32)+18] = p.position.z;
				
				verticesPoint[(i*32)+19] = p.colour.r;
				verticesPoint[(i*32)+20] = p.colour.g;
				verticesPoint[(i*32)+21] = p.colour.b;
				
				verticesPoint[(i*32)+22] = 0.0f;
				verticesPoint[(i*32)+23] = height;
				
				
				verticesQuad[(i*32)+24] = nPos1.x;
				verticesQuad[(i*32)+25] = nPos1.y-height;
				verticesQuad[(i*32)+26] = nPos1.z;
				
				verticesPoint[(i*32)+27] = p.colour.r;
				verticesPoint[(i*32)+28] = p.colour.g;
				verticesPoint[(i*32)+29] = p.colour.b;
				
				verticesPoint[(i*32)+30] = width;
				verticesPoint[(i*32)+31] = height;
			}
			i++;
		}
		
		for (i *= ((pointMode) ? 6 : 32); i < ((pointMode) ? verticesPoint.length : verticesQuad.length); i++)
		{
			if (pointMode) verticesPoint[i] = 0; else verticesQuad[i] = 0;
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
