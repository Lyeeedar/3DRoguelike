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
package com.lyeeedar.Graphics.ParticleEffects;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.OrderedMap;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Utils.Bag;
import com.lyeeedar.Utils.FileUtils;

public class ParticleEmitter implements Serializable {

	private transient static final int VERTEX_SIZE = 9;

	private static final long serialVersionUID = 6308492057144008114L;

	public enum ParticleAttribute {
		SPRITE,
		SIZE,
		COLOUR,
		VELOCITY
	}

	public final String UID;
	public String name;

	// ----- Particle Parameters ----- //
	private TimelineValue[] sprite;
	private TimelineValue[] size;
	private TimelineValue[] colour;
	private TimelineValue[] velocity;
	// ----- End Particle Parameters ----- //

	// ----- Emitter parameters ----- //
	public int maxParticles;
	public float particleLifetime;
	public float particleLifetimeVar;
	public float emissionTime;
	public float ex, ey, ez;
	public int emissionType;
	public int blendFuncSRC;
	public int blendFuncDST;
	public String atlasName;
	// ----- End Emitter parameters ----- //

	// ----- Light ----- //
	private float lightAttenuation;
	private float lightPower;
	private boolean isLightStatic;
	private Color lightColour;
	private boolean lightFlicker;
	private float lightx, lighty, lightz;
	// ----- End Light ----- //

	// ----- Transient Variables ----- //
	public transient float distance = 0;
	private transient static ShaderProgram shader;
	private transient static String currentAtlas;
	private transient static int currentBlendSRC, currentBlendDST;
	public transient TextureAtlas atlas;
	public transient Texture atlasTexture;
	private transient float[][] topLeftTexCoords;
	private transient float[][] topRightTexCoords;
	private transient float[][] botLeftTexCoords;
	private transient float[][] botRightTexCoords;
	private transient Bag<Particle> active;
	private transient Bag<Particle> inactive;
	private transient Vector3 quad;
	private transient float[] vertices;
	private transient Mesh mesh;
	private transient Random ran;
	private transient Matrix4 tmpMat;
	private transient Matrix4 tmpRot;
	private transient Vector3 pos;
	private transient int signx;
	private transient int signy;
	private transient int signz;
	private transient int v;
	private transient float emissionCD;
	private transient PointLight light;
	private transient int i;
	private transient int i2;
	private transient int arrayLen;
	// ----- End Transient Variables ----- //

	// ----- Non-Essential Variables ----- //
	private String lightUID;
	private float x, y, z;
	private float radius;
	// ----- End Non-Essential Variables ----- //

	public ParticleEmitter(float particleLifetime, float particleLifetimeVar, float emissionTime, 
			float ex, float ey, float ez,
			int emissionType,
			int blendFuncSRC, int blendFuncDST,
			String atlasName,
			String name)
	{
		this.UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		this.name = name;

		this.particleLifetime = particleLifetime;
		this.particleLifetimeVar = particleLifetimeVar;
		this.emissionTime = emissionTime;
		this.ex = ex;
		this.ey = ey;
		this.ez = ez;
		this.emissionType = emissionType;
		this.blendFuncSRC = blendFuncSRC;
		this.blendFuncDST = blendFuncDST;
		this.atlasName = atlasName;
	}

	public int getActiveParticles() {
		return active.size;
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

		if (light != null) light.position.set(x+lightx, y+lighty, z+lightz);
	}

	public void setTimeline(TimelineValue[] sprite, TimelineValue[] size, TimelineValue[] colour, TimelineValue[] velocity)
	{
		this.sprite = sprite;
		this.size = size;
		this.colour = colour;
		this.velocity = velocity;
	}

	public TimelineValue[] getSpriteTimeline()
	{
		return sprite;
	}
	/**
	 * Set the sprite number timeline. <p>
	 *  Each point in the timeline is specified by a float array. <br> array[0] is the time in the timeline, array[1] is the sprite number (casted to an int)
	 *  Optionally setting interpolated to true will linearly interpolate between each value in the timeline
	 * @param values
	 */
	public void setSpriteTimeline(boolean interpolated, float[]... values)
	{
		this.sprite = new TimelineValue[values.length];

		for (int i = 0; i < values.length; i++)
		{
			this.sprite[i] = new TimelineValue(values[i][0], values[i][1]);
		}

		for (int i = 0; i < values.length-1; i++)
		{
			this.sprite[i].setInterpolated(true, this.sprite[i+1]);
		}
	}
	
