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
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Bag;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;

public class ParticleEmitter implements Serializable {
	
	private transient static final int VERTEX_SIZE = 9;
	
	private static final long serialVersionUID = 6308492057144008114L;
	private transient Vector3 pos;
	private transient int signx;
	private transient int signy;
	private transient int signz;
	public transient float distance = 0;
	
	private enum ParticleAttribute {
		SPRITE,
		SIZE,
		COLOUR,
		VELOCITY
	}
	
	public final String UID;
	
	// ----- Particle Parameters ----- //
	private TimelineInteger[] sprite;
	private TimelineFloat[] size;
	private TimelineFloat[] colour;
	private TimelineFloat[] velocity;
	// ----- End Particle Parameters ----- //
	
	// ----- Emitter parameters ----- //
	private final int particles;
	private final float particleLifetime;
	private final float particleLifetimeVar;
	private final float emissionTime;
	private float x, y, z;
	private final float ex, ey, ez;
	private final float radius;
	private final int emissionType;
	private final int blendFuncSRC;
	private final int blendFuncDST;
	// ----- End Emitter parameters ----- //
	
	// ----- Transient Variables ----- //
	private transient static ShaderProgram shader;
	private transient static String boundAtlas;
	private transient Texture atlasTexture;
	private transient float[][] topLeftTexCoords;
	private transient float[][] topRightTexCoords;
	private transient float[][] botLeftTexCoords;
	private transient float[][] botRightTexCoords;
	private String atlasName;
	private transient Bag<Particle> active;
	private transient Bag<Particle> inactive;
	private transient Vector3 quad;
	private transient float[] vertices;
	private transient Mesh mesh;
	private transient Random ran;
	private transient Matrix4 tmpMat;
	private transient Matrix4 tmpRot;
	private transient int v;
	private transient float emissionCD;
	// ----- End Transient Variables ----- //
	
	// ----- Light ----- //
	private transient PointLight light;
	private String lightUID;
	private float lightAttenuation;
	private float lightPower;
	private boolean isLightStatic;
	private Color lightColour;
	private boolean lightFlicker;
	// ----- End Light ----- //
	
	public ParticleEmitter(float particleLifetime, float particleLifetimeVar, float emissionTime, 
			float ex, float ey, float ez,
			int emissionType,
			int blendFuncSRC, int blendFuncDST,
			String atlasName)
	{
		this.UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		
		this.particleLifetime = particleLifetime;
		this.particleLifetimeVar = particleLifetimeVar;
		this.emissionTime = emissionTime;
		this.ex = ex;
		this.ey = ey;
		this.ez = ez;
		this.emissionType = emissionType;
		this.blendFuncSRC = blendFuncSRC;
		this.blendFuncDST = blendFuncDST;
		this.particles = (int) (particleLifetime / emissionTime);
		this.radius = ex+ey+ez;
		this.atlasName = atlasName;
	}
	
	public int getActiveParticles() {
		return active.size();
	}
	
	public float getRadius()
	{
		return radius;
	}
	
	public Vector3 getPosition()
	{
		return pos.set(x, y, z);
	}
	
	public void setPosition(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		
		if (light != null) light.position.set(x, y, z);
	}

	/**
	 * Set the sprite number timeline. <p>
	 *  Each point in the timeline is specified by a float array. <br> array[0] is the time in the timeline, array[1] is the sprite number (casted to an int)
	 *  Optionally setting interpolated to true will linearly interpolate between each value in the timeline
	 * @param values
	 */
	public void setSpriteTimeline(boolean interpolated, float[]... values)
	{
		this.sprite = new TimelineInteger[values.length];
		
		for (int i = 0; i < values.length; i++)
		{
			this.sprite[i] = new TimelineInteger(values[i][0], (int)values[i][1]);
		}
		
		for (int i = 0; i < values.length-1; i++)
		{
			this.sprite[i].setInterpolated(true, this.sprite[i+1]);
		}
	}
	
