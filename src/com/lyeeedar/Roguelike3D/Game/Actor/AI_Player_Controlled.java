/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
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
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;

public class AI_Player_Controlled extends AI_Package {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4865259496600877342L;

	public AI_Player_Controlled(GameActor actor) {
		super(actor);
	}

	private transient float move = 0;
	private transient float speed = 0;
	private transient float xR = 0;
	private transient float yR = 0;

	private transient float headBob = 0;
	private transient float whiplashCD = 0;
	private transient float whiplashSTEP = 0;
	
	private transient boolean jumpCD = false;
	
	@Override
	public void evaluateAI(float delta) {

		headBob += delta*15;
		
		if (whiplashCD > 0)
		{
			whiplashCD -= delta;
			actor.offsetRot.y += whiplashSTEP*delta;
			
			if (whiplashCD <= 0)
			{
				actor.offsetRot.y = 0;
				actor.collidedVertically = false;
				actor.collidedHorizontally = false;
			}
		}
		
		if (actor.collidedHorizontally && whiplashCD <= 0)
		{
			whiplashSTEP = -1;
			actor.offsetRot.y = GameActor.WHIPLASHAMOUNT;
			whiplashCD = GameActor.WHIPLASHCD;
		}
		else if (actor.collidedVertically && whiplashCD <= 0)
		{
			whiplashSTEP = 1;
			actor.offsetRot.y = -GameActor.WHIPLASHAMOUNT;
			whiplashCD = GameActor.WHIPLASHCD;
		}
		
		move = delta * 10f;
		speed = GameData.calculateSpeed(actor.WEIGHT, actor.STRENGTH);
		
		if (actor.grounded)
		{
			if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) actor.left_right(speed);
			if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) actor.left_right(-speed);

			if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) actor.forward_backward(2*speed);
			else headBob = 0;
			if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) actor.forward_backward(-speed/2);

			if ((actor.grounded) && (Gdx.input.isKeyPressed(Keys.SPACE) && !jumpCD)) 
			{
				jumpCD = true;
				actor.grounded = false;
				actor.velocity.y += 0.4;
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
		actor.offsetPos.y = ((float) Math.sin(headBob)/5) + 2;
		
		if (Gdx.input.isKeyPressed(Keys.B))
		{			
			actor.getVelocity().set(0, 2, 2);
		}
		
		if (Gdx.input.isKeyPressed(Keys.F))
		{			
			actor.positionYAbsolutely(20);
		}
		
		if (Gdx.input.isKeyPressed(Keys.L))
		{			
			GameData.load();
		}
		
		if (actor.L_HAND != null) {
			if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
			{
				actor.L_HAND.held();
			}
			else
			{
				actor.L_HAND.released();
			}
		}
		
		if (actor.R_HAND != null) {
			if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
			{
				actor.R_HAND.held();
			}
			else
			{
				actor.R_HAND.released();
			}
		}

		xR = (float)Gdx.input.getDeltaX()*GameObject.xrotate;
		yR = (float)Gdx.input.getDeltaY()*GameObject.yrotate;
		
		if (xR < -15.0f) xR = -15.0f;
		else if (xR > 15.0f) xR = 15.0f;
		
		if (yR < -13.0f) yR = -13.0f;
		else if (yR > 13.0f) yR = 13.0f;
		
		actor.Yrotate(yR);
		actor.Xrotate(xR);
		
		actor.applyMovement(delta, GameData.gravity*10f*(float)actor.WEIGHT);
		actor.velocity.y -= GameData.gravity*move*(float)actor.WEIGHT;


	}

}
