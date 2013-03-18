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
	
	@Override
	public void evaluateAI(float delta) {

		headBob += delta*15;
		
		move = delta * 10f;
		speed = GameData.calculateSpeed(actor.WEIGHT, actor.STRENGTH);
		
		if (actor.grounded)
		{
			if (GameData.controls.left()) actor.left_right(speed);
			if (GameData.controls.right()) actor.left_right(-speed);

			if (GameData.controls.up()) actor.forward_backward(2*speed);
			else headBob = 0;
			if (GameData.controls.down()) actor.forward_backward(-speed/2);

//			if ((actor.grounded) && (Gdx.input.isKeyPressed(Keys.SPACE) && !jumpCD)) 
//			{
//				jumpCD = true;
//				actor.grounded = false;
//				actor.velocity.y += 0.4;
//			}
//			else if (!Gdx.input.isKeyPressed(Keys.SPACE))
//			{
//				jumpCD = false;
//			}
		}
		else
		{
			headBob = 0;
		}
		actor.offsetPos.set(0, ((float) Math.sin(headBob)/5) + 2, 0);
		
		if (actor.L_HAND != null) {
			if (GameData.controls.leftClick())
			{
				actor.L_HAND.held();
			}
			else
			{
				actor.L_HAND.released();
			}
		}
		
		if (actor.R_HAND != null) {
			if (GameData.controls.rightClick())
			{
				actor.R_HAND.held();
			}
			else
			{
				actor.R_HAND.released();
			}
		}

		xR = (float)GameData.controls.getDeltaX()*GameObject.X_ROTATE;
		yR = (float)GameData.controls.getDeltaY()*GameObject.Y_ROTATE;
		
		if (xR < -15.0f) xR = -15.0f;
		else if (xR > 15.0f) xR = 15.0f;
		
		if (yR < -13.0f) yR = -13.0f;
		else if (yR > 13.0f) yR = 13.0f;
		
		actor.Yrotate(yR);
		actor.Xrotate(xR);
		
		actor.applyMovement(delta, GameData.gravity*10f*(float)actor.WEIGHT);
		actor.velocity.add(0, -GameData.gravity*move*(float)actor.WEIGHT, 0);

	}

}
