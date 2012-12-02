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
package com.lyeeedar.Roguelike3D.Game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.lyeeedar.Roguelike3D.Game.Actor.CollisionBox;
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

public class GameObject {
	
	public static final float PHYSICS_DAMAGE_THRESHHOLD = 2.0f;
	
	public String UID;
	
	protected final Random ran = new Random();
	
	protected final static float xrotate = -800f/720f;
	protected final static float yrotate = -600f/720f;

	// x y z
	protected final Vector3 position = new Vector3();
	protected final Vector3 rotation = new Vector3(1, 0, 1);
	protected final Vector3 velocity = new Vector3();
	
	public final Vector3 up = new Vector3(0, 1, 0);

	protected final Vector3 tmpVec = new Vector3();
	protected final Matrix4 tmpMat = new Matrix4();
	
	public CollisionBox collisionBox;

	public VisibleObject vo;
	
	public StillModel collisionMesh;
	public StillModelAttributes collisionAttributes;
	
	public boolean grounded = true;
	
	public PointLight boundLight;
	
	protected Matrix4 view = new Matrix4();
	
	public boolean visible = true;
	
	public String description = "";

	public GameObject(VisibleObject vo, float x, float y, float z)
	{
		create(vo, x, y, z);
	}
	
	public GameObject(String model, Color colour, String texture, float x, float y, float z)
	{
		this(ObjLoader.loadObj(Gdx.files.internal("data/models/"+model+".obj").read()), colour, texture, x, y, z);
	}
	
	public GameObject(Mesh mesh, Color colour, String texture, float x, float y, float z)
	{
		this(new VisibleObject(mesh, GL20.GL_TRIANGLES, colour, texture), x, y, z);
	}
	
	public void create(VisibleObject vo, float x, float y, float z)
	{
		UID = this.toString()+System.currentTimeMillis()+this.hashCode()+System.nanoTime();
		
		this.vo = vo;
		position.x = x;
		position.y = y;
		position.z = z;
		
		BoundingBox box = new BoundingBox();
		if (vo == null)
		{
			box = new BoundingBox(new Vector3(0, 0, 0), new Vector3(5, 5, 5));
		}
		else
		{
			vo.model.getBoundingBox(box);
		}

		Vector3 dimensions = box.getDimensions();
		
		float dist = 0;
		
		if (Math.abs(dimensions.x) > Math.abs(dimensions.z))
		{
			dimensions.z = dimensions.x * (dimensions.z / Math.abs(dimensions.z));
			dist = dimensions.z;
		}
		else
		{
			dimensions.x = dimensions.z * (dimensions.x / Math.abs(dimensions.x));
			dist = dimensions.x;
		}
		
		vo.attributes.radius = dist/2;
		
		Mesh mesh = Shapes.genCuboid(dimensions.cpy());
		StillModel model = new StillModel(new SubMesh[]{new StillSubMesh("Collision Box", mesh, GL20.GL_LINE_LOOP)});
		MaterialAttribute c = new ColorAttribute(Color.RED, ColorAttribute.diffuse);
		MaterialAttribute t = new TextureAttribute(new Texture(Gdx.files.internal("data/textures/blank.png")), 0, TextureAttribute.diffuseTexture);
		Material material = new Material("basic", c, t);	
		collisionAttributes = new StillModelAttributes(material, 1);

		collisionMesh = model;
		collisionBox = new CollisionBox(dimensions);
		collisionBox.position.add(x, y, z);
		
		translate(0, 0, 0);
	}

