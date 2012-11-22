package com.lyeeedar.Roguelike3D.Game.Actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class Enemy extends GameActor {

	public Enemy(VisibleObject vo, float x, float y, float z) {
		super(vo, x, y, z);
		// TODO Auto-generated constructor stub
	}
	
	public Enemy(String model, Color colour, String texture, float x, float y, float z)
	{
		super(model, colour, texture, x, y, z);
	}

	int arms = 2;
	
	float moveLock = 0;
	float turnLock = 0;
	
	int turnDirection = 0;
	
	@Override
	public void update(float delta) {
		
		float move = delta*10;
		
		velocity.y -= GameData.gravity*move;
		
		moveLock -= delta;
		turnLock -= delta;
		
		if (moveLock > 0)
		{
			forward_backward(move);
		}
		else
		{
			if (ran.nextInt(3) == 1)
			{
				moveLock = ran.nextInt(10);
			}
		}
			
		if (turnLock > 0)
		{
			rotate(turnDirection*xrotate*move*5, 0, 1, 0);
		}
		else
		{
			if (ran.nextInt(130) == 1)
			{
				turnLock = ran.nextInt(3);
				
				if (ran.nextInt(2) == 0)
				{
					turnDirection = 1;
				}
				else
				{
					turnDirection = -1;
				}
			}
		}
		
		applyMovement();
	}

}