	public void setSpriteTimeline(List<TimelineValue> values)
	{
		TimelineValue[] array = new TimelineValue[values.size()];
		sprite = values.toArray(array);
	}

	public TimelineValue[] getSizeTimeline()
	{
		return size;
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
		this.size = new TimelineValue[values.length];

		for (int i = 0; i < values.length; i++)
		{
			this.size[i] = new TimelineValue(values[i][0], values[i][1], values[i][2]);
		}

		for (int i = 0; i < values.length-1; i++)
		{
			this.size[i].setInterpolated(true, this.size[i+1]);
		}
	}
	
	public void setSizeTimeline(List<TimelineValue> values)
	{
		TimelineValue[] array = new TimelineValue[values.size()];
		size = values.toArray(array);
	}

	public TimelineValue[] getColourTimeline()
	{
		return colour;
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
		this.colour = new TimelineValue[values.length];

		for (int i = 0; i < values.length; i++)
		{
			this.colour[i] = new TimelineValue(values[i][0], values[i][1], values[i][2], values[i][3], values[i][4]);
		}

		for (int i = 0; i < values.length-1; i++)
		{
			this.colour[i].setInterpolated(true, this.colour[i+1]);
		}
	}
	
	public void setColourTimeline(List<TimelineValue> values)
	{
		TimelineValue[] array = new TimelineValue[values.size()];
		colour = values.toArray(array);
	}

	public TimelineValue[] getVelocityTimeline()
	{
		return velocity;
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
		this.velocity = new TimelineValue[values.length];

		for (int i = 0; i < values.length; i++)
		{
			this.velocity[i] = new TimelineValue(values[i][0], values[i][1], values[i][2], values[i][3]);
		}

		for (int i = 0; i < values.length-1; i++)
		{
			this.velocity[i].setInterpolated(true, this.velocity[i+1]);
		}
	}
	
	public void setVelocityTimeline(List<TimelineValue> values)
	{
		TimelineValue[] array = new TimelineValue[values.size()];
		velocity = values.toArray(array);
	}