	float startVelocity = 0;
	float endVelocity = 0;
	float negatedVelocity = 0;
	final Ray ray = new Ray(new Vector3(), new Vector3());
	public void applyMovement()
	{
		if (velocity.len2() == 0) return;
		
		startVelocity = Math.abs(velocity.x)+Math.abs(velocity.y)+Math.abs(velocity.z);
		
		if (velocity.x < -2) velocity.x = -2;
		if (velocity.x > 2) velocity.x = 2;
		
		if (velocity.y < -2) velocity.y = -2;
		if (velocity.y > 2) velocity.y = 2;
		
		if (velocity.z < -2) velocity.z = -2;
		if (velocity.z > 2) velocity.z = 2;
		
		Level lvl = GameData.level;
		
		Tile below = lvl.getTile(position.x/10, position.z/10) ;
		
		// below
		if (position.y+velocity.y-vo.attributes.radius < below.floor) {
			velocity.y = 0;
			this.positionYAbsolutely(below.floor+vo.attributes.radius);
			grounded = true;
		}
		// above
		else if (position.y+velocity.y+vo.attributes.radius > below.roof) {
			tmpVec.set(position);
			this.positionYAbsolutely(below.roof-vo.attributes.radius);
			
			if (lvl.checkCollision(position.tmp(), vo.attributes.radius, UID))
			{
				this.positionAbsolutely(tmpVec);
			}
			
			velocity.y = 0;
			grounded = false;
		}
		else
		{
			this.translate(0, velocity.y, 0);
			grounded = false;
		}
		
		if (lvl.checkCollision(position.tmp().add(velocity.x, 0, velocity.z), vo.attributes.radius, UID)) {

			if (lvl.checkCollision(position.tmp().add(velocity.x, 0, 0), vo.attributes.radius, UID)) {
				getVelocity().x = 0;
			}

			if (lvl.checkCollision(position.tmp().add(velocity.x, 0, velocity.z), vo.attributes.radius, UID)) {
				getVelocity().z = 0;
			}
		}
		
		this.translate(getVelocity().x, 0, getVelocity().z);
		
		if (grounded)
		{
			if (getVelocity().x != 0)
			{
				getVelocity().x = 0;
			}
			
			if (getVelocity().z != 0)
			{
				getVelocity().z = 0;
			}
		}
		
		endVelocity = Math.abs(velocity.x)+Math.abs(velocity.y)+Math.abs(velocity.z);

		negatedVelocity = startVelocity-endVelocity;
		
		if (negatedVelocity > PHYSICS_DAMAGE_THRESHHOLD)
		{
			System.out.println("ouch! "+negatedVelocity);
		}
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
		
		vo.attributes.getRotation().setToLookAt(tmpVec.set(0, 0, 0).add(rotation), up).inv();
	}

	public void translate(float x, float y, float z)
	{
		translate(new Vector3(x, y, z));
	}
	
	public void translate(Vector3 vec)
	{
		position.add(vec);
		collisionBox.position.set(position);
		vo.attributes.getTransform().setToTranslation(position);
		if (boundLight != null) boundLight.position.set(position);
	}
	
	public void positionAbsolutely(Vector3 position)
	{
		this.position.set(position);
		collisionBox.position.set(position);
		vo.attributes.getTransform().setToTranslation(this.position);
		if (boundLight != null) boundLight.position.set(position);
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

	public final Vector3 nvec = new Vector3();
	public Vector3 getCPosition()
	{
		nvec.set(position).div(10);
		return nvec;
	}
	
	public Vector3 getPosition() {
		return position;
	}

	public Vector3 getVelocity() {
		return velocity;
	}

	public VisibleObject getVo() {
		return vo;
	}

	public void setVo(VisibleObject vo) {
		this.vo = vo;
	}

	public Vector3 getRotation() {
		return rotation;
	}

	public void dispose()
	{
		vo.dispose();
	}


	public String getUID() {
		return UID;
	}

	public void setUID(String uID) {
		UID = uID;
	}

	public CollisionBox getCollisionBox() {
		return collisionBox;
	}

	public void setCollisionBox(CollisionBox collisionBox) {
		this.collisionBox = collisionBox;
	}

	public StillModel getCollisionMesh() {
		return collisionMesh;
	}
	
	public void update(float delta)
	{
		
	}

}
