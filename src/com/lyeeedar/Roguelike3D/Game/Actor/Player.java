package com.lyeeedar.Roguelike3D.Game.Actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class Player extends GameActor {

	
	public Player(VisibleObject vo, float x, float y, float z) {
		super(vo, x, y, z);
		// TODO Auto-generated constructor stub
	}
	
	public Player(String model, Color colour, String texture, float x, float y, float z)
	{
		super(model, colour, texture, x, y, z);
	}

	@Override
	public void update(float delta) {
		
		float move = delta * 10;

		if (grounded)
		{
			if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) left_right(move);
			if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) left_right(-move);

			if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) forward_backward(move);
			if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) forward_backward(-move);

			if ( (grounded) && (Gdx.input.isKeyPressed(Keys.SPACE))) velocity.y += 0.4;
		}
		
		if (Gdx.input.isKeyPressed(Keys.B))
		{
			velocity.y = 1.3f;
			velocity.x = 0.9f;
		}
		
		applyMovement();

		float pitch = (float)Gdx.input.getDeltaY()*yrotate*move;
		Vector3 dir = rotation.cpy().nor();
		//System.out.println(dir.y);
		//if( (dir.y>-0.7) && (pitch<0) || (dir.y<+0.7) && (pitch>0) )
		//{
			//rotate(pitch, 1, 0, 0);
			//rotate(pitch, rotation.x, 0, rotation.y);
		//}

		if( (dir.y>-0.7) && (pitch<0) || (dir.y<+0.7) && (pitch>0) )
		{
			Vector3 localAxisX = rotation.cpy();
			localAxisX.crs(up.tmp()).nor();
			rotate(pitch, localAxisX.x, localAxisX.y, localAxisX.z);

		}

		rotate((float)Gdx.input.getDeltaX()*xrotate*move, 0, 1, 0);
	}
	
	

}
