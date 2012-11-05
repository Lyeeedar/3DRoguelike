package com.lyeeedar.Roguelike3D.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Graphics.VisibleObject;

public class Player extends GameActor {

	
	public Player(VisibleObject vo, float x, float y, float z) {
		super(vo, x, y, z);
		// TODO Auto-generated constructor stub
	}
	
	float xrotate = -640f/720f;
	float yrotate = -480f/720f;

	@Override
	public void update(float delta) {

		if (Gdx.input.isKeyPressed(Keys.LEFT)) left_right(0.3f);
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) left_right(-0.3f);

		if (Gdx.input.isKeyPressed(Keys.UP)) forward_backward(0.3f);
		if (Gdx.input.isKeyPressed(Keys.DOWN)) forward_backward(-0.3f);
		
		applyMovement();

		float pitch = (float)Gdx.input.getDeltaY()*yrotate;

		Vector3 dir = rotation.cpy();
		if( (dir.nor().y>-0.9) && (pitch<0) || (dir.nor().y<+0.9) && (pitch>0) )
		{
			Vector3 localAxisX = rotation.cpy();
			localAxisX.crs(up.tmp()).nor();
			rotate(pitch, localAxisX.x, localAxisX.y, localAxisX.z);
			up.nor();
		}

		rotate((float)Gdx.input.getDeltaX()*xrotate, 0, 1, 0);

	}
	
	

}
