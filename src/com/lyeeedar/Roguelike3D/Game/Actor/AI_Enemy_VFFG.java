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

import java.util.ArrayDeque;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Pools;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Utils.Bag;

public class AI_Enemy_VFFG extends AI_Package {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1020570810692893435L;
	public static final float VIEW_NEAR = 0.1f;
	public static final float VIEW_FAR = 1000f;
	
	private final ArrayDeque<int[]> moves = new ArrayDeque<int[]>();
	
	private int[] tile = new int[2];
	
	private final int violence; private final int flee; private final int feed; private final int guard;

	public AI_Enemy_VFFG(GameActor actor, int violence, int flee, int feed, int guard) {
		super(actor);
		
		this.violence = violence;
		this.flee = flee;
		this.feed = feed;
		this.guard = guard;
	}

	private transient float move = 0;
	@Override
	public void evaluateAI(float delta) {
		Vector3 tmp = Pools.obtain(Vector3.class);
		
		int x = (int)((actor.position.x / 10) + 0.5f);
		int y = (int)((actor.position.y / 10) + 0.5f);
		
		move = GameData.calculateSpeed(actor.WEIGHT, actor.STRENGTH);
		actor.velocity.add(0, -GameData.gravity*move*actor.WEIGHT, 0);
		
		if (moves.size() == 0)
		{
			
		}
		else if (moves.getFirst()[0] == x && moves.getFirst()[1] == y)
		{
			moves.removeFirst();
		}
		else
		{
			int[] temp = moves.getFirst();
			tile[0] = temp[0] * 10;
			tile[1] = temp[1] * 10;
			
			double a = angle(actor.rotation, tmp.set(actor.position).sub(tile[0], 0, tile[1]).nor());

			if (Math.abs(a) < delta*100)
			{
				actor.rotate(0,  1, 0, (float) a);
				actor.forward_backward(move);
			}
			else if (a > 0)
			{
				actor.rotate(0, 1, 0, -delta*100);
			}
			else
			{
				actor.rotate(0, 1, 0, delta*100);
			}
		}
		
		Bag<GameActor> actors = getVisibleActors();
		
		for (GameActor ga : actors)
		{
			if (!actor.checkFaction(ga.FACTIONS))
			{
				double a = angle(actor.rotation, tmp.set(actor.position).sub(ga.position).nor());
				float dist = actor.position.dst(ga.position);

				if (Math.abs(a) < 15)
				{
					if (actor.L_HAND != null)
					{
						actor.L_HAND.released();
						if (dist <= actor.L_HAND.range)
						{
							actor.L_HAND.held();
						}
					}
					
					if (actor.R_HAND != null)
					{
						actor.R_HAND.released();
						if (dist <= actor.R_HAND.range)
						{
							actor.R_HAND.held();
						}
					}
					
					break;
				}
			}
		}
		
		Pools.free(tmp);
		actor.applyMovement(delta, GameData.gravity*10*(float)actor.WEIGHT);
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

	public Bag<GameActor> getVisibleActors()
	{
		Camera cam = new PerspectiveCamera();
		cam.position.set(actor.position);
		cam.direction.set(actor.rotation);
		cam.near = VIEW_NEAR;
		cam.far = VIEW_FAR;
		cam.update();
		
		Bag<GameActor> actors = new Bag<GameActor>();
		
//		for (GameActor ga : GameData.level.getActors())
//		{
//			if (actor.getPosition().dst2(ga.getPosition()) < VIEW_FAR)
//			{
//				Ray ray = new Ray(actor.getPosition(), ga.getPosition().tmp().sub(actor.getPosition()).nor());
//				if (!GameData.level.checkLevelCollisionRay(ray, ga.getPosition().dst2(actor.getPosition()))) actors.add(ga);
//			}
//		}
		
		return actors;
	}
}
