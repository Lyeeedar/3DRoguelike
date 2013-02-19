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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Graphics.*;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Roguelike3D.Graphics.Materials.ColorAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Materials.MaterialAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.TextureAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModel;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModelAttributes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillSubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Models.SubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.MotionTrail;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.ForwardRenderer;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer;

public abstract class GameObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1356577977889288007L;

	public static final float PHYSICS_DAMAGE_THRESHHOLD = 2.0f;
	
	public final static float xrotate = -360f/400f;
	public final static float yrotate = -360f/400f;
	
	public final String UID;
	
	protected Random ran = new Random();

	// x y z
	protected final Vector3 position = new Vector3();
	protected final Vector3 rotation = new Vector3(1, 0, 1);
	public final Vector3 velocity = new Vector3();
	
	public final Vector3 up = new Vector3(0, 1, 0);

	protected final Vector3 tmpVec = new Vector3();
	protected final Matrix4 tmpMat = new Matrix4();

	public VisibleObject vo;
	protected transient PointLight boundLight;
	protected transient ParticleEmitter particleEmitter;
	public String particleEmitterUID;
	
	public String boundLightUID;
	
	public boolean grounded = true;
	public boolean visible = true;
	public boolean solid = true;
	
	public String shortDesc = "";
	public String longDesc = "";
	
	public transient boolean collidedVertically = false;
	public transient boolean collidedHorizontally = false;

	private transient float startVelocityHori = 0;
	private transient float endVelocityHori = 0;
	private transient float startVelocityVert = 0;
	private transient float endVelocityVert = 0;
	private transient float negatedVelocity = 0;

	public GameObject(Colour colour, String texture, float x, float y, float z, float scale, int primitive_type, String... model)
	{
		UID = this.toString()+System.currentTimeMillis()+this.hashCode()+System.nanoTime();
		
		VisibleObject vo = new VisibleObject(primitive_type, colour, texture, scale, model);
		
		this.vo = vo;
		position.set(x, y, z);
	}
	
	public void addParticleEmitter(ParticleEmitter emitter)
	{
		this.particleEmitter = emitter;
		this.particleEmitterUID = emitter.UID;
	}
	
	public void fixReferences()
	{
		if (particleEmitterUID != null)
		{
			particleEmitter = GameData.level.getParticleEmitter(particleEmitterUID);
		}
		
		if (boundLightUID != null)
		{
			boundLight = GameData.lightManager.getDynamicLight(boundLightUID);
		}
		
		fixReferencesSuper();
	}
	
	public abstract void fixReferencesSuper();
	
	public void create()
	{
		vo.create();

		translate(0, 0, 0);
		
		created();
	}
	
	public abstract void created();

	private final Vector3 tmpVelocity = new Vector3();
	
	public void applyMovement(float delta, float vertical_acceleration)
	{
		if (velocity.len2() == 0) return;
		
		startVelocityHori = Math.abs(velocity.x)+Math.abs(velocity.z);
		startVelocityVert = Math.abs(velocity.y);
		
		if (velocity.x < -2) velocity.x = -4;
		if (velocity.x > 2) velocity.x = 4;
		
		if (velocity.y < -2) velocity.y = -4;
		if (velocity.y > 2) velocity.y = 4;
		
		if (velocity.z < -2) velocity.z = -4;
		if (velocity.z > 2) velocity.z = 4;
		
		Level lvl = GameData.level;
		
		tmpVelocity.set(velocity.x, (velocity.y - 0.5f*vertical_acceleration*delta), velocity.z);
		tmpVelocity.mul(delta*100);
		
		Tile below = lvl.getTile(position.x/10, position.z/10);
		
		//float vertical_movement = (velocity.y - 0.5f*vertical_acceleration*delta)*delta*100;
		
		// Check for collision
		if (lvl.checkCollision(position.tmp().add(tmpVelocity), vo.attributes.radius, UID))
		{
			// Collision! Now time to find which axis the collision was on. (Vertical or Horizontal)
			
			// ----- Check Vertical START ----- //
			
			if ((lvl.checkEntities(position.tmp().add(0, tmpVelocity.y, 0), vo.attributes.radius, UID) != null) || 
					(lvl.checkLevelObjects(position.tmp().add(0, tmpVelocity.y, 0), vo.attributes.radius) != null))
			{
				velocity.y = 0;
				grounded = true;
			}
			// below
			else if (position.y+tmpVelocity.y-getRadius() < below.floor) {
				velocity.y = 0;
				tmpVec.set(position);
				this.positionYAbsolutely(below.floor+getRadius());
				
				if (lvl.checkCollision(position.tmp(), vo.attributes.radius, UID))
				{
					this.positionAbsolutely(tmpVec);
				}
				grounded = true;
			}
			// above
			else if (lvl.hasRoof && position.y+tmpVelocity.y+getRadius() > below.roof) {
				velocity.y = 0;
				tmpVec.set(position);
				this.positionYAbsolutely(below.roof-vo.attributes.radius);
				
				if (lvl.checkCollision(position.tmp(), vo.attributes.radius, UID))
				{
					this.positionAbsolutely(tmpVec);
				}
				grounded = false;
			}
			// No y collision
			else
			{
				this.translate(0, tmpVelocity.y, 0);
				grounded = false;
			}
			
			// ----- Check Vertical END ----- //
			
			
			// ----- Check Horizontal START ----- //
			
			if (lvl.checkCollision(position.tmp().add(tmpVelocity.x, 0, tmpVelocity.z), vo.attributes.radius, UID)) {

				if (lvl.checkCollision(position.tmp().add(tmpVelocity.x, 0, 0), vo.attributes.radius, UID)) {
					velocity.x = 0;
					tmpVelocity.x = 0;
				}

				if (lvl.checkCollision(position.tmp().add(tmpVelocity.x, 0, tmpVelocity.z), vo.attributes.radius, UID)) {
					velocity.z = 0;
					tmpVelocity.z = 0;
				}
			}
			
			this.translate(tmpVelocity.x, 0, tmpVelocity.z);
			
			// ----- Check Horizontal END ----- //
		}
		// No collision! So move normally
		else
		{
			this.translate(tmpVelocity);
			grounded = false;
		}
		
		if (grounded)
		{
			velocity.x = 0;
			velocity.z = 0;
		}
		
//		// Work our negated velocity from collision
//		endVelocityHori = Math.abs(velocity.x)+Math.abs(velocity.z);
//		negatedVelocity = startVelocityHori-endVelocityHori;
//		
//		if (negatedVelocity > PHYSICS_DAMAGE_THRESHHOLD)
//		{
//			collidedHorizontally = true;
//			System.out.println("ouch! Hori!"+negatedVelocity);
//		}
//		
//		endVelocityVert = Math.abs(velocity.y);
//
//		negatedVelocity = startVelocityVert-endVelocityVert;
//		
//		if (negatedVelocity > PHYSICS_DAMAGE_THRESHHOLD)
//		{
//			//collidedVertically = true;
//			System.out.println("ouch! Vert!"+negatedVelocity);
//		}
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
		rotate(tmpVec.set(0, 1, 0), angle);
	}
	
	public void rotate (float x, float y, float z, float angle) {
		rotate(tmpVec.set(x, y, z), angle);
	}

	/** Rotates the direction and up vector of this camera by the given angle around the given axis. The direction and up vector
	 * will not be orthogonalized.
	 *
	 * @param axis
	 * @param angle the angle */
	public void rotate (Vector3 axis, float angle) {
		tmpMat.setToRotation(axis, angle);
		rotation.mul(tmpMat).nor();
		up.mul(tmpMat).nor();
		
		if (vo.attributes != null) vo.attributes.getRotation().setToLookAt(tmpVec.set(0, 0, 0).add(rotation), up).inv();
	}

	public void translate(float x, float y, float z)
	{
		translate(new Vector3(x, y, z));
	}
	
	public void translate(Vector3 vec)
	{
		position.add(vec);

		positionAbsolutely(position);
	}
	
	public void positionAbsolutely(Vector3 position)
	{
		this.position.set(position);
		if (vo.attributes != null) vo.attributes.getTransform().setToTranslation(position);
		if (boundLight != null) boundLight.position.set(position);
		if (particleEmitter != null) particleEmitter.setPosition(vo.attributes.getSortCenter());
	}

	public void positionYAbsolutely(float y)
	{
		positionAbsolutely(tmpVec.set(position.x, y, position.z));
	}
	
	public void positionAbsolutely(float x, float y, float z)
	{
		positionAbsolutely(tmpVec.set(x, y, z));
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
		if (boundLight != null) GameData.lightManager.removeDynamicLight(boundLightUID);
		if (particleEmitter != null) {
			GameData.level.removeParticleEmitter(particleEmitterUID);
			particleEmitter.dispose();
		}
		disposed();
	}
	
	protected abstract void disposed();

	public String getUID() {
		return UID;
	}
	
	public abstract void update(float delta);
	
	public abstract void draw(Renderer renderer);
	
	public abstract void activate();
	
	public abstract String getActivatePrompt();

	/**
	 * @return the boundLight
	 */
	public PointLight getBoundLight() {
		return boundLight;
	}

	/**
	 * @param boundLight the boundLight to set
	 */
	public void setBoundLight(PointLight boundLight) {
		this.boundLight = boundLight;
	}

	/**
	 * @return the boundLightUID
	 */
	public String getBoundLightUID() {
		return boundLightUID;
	}

	/**
	 * @param boundLightUID the boundLightUID to set
	 */
	public void setBoundLightUID(String boundLightUID) {
		this.boundLightUID = boundLightUID;
	}

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

}
