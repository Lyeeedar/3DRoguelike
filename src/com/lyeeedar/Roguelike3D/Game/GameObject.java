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
package com.lyeeedar.Roguelike3D.Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pools;
import com.lyeeedar.Graphics.ParticleEffects.ParticleEffect;
import com.lyeeedar.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer;

public abstract class GameObject implements Serializable {

	private static final long serialVersionUID = 1356577977889288007L;

	public static final float PHYSICS_DAMAGE_THRESHHOLD = 2.0f;
	public static final float X_ROTATE = -360f/500f;
	public static final float Y_ROTATE = -360f/500f;
	public static final int MAX_SPEED = 4;
	
	public final String UID;
	
	public final Vector3 position = new Vector3();
	public final Vector3 rotation = new Vector3(1, 0, 1);
	public final Vector3 velocity = new Vector3();
	public final Vector3 up = new Vector3(0, 1, 0);
	public float radius;

	public VisibleObject vo;
	public ParticleEffect particleEffect;
	
	public boolean grounded = true;
	public boolean visible = true;
	public boolean solid = true;
	
	public String shortDesc = "";
	public String longDesc = "";
	
	public transient Random ran;
	public transient Vector3 offsetPos;
	public transient Vector3 offsetRot;

	public GameObject(Color colour, String texture, float x, float y, float z, float scale, int primitive_type, String... model)
	{
		UID = this.toString()+System.currentTimeMillis()+this.hashCode()+System.nanoTime();
		
		VisibleObject vo = new VisibleObject(primitive_type, colour, texture, scale, model);
		
		this.vo = vo;
		position.set(x, y, z);
	}
	
	// ----- Movement and rotation ----- //
	
	public void applyMovement(float delta, float vertical_acceleration)
	{
		if (velocity.len2() == 0) return;
		
		if (velocity.x < -MAX_SPEED) velocity.x = -MAX_SPEED;
		if (velocity.x > MAX_SPEED) velocity.x = MAX_SPEED;
		
		if (velocity.y < -MAX_SPEED) velocity.y = -MAX_SPEED;
		if (velocity.y > MAX_SPEED) velocity.y = MAX_SPEED;
		
		if (velocity.z < -MAX_SPEED) velocity.z = -MAX_SPEED;
		if (velocity.z > MAX_SPEED) velocity.z = MAX_SPEED;
		
		Level lvl = GameData.level;
		
		Vector3 tmp = Pools.obtain(Vector3.class);
		Vector3 v = Pools.obtain(Vector3.class).set(velocity.x, (velocity.y - 0.5f*vertical_acceleration*delta), velocity.z);
		v.mul(delta*100);
		
		Tile below = lvl.getTile(position.x+v.x, position.z+v.z);
		if (below == null) {
			velocity.x = 0;
			velocity.z = 0;
		}
		
		// Check for collision
		if (lvl.collideSphereAll(position.x+v.x, position.y+v.y, position.z+v.z, radius, UID))
		{
			// Collision! Now time to find which axis the collision was on. (Vertical or Horizontal)
			
			// ----- Check Vertical START ----- //
			
			if (lvl.collideSphereActorsAll(position.x, position.y+v.y, position.z, radius, UID) != null || 
					lvl.collideSphereLevelObjectsAll(position.x, position.y+v.y, position.z, radius) != null)
			{
				velocity.y = 0;
				grounded = true;
			}
			// below
			else if (position.y+v.y-radius < below.floor) {
				
				velocity.y = 0;
				grounded = true;
				
				tmp.set(position.x, below.floor+radius, position.z);
				if (!lvl.collideSphereAll(tmp.x, tmp.y, tmp.z, radius, UID))
				{
					this.positionAbsolutely(tmp.x, tmp.y, tmp.z);
				}
			}
			// above
			else if (lvl.hasRoof && position.y+v.y+radius > below.roof) {
				velocity.y = 0;
				grounded = false;
				
				tmp.set(position.x, below.roof-radius, position.z);
				if (!lvl.collideSphereAll(tmp.x, tmp.y, tmp.z, radius, UID))
				{
					this.positionAbsolutely(tmp.x, tmp.y, tmp.z);
				}
			}
			// No y collision
			else
			{
				this.translate(0, v.y, 0);
				grounded = false;
			}

			// ----- Check Vertical END ----- //
			
			
			// ----- Check Horizontal START ----- //
			
			if (lvl.collideSphereAll(position.x+v.x, position.y, position.z+v.z, radius, UID)) {

				if (lvl.collideSphereAll(position.x+v.x, position.y, position.z, radius, UID)) {
					velocity.x = 0;
					v.x = 0;
				}

				if (lvl.collideSphereAll(position.x, position.y, position.z+v.z, radius, UID)) {
					velocity.z = 0;
					v.z = 0;
				}
			}
			
			this.translate(v.x, 0, v.z);
			
			// ----- Check Horizontal END ----- //
		}
		// No collision! So move normally
		else
		{
			this.translate(v.x, v.y, v.z);
			grounded = false;
		}
		
		if (grounded)
		{
			velocity.x = 0;
			velocity.z = 0;
		}
		
		Pools.free(v);
		Pools.free(tmp);
	}
	
