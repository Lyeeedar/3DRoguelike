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
package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedModel;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedOneHandedStab;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedOneHandedSwing;

public class MeleeWeapon extends Equipment_HAND {
	
	private static final long serialVersionUID = -5519914081029109584L;
	
	public enum Melee_Weapon_Style {
		SWING,
		STAB
	}

	public static Melee_Weapon_Style convertWeaponStyle(String wep_style)
	{
		for (Melee_Weapon_Style mws : Melee_Weapon_Style.values())
		{
			if (wep_style.equalsIgnoreCase(""+mws)) return mws;
		}

		return null;
	}
	
	public String getString()
	{
		String elements = "";
		for (Element ele : Element.values())
		{
			elements += ele+": " + ele_dam.get(ele)  + "\n";
		}
		return 	"-----------------" +
				"Melee Weapon" + "\n" +
				"Strength: " + strength  + "\n" +
				"Pierce: " + dam_dam.get(Damage_Type.PIERCE)  + "\n" +
				"Impact: " + dam_dam.get(Damage_Type.IMPACT)  + "\n" +
				"Touch: " + dam_dam.get(Damage_Type.TOUCH)  + "\n" +
				elements +
				"Attack CD: " + attack_speed + "\n" +
				"Weight: " + WEIGHT + "\n" +
				"-----------------"
				;
	}

	public MeleeWeapon(Melee_Weapon_Style style,  
			float weight, int strength, HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam,
			float attack_speed, boolean two_handed, RiggedModel model) {
		super(weight, strength, ele_dam, dam_dam, attack_speed, two_handed, model);
		
		if (style == Melee_Weapon_Style.SWING)
		{
			model.rootNode.setBehaviour(new RiggedOneHandedSwing(model.rootNode, weight, attack_speed));
		}
		else if (style == Melee_Weapon_Style.STAB)
		{
			model.rootNode.setBehaviour(new RiggedOneHandedStab(model.rootNode, weight, attack_speed));
		}

	}

	@Override
	protected void updated(float delta)
	{
		GameActor ga = model.rootNode.checkCollision(getHolder());
		
		if (ga != null)
		{
			model.rootNode.cancel();
			
			if (ga != getHolder()) {
				damage(ga);
			}
		}
	}
	
	protected void drawed(Camera cam)
	{
	}
	
	public void dispose()
	{
	}

	@Override
	protected void fixReferencesSuper() {
	}

	@Override
	protected void unequipped() {
	}

	@Override
	protected void equipped(GameActor actor, int side) {

	}

	@Override
	public Table getDescriptionWidget(Skin skin) {
		return null;
	}

	@Override
	public Table getComparisonWidget(Equippable other, Skin skin) {
		return null;
	}
}