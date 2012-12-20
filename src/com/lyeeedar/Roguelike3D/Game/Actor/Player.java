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

	public Player(String model, Color colour, String texture, float x, float y, float z, float scale)
	{
		super(model, colour, texture, x, y, z, scale);

		WEIGHT = 1;
		
		R_HAND = new MeleeWeapon(this, Weapon_Style.SWING, 2);
		L_HAND = new MeleeWeapon(this, Weapon_Style.SWING, 1);
		
		ai = new AI_Player_Controlled(this);

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
