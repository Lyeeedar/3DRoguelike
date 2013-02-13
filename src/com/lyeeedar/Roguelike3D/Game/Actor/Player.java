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

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND.WeaponType;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Melee_Weapon_Style;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.MotionTrail;

public class Player extends GameActor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6756577346541496175L;

	public Player(Colour colour, String texture, float x, float y, float z, float scale, int primitive_type, String... model)
	{
		super(colour, texture, x, y, z, scale, primitive_type, model);

		WEIGHT = 1;
		
		HashMap<Damage_Type, Integer> DAM_DEF = new HashMap<Damage_Type, Integer>();
		
		DAM_DEF.put(Damage_Type.PIERCE, 50);
		DAM_DEF.put(Damage_Type.IMPACT, 50);
		DAM_DEF.put(Damage_Type.TOUCH, 0);

		HashMap<Element, Integer> ELE_DEF = new HashMap<Element, Integer>();
		
		ELE_DEF.put(Element.FIRE, 0);
		ELE_DEF.put(Element.AIR, 0);
		ELE_DEF.put(Element.WATER, 0);
		ELE_DEF.put(Element.WOOD, 0);
		ELE_DEF.put(Element.METAL, 0);
		ELE_DEF.put(Element.AETHER, 100);
		ELE_DEF.put(Element.VOID, 0);
		
		R_HAND = Equipment_HAND.getWeapon(WeaponType.MELEE, "sword", "SWING", 15, ELE_DEF, DAM_DEF, 20, 85, false, 3);
		R_HAND.equip(this, 2);
		L_HAND = Equipment_HAND.getWeapon(WeaponType.MELEE, "torch", "SWING", 15, ELE_DEF, DAM_DEF, 71, 13, false, 3);
		L_HAND.equip(this, 1);
		
		ai = new AI_Player_Controlled(this);

	}
}