	/**
	 * Set the size timeline. <p>
	 *  Each point in the timeline is specified by a float array. <br> array[0] is the time in the timeline, array[1] is the width, array[2] is the height
	 *  Optionally setting interpolated to true will linearly interpolate between each value in the timeline
	 * @param interpolated
	 * @param values
	 */
	public void setSizeTimeline(boolean interpolated, float[]... values)
	{
		this.size = new TimelineFloat[values.length];
		
		for (int i = 0; i < values.length; i++)
		{
			this.size[i] = new TimelineFloat(values[i][0], values[i][1], values[i][2]);
		}
		
		for (int i = 0; i < values.length-1; i++)
		{
			this.size[i].setInterpolated(true, this.size[i+1]);
		}
	}
	
	/**
	 * Set the colour timeline. <p>
	 *  Each point in the timeline is specified by a float array. <br> array[0] is the time in the timeline, array[1] is red, array[2] is green, array[3] is blue and array[4] is alpha
	 *  Optionally setting interpolated to true will linearly interpolate between each value in the timeline
	 * @param interpolated
	 * @param values
	 */
	public void setColourTimeline(boolean interpolated, float[]... values)
	{
		this.colour = new TimelineFloat[values.length];
		
		for (int i = 0; i < values.length; i++)
		{
			this.colour[i] = new TimelineFloat(values[i][0], values[i][1], values[i][2], values[i][3], values[i][4]);
		}
		
		for (int i = 0; i < values.length-1; i++)
		{
			this.colour[i].setInterpolated(true, this.colour[i+1]);
		}
	}
	
	/**
	 * Set the velocity timeline. <p>
	 *  Each point in the timeline is specified by a float array. <br> array[0] is the time in the timeline, array[1] is the x velocity, array[2] is the y velocity and array[3] is the z velocity
	 *  Optionally setting interpolated to true will linearly interpolate between each value in the timeline
	 * @param interpolated
	 * @param values
	 */
	public void setVelocityTimeline(boolean interpolated, float[]... values)
	{
		this.velocity = new TimelineFloat[values.length];
		
		for (int i = 0; i < values.length; i++)
		{
			this.velocity[i] = new TimelineFloat(values[i][0], values[i][1], values[i][2], values[i][3]);
		}
		
		for (int i = 0; i < values.length-1; i++)
		{
			this.velocity[i].setInterpolated(true, this.velocity[i+1]);
		}
	}
	
	/**
	 * Method to create a basic particle emitter. <p>
	 * This particles in this emitter will have a constant width and height,
	 *  a constant velocity (vx, vy, vz)
	 *   and will interpolate its colour from the start colour to the end over the particles lifetime.
	 * @param width
	 * @param height
	 * @param start
	 * @param end
	 * @param vx
	 * @param vy
	 * @param vz
	 */
	public void createBasicEmitter(float width, float height, Color start, Color end, float vx, float vy, float vz)
	{
		this.sprite = new TimelineInteger[]{new TimelineInteger(0, 0)};
		
		this.size = new TimelineFloat[]{new TimelineFloat(0, width, height)};
		
		this.colour = new TimelineFloat[]{new TimelineFloat(0, start.r, start.g, start.b, start.a), new TimelineFloat(particleLifetime, end.r, end.g, end.b, end.a)};
		this.colour[0].setInterpolated(true, this.colour[1]);
		
		this.velocity = new TimelineFloat[]{new TimelineFloat(0, vx, vy, vz)};
	}
	
