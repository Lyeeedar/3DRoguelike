package com.lyeeedar.Roguelike3D.Game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Game.Actor.CollisionBox;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Graphics.*;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class GameObject {
	
	public String UID;
	
	protected final Random ran = new Random();
	
	protected final static float xrotate = -800f/720f;
	protected final static float yrotate = -600f/720f;

	// x y z
	protected Vector3 position = new Vector3();
	protected Vector3 rotation = new Vector3(1, 0, 1);
	protected Vector3 velocity = new Vector3();
	
	public Vector3 up = new Vector3(0, 1, 0);

	protected Vector3 tmpVec = new Vector3();
	protected Matrix4 tmpMat = new Matrix4();
	
	public CollisionBox collisionBox;

	public VisibleObject vo;
	
	public Mesh collisionMesh;
	
	public boolean grounded = true;
	
	public PointLight boundLight;
	
	protected Matrix4 view = new Matrix4();

	public GameObject(VisibleObject vo, float x, float y, float z)
	{
		create(vo, x, y, z);
	}
	
	public GameObject(String model, Color colour, String texture, float x, float y, float z)
	{
		if (model == null)
		{
			vo = null;
		}
		else
		{
			Mesh mesh = ObjLoader.loadObj(Gdx.files.internal("data/models/"+model+".obj").read());
			vo = new VisibleObject(mesh, GL20.GL_TRIANGLES, colour, texture);
		}
		
		create(vo, x, y, z);
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
			box = new BoundingBox(new Vector3(0, 0, 0), new Vector3(10, 10, 10));
		}
		else
		{
			vo.model.getBoundingBox(box);
		}
		 

		Vector3 dimensions = box.getDimensions().div(2.0f);
		
		float min = 0;
	
		if (Math.abs(dimensions.x) > Math.abs(dimensions.z))
		{
			min = box.min.x;
			dimensions.z = dimensions.x * (dimensions.z / Math.abs(dimensions.z));
		}
		else
		{
			min = box.min.z;
			dimensions.x = dimensions.z * (dimensions.x / Math.abs(dimensions.x));
		}
		
		Mesh mesh = Shapes.genCuboid(dimensions);
		
		//Shapes.translateCubeMesh(mesh, min, box.min.y, min);
		
		collisionMesh = mesh;
		collisionBox = new CollisionBox(dimensions);
		collisionBox.position = new Vector3(x, y, z);
	}

	public void applyMovement()
	{
		velocity.y -= GameData.gravity;
		
		if (velocity.x < -2) velocity.x = -1;
		if (velocity.x > 2) velocity.x = 1;
		
		if (velocity.y < -2) velocity.y = -1;
		if (velocity.y > 2) velocity.y = 1;
		
		if (velocity.z < -2) velocity.z = -1;
		if (velocity.z > 2) velocity.z = 1;
		
		Level lvl = GameData.level;
		
		// Apply up/down movement (y axis)
		Vector3 cpos = this.getCPosition();
		
		final CollisionBox box = new CollisionBox();
		collisionBox.cpy(box);
		box.translate(0, getVelocity().y, 0);
		
		if (lvl.checkCollision(cpos.x, cpos.y + getVelocity().y, cpos.z, box, UID)) {
			
			Tile t = lvl.getTile(cpos.x, cpos.z);
			
			float ypos = cpos.y*10;
			
			if (ypos == t.height)
			{
				getVelocity().y = 0;
				grounded = true;
			}
			else if (ypos < t.floor)
			{
				System.out.println("lower than the floor!    "+UID);
				this.positionYAbsolutely(t.floor*10);
				getVelocity().y = 0;
				grounded = true;
			}
			else if (ypos > t.roof)
			{
				System.out.println("higher than the roof!    "+UID);
				this.positionYAbsolutely((t.roof-1)*10);
				getVelocity().y = 0;
			}
			else if (getVelocity().y < 0)
			{
				getVelocity().y = t.height - cpos.y;
				grounded = true;
				
				collisionBox.cpy(box);
				box.translate(0, getVelocity().y, 0);
				
				if (getVelocity().y > 0 || lvl.checkEntities(cpos.x, cpos.y + getVelocity().y, cpos.z, box, UID) != null)
				{
					getVelocity().y = 0;
				}
			}
			else
			{
				getVelocity().y = 0;
			}
			
		}
		else
		{
			grounded = false;
		}
		this.translate(0, getVelocity().y, 0);
		
		
		cpos.y += getVelocity().y;
		// Apply x and z axis movement
		
		collisionBox.cpy(box);
		box.translate(getVelocity().x, 0, getVelocity().z);
		
		if (lvl.checkCollision(cpos.x + getVelocity().x, cpos.y, cpos.z	+ getVelocity().z, box, UID)) {
			
			collisionBox.cpy(box);
			box.translate(getVelocity().x, 0, 0);
			
			if (lvl.checkCollision(cpos.x + getVelocity().x, cpos.y, cpos.z, box, UID)) {
				getVelocity().x = 0;
			}

			collisionBox.cpy(box);
			box.translate(0, 0, getVelocity().z);
			
			if (lvl.checkCollision(cpos.x, cpos.y, cpos.z + getVelocity().z, box, UID)) {
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
		
	}

	public void rotate (float angle, float axisX, float axisY, float axisZ) {
		rotate(tmpVec.set(axisX, axisY, axisZ), angle);
	}

	/** Rotates the direction and up vector of this camera by the given angle around the given axis. The direction and up vector
	 * will not be orthogonalized.
	 *
	 * @param axis
	 * @param angle the angle */
	public void rotate (Vector3 axis, float angle) {
		vo.attributes.getTransform().rotate(axis.x, axis.y, axis.z, angle);
		
		tmpMat.setToRotation(axis, angle);
		rotation.mul(tmpMat).nor();
		up.mul(tmpMat).nor();
	}

	public void translate(float x, float y, float z)
	{
		translate(new Vector3(x, y, z));
	}
	
	public void translate(Vector3 vec)
	{
		vo.attributes.getTransform().translate(vec);
		position.add(vec);
		collisionBox.translate(vec);
		if (boundLight != null) boundLight.position.set(position);
	}
	
	public void positionAbsolutely(Vector3 position)
	{
		vo.attributes.getTransform().setToTranslation(position);
		this.position.set(position);
		collisionBox.position.set(position);
		if (boundLight != null) boundLight.position.set(position);
	}

	public void positionYAbsolutely(float y)
	{
		positionAbsolutely(tmpVec.set(position.x, y, position.z));
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


	public void setVelocity(Vector3 velocity) {
		this.velocity = velocity;
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

	public Mesh getCollisionMesh() {
		return collisionMesh;
	}

	public void setCollisionMesh(Mesh collisionMesh) {
		this.collisionMesh = collisionMesh;
	}
	
	public void update(float delta)
	{
		
	}

}
