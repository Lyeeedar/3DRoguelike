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
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;

public class ParticleEmitter implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6308492057144008114L;

	public static float POINT_SIZE_MAX = 0;
	
	public transient float distance = 0;
	
	public final String UID;
	
	public transient ArrayDeque<Particle> active;
	public transient ArrayDeque<Particle> inactive;
	
	transient PointLight boundLight;
	public String boundLightUID;
	
	final Random ran = new Random();
	
	transient float time = 0;
	
	float x; float y; float z; public float vx; public float vy; public float vz; float speed;
	
	float radius;
	
	public int particles;
	
	transient Texture texture;
	public String textureName;
	
	final static transient ShaderProgram pointShader = new ShaderProgram(
			Gdx.files.internal("data/shaders/model/particlePoint.vertex.glsl").readString(),
			Gdx.files.internal("data/shaders/model/particlePoint.fragment.glsl").readString());
	final static transient ShaderProgram quadShader = new ShaderProgram(
			Gdx.files.internal("data/shaders/model/particleQuad.vertex.glsl").readString(),
			Gdx.files.internal("data/shaders/model/particleQuad.fragment.glsl").readString());
	
	/**
	 * 0 = none
	 * 1 = point
	 * 2 = quad
	 */
	private static int activeShader = 0;
	
	transient Mesh meshPoint;
	transient Mesh meshQuad;
	
	transient GameObject bound;
	public final String boundUID;
	
	float ox; float oy; float oz;
	
	private boolean pointMode = false;
	
	Vector3[] particle = {new Vector3(), new Vector3(), new Vector3(), new Vector3()};
	
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
		this.boundUID = bound.UID;
		
		radius = vx + vz;
		
		create();
	}
	
	public void create() {
		
		active = new ArrayDeque<Particle>(particles);
		inactive = new ArrayDeque<Particle>(particles);
		
		for (int i = 0; i < particles; i++)
		{
			Particle p = new Particle(false);
			inactive.add(p);
		}
		meshPoint = new Mesh(false, particles, 0, 
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Generic, 3, "a_colour"));
		
		meshQuad = new Mesh(false, particles*4, particles*6, 
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Generic, 3, "a_colour"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
		
		meshQuad.setIndices(genIndices(particles));

		verticesPoint = new float[particles*6];
		verticesQuad = new float[particles*32];
	}
	
	public void fixReferences()
	{
		if (boundLightUID != null) boundLight = GameData.lightManager.getDynamicLight(boundLightUID);
		
		bound = GameData.level.getActor(boundUID);
		if (bound == null) bound = GameData.level.getLevelObject(boundUID);
		this.texture = new Texture(Gdx.files.internal(textureName));
		
		create();
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
	
	Vector3 velocity; float atime; Colour start; Colour end; float width; float height;
	
	float intensity; float attenuation;
	
	public void setTexture(String texture, Vector3 velocity, float atime, Colour start, Colour end, boolean light, float intensity, float attenuation)
	{
		this.textureName = texture;
		this.texture = new Texture(Gdx.files.internal(texture));
		this.velocity = velocity;
		this.atime = atime;
		this.start = start;
		this.end = end;
		this.width = this.texture.getWidth();
		this.height = 1;//this.texture.getHeight();
		
		particle[0].set(0, height, 0);
		particle[1].set(width, height, 0);
		particle[2].set(0, 0, 0);
		particle[3].set(width, 0, 0);
		
		if (light)
		{
			this.intensity = intensity;
			this.attenuation = attenuation;
			
			Colour lightCol = new Colour((start.r+end.r)/2f, (start.g+end.g)/2f, (start.b+end.b)/2f, 1.0f);
			
			if (boundLight == null)
			{
				boundLight = new PointLight(new Vector3(x+(vx/2f), y+vy, z+(vz/2)), lightCol, attenuation, intensity);
				boundLightUID = boundLight.UID;
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
				boundLightUID = null;
			}
		}
	}
	
	transient float[] verticesPoint;
	transient float[] verticesQuad;
	public void render(Camera cam)
	{
		if (pointMode) {
			
			Gdx.gl.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE);
			Gdx.gl.glEnable(GL11.GL_POINT_SPRITE_OES);
			Gdx.gl.glEnable(GL20.GL_BLEND); 
			//Gdx.gl.glBlendFunc(GL20.GL_DST_ALPHA, GL20.GL_SRC_ALPHA);
			Gdx.gl.glDepthMask(false);
			
			if (activeShader == 0)
			{
				activeShader = 1;
				pointShader.begin();
				pointShader.setUniformMatrix("u_mv", cam.combined);
			}
			else if (activeShader == 2)
			{
				quadShader.end();
				
				activeShader = 1;
				pointShader.begin();
				pointShader.setUniformMatrix("u_mv", cam.combined);
			}

			pointShader.setUniformf("u_point", getPointSize(cam));
			texture.bind(0);
			pointShader.setUniformi("u_texture", 0);
			meshPoint.setVertices(verticesPoint);
			meshPoint.render(pointShader, GL20.GL_POINTS);

			Gdx.gl.glDepthMask(true);
		}
		else
		{
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glDepthMask(false);
			
			if (activeShader == 0)
			{
				activeShader = 2;
				quadShader.begin();
				quadShader.setUniformMatrix("u_mv", cam.combined);
			}
			else if (activeShader == 1)
			{
				pointShader.end();
				
				activeShader = 2;
				quadShader.begin();
				quadShader.setUniformMatrix("u_mv", cam.combined);
			}

			texture.bind(0);
			quadShader.setUniformi("u_texture", 0);
			
			meshQuad.setVertices(verticesQuad);
			meshQuad.render(quadShader, GL20.GL_TRIANGLES);

			Gdx.gl.glDepthMask(true);
		}
	}
	
	public static void end()
	{
		if (activeShader == 1)
		{
			activeShader = 0;
			pointShader.end();
		}
		else if (activeShader == 2)
		{
			activeShader = 0;
			quadShader.end();
		}
	}
	
	public float getPointSize(Camera cam)
	{
		
		float dist = cam.position.dst(getPos());
		
		float size = (width > height) ? width : height;
		
		return size / ((0.08f * dist)+(0.002f * dist * dist));
	}
	
	private transient int signx;
	private transient int signy;
	private transient int signz;
	
	private final Matrix4 mat = new Matrix4();

	public void update(float delta, Camera cam)
	{
		pointMode = (getPointSize(cam) > POINT_SIZE_MAX) ? false : true;

		x = bound.getPosition().x+ox;
		y = bound.getPosition().y+oy;
		z = bound.getPosition().z+oz;
		
		if (boundLight != null)
		{
			boundLight.position.set(x, y, z);
			
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
				verticesPoint[(i*6)+0] = p.position.x;
				verticesPoint[(i*6)+1] = p.position.y;
				verticesPoint[(i*6)+2] = p.position.z;
				
				verticesPoint[(i*6)+3] = p.colour.r;
				verticesPoint[(i*6)+4] = p.colour.g;
				verticesPoint[(i*6)+5] = p.colour.b;
			}
			else
			{

				mat.setToTranslation(p.position).mul(GameData.player.vo.attributes.getRotation());
				
				Vector3 nPostl = particle[0].tmp().mul(mat);

				verticesQuad[(i*32)+0] = nPostl.x;
				verticesQuad[(i*32)+1] = nPostl.y;
				verticesQuad[(i*32)+2] = nPostl.z;
				
				verticesQuad[(i*32)+3] = p.colour.r;
				verticesQuad[(i*32)+4] = p.colour.g;
				verticesQuad[(i*32)+5] = p.colour.b;
				
				verticesQuad[(i*32)+6] = 0.0f;
				verticesQuad[(i*32)+7] = 0.0f;
				
				Vector3 nPostr = particle[1].tmp().mul(mat);
				
				verticesQuad[(i*32)+8] = nPostr.x;
				verticesQuad[(i*32)+9] = nPostr.y;
				verticesQuad[(i*32)+10] = nPostr.z;
				
				verticesQuad[(i*32)+11] = p.colour.r;
				verticesQuad[(i*32)+12] = p.colour.g;
				verticesQuad[(i*32)+13] = p.colour.b;
				
				verticesQuad[(i*32)+14] = width;
				verticesQuad[(i*32)+15] = 0.0f;
				
				Vector3 nPosbl = particle[2].tmp().mul(mat);
				
				verticesQuad[(i*32)+16] = nPosbl.x;
				verticesQuad[(i*32)+17] = nPosbl.y;
				verticesQuad[(i*32)+18] = nPosbl.z;
				
				verticesQuad[(i*32)+19] = p.colour.r;
				verticesQuad[(i*32)+20] = p.colour.g;
				verticesQuad[(i*32)+21] = p.colour.b;
				
				verticesQuad[(i*32)+22] = 0.0f;
				verticesQuad[(i*32)+23] = height;
				
				Vector3 nPosbr = particle[3].tmp().mul(mat);

				verticesQuad[(i*32)+24] = nPosbr.x;
				verticesQuad[(i*32)+25] = nPosbr.y;
				verticesQuad[(i*32)+26] = nPosbr.z;
				
				verticesQuad[(i*32)+27] = p.colour.r;
				verticesQuad[(i*32)+28] = p.colour.g;
				verticesQuad[(i*32)+29] = p.colour.b;
				
				verticesQuad[(i*32)+30] = width;
				verticesQuad[(i*32)+31] = height;
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
			p.set(velocity, atime*ran.nextFloat(), start, end, 
					x+(float)(vx*ran.nextGaussian()*signx), 
					y+(float)(vy*ran.nextGaussian()*signy),
					z+(float)(vz*ran.nextGaussian()*signz));
	
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

class Particle {
	
	Vector3 velocity;
	float remainingTime;

	Colour colour = new Colour();
	
	float rstep;
	float gstep;
	float bstep;
	
	public boolean alive = true;
	
	final String UID;
	
	Vector3 position = new Vector3();
	
	public Particle(boolean alive)
	{
		UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		alive = false;
	}
	
	public Particle(Vector3 velocity, float time, Colour start, Colour end, float x, float y, float z)
	{
		UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		this.velocity.set(velocity);
		this.remainingTime = time;
		this.colour.set(start);
		this.position.set(x, y, z);
		
		float rdiff = end.r-start.r;
		rstep = rdiff/time;
		
		float gdiff = end.g-start.g;
		gstep = gdiff/time;
		
		float bdiff = end.b-start.b;
		bstep = bdiff/time;
	}
	
	public void set(Vector3 velocity, float time, Colour start, Colour end, float x, float y, float z)
	{		
		alive = true;
		
		this.velocity = velocity;
		this.remainingTime = time;
		this.colour.set(start);
		this.position.set(x, y, z);
		
		float rdiff = end.r-start.r;
		rstep = rdiff/time;
		
		float gdiff = end.g-start.g;
		gstep = gdiff/time;
		
		float bdiff = end.b-start.b;
		bstep = bdiff/time;
	}
	
	private final Vector3 tmpVec = new Vector3();
	public void update(float delta)
	{
		remainingTime -= delta;
		if (remainingTime < 0) alive = false;
		if (!alive) return;
		
		position.add(velocity.tmp().mul(delta));
		
		colour.r += rstep*delta;
		colour.g += gstep*delta;
		colour.b += bstep*delta;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Particle)) return false;
		Particle p = (Particle)o;
		
		if (p.alive != alive) return false;
		
		if (p.UID.equals(UID)) return true;
		else return false;
	}
}