	public void addLight(boolean isStatic, float attenuation, float power, Color colour, boolean flicker)
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
			shader = new ShaderProgram(SHADER_VERTEX, SHADER_FRAGMENT);
		}
		
		ran = new Random();

		TextureAtlas atlas = GameData.loadAtlas(atlasName);
		Set<Texture> atlasTextures = atlas.getTextures();
		Iterator<Texture> itr = atlasTextures.iterator();
	
		atlasTexture = itr.next();
		
		int maxIndex = 0;
		for (TimelineInteger spriteTL : sprite)
		{
			Integer index = spriteTL.getValues()[0];
			
			if (index > maxIndex) maxIndex = index;
		}
		
		topLeftTexCoords = new float[maxIndex+1][2];
		topRightTexCoords = new float[maxIndex+1][2];
		botLeftTexCoords = new float[maxIndex+1][2];
		botRightTexCoords = new float[maxIndex+1][2];
		
		for (int i = 0; i < maxIndex+1; i++)
		{
			AtlasRegion region = atlas.findRegion("sprite"+i);
			
			float[] tl = {(float)region.getRegionX()/(float)atlasTexture.getWidth(), (float)region.getRegionY()/(float)atlasTexture.getHeight()};
			float[] tr = {(float)(region.getRegionX()+region.getRegionWidth())/(float)atlasTexture.getWidth(), (float)region.getRegionY()/(float)atlasTexture.getHeight()};
			float[] bl = {(float)region.getRegionX()/(float)atlasTexture.getWidth(), (float)(region.getRegionY()+region.getRegionHeight())/(float)atlasTexture.getHeight()};
			float[] br = {(float)(region.getRegionX()+region.getRegionWidth())/(float)atlasTexture.getWidth(), (float)(region.getRegionY()+region.getRegionHeight())/(float)atlasTexture.getHeight()};
			
			topLeftTexCoords[i] = tl;
			topRightTexCoords[i] = tr;
			botLeftTexCoords[i] = bl;
			botRightTexCoords[i] = br;
		}
		
		active = new Bag<Particle>(particles);
		inactive = new Bag<Particle>(particles);
		
		for (int i = 0; i < particles; i++)
		{
			Particle p = new Particle();
			inactive.add(p);
		}
		
		vertices = new float[particles*VERTEX_SIZE*4];
		mesh = new Mesh(false, particles*4, particles*6,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Generic, 4, "a_colour"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		mesh.setVertices(vertices);
		mesh.setIndices(genIndices(particles));
		
		quad = new Vector3();
		tmpMat = new Matrix4();
		tmpRot = new Matrix4();
		pos = new Vector3();
	}
	
	public void dispose()
	{
		if (mesh != null) mesh.dispose();
		mesh = null;
	}
	
	public void delete()
	{
		if (light != null) {
			if (isLightStatic) GameData.lightManager.removeStaticLight(light.UID);
			else GameData.lightManager.removeDynamicLight(light.UID);
			
			light = null;
		}
	}
	
	public void fixReferences()
	{
		create();
		
		pos = new Vector3();
		
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
			
			indices[(i*6)+3] = (short) ((i*4)+1);
			indices[(i*6)+4] = (short) ((i*4)+3);
			indices[(i*6)+5] = (short) ((i*4)+2);
		}
		return indices;
	}

	public void render()
	{
		Gdx.gl.glBlendFunc(blendFuncSRC, blendFuncDST);
		
		if (boundAtlas != null && atlasName.equals(boundAtlas)) {
			
		}
		else
		{
			atlasTexture.bind(0);
			shader.setUniformi("u_texture", 0);
			
			boundAtlas = atlasName;
		}
		
		mesh.render(shader, GL20.GL_TRIANGLES, 0, active.size()*4);
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
		
		boundAtlas = null;
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
			
			Float[] velocity = getAttributeValue(p.lifetime, ParticleAttribute.VELOCITY);

			p.update(delta, velocity[0], velocity[1], velocity[2]);

			if (p.lifetime > particleLifetime)
			{
				pItr.remove();
				inactive.add(p);
				continue;
			}
			
			//tmpRot.setToLookAt(cam.direction, cam.up);
			//tmpMat.setToTranslation(p.x, p.y, p.z);//.mul(tmpRot);
			tmpMat.setToTranslation(p.x, p.y, p.z).mul(GameData.player.vo.attributes.getRotation());
			
			Integer[] sprite = getAttributeValue(p.lifetime, ParticleAttribute.SPRITE);
			Float[] size = getAttributeValue(p.lifetime, ParticleAttribute.SIZE);
			Float[] colour = getAttributeValue(p.lifetime, ParticleAttribute.COLOUR);
			
			Vector3 nPos = quad
					.set(-size[0].floatValue()/2, size[1].floatValue()/2, 0)
					.mul(tmpMat);

			v = 0;
			
			vertices[(i*VERTEX_SIZE*4)+v+0] = nPos.x;
			vertices[(i*VERTEX_SIZE*4)+v+1] = nPos.y;
			vertices[(i*VERTEX_SIZE*4)+v+2] = nPos.z;
			
			vertices[(i*VERTEX_SIZE*4)+v+3] = colour[0];
			vertices[(i*VERTEX_SIZE*4)+v+4] = colour[1];
			vertices[(i*VERTEX_SIZE*4)+v+5] = colour[2];
			vertices[(i*VERTEX_SIZE*4)+v+6] = colour[3];

			vertices[(i*VERTEX_SIZE*4)+v+7] = topLeftTexCoords[sprite[0]][0];
			vertices[(i*VERTEX_SIZE*4)+v+8] = topLeftTexCoords[sprite[0]][1];
			
			nPos = quad
					.set(size[0]/2, size[1]/2, 0)
					.mul(tmpMat);

			v += VERTEX_SIZE;
			
			vertices[(i*VERTEX_SIZE*4)+v+0] = nPos.x;
			vertices[(i*VERTEX_SIZE*4)+v+1] = nPos.y;
			vertices[(i*VERTEX_SIZE*4)+v+2] = nPos.z;
			
			vertices[(i*VERTEX_SIZE*4)+v+3] = colour[0];
			vertices[(i*VERTEX_SIZE*4)+v+4] = colour[1];
			vertices[(i*VERTEX_SIZE*4)+v+5] = colour[2];
			vertices[(i*VERTEX_SIZE*4)+v+6] = colour[3];
			
			vertices[(i*VERTEX_SIZE*4)+v+7] = topRightTexCoords[sprite[0]][0];
			vertices[(i*VERTEX_SIZE*4)+v+8] = topRightTexCoords[sprite[0]][1];
			
			nPos = quad
					.set(-size[0]/2, -size[1]/2, 0)
					.mul(tmpMat);

			v += VERTEX_SIZE;
			
			vertices[(i*VERTEX_SIZE*4)+v+0] = nPos.x;
			vertices[(i*VERTEX_SIZE*4)+v+1] = nPos.y;
			vertices[(i*VERTEX_SIZE*4)+v+2] = nPos.z;
			
			vertices[(i*VERTEX_SIZE*4)+v+3] = colour[0];
			vertices[(i*VERTEX_SIZE*4)+v+4] = colour[1];
			vertices[(i*VERTEX_SIZE*4)+v+5] = colour[2];
			vertices[(i*VERTEX_SIZE*4)+v+6] = colour[3];
			
			vertices[(i*VERTEX_SIZE*4)+v+7] = botLeftTexCoords[sprite[0]][0];
			vertices[(i*VERTEX_SIZE*4)+v+8] = botLeftTexCoords[sprite[0]][1];
			
			nPos = quad
					.set(size[0]/2, -size[1]/2, 0)
					.mul(tmpMat);

			v += VERTEX_SIZE;
			
			vertices[(i*VERTEX_SIZE*4)+v+0] = nPos.x;
			vertices[(i*VERTEX_SIZE*4)+v+1] = nPos.y;
			vertices[(i*VERTEX_SIZE*4)+v+2] = nPos.z;
			
			vertices[(i*VERTEX_SIZE*4)+v+3] = colour[0];
			vertices[(i*VERTEX_SIZE*4)+v+4] = colour[1];
			vertices[(i*VERTEX_SIZE*4)+v+5] = colour[2];
			vertices[(i*VERTEX_SIZE*4)+v+6] = colour[3];
			
			vertices[(i*VERTEX_SIZE*4)+v+7] = botRightTexCoords[sprite[0]][0];
			vertices[(i*VERTEX_SIZE*4)+v+8] = botRightTexCoords[sprite[0]][1];

			i++;
		}
		mesh.setVertices(vertices);
		
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
						z+(float)(ez*ran.nextGaussian()*signz)
						);

			}
			else
			{
				System.err.println("Invalid emission type! "+emissionType);
			}
			active.add(p);
			
			emissionCD += emissionTime;
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Number, N extends TimelineValue<T, N>> T[] getAttributeValue(float time, ParticleAttribute pa) {
		TimelineValue<T, N> tv = null;
		if (pa == ParticleAttribute.SPRITE)
		{
			tv = (TimelineValue<T, N>) searchTimeline(time, sprite);
		}
		else if (pa == ParticleAttribute.SIZE)
		{
			tv = (TimelineValue<T, N>) searchTimeline(time, size);
		}
		else if (pa == ParticleAttribute.COLOUR)
		{
			tv = (TimelineValue<T, N>) searchTimeline(time, colour);
		}
		else if (pa == ParticleAttribute.VELOCITY)
		{
			tv = (TimelineValue<T, N>) searchTimeline(time, velocity);
		}
		
		return tv.getValuesInterpolated(time);
	}
	
	private <T extends Number, N extends TimelineValue<T, N>> TimelineValue<T, N> searchTimeline(float time, TimelineValue<T, N>[] value)
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

	public ParticleEmitter copy()
	{
		ParticleEmitter copy = new ParticleEmitter(particleLifetime, particleLifetimeVar, emissionTime, VERTEX_SIZE, ey, ez, emissionType, blendFuncSRC, blendFuncDST, atlasName);
		
		TimelineInteger[] cpySprite = new TimelineInteger[sprite.length];
		for (int i = 0; i < sprite.length; i++) cpySprite[i] = sprite[i].copy();
		copy.sprite = cpySprite;
		
		TimelineFloat[] cpySize = new TimelineFloat[size.length];
		for (int i = 0; i < size.length; i++) cpySize[i] = size[i].copy();
		copy.size = cpySize;

		TimelineFloat[] cpyColour = new TimelineFloat[colour.length];
		for (int i = 0; i < colour.length; i++) cpyColour[i] = colour[i].copy();
		copy.colour = cpyColour;
		
		TimelineFloat[] cpyVelocity = new TimelineFloat[velocity.length];
		for (int i = 0; i < velocity.length; i++) cpyVelocity[i] = velocity[i].copy();
		copy.velocity = cpyVelocity;

		if (lightUID != null)
			copy.addLight(isLightStatic, lightAttenuation, lightPower, lightColour, lightFlicker);
		
		return copy;
	}
	
	/**
	 * A particle, containing its current lifetime and position.
	 * @author Philip
	 *
	 */
	class Particle {
		float lifetime;
		float x, y, z;
		
		public Particle()
		{
		}
		
		public void update(float delta, float vx, float vy, float vz)
		{
			lifetime += delta;
			x += vx*delta;
			y += vy*delta;
			z += vz*delta;
		}
		
		public void set(float lifetime, float x, float y, float z)
		{
			this.lifetime = lifetime;
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
	abstract class TimelineValue<T extends Number, N extends TimelineValue<T, N>> implements Serializable
	{
		private static final long serialVersionUID = -4434625075360858305L;
		
		final T[] values;
		final float time;
		
		boolean interpolated = false;
		Float[] valueStep;
		T[] interpolatedValues;
		
		transient float timeStep;
		
		public TimelineValue(float time, T[] values)
		{
			this.time = time;
			this.values = values;
		}
		
		public T[] getValues()
		{
			return values;
		}
		
		protected void setValues(boolean interpolated, Float[] valueStep, T[] interpolatedValues)
		{
			this.interpolated = interpolated;
			this.valueStep = valueStep;
			this.interpolatedValues = interpolatedValues;
		}
		
		public abstract T[] getValuesInterpolated(float currentTime);
		public abstract void setInterpolated(boolean interpolated, N nextValue);
		public abstract N copy();
		
	}

	class TimelineInteger extends TimelineValue<Integer, TimelineInteger> 
	{

		private static final long serialVersionUID = -6679143723684340688L;

		public TimelineInteger(float time, Integer... values)
		{
			super(time, values);
		}
		
		public Integer[] getValuesInterpolated(float currentTime)
		{
			if (!interpolated)
			{
				return values;
			}

			timeStep = currentTime-time;
			for (int i = 0; i < values.length; i++)
			{
				Float value = values[i].floatValue()+(valueStep[i].floatValue()*timeStep);
				interpolatedValues[i] = value.intValue();
			}

			return interpolatedValues;
		}
		
		public void setInterpolated(boolean interpolated, TimelineInteger nextValue)
		{
			this.interpolated = interpolated;
			
			if (interpolated)
			{
				interpolatedValues = new Integer[values.length];
				valueStep = new Float[values.length];
				
				for (int i = 0; i < nextValue.values.length; i++)
				{
					Float value = (nextValue.values[i].floatValue() - values[i].floatValue()) / (nextValue.time - time);
					valueStep[i] = value;
				}
			}
		}
		
		public TimelineInteger copy()
		{
			TimelineInteger copy = new TimelineInteger(time, values);
			copy.setValues(interpolated, valueStep, interpolatedValues);
			return copy;
		}
	}

	class TimelineFloat extends TimelineValue<Float, TimelineFloat>
	{

		private static final long serialVersionUID = -5219903547907274562L;

		public TimelineFloat(float time, Float... values)
		{
			super(time, values);
		}
		
		public Float[] getValuesInterpolated(float currentTime)
		{
			if (!interpolated)
			{
				return values;
			}

			timeStep = currentTime-time;
			for (int i = 0; i < values.length; i++)
			{
				Float value = values[i].floatValue()+(valueStep[i].floatValue()*timeStep);
				interpolatedValues[i] = value.floatValue();
			}

			return interpolatedValues;
		}
		
		public void setInterpolated(boolean interpolated, TimelineFloat nextValue)
		{
			this.interpolated = interpolated;
			
			if (interpolated)
			{
				interpolatedValues = new Float[values.length];
				valueStep = new Float[values.length];
				
				for (int i = 0; i < nextValue.values.length; i++)
				{
					Float value = (nextValue.values[i].floatValue() - values[i].floatValue()) / (nextValue.time - time);
					valueStep[i] = value;
				}
			}
		}
		
		public TimelineFloat copy()
		{
			TimelineFloat copy = new TimelineFloat(time, values);
			copy.setValues(interpolated, valueStep, interpolatedValues);
			return copy;
		}
	}
	
	private static final String SHADER_VERTEX = 
			"attribute vec3 a_position;" + "\n" +
			"attribute vec4 a_colour;" + "\n" +
			"attribute vec2 a_texCoords;" + "\n" +

			"uniform mat4 u_pv;" + "\n" +

			"varying vec4 v_colour;" + "\n" +
			"varying vec2 v_texCoords;" + "\n" +

			"void main() {" + "\n" +
				"v_colour = a_colour;" + "\n" +
				"v_texCoords = a_texCoords;" + "\n" +

				"gl_Position = u_pv * vec4(a_position, 1.0);" + "\n" +
			"}";

	private static final String SHADER_FRAGMENT = 
			"uniform sampler2D u_texture;" + "\n" +
	
			"varying vec4 v_colour;" + "\n" +
			"varying vec2 v_texCoords;" + "\n" +

			"void main() {" + "\n" +
				"gl_FragColor = texture2D(u_texture, v_texCoords) * v_colour;" + "\n" +
	   		"}";
}