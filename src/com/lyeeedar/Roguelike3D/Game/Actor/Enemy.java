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
package com.lyeeedar.Roguelike3D.Game.Actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class Enemy extends GameActor {

	public Enemy(Mesh mesh, Color colour, String texture, float x, float y,
			float z, float scale) {
		super(mesh, colour, texture, x, y, z, scale);
	}

	public Enemy(VisibleObject vo, float x, float y, float z, float scale) {
		super(vo, x, y, z, scale);

	}
	
	public Enemy(String model, Color colour, String texture, float x, float y, float z, float scale)
	{
		super(model, colour, texture, x, y, z, scale);
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
			Xrotate(turnDirection*xrotate*move*5);
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
