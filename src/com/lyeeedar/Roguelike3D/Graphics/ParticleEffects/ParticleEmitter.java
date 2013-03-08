/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Graphics.ParticleEffects;

import java.io.Serializable;
import java.nio.FloatBuffer;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.lyeeedar.Roguelike3D.Bag;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;

public class ParticleEmitter implements Serializable {
	
	private transient static final int VERTEX_SIZE = 10;
	
	private static final long serialVersionUID = 6308492057144008114L;
	private transient int signx;
	private transient int signy;
	private transient int signz;
	public transient float distance = 0;
	
	private transient int activeParticles = 0;
	
	private enum ParticleAttribute {
		SPRITE,
		SIZE,
		COLOUR,
		VELOCITY
	}
	
	public final String UID;
	
	// ----- Particle Parameters ----- //
	private TimelineValue<Integer>[] sprite;
	private TimelineValue<Float>[] size;
	private TimelineValue<Float>[] colour;
	private TimelineValue<Float>[] velocity;
	// ----- End Particle Parameters ----- //
	
	// ----- Emitter parameters ----- //
	private int particles;
	private float particleLifetime;
	private float particleLifetimeVar;
	private float emissionTime;
	private float x, y, z;
	private float ex, ey, ez;
	private int emissionType;
	private int blendFuncSRC;
	private int blendFuncDST;
	// ----- End Emitter parameters ----- //
	
	// ----- Transient Variables ----- //
	private transient static ShaderProgram shader;
	private transient static String[] boundSprites;
	private transient TextureAtlas atlas;
	private String atlasName;
	private transient Bag<Particle> active;
	private transient Bag<Particle> inactive;
	private transient Vector3 quad;
	private transient float[] vertices;
	private transient Mesh mesh;
	private transient Random ran;
	private transient Matrix4 tmpMat;
	private transient Matrix4 tmpRot;
	// ----- End Transient Variables ----- //
	
	// ----- Light ----- //
	private transient PointLight light;
	private String lightUID;
	private float lightAttenuation;
	private float lightPower;
	private boolean isLightStatic;
	private Colour lightColour;
	private boolean lightFlicker;
	// ----- End Light ----- //
	
	private transient float emissionCD;
	
	public ParticleEmitter()
	{	
		this.UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
	}
	