	public abstract void changeTile(Tile src, Tile dst);
	
	public void Yrotate (float angle) {	
		Vector3 dir = Pools.obtain(Vector3.class);
		dir.set(rotation).nor();
		if(dir.y>-0.7 && angle<0 || dir.y<+0.7 && angle>0)
		{
			Vector3 localAxisX = Pools.obtain(Vector3.class).set(rotation);
			localAxisX.crs(up).nor();
			rotate(localAxisX.x, localAxisX.y, localAxisX.z, angle);
			Pools.free(localAxisX);
		}
		Pools.free(dir);
	}

	public void Xrotate (float angle) {
		rotate(0, 1, 0, angle);
	}

	public void rotate (float x, float y, float z, float angle) {
		Vector3 axis = Pools.obtain(Vector3.class).set(x, y, z);
		Matrix4 tmpMat = Pools.obtain(Matrix4.class).idt();
		tmpMat.setToRotation(axis, angle);
		Pools.free(axis);
		rotation.mul(tmpMat).nor();
		up.mul(tmpMat).nor();
		Pools.free(tmpMat);
		
		if (vo.attributes != null) {
			Vector3 lookAt = Pools.obtain(Vector3.class).set(0, 0, 0).add(rotation);
			vo.attributes.getRotation().setToLookAt(lookAt, up).inv();
			Pools.free(lookAt);
		}
	}
	
	public void translate(float x, float y, float z)
	{
		positionAbsolutely(position.x+x, position.y+y, position.z+z);
	}
	
	public void positionAbsolutely(float x, float y, float z)
	{
		Tile start = GameData.level.getTile(position.x, position.z);
		position.set(x, y, z);

		Tile end = GameData.level.getTile(position.x, position.z);
		if (!start.equals(end))
		{
			changeTile(start, end);
		}
		
		if (vo.attributes != null) vo.attributes.position.setToTranslation(position);
		if (particleEffect != null) particleEffect.setPosition(vo.attributes.getSortCenter());
	}

	public void positionYAbsolutely(float y)
	{
		positionAbsolutely(position.x, y, position.z);
	}
	
	public void left_right(float mag)
	{
		velocity.x += (float)Math.sin(rotation.z) * mag;
		velocity.z += -(float)Math.sin(rotation.x) * mag;
	}

	public void forward_backward(float mag)
	{
		velocity.x += (float)Math.sin(rotation.x) * mag;
		velocity.z += (float)Math.sin(rotation.z) * mag;
	}
	
	// ----- Rendering and Updating ----- //
	
	public void render(Renderer renderer, ArrayList<ParticleEmitter> emitters, Camera cam)
	{
		if (particleEffect != null)
		{
			particleEffect.getVisibleEmitters(emitters, cam);
		}
		if (visible) vo.render(renderer);
		
		rendered(renderer, emitters, cam);
	}
	
	protected abstract void rendered(Renderer renderer, ArrayList<ParticleEmitter> emitters, Camera cam);
	public abstract void update(float delta, Camera cam);
	public abstract void activate();
	public abstract String getActivatePrompt();
	
	// ----- Creation and Destruction ----- //

	public void getLight(LightManager lightManager)
	{
		if (particleEffect != null) particleEffect.getLight(lightManager);
	}
		
	public void addParticleEffect(ParticleEffect effect)
	{
		if (particleEffect != null)
		{
			particleEffect.dispose();
			particleEffect.delete();
		}
		this.particleEffect = effect;
	}
	
	public void create()
	{
		ran = new Random();
		offsetPos = Pools.obtain(Vector3.class).set(0, 0, 0);
		offsetRot = Pools.obtain(Vector3.class).set(0, 0, 0);
		
		vo.create();
		radius = vo.attributes.radius;
		if (particleEffect != null) {
			particleEffect.setPosition(position);
			particleEffect.create();
		}

		created();
	}
	
	protected abstract void created();
	
	public void dispose()
	{
		Pools.free(offsetPos);
		offsetPos = null;
		Pools.free(offsetRot);
		offsetRot = null;
		
		vo.dispose();
		if (particleEffect != null) {
			particleEffect.dispose();
		}
		disposed();
	}
	
	protected abstract void disposed();
	
	public void bakeLights(LightManager lightManager, boolean bakeStatics)
	{
		vo.bakeLights(lightManager, bakeStatics);
	}
	
	public abstract void fixReferences();
	
	// ----- Getters ----- //
	
	public Matrix4 getTransform()
	{
		return vo.attributes.position;
	}
	
	public Matrix4 getRotationMatrix()
	{
		return vo.attributes.rotation;
	}
	
	public Vector3 getBoundingBox()
	{
		return vo.attributes.box;
	}
}
