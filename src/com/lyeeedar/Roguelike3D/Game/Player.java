package com.lyeeedar.Roguelike3D.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Graphics.VisibleObject;

public class Player extends GameActor {

	
	public Player(VisibleObject vo, float x, float y, float z) {
		super(vo, x, y, z);
		// TODO Auto-generated constructor stub
	}
	
	public Player(String model, Vector3 colour, String texture, float x, float y, float z)
	{
		super(model, colour, texture, x, y, z);
	}

	@Override
	public void update(float delta) {
		
		float move = delta * 10;

		if (Gdx.input.isKeyPressed(Keys.LEFT)) left_right(move);
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) left_right(-move);

		if (Gdx.input.isKeyPressed(Keys.UP)) forward_backward(move);
		if (Gdx.input.isKeyPressed(Keys.DOWN)) forward_backward(-move);
		
		applyMovement();

		float pitch = (float)Gdx.input.getDeltaY()*yrotate*move;
		Vector3 dir = rotation;
		if( (dir.y>-0.7) && (pitch<0) || (dir.y<+0.7) && (pitch>0) )
		{
			Vector3 localAxisX = rotation.cpy();
			localAxisX.crs(up).nor();
			rotate(pitch, localAxisX.x, localAxisX.y, localAxisX.z);
		}

		rotate((float)Gdx.input.getDeltaX()*xrotate*move, 0, 1, 0);
		
		updateView();

	}
	
	

}