	public void setPosition(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void setEmitterParameters(float particleLifetime, float emissionTime, 
			float ex, float ey, float ez,
			int emissionType,
			int blendFuncSRC, int blendFuncDST)
	{
		this.particleLifetime = particleLifetime;
		this.emissionTime = emissionTime;
		this.ex = ex;
		this.ey = ey;
		this.ez = ez;
		this.emissionType = emissionType;
		this.blendFuncSRC = blendFuncSRC;
		this.blendFuncDST = blendFuncDST;
		this.particles = (int) (particleLifetime / emissionTime);
	}
	
	@SuppressWarnings("unchecked")
	public void setParticleParameters(String atlasName, float width, float height, Colour start, Colour end, float vx, float vy, float vz)
	{
		this.atlasName = atlasName;
		this.atlas = GameData.loadAtlas(atlasName);
		
		this.sprite = new TimelineValue[]{new TimelineValue<Integer>(0, 0)};
		
		this.size = new TimelineValue[]{new TimelineValue<Float>(0, width, height)};
		
		this.colour = new TimelineValue[]{new TimelineValue<Float>(0, start.r, start.g, start.b, start.a), new TimelineValue<Float>(particleLifetime, end.r, end.g, end.b, end.a)};
		this.colour[0].setInterpolated(true, this.colour[1]);
		
		this.velocity = new TimelineValue[]{new TimelineValue<Float>(0, vx, vy, vz)};
	}
	
	public void addLight(boolean isStatic, float attenuation, float power, Colour colour, boolean flicker)
	{
		this.lightAttenuation = attenuation;
		this.lightPower = power;
		this.isLightStatic = isStatic;
		this.lightColour = colour;
		this.lightFlicker = flicker;
		
		if (light != null)
		{
			if (isLightStatic) GameData.lightManager.removeStaticLight(light.UID);
			else GameData.lightManager.removeDynamicLight(light.UID);
			
			light = null;
		}

		light = new PointLight(new Vector3(x+(ex/2f), y+ey, z+(ez/2)), colour, attenuation, power);
		lightUID = light.UID;
		
		if (isStatic) GameData.lightManager.addStaticLight(light);
		else GameData.lightManager.addDynamicLight(light);
	}
	
	public void create() {
		
		if (shader == null)
		{
			shader = new ShaderProgram(shader_vertex, shader_fragment);
		}
		if (boundSprites == null)
		{
			boundSprites = new String[10];
		}
		
		ran = new Random();
		for (int i = 0; i < spriteNames.length; i++)
		{
			sprites[i] = GameData.loadTexture(spriteNames[i], true);
		}
		
		active = new Bag<Particle>(particles);
		inactive = new Bag<Particle>(particles);
		
		for (int i = 0; i < particles; i++)
		{
			Particle p = new Particle();
			inactive.add(p);
		}
		
		vertices = new float[particles*VERTEX_SIZE*10];
		mesh = new Mesh(false, particles*4, particles*6, 
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Generic, 4, "a_colour"),
				new VertexAttribute(Usage.Generic, 1, "a_sprite"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		mesh.setVertices(vertices);
		mesh.setIndices(genIndices(particles));
		
		quad = new Vector3();
		tmpMat = new Matrix4();
		tmpRot = new Matrix4();
	}
	
	public void dispose()
	{
		mesh.dispose();
		mesh = null;
		
		if (light != null) {
			if (isLightStatic) GameData.lightManager.removeStaticLight(light.UID);
			else GameData.lightManager.removeDynamicLight(light.UID);
			
			light = null;
		}
	}
	
	public void fixReferences()
	{
		create();
		
		if (lightUID != null)
		{
			if (isLightStatic)
			{
				light = GameData.lightManager.getStaticLight(lightUID);
			}
			else
			{
				light = GameData.lightManager.getDynamicLight(lightUID);
			}
		}
	}
	
	public short[] genIndices(int faces)
	{
		short[] indices = new short[faces * 6];
		
		for (short i = 0; i < faces; i++)
		{
			indices[(i*6)+0] = (short) ((i*4)+0);
			indices[(i*6)+1] = (short) ((i*4)+1);
			indices[(i*6)+2] = (short) ((i*4)+2);
			
			indices[(i*6)+3] = (short) ((i*4)+0);
			indices[(i*6)+4] = (short) ((i*4)+2);
			indices[(i*6)+5] = (short) ((i*4)+3);
		}
		return indices;
	}

	public void render()
	{
		atlas.getTextures().

		Gdx.gl.glBlendFunc(blendFuncSRC, blendFuncDST);
		
		mesh.render(shader, GL20.GL_TRIANGLES, 0, VERTEX_SIZE * activeParticles);
	}
	
	public static void begin(Camera cam)
	{
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(false);
		
		shader.begin();
		shader.setUniformMatrix("u_pv", cam.combined);
	}
	
	public static void end()
	{
		shader.end();
		
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	public void update(float delta, Camera cam)
	{
		if (light != null)
		{
			if (lightFlicker) light.attenuation = (float) (lightAttenuation *
					(1-((1-((float)inactive.size() / (float)active.size())))/2));
		}
		
		Iterator<Particle> pItr = active.iterator();
		
		int i = 0;
		while (pItr.hasNext())
		{
			Particle p = pItr.next();
			
			Float[] velocity = getValue(p.lifetime, ParticleAttribute.VELOCITY);
			velocity[0] *= delta;
			velocity[1] *= delta;
			velocity[2] *= delta;
			p.update(delta, velocity[0], velocity[1], velocity[2]);
			
			tmpRot.setToLookAt(cam.direction, GameData.UP);
			tmpMat.setToTranslation(p.x, p.y, p.z).mul(tmpRot);
			
			if (p.lifetime > particleLifetime)
			{
				pItr.remove();
				inactive.add(p);
				continue;
			}
			
			Integer[] sprite = getValue(p.lifetime, ParticleAttribute.SPRITE);
			Float[] size = getValue(p.lifetime, ParticleAttribute.SIZE);
			Float[] colour = getValue(p.lifetime, ParticleAttribute.COLOUR);
			
			Vector3 nPos = quad.set(p.x, p.y, p.z).add(-size[0]/2, size[1]/2, 0).mul(tmpMat);

			vertices[(i*VERTEX_SIZE)+0] = nPos.x;
			vertices[(i*VERTEX_SIZE)+1] = nPos.y;
			vertices[(i*VERTEX_SIZE)+2] = nPos.z;
			
			vertices[(i*VERTEX_SIZE)+3] = colour[0];
			vertices[(i*VERTEX_SIZE)+4] = colour[1];
			vertices[(i*VERTEX_SIZE)+5] = colour[2];
			vertices[(i*VERTEX_SIZE)+6] = colour[3];
			
			vertices[(i*VERTEX_SIZE)+7] = sprite[0];
			
			vertices[(i*VERTEX_SIZE)+8] = 0.0f;
			vertices[(i*VERTEX_SIZE)+9] = 0.0f;
			
			nPos = quad.set(p.x, p.y, p.z).add(size[0]/2, size[1]/2, 0).mul(tmpMat);

			vertices[(i*VERTEX_SIZE)+10] = nPos.x;
			vertices[(i*VERTEX_SIZE)+11] = nPos.y;
			vertices[(i*VERTEX_SIZE)+12] = nPos.z;
			
			vertices[(i*VERTEX_SIZE)+13] = colour[0];
			vertices[(i*VERTEX_SIZE)+14] = colour[1];
			vertices[(i*VERTEX_SIZE)+15] = colour[2];
			vertices[(i*VERTEX_SIZE)+16] = colour[3];
			
			vertices[(i*VERTEX_SIZE)+17] = sprite[0];
			
			vertices[(i*VERTEX_SIZE)+18] = 1.0f;
			vertices[(i*VERTEX_SIZE)+19] = 0.0f;
			
			nPos = quad.set(p.x, p.y, p.z).add(-size[0]/2, -size[1]/2, 0).mul(tmpMat);

			vertices[(i*VERTEX_SIZE)+20] = nPos.x;
			vertices[(i*VERTEX_SIZE)+21] = nPos.y;
			vertices[(i*VERTEX_SIZE)+22] = nPos.z;
			
			vertices[(i*VERTEX_SIZE)+23] = colour[0];
			vertices[(i*VERTEX_SIZE)+24] = colour[1];
			vertices[(i*VERTEX_SIZE)+25] = colour[2];
			vertices[(i*VERTEX_SIZE)+26] = colour[3];
			
			vertices[(i*VERTEX_SIZE)+27] = sprite[0];
			
			vertices[(i*VERTEX_SIZE)+28] = 0.0f;
			vertices[(i*VERTEX_SIZE)+29] = 1.0f;
			
			nPos = quad.set(p.x, p.y, p.z).add(size[0]/2, -size[1]/2, 0).mul(tmpMat);

			vertices[(i*VERTEX_SIZE)+30] = nPos.x;
			vertices[(i*VERTEX_SIZE)+31] = nPos.y;
			vertices[(i*VERTEX_SIZE)+32] = nPos.z;
			
			vertices[(i*VERTEX_SIZE)+33] = colour[0];
			vertices[(i*VERTEX_SIZE)+34] = colour[1];
			vertices[(i*VERTEX_SIZE)+35] = colour[2];
			vertices[(i*VERTEX_SIZE)+36] = colour[3];
			
			vertices[(i*VERTEX_SIZE)+37] = sprite[0];
			
			vertices[(i*VERTEX_SIZE)+38] = 1.0f;
			vertices[(i*VERTEX_SIZE)+39] = 1.0f;

			i++;
		}
		mesh.setVertices(vertices);
		activeParticles = active.size();
		
		emissionCD -= delta;
		
		if (inactive.size() == 0) return;
		
		while (emissionCD < 0 && inactive.size() > 0)
		{
			Particle p = inactive.remove(0);
			
			if (emissionType == 0)
			{
				signx = (ran.nextInt(2) == 0) ? 1 : -1;
				signy = (ran.nextInt(2) == 0) ? 1 : -1;
				signz = (ran.nextInt(2) == 0) ? 1 : -1;
				p.set(particleLifetimeVar*ran.nextFloat(),
						x+(float)(ex*ran.nextGaussian()*signx), 
						y+(float)(ey*ran.nextGaussian()*signy),
						z+(float)(ez*ran.nextGaussian()*signz));

			}
			active.add(p);
			
			emissionCD += emissionTime;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T extends Number> T[] getValue(float time, ParticleAttribute pa) {
		TimelineValue tv = null;
		if (pa == ParticleAttribute.SPRITE)
		{
			tv = searchTimeline(time, sprite);
		}
		else if (pa == ParticleAttribute.SIZE)
		{
			tv = searchTimeline(time, size);
		}
		else if (pa == ParticleAttribute.COLOUR)
		{
			tv = searchTimeline(time, colour);
		}
		else if (pa == ParticleAttribute.VELOCITY)
		{
			tv = searchTimeline(time, velocity);
		}
		
		return (T[]) tv.getValue(time);
	}
	
	private <T extends Number> TimelineValue<T> searchTimeline(float time, TimelineValue<T>[] value)
	{
		for (int i = 0; i < value.length; i++)
		{
			if (value[i].time > time)
			{
				return value[i-1];
			}
		}
		return value[value.length-1];
	}

	class Particle {
		float lifetime;
		float x, y, z;
		
		public Particle()
		{
		}
		
		public void update(float delta, float vx, float vy, float vz)
		{
			lifetime += delta;
			x += vx;
			y += vy;
			z += vz;
		}
		
		public void set(float lifetime, float x, float y, float z)
		{
			this.lifetime = lifetime;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	class TimelineValue<T extends Number> implements Serializable {
		
		private static final long serialVersionUID = 5296859966301178337L;
		
		final Number[] values;
		final float time;
		
		boolean interpolated = false;
		Number[] valueStep;
		Number[] interpolatedValues;
		
		transient float timeStep;
		
		public TimelineValue(float time, T... values)
		{
			this.values = values;
			this.time = time;
		}
		
		@SuppressWarnings("unchecked")
		public T[] getValue(float currentTime)
		{
			if (!interpolated)
			{
				return (T[]) values;
			}

			timeStep = currentTime-time;
			for (int i = 0; i < values.length; i++)
			{
				interpolatedValues[i] = values[i].floatValue()+(valueStep[i].floatValue()*timeStep);
			}
			
			return (T[]) interpolatedValues;
		}
		
		public void setInterpolated(boolean interpolated, TimelineValue<T> nextValue)
		{
			this.interpolated = interpolated;
			
			if (interpolated)
			{
				interpolatedValues = new Number[values.length];
				valueStep = new Number[values.length];
				
				for (int i = 0; i < nextValue.values.length; i++)
				{
					valueStep[i] = (nextValue.values[i].floatValue() - values[i].floatValue()) / (nextValue.time - time);
				}
			}
		}
	}

	private static final int[] texture_indexes = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
	
	private static final String shader_vertex = 
			"attribute vec3 a_position;" + "\n" +
			"attribute vec4 a_colour;" + "\n" +
			"attribute float a_sprite" + "\n" +
			"attribute vec2 a_texCoords;" + "\n" +

			"uniform mat4 u_pv;" + "\n" +

			"varying vec4 v_colour;" + "\n" +
			"varying int v_sprite;" + "\n" +
			"varying vec2 v_texCoords;" + "\n" +

			"void main() {" + "\n" +
				"v_sprite = int(a_sprite);" + "\n" +
				"v_colour = a_colour;" + "\n" +
				"v_texCoords = a_texCoords;" + "\n" +

				"gl_Position = u_pv * vec4(a_position, 1.0);" + "\n" +
			"}";

	private static final String shader_fragment = 
			"uniform sampler2D u_texture[10];" + "\n" +
	
			"varying vec4 v_colour;" + "\n" +
			"varying int v_sprite;" + "\n" +
			"varying vec2 v_texCoords;" + "\n" +

			"void main() {" + "\n" +
				"gl_FragColor = texture2D(u_texture[v_sprite], v_texCoords) * v_colour;" + "\n" +
	   		"}";
}