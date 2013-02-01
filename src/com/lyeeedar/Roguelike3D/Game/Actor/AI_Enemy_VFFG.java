package com.lyeeedar.Roguelike3D.Game.Actor;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lyeeedar.Roguelike3D.Game.GameData;

public class AI_Enemy_VFFG extends AI_Package {
	
	public static final float VIEW_NEAR = 0.1f;
	public static final float VIEW_FAR = 1000f;

	public AI_Enemy_VFFG(GameActor actor) {
		super(actor);
	}

	float move = 0;
	@Override
	public void evaluateAI(float delta) {
		
		move = delta * 10f;
		
		actor.velocity.y -= GameData.gravity*move*actor.WEIGHT;
		
		ArrayList<GameActor> actors = getVisibleActors();
		
		for (GameActor ga : actors)
		{
			if (!actor.checkFaction(ga.FACTIONS))
			{
				double a = angle(actor.getRotation(), actor.getPosition().tmp().sub(ga.getPosition()).nor());

				if (Math.abs(a) < delta*100)
				{
					actor.rotate(0,  1, 0, (float) a);
				}
				else if (a > 0)
				{
					actor.rotate(0, 1, 0, -delta*100);
				}
				else
				{
					actor.rotate(0, 1, 0, delta*100);
				}
				
				attack();
				
				actor.forward_backward(move);
			}
		}
		
		actor.applyMovement();
		
		left_cooldown -= delta;
		right_cooldown -= delta;
	}
	
	private static final Vector3 up = new Vector3(0, 1, 0);
	public double angle(Vector3 v1, Vector3 v2)
	{
		Vector3 referenceForward = v1;
		Vector3 referenceRight = up.tmp2().crs(referenceForward);
		Vector3 newDirection = v2;
		float angle = (float) Math.toDegrees(Math.acos(v1.dot(v2) / (v1.len()*v2.len())));
		float sign = (newDirection.dot(referenceRight) > 0.0f) ? 1.0f: -1.0f;
		float finalAngle = sign * angle;
		return finalAngle;
	}
	
	float left_cooldown = 0;
	float right_cooldown = 0;

	public void attack()
	{
		if (actor.L_HAND != null && left_cooldown < 0)
		{
			left_cooldown = actor.L_HAND.CD;
			actor.L_HAND.use();
		}
		
		if (actor.R_HAND != null && right_cooldown < 0)
		{
			right_cooldown = actor.R_HAND.CD;
			actor.R_HAND.use();
		}
		
	}

	public ArrayList<GameActor> getVisibleActors()
	{
		Camera cam = new PerspectiveCamera();
		cam.position.set(actor.getPosition());
		cam.direction.set(actor.getRotation());
		cam.near = VIEW_NEAR;
		cam.far = VIEW_FAR;
		cam.update();
		
		ArrayList<GameActor> actors = new ArrayList<GameActor>();
		
		for (GameActor ga : GameData.level.actors)
		{
			if (actor.getPosition().dst2(ga.getPosition()) < VIEW_FAR)
			{
				Ray ray = new Ray(actor.getPosition(), ga.getPosition().tmp().sub(actor.getPosition()).nor());
				if (!GameData.level.checkLevelCollisionRay(ray, ga.getPosition().dst2(actor.getPosition()))) actors.add(ga);
			}
		}
		
		return actors;
	}
}