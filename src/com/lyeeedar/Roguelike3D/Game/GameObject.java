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
import com.badlogic.gdx.math.Vector;
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
	
	protected final Vector3 position = new Vector3();
	protected final Vector3 rotation = new Vector3(1, 0, 1);
	protected final Vector3 velocity = new Vector3();
	protected final Vector3 up = new Vector3(0, 1, 0);

	protected VisibleObject vo;
	protected ParticleEffect particleEffect;
	
	protected boolean grounded = true;
	protected boolean visible = true;
	protected boolean solid = true;
	
	protected String shortDesc = "";
	protected String longDesc = "";
	
	protected transient Random ran;
	protected transient Matrix4 tmpMat;
	protected transient Vector3 offsetPos;
	protected transient Vector3 offsetRot;

	public GameObject(Color colour, String texture, float x, float y, float z, float scale, int primitive_type, String... model)
	{
		UID = this.toString()+System.currentTimeMillis()+this.hashCode()+System.nanoTime();
		
		VisibleObject vo = new VisibleObject(primitive_type, colour, texture, scale, model);
		
		this.vo = vo;
		position.set(x, y, z);
	}
	
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

	public void getLight(LightManager lightManager)
	{
		if (particleEffect != null) particleEffect.getLight(lightManager);
	}
	
	public ParticleEffect getParticleEffect()
	{
		return particleEffect;
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
	
	public abstract void fixReferences();
	
	public void create()
	{
		ran = new Random();
		tmpMat = new Matrix4();
		offsetPos = new Vector3();
		offsetRot = new Vector3();
		
		vo.create();
		if (particleEffect != null) {
			particleEffect.setPosition(position);
			particleEffect.create();
		}

		created();
	}
	
	protected abstract void created();

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
		if (lvl.collideSphereAll(position.x+v.x, position.y+v.y, position.z+v.z, getRadius(), UID))
		{
			// Collision! Now time to find which axis the collision was on. (Vertical or Horizontal)
			
			// ----- Check Vertical START ----- //
			
			if (lvl.collideSphereActorsAll(position.x, position.y+v.y, position.z, getRadius(), UID) != null || 
					lvl.collideSphereLevelObjectsAll(position.x, position.y+v.y, position.z, getRadius()) != null)
			{
				velocity.y = 0;
				grounded = true;
			}
			// below
			else if (position.y+v.y-getRadius() < below.floor) {
				
				velocity.y = 0;
				grounded = true;
				
				tmp.set(position.x, below.floor+getRadius(), position.z);
				if (!lvl.collideSphereAll(tmp.x, tmp.y, tmp.z, getRadius(), UID))
				{
					this.positionAbsolutely(tmp.x, tmp.y, tmp.z);
				}
			}
			// above
			else if (lvl.hasRoof && position.y+v.y+getRadius() > below.roof) {
				velocity.y = 0;
				grounded = false;
				
				tmp.set(position.x, below.roof-getRadius(), position.z);
				if (!lvl.collideSphereAll(tmp.x, tmp.y, tmp.z, getRadius(), UID))
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
			
			if (lvl.collideSphereAll(position.x+v.x, position.y, position.z+v.z, getRadius(), UID)) {

				if (lvl.collideSphereAll(position.x+v.x, position.y, position.z, getRadius(), UID)) {
					velocity.x = 0;
					v.x = 0;
				}

				if (lvl.collideSphereAll(position.x, position.y, position.z+v.z, getRadius(), UID)) {
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
	
	public void bakeLights(LightManager lightManager, boolean bakeStatics)
	{
		vo.bakeLights(lightManager, bakeStatics);
	}
	
	public void setOffsetPos(float x, float y, float z)
	{
		offsetPos.set(x, y, z);
	}
	
	public void setOffsetRot(float x, float y, float z)
	{
		offsetRot.set(x, y, z);
	}
	
	public void accelerateY(float val)
	{
		velocity.y += val;
	}
	
	public void Yrotate (float angle) {
		
		Vector3 dir = rotation.cpy().nor();

		if( (dir.y>-0.7) && (angle<0) || (dir.y<+0.7) && (angle>0) )
		{
			Vector3 localAxisX = rotation.cpy();
			localAxisX.crs(up.tmp()).nor();
			rotate(localAxisX.x, localAxisX.y, localAxisX.z, angle);

		}
	}

	public void Xrotate (float angle) {
		rotate(0, 1, 0, angle);
	}

	/** Rotates the direction and up vector of this camera by the given angle around the given axis. The direction and up vector
	 * will not be orthogonalized.
	 *
	 * @param axis
	 * @param angle the angle */
	public void rotate (float x, float y, float z, float angle) {
		Vector3 axis = Pools.obtain(Vector3.class).set(x, y, z);
		tmpMat.setToRotation(axis, angle);
		Pools.free(axis);
		rotation.mul(tmpMat).nor();
		up.mul(tmpMat).nor();
		
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
		this.position.set(x, y, z);

		Tile end = GameData.level.getTile(position.x, position.z);
		if (!start.equals(end))
		{
			changeTile(start, end);
		}
		
		if (vo.attributes != null) vo.attributes.getTransform().setToTranslation(position);
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
	
	public float getRadius()
	{
		return vo.attributes.radius;
	}
	
	public Vector3 getPosition() {
		return position;
	}
	
	public Vector3 getTruePosition() {
		return vo.attributes.getSortCenter();
	}
	
	public Matrix4 getTransform()
	{
		return vo.attributes.getTransform();
	}
	
	public Matrix4 getRotationMatrix()
	{
		return vo.attributes.getRotation();
	}
	
	public Vector3 getBoundingBox()
	{
		return vo.attributes.box;
	}

	public Vector3 getVelocity() {
		return velocity;
	}

	public VisibleObject getVo() {
		return vo;
	}

	public Vector3 getRotation() {
		return rotation;
	}

	public void dispose()
	{
		vo.dispose();
		if (particleEffect != null) {
			particleEffect.dispose();
		}
		disposed();
	}
	
	protected abstract void disposed();
	public abstract void update(float delta, Camera cam);
	public abstract void activate();
	public abstract String getActivatePrompt();

	/**
	 * @return the grounded
	 */
	public boolean isGrounded() {
		return grounded;
	}

	/**
	 * @param grounded the grounded to set
	 */
	public void setGrounded(boolean grounded) {
		this.grounded = grounded;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the solid
	 */
	public boolean isSolid() {
		return solid;
	}

	/**
	 * @param solid the solid to set
	 */
	public void setSolid(boolean solid) {
		this.solid = solid;
	}

	/**
	 * @return the shortDesc
	 */
	public String getShortDesc() {
		return shortDesc;
	}

	/**
	 * @param shortDesc the shortDesc to set
	 */
	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	/**
	 * @return the longDesc
	 */
	public String getLongDesc() {
		return longDesc;
	}

	/**
	 * @param longDesc the longDesc to set
	 */
	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}

	public Vector3 getOffsetPos() {
		return offsetPos;
	}

	public void setOffsetPos(Vector3 offsetPos) {
		this.offsetPos = offsetPos;
	}

	public Vector3 getOffsetRot() {
		return offsetRot;
	}

	public void setOffsetRot(Vector3 offsetRot) {
		this.offsetRot = offsetRot;
	}

}
