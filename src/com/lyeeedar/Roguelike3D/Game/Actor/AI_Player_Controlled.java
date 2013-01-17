package com.lyeeedar.Roguelike3D.Game.Actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;

public class AI_Player_Controlled extends AI_Package {

	public AI_Player_Controlled(GameActor actor) {
		super(actor);
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
		
		left_cooldown -= delta;
		right_cooldown -= delta;
		
		move = delta * 10f;
		
		actor.velocity.y -= GameData.gravity*move*actor.WEIGHT;

		if (actor.grounded)
		{
			if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) actor.left_right(move);
			if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) actor.left_right(-move);

			if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) actor.forward_backward(move*2);
			else headBob = 0;
			if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) actor.forward_backward(-move/2);

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
		actor.offsetPos.y = (float) Math.sin(headBob)/5;
		
		if (Gdx.input.isKeyPressed(Keys.B))
		{			
			actor.getVelocity().set(0, 2, 2);
		}
		
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && left_cooldown < 0)
		{
			left_cooldown = 0.5f;
			
			if (actor.L_HAND != null) actor.L_HAND.use();
		}
		
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && right_cooldown < 0)
		{
			right_cooldown = 0.5f;

			if (actor.R_HAND != null) actor.R_HAND.use();
		}
		
		actor.applyMovement();

		xR = (float)Gdx.input.getDeltaX()*GameObject.xrotate*move;
		yR = (float)Gdx.input.getDeltaY()*GameObject.yrotate*move;
		
		if (xR < -5.0f) xR = -5.0f;
		else if (xR > 5.0f) xR = 5.0f;
		
		if (yR < -3.0f) yR = -3.0f;
		else if (yR > 3.0f) yR = 3.0f;
		
		actor.Yrotate(yR);

		actor.Xrotate(xR);
		
		if (actor.L_HAND != null) actor.L_HAND.update(delta);
		if (actor.R_HAND != null) actor.R_HAND.update(delta);

	}

}