	/**
	 * Method to create a basic particle emitter. <p>
	 * The particles in this emitter will have a constant width and height,
	 *  a constant velocity (vx, vy, vz)
	 *   and will interpolate its colour from the start colour to the end over the particles lifetime.
	 *   The image used will be the sprite in the atlas designated as 'sprite0'.
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
		this.sprite = new TimelineValue[]{new TimelineValue(0, 0)};

		this.size = new TimelineValue[]{new TimelineValue(0, width, height)};

		this.colour = new TimelineValue[]{new TimelineValue(0, start.r, start.g, start.b, start.a), new TimelineValue(particleLifetime, end.r, end.g, end.b, end.a)};
		this.colour[0].setInterpolated(true, this.colour[1]);

		this.velocity = new TimelineValue[]{new TimelineValue(0, vx, vy, vz)};
	}

	public void addLight(boolean isStatic, float attenuation, float power, Color colour, boolean flicker, float x, float y, float z)
	{
		this.lightAttenuation = attenuation;
		this.lightPower = power;
		this.isLightStatic = isStatic;
		this.lightColour = colour;
		this.lightFlicker = flicker;
		this.lightx = x;
		this.lighty = y;
		this.lightz = z;
	}

	public void create() {

		if (shader == null)
		{
			shader = new ShaderProgram(SHADER_VERTEX, SHADER_FRAGMENT);
		}

		ran = new Random();

		quad = Pools.obtain(Vector3.class).set(0, 0, 0);
		tmpMat = Pools.obtain(Matrix4.class).idt();
		tmpRot = Pools.obtain(Matrix4.class).idt();
		pos = Pools.obtain(Vector3.class).set(0, 0, 0);
		
		calculateRadius();
		reloadParticles();
		reloadTextures();
	}
	
	public void dispose()
	{
		if (quad != null) Pools.free(quad);
		quad = null;
		if (tmpMat != null) Pools.free(tmpMat);
		tmpMat = null;
		if (tmpRot != null) Pools.free(tmpRot);
		tmpRot = null;
		if (pos != null) Pools.free(pos);
		pos = null;
		if (mesh != null) mesh.dispose();
		mesh = null;
	}
	
	public void getLight(LightManager lightManager)
	{
		if (lightColour == null) return;
		
		if (light != null)
		{
			if (isLightStatic) lightManager.removeStaticLight(light.UID);
			else lightManager.removeDynamicLight(light.UID);

			light = null;
		}
		
		light = new PointLight(new Vector3(x+lightx+(ex/2f), y+lighty+ey, z+lightz+(ez/2)), lightColour.cpy(), lightAttenuation, lightPower);
		lightUID = light.UID;

		if (isLightStatic) lightManager.addStaticLight(light);
		else lightManager.addDynamicLight(light);
	}
	
	public void calculateParticles()
	{
		maxParticles = (int) ((float)particleLifetime / (float)emissionTime);
	}
	
	public void calculateRadius()
	{
		this.radius = ex+ey+ez;
	}
	
	public void reloadParticles()
	{
		active = new Bag<Particle>(maxParticles);
		inactive = new Bag<Particle>(maxParticles);

		for (int i = 0; i < maxParticles; i++)
		{
			Particle p = new Particle();
			inactive.add(p);
		}

		vertices = new float[maxParticles*VERTEX_SIZE*4];
		
		if (mesh != null) mesh.dispose();
		mesh = new Mesh(false, maxParticles*4, maxParticles*6,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Generic, 4, "a_colour"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		mesh.setVertices(vertices);
		mesh.setIndices(genIndices(maxParticles));

	}
	
	public void reloadTextures()
	{
		atlas = FileUtils.loadAtlas(atlasName);
		Set<Texture> atlasTextures = atlas.getTextures();
		Iterator<Texture> itr = atlasTextures.iterator();

		atlasTexture = itr.next();

		int maxIndex = 0;
		for (TimelineValue spriteTL : sprite)
		{
			int index = (int) spriteTL.getValues()[0];

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
		if (currentBlendSRC == blendFuncSRC && currentBlendDST == blendFuncDST)
		{}
		else {
			Gdx.gl.glBlendFunc(blendFuncSRC, blendFuncDST);
			currentBlendSRC = blendFuncSRC;
			currentBlendDST = blendFuncDST;
		}

		if (currentAtlas != null && atlasName.equals(currentAtlas)) {

		}
		else
		{
			atlasTexture.bind(0);
			shader.setUniformi("u_texture", 0);

			currentAtlas = atlasName;
		}

		mesh.render(shader, GL20.GL_TRIANGLES, 0, active.size*4);
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

		currentAtlas = null;
		currentBlendSRC = currentBlendDST = 0;
	}

	public void update(float delta, Camera cam)
	{
		if (light != null)
		{
			light.positionAbsolutely(x+lightx+(ex/2f), y+lighty+ey, z+lightz+(ez/2));
			if (lightFlicker) light.attenuation = (float) (lightAttenuation *
					(1-((1-((float)inactive.size / (float)active.size)))/2));
		}
		
		tmpRot.set(cam.view).inv();
		tmpRot.getValues()[Matrix4.M03] = 0;
		tmpRot.getValues()[Matrix4.M13] = 0;
		tmpRot.getValues()[Matrix4.M23] = 0;

		Iterator<Particle> pItr = active.iterator();

		i = 0;
		while (pItr.hasNext())
		{
			Particle p = pItr.next();

			float[] velocity = getAttributeValue(p.lifetime, ParticleAttribute.VELOCITY);

			p.update(delta, velocity[0], velocity[1], velocity[2]);

			if (p.lifetime > particleLifetime)
			{
				pItr.remove();
				inactive.add(p);
				continue;
			}

			tmpMat.setToTranslation(p.x, p.y, p.z).mul(tmpRot);

			int sprite = (int) getAttributeValue(p.lifetime, ParticleAttribute.SPRITE)[0];
			float[] size = getAttributeValue(p.lifetime, ParticleAttribute.SIZE);
			float[] colour = getAttributeValue(p.lifetime, ParticleAttribute.COLOUR);

			quad
				.set(-size[0]/2, size[1]/2, 0)
				.mul(tmpMat);

			v = 0;

			vertices[(i*VERTEX_SIZE*4)+v+0] = quad.x;
			vertices[(i*VERTEX_SIZE*4)+v+1] = quad.y;
			vertices[(i*VERTEX_SIZE*4)+v+2] = quad.z;

			vertices[(i*VERTEX_SIZE*4)+v+3] = colour[0];
			vertices[(i*VERTEX_SIZE*4)+v+4] = colour[1];
			vertices[(i*VERTEX_SIZE*4)+v+5] = colour[2];
			vertices[(i*VERTEX_SIZE*4)+v+6] = colour[3];

			vertices[(i*VERTEX_SIZE*4)+v+7] = topLeftTexCoords[sprite][0];
			vertices[(i*VERTEX_SIZE*4)+v+8] = topLeftTexCoords[sprite][1];

			quad
				.set(size[0]/2, size[1]/2, 0)
				.mul(tmpMat);

			v += VERTEX_SIZE;

			vertices[(i*VERTEX_SIZE*4)+v+0] = quad.x;
			vertices[(i*VERTEX_SIZE*4)+v+1] = quad.y;
			vertices[(i*VERTEX_SIZE*4)+v+2] = quad.z;

			vertices[(i*VERTEX_SIZE*4)+v+3] = colour[0];
			vertices[(i*VERTEX_SIZE*4)+v+4] = colour[1];
			vertices[(i*VERTEX_SIZE*4)+v+5] = colour[2];
			vertices[(i*VERTEX_SIZE*4)+v+6] = colour[3];

			vertices[(i*VERTEX_SIZE*4)+v+7] = topRightTexCoords[sprite][0];
			vertices[(i*VERTEX_SIZE*4)+v+8] = topRightTexCoords[sprite][1];

			quad
				.set(-size[0]/2, -size[1]/2, 0)
				.mul(tmpMat);

			v += VERTEX_SIZE;

			vertices[(i*VERTEX_SIZE*4)+v+0] = quad.x;
			vertices[(i*VERTEX_SIZE*4)+v+1] = quad.y;
			vertices[(i*VERTEX_SIZE*4)+v+2] = quad.z;

			vertices[(i*VERTEX_SIZE*4)+v+3] = colour[0];
			vertices[(i*VERTEX_SIZE*4)+v+4] = colour[1];
			vertices[(i*VERTEX_SIZE*4)+v+5] = colour[2];
			vertices[(i*VERTEX_SIZE*4)+v+6] = colour[3];

			vertices[(i*VERTEX_SIZE*4)+v+7] = botLeftTexCoords[sprite][0];
			vertices[(i*VERTEX_SIZE*4)+v+8] = botLeftTexCoords[sprite][1];

			quad
				.set(size[0]/2, -size[1]/2, 0)
				.mul(tmpMat);

			v += VERTEX_SIZE;

			vertices[(i*VERTEX_SIZE*4)+v+0] = quad.x;
			vertices[(i*VERTEX_SIZE*4)+v+1] = quad.y;
			vertices[(i*VERTEX_SIZE*4)+v+2] = quad.z;

			vertices[(i*VERTEX_SIZE*4)+v+3] = colour[0];
			vertices[(i*VERTEX_SIZE*4)+v+4] = colour[1];
			vertices[(i*VERTEX_SIZE*4)+v+5] = colour[2];
			vertices[(i*VERTEX_SIZE*4)+v+6] = colour[3];

			vertices[(i*VERTEX_SIZE*4)+v+7] = botRightTexCoords[sprite][0];
			vertices[(i*VERTEX_SIZE*4)+v+8] = botRightTexCoords[sprite][1];

			i++;
		}
		mesh.setVertices(vertices);

		emissionCD -= delta;

		arrayLen = inactive.size;
		
		if (arrayLen == 0) return;

		while (emissionCD < 0 && arrayLen > 0)
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
			arrayLen--;
		}
	}

	private float[] getAttributeValue(float time, ParticleAttribute pa) {
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

		return tv.getValuesInterpolated(time);
	}

	private TimelineValue searchTimeline(float time, TimelineValue[] value)
	{
		arrayLen = value.length;
		for (i2 = 0; i2 < arrayLen; i2++)
		{
			if (value[i2].time > time)
			{
				return value[i2-1];
			}
		}
		return value[arrayLen-1];
	}

	public ParticleEmitter copy()
	{
		ParticleEmitter copy = new ParticleEmitter(particleLifetime, particleLifetimeVar, emissionTime, ex, ey, ez, emissionType, blendFuncSRC, blendFuncDST, atlasName, name);
		copy.maxParticles = maxParticles;

		TimelineValue[] cpySprite = new TimelineValue[sprite.length];
		for (int i = 0; i < sprite.length; i++) cpySprite[i] = sprite[i].copy();
		copy.sprite = cpySprite;

		TimelineValue[] cpySize = new TimelineValue[size.length];
		for (int i = 0; i < size.length; i++) cpySize[i] = size[i].copy();
		copy.size = cpySize;

		TimelineValue[] cpyColour = new TimelineValue[colour.length];
		for (int i = 0; i < colour.length; i++) cpyColour[i] = colour[i].copy();
		copy.colour = cpyColour;

		TimelineValue[] cpyVelocity = new TimelineValue[velocity.length];
		for (int i = 0; i < velocity.length; i++) cpyVelocity[i] = velocity[i].copy();
		copy.velocity = cpyVelocity;

		if (lightUID != null)
			copy.addLight(isLightStatic, lightAttenuation, lightPower, lightColour, lightFlicker, lightx, lighty, lightz);

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

	public static class TimelineValue implements Serializable, Json.Serializable
	{
		private static final long serialVersionUID = -4434625075360858305L;

		public float[] values;
		public float time;

		public boolean interpolated = false;
		public float[] valueStep;
		public float[] interpolatedValues;

		private transient float timeStep;
		private transient int arrayLen;
		private transient int i;

		public TimelineValue(){}

		public TimelineValue(float time, float... values)
		{
			this.time = time;
			this.values = values;
		}

		public float[] getValues()
		{
			return values;
		}

		protected void setValues(boolean interpolated, float[] valueStep, float[] interpolatedValues)
		{
			this.interpolated = interpolated;
			this.valueStep = valueStep;
			this.interpolatedValues = interpolatedValues;
		}

		public float[] getValuesInterpolated(float currentTime)
		{
			if (!interpolated)
			{
				return values;
			}

			timeStep = currentTime-time;
			arrayLen = values.length;
			for (i = 0; i < arrayLen; i++)
			{
				float value = values[i]+(valueStep[i]*timeStep);
				interpolatedValues[i] = value;
			}

			return interpolatedValues;
		}

		public void setInterpolated(boolean interpolated, TimelineValue nextValue)
		{
			this.interpolated = interpolated;

			if (interpolated)
			{
				interpolatedValues = new float[values.length];
				valueStep = new float[values.length];

				arrayLen = nextValue.values.length;
				for (i = 0; i < arrayLen; i++)
				{
					float value = (nextValue.values[i] - values[i]) / (nextValue.time - time);
					valueStep[i] = value;
				}
			}
		}

		public TimelineValue copy()
		{
			TimelineValue copy = new TimelineValue(time, values);
			copy.setValues(interpolated, valueStep, interpolatedValues);
			return copy;
		}

		public void write (Json json) {
			json.writeValue("time", time);
			json.writeValue("values", values);
			json.writeValue("interpolated", interpolated);
			if (interpolated) json.writeValue("value step", valueStep);
		}
		
		public void read (Json json, OrderedMap<String, Object> jsonMap) {
			Iterator<Entry<String, Object>> itr = jsonMap.entries().iterator();
			while (itr.hasNext())
			{
				Entry<String, Object> entry = itr.next();
				if (entry.key.equals("time"))
				{
					time = (Float) entry.value;
				}
				else if (entry.key.equals("values"))
				{
					values = (float[]) entry.value;
				}
				else if (entry.key.equals("interpolated"))
				{
					interpolated = (Boolean) entry.value;
					if (interpolated)
					{
						interpolatedValues = new float[values.length];
					}
				}
				else if (entry.key.equals("value step"))
				{
					valueStep = (float[]) entry.value;
				}
			}
		}
	}
	
	/**
	 * Write this particle emitter to the given json instance.
	 * @param json
	 */
	private void write (Json json) {
		json.writeObjectStart();
		json.writeValue("name", name);
		json.writeValue("sprite", sprite);
		json.writeValue("size", size);
		json.writeValue("colour", colour);
		json.writeValue("velocity", velocity);

		json.writeValue("max particles", maxParticles);
		json.writeValue("particle lifetime", particleLifetime);
		json.writeValue("particle lifetime variance", particleLifetimeVar);
		json.writeValue("emission time", emissionTime);
		json.writeValue("emission x", ex);
		json.writeValue("emission y", ey);
		json.writeValue("emission z", ez);
		json.writeValue("emission type", emissionType);
		json.writeValue("blend func SRC", blendFuncSRC);
		json.writeValue("blend func DST", blendFuncDST);
		json.writeValue("atlas name", atlasName);

		if (lightUID != null)
		{
			json.writeValue("light attenuation", lightAttenuation);
			json.writeValue("light power", lightPower);
			json.writeValue("light static", isLightStatic);
			json.writeValue("light flicker", lightFlicker);
			json.writeValue("light colour", lightColour);
			json.writeValue("light offset x", lightx);
			json.writeValue("light offset y", lighty);
			json.writeValue("light offset z", lightz);
		}

		json.writeObjectEnd();
	}

