package com.lyeeedar.Roguelike3D.Game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Graphics.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.VisibleObject;

public class GameObject {
	
	public String UID;
	
	final Random ran = new Random();
	
	final static float xrotate = -800f/720f;
	final static float yrotate = -600f/720f;

	// x y z
	protected Vector3 position = new Vector3();
	protected Vector3 rotation = new Vector3(-1, 0, 0);
	protected Matrix4 view = new Matrix4();
	protected Vector3 velocity = new Vector3();

	protected Vector3 tmpVec = new Vector3();
	protected Matrix4 tmpMat = new Matrix4();
	
	public CollisionBox collisionBox;

	public VisibleObject vo;
	
	public Mesh collisionMesh;
	
	public boolean grounded = true;

	public GameObject(VisibleObject vo, float x, float y, float z)
	{
		create(vo, x, y, z);
	}
	
	public GameObject(String model, Vector3 colour, String texture, float x, float y, float z)
	{
		Mesh mesh = ObjLoader.loadObj(Gdx.files.internal("data/models/"+model+".obj").read());
		this.vo = new VisibleObject(mesh, colour, texture);
		
		create(vo, x, y, z);
	}
	
	public void create(VisibleObject vo, float x, float y, float z)
	{
		UID = this.toString()+System.currentTimeMillis()+this.hashCode()+System.nanoTime();
		
		this.vo = vo;
		position.x = x;
		position.y = y;
		position.z = z;
		
		BoundingBox box = vo.getMesh().calculateBoundingBox();

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
	}

	public void applyMovement()
	{
		float oldX = position.x/10;
		float oldZ = position.z/10;
		
		velocity.y -= GameData.gravity;
		
		if (velocity.x < -2) velocity.x = -1;
		if (velocity.x > 2) velocity.x = 1;
		
		if (velocity.y < -2) velocity.y = -1;
		if (velocity.y > 2) velocity.y = 1;
		
		if (velocity.z < -2) velocity.z = -1;
		if (velocity.z > 2) velocity.z = 1;
		
		Level lvl = GameData.currentLevel;
		
		// Apply up/down movement (y axis)
		Vector3 cpos = this.getCPosition();
		
		CollisionBox box = collisionBox.cpy();
		box.translate(0, getVelocity().y, 0);
		
		if (lvl.checkCollision(cpos.x, cpos.y + getVelocity().y, cpos.z, box, UID)) {
			
			Tile t = lvl.getTile(cpos.x, cpos.y + getVelocity().y, cpos.z);
			
			if (cpos.y == t.height)
			{
				getVelocity().y = 0;
				grounded = true;
			}
			else if (getVelocity().y < 0)
			{
				getVelocity().y = t.height - cpos.y;
				grounded = true;
				
				box = collisionBox.cpy();
				box.translate(0, getVelocity().y, 0);
				
				if (lvl.checkEntities(cpos.x, cpos.y + getVelocity().y, cpos.z, box, UID) != null)
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
		
		box = collisionBox.cpy();
		box.translate(getVelocity().x, 0, getVelocity().z);
		
		if (lvl.checkCollision(cpos.x + getVelocity().x, cpos.y, cpos.z	+ getVelocity().z, box, UID)) {
			
			box = collisionBox.cpy();
			box.translate(getVelocity().x, 0, 0);
			
			if (lvl.checkCollision(cpos.x + getVelocity().x, cpos.y, cpos.z, box, UID)) {
				getVelocity().x = 0;
			}

			box = collisionBox.cpy();
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
				getVelocity().x /= 1.1f;
				
				if (Math.abs(getVelocity().x) < 0.01f) getVelocity().x = 0;
				
				getVelocity().x = 0;
			}
			
			if (getVelocity().z != 0)
			{
				getVelocity().z /= 1.1f;
				
				if (Math.abs(getVelocity().z) < 0.01f) getVelocity().z = 0;
				
				getVelocity().z = 0;
			}
		}
		
		float newX = position.x/10;
		float newZ = position.z/10;
		
		GameData.currentLevel.moveActor(oldX, oldZ, newX, newZ, UID);
		
	}
	
	Matrix4 rotationMatrix = new Matrix4();
	public void rotate(float angle, float x, float y, float z)
	{
		rotationMatrix.rotate(x, y, z, angle);
		
		rotation = new Vector3(0, 0, -1).rot(rotationMatrix);
	}

	public void translate(float x, float y, float z)
	{
		translate(new Vector3(x, y, z));
	}
	
	public void translate(Vector3 vec)
	{
		position.add(vec);
		collisionBox.translate(vec);
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
	
	static final Vector3 up = new Vector3(0, 1, 0);
	public void updateView()
	{
		view.setToLookAt(position, position.cpy().add(rotation), up);
	}

	public Vector3 getCPosition()
	{
		Vector3 nvec = position.cpy();
		nvec.div(10);
		return nvec;
	}
	
	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position.cpy();
		this.collisionBox.position = position.cpy();
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

	public void setRotation(Vector3 euler_rotation) {
		this.rotation = euler_rotation;
	}
	
	public void dispose()
	{
		vo.dispose();
	}

	public Matrix4 getView() {
		return view;
	}

	public void setView(Matrix4 view) {
		this.view = view;
	}

	public Matrix4 getRotationMatrix() {
		return rotationMatrix;
	}

	public void setRotationMatrix(Matrix4 rotationMatrix) {
		this.rotationMatrix = rotationMatrix;
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

}
