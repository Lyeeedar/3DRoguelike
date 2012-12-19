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
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Weapon_Style;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.MotionTrail;

public class Player extends GameActor {
	
	public static final float WHIPLASHCD = 0.1f;
	public static final float WHIPLASHAMOUNT = 0.1f;
	
	public GameObject lookedAtObject;

	public final Vector3 offsetPos = new Vector3();
	public final Vector3 offsetRot = new Vector3();
	
	public Player(String model, Color colour, String texture, float x, float y, float z, float scale)
	{
		super(model, colour, texture, x, y, z, scale);
		//visible = false;
		//description = "This is you. Wave to yourself you!";
		WEIGHT = 1;
		
		R_HAND = new MeleeWeapon(this, Weapon_Style.SWING, 2);
		//R_HAND.use();
		
		L_HAND = new MeleeWeapon(this, Weapon_Style.SWING, 1);
		//L_HAND.use();
	}
	
	float left_cooldown = 0;
	float right_cooldown = 0;

	float move = 0;
	float xR = 0;
	float yR = 0;

	float headBob = 0;
	float whiplashCD = 0;
	float whiplashSTEP = 0;
	
	boolean jumpCD = false;
	@Override
	public void update(float delta) {	
		
		headBob += delta*15;
		
		if (whiplashCD > 0)
		{
			whiplashCD -= delta;
			offsetRot.y += whiplashSTEP*delta;
			
			if (whiplashCD <= 0)
			{
				offsetRot.y = 0;
				collidedVertically = false;
				collidedHorizontally = false;
			}
		}
		
		if (collidedHorizontally && whiplashCD <= 0)
		{
			whiplashSTEP = -1;
			offsetRot.y = WHIPLASHAMOUNT;
			whiplashCD = WHIPLASHCD;
		}
		else if (collidedVertically && whiplashCD <= 0)
		{
			whiplashSTEP = 1;
			offsetRot.y = -WHIPLASHAMOUNT;
			whiplashCD = WHIPLASHCD;
		}
		
		left_cooldown -= delta;
		right_cooldown -= delta;
		
		move = delta * 10;
		
		velocity.y -= GameData.gravity*move*WEIGHT;

		if (grounded)
		{
			if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) left_right(move);
			if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) left_right(-move);

			if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) forward_backward(move*2);
			else headBob = 0;
			if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) forward_backward(-move/2);

			if ((grounded) && (Gdx.input.isKeyPressed(Keys.SPACE) && !jumpCD)) 
			{
				jumpCD = true;
				grounded = false;
				velocity.y += 0.4;
			}
			else if (!Gdx.input.isKeyPressed(Keys.SPACE))
			{
				jumpCD = false;
			}
		}
		else
		{
			headBob = 0;
		}
		offsetPos.y = (float) Math.sin(headBob)/5;
		
		if (Gdx.input.isKeyPressed(Keys.B))
		{			
			this.getVelocity().set(0, 2, 2);
		}
		
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && left_cooldown < 0)
		{
			left_cooldown = 0.5f;
			
			if (L_HAND != null) L_HAND.use();
		}
		
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && right_cooldown < 0)
		{
			right_cooldown = 0.5f;

			if (R_HAND != null) R_HAND.use();
		}
		
		applyMovement();

		xR = (float)Gdx.input.getDeltaX()*xrotate*move;
		yR = (float)Gdx.input.getDeltaY()*yrotate*move;
		
		if (xR < -5.0f) xR = -5.0f;
		else if (xR > 5.0f) xR = 5.0f;
		
		if (yR < -3.0f) yR = -3.0f;
		else if (yR > 3.0f) yR = 3.0f;
		
		Yrotate(yR);

		Xrotate(xR);
		
		if (L_HAND != null) L_HAND.update(delta);
		if (R_HAND != null) R_HAND.update(delta);

	}
	
	@Override
	public void draw(Camera cam)
	{
		if (L_HAND != null) L_HAND.draw(cam);
		if (R_HAND != null) R_HAND.draw(cam);
	}
	@Override
	public void activate() {
	}

}