	/**
	 * Get a json instance set up for reading and writing a particle emitter
	 * @return
	 */
	public static Json getJson (Json json) {
		json.setSerializer(ParticleEmitter.class, new Json.Serializer<ParticleEmitter>() {
			@SuppressWarnings("rawtypes")
			public void write (Json json, ParticleEmitter emitter, Class knownType) {
				emitter.write(json);
			}

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public ParticleEmitter read (Json json, Object jsonData, Class type) {

				// ----- Particle Parameters ----- //
				TimelineValue[] sprite = null;
				TimelineValue[] size = null;
				TimelineValue[] colour = null;
				TimelineValue[] velocity = null;
				// ----- End Particle Parameters ----- //

				// ----- Emitter parameters ----- //
				String name = null;
				int maxParticles = 0;
				float particleLifetime = 0;
				float particleLifetimeVar = 0;
				float emissionTime = 0;
				float ex = 0, ey = 0, ez = 0;
				int emissionType = 0;
				int blendFuncSRC = 0;
				int blendFuncDST = 0;
				String atlasName = null;
				// ----- End Emitter parameters ----- //

				// ----- Light ----- //
				float lightAttenuation = 0;
				float lightPower = 0;
				boolean isLightStatic = false;
				Color lightColour = null;
				boolean lightFlicker = false;
				float lightx = 0, lighty = 0, lightz = 0;
				// ----- End Light ----- //

				OrderedMap<String, Object> jsonMap = (OrderedMap<String, Object>) jsonData;
				Iterator<Entry<String, Object>> itr = jsonMap.entries();

				while (itr.hasNext())
				{
					Entry<String, Object> entry = itr.next();

					if (entry.key.equals("sprite"))
					{
						sprite = json.readValue(TimelineValue[].class, entry.value);
					}
					else if (entry.key.equals("size"))
					{
						size = json.readValue(TimelineValue[].class, entry.value);
					}
					else if (entry.key.equals("colour"))
					{
						colour = json.readValue(TimelineValue[].class, entry.value);
					}
					else if (entry.key.equals("velocity"))
					{
						velocity = json.readValue(TimelineValue[].class, entry.value);
					}

					else if (entry.key.equals("name"))
					{
						name = (String) entry.value;
					}
					else if (entry.key.equals("max particles"))
					{
						maxParticles = ((Float) entry.value).intValue();
					}
					else if (entry.key.equals("particle lifetime"))
					{
						particleLifetime = (Float) entry.value;
					}
					else if (entry.key.equals("particle lifetime variance"))
					{
						particleLifetimeVar = (Float) entry.value;
					}
					else if (entry.key.equals("emission time"))
					{
						emissionTime = (Float) entry.value;
					}
					else if (entry.key.equals("emission x"))
					{
						ex = (Float) entry.value;
					}
					else if (entry.key.equals("emission y"))
					{
						ey = (Float) entry.value;
					}
					else if (entry.key.equals("emission z"))
					{
						ez = (Float) entry.value;
					}
					else if (entry.key.equals("emission type"))
					{
						emissionType = ((Float) entry.value).intValue();
					}
					else if (entry.key.equals("blend func SRC"))
					{
						blendFuncSRC = ((Float) entry.value).intValue();
					}
					else if (entry.key.equals("blend func DST"))
					{
						blendFuncDST = ((Float) entry.value).intValue();
					}
					else if (entry.key.equals("atlas name"))
					{
						atlasName = (String) entry.value;
					}
					
					else if (entry.key.equals("light attenuation"))
					{
						lightAttenuation = (Float) entry.value;
					}
					else if (entry.key.equals("light power"))
					{
						lightPower = (Float) entry.value;
					}
					else if (entry.key.equals("light static"))
					{
						isLightStatic = (Boolean) entry.value;
					}
					else if (entry.key.equals("light flicker"))
					{
						lightFlicker = (Boolean) entry.value;
					}
					else if (entry.key.equals("light colour"))
					{
						lightColour = json.readValue(Color.class, entry.value);
					}
					else if (entry.key.equals("light offset x"))
					{
						lightx = (Float) entry.value;
					}
					else if (entry.key.equals("light offset y"))
					{
						lighty = (Float) entry.value;
					}
					else if (entry.key.equals("light offset z"))
					{
						lightz = (Float) entry.value;
					}
				}

				ParticleEmitter emitter = new ParticleEmitter(particleLifetime, particleLifetimeVar, emissionTime, 
						ex, ey, ez,
						emissionType,
						blendFuncSRC, blendFuncDST,
						atlasName, name);
				emitter.maxParticles = maxParticles;

				emitter.setTimeline(sprite, size, colour, velocity);
				
				if (lightColour != null) emitter.addLight(isLightStatic, lightAttenuation, lightPower, lightColour, lightFlicker, lightx, lighty, lightz);

				return emitter;
			}
		});

		return json;
	}

	public static ParticleEmitter load (String file)
	{
		Json json = getJson(new Json());

		return json.fromJson(ParticleEmitter.class, file);
	}

	public static ParticleEmitter load (FileHandle file)
	{
		Json json = getJson(new Json());

		return json.fromJson(ParticleEmitter.class, file);
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
			"#ifdef GL_ES\n" +
            "precision highp float;\n" + 
            "#endif\n" + 
            
			"uniform sampler2D u_texture;" + "\n" +

			"varying vec4 v_colour;" + "\n" +
			"varying vec2 v_texCoords;" + "\n" +

			"void main() {" + "\n" +
			"gl_FragColor = texture2D(u_texture, v_texCoords) * v_colour;" + "\n" +
			"}";
	
	private static final ParticleEmitterComparator comparator = new ParticleEmitterComparator();
	public static Comparator<ParticleEmitter> getComparator()
	{
		return comparator;
	}
	
	static class ParticleEmitterComparator implements Comparator<ParticleEmitter>
	{
		public int compare(ParticleEmitter p1, ParticleEmitter p2) {
			return (p1.distance < p2.distance) ? 1 : -1;
		}
	}
}