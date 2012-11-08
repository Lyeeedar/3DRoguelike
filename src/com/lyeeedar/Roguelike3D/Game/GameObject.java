package com.lyeeedar.Roguelike3D.Game;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Graphics.VisibleObject;

public class GameObject {
	
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
	
	public boolean collision = true;

	public VisibleObject vo;

	public GameObject(VisibleObject vo)
	{
		this.vo = vo;
	}

	public GameObject(VisibleObject vo, float x, float y, float z)
	{
		this.vo = vo;
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	public GameObject(String model, Vector3 colour, String texture, float x, float y, float z)
	{
		Mesh mesh = ObjLoader.loadObj(Gdx.files.internal("data/models/"+model+".obj").read());
		this.vo = new VisibleObject(mesh, colour, texture);
		position.x = x;
		position.y = y;
		position.z = z;
	}
	

	public void applyMovement()
	{
		Level lvl = GameData.currentLevel;
		
		if (collision) {
			// Apply up/down movement (y axis)
			Vector3 cpos = this.getCPosition();
			if (lvl.checkCollision(cpos.x, cpos.y + getVelocity().y, cpos.z)) {
				getVelocity().y = 0;
			}
			this.translate(0, getVelocity().y, 0);
			cpos.y += getVelocity().y;
			// Apply x and z axis movement
			if (lvl.checkCollision(cpos.x + getVelocity().x, cpos.y, cpos.z
					+ getVelocity().z)) {
				if (lvl.checkCollision(cpos.x + getVelocity().x, cpos.y, cpos.z)) {
					getVelocity().x = 0;
				}

				if (lvl.checkCollision(cpos.x, cpos.y, cpos.z + getVelocity().z)) {
					getVelocity().z = 0;
				}
			}
		}
		this.translate(getVelocity().x, 0, getVelocity().z);
		
		getVelocity().x = 0;
		getVelocity().z = 0;
		
	}
	
	Matrix4 rotationMatrix = new Matrix4();
	public void rotate(float angle, float x, float y, float z)
	{
		rotationMatrix.setToRotation(x, y, z, angle);
		
		rotation.rot(rotationMatrix);
	}

	public void translate(float x, float y, float z)
	{
		position.add(x, y, z);
	}
	
	public void translate(Vector3 vec)
	{
		position.add(vec);
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
		this.position = position;
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

}
