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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Melee_Weapon_Style;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Materials.MaterialAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.TextureAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModel;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillSubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Models.SubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedModel;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedModelNode;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;

public abstract class Equipment_HAND extends Equippable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1462740444282967851L;
	public enum WeaponType {
		MELEE,
		RANGED,
		SPELL
	}
	
	public static WeaponType convertStringtoWepType(String type)
	{
		for (WeaponType w : WeaponType.values())
		{
			if (type.equalsIgnoreCase(""+w)) return w;
		}
		
		return null;
	}

	public static Equipment_HAND getWeapon(String typeString, String visualType, String styleString, 
			int strength, HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam,
			float attack_speed, float weight, boolean two_handed, int range) {
		
		WeaponType type = convertStringtoWepType(typeString);
		
		return getWeapon(type, visualType, styleString, strength, ele_dam, dam_dam, attack_speed, weight, two_handed, range);
	}
	
	public static Equipment_HAND getWeapon(WeaponType type, String visualType, String styleString, 
			int strength, HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam,
			float attack_speed, float weight, boolean two_handed, int range) {
		
		Equipment_HAND weapon = null;
		RiggedModel model = null;

		if (type == WeaponType.MELEE)
		{
			if (visualType.equalsIgnoreCase("sword"))
			{
				model = RiggedModel.getSword(range);
			}
			else if (visualType.equalsIgnoreCase("torch"))
			{
				model = RiggedModel.getTorch();
			}
			weapon = new MeleeWeapon(MeleeWeapon.convertWeaponStyle(styleString),  
					strength, ele_dam, dam_dam,
					attack_speed, weight, two_handed, model);
		}
		
		return weapon;
	}
	
	public final boolean two_handed;
	public final float attack_speed;
	public final int strength;
	public final HashMap<Element, Integer> ele_dam;
	public final HashMap<Damage_Type, Integer> dam_dam;
	
	public transient GameActor holder;
	public String holderUID;
	/**
	 * 0 = none
	 * 1 = left
	 * 2 = right
	 */
	public int equippedSide;
	
	public transient float useCD = 0;
	
	public final RiggedModel model;
	
	public Equipment_HAND(float WEIGHT, int strength, 
			HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam,
			float attack_speed, boolean two_handed, RiggedModel model) {
		super(WEIGHT, Item_Type.WEAPON);
		
		this.model = model;
		this.two_handed = two_handed;
		this.strength = strength;
		this.ele_dam = ele_dam;
		this.dam_dam = dam_dam;
		this.attack_speed = attack_speed;
	}
	
	public void damage(GameActor ga)
	{
		System.out.println("HIT on "+ga.UID);
		ga.damage(strength, ele_dam, dam_dam);
	}
	
	public void unequip()
	{
		System.out.println("Unequipping");
		holder = null;
		holderUID = null;
		equippedSide = 0;
		unequipped();
	}
	protected abstract void unequipped();
	
	public void equip(GameActor actor, int side)
	{
		System.out.println("Equipping");
		
		model.create();
		
		if (side == 1)
		{
			actor.L_HAND = this;
			if (two_handed) {
				actor.R_HAND.unequip();
				actor.R_HAND = null;
			}
		}
		else if (side == 2)
		{
			actor.R_HAND = this;
			if (two_handed) {
				actor.L_HAND.unequip();
				actor.L_HAND = null;
			}
		}
		else
		{
			System.err.println("Invalid equip side: "+side);
		}
		
		holder = actor;
		holderUID = holder.UID;
		equippedSide = side;
		equipped(actor, side);
		
	}
	protected abstract void equipped(GameActor actor, int side);
	
	public void held()
	{
		model.held();
		if (holder == null)
		{
			System.err.println("Holder null!");
		}
		
		if (useCD > 0) return;
		useCD = attack_speed;
	}
	
	public void released()
	{
		model.released();
	}
	
	Matrix4 tmp = new Matrix4();
	public void draw(PrototypeRendererGL20 renderer)
	{
		model.draw(renderer);
	}
	protected abstract void drawed(Camera cam);
	
	public void update(float delta)
	{
		model.update(delta, holder);
		model.composeMatrixes(tmp.set(holder.vo.attributes.getTransform()).mul(holder.vo.attributes.getRotation()));
		
		useCD -= delta;
		updated(delta);
	}
	protected abstract void updated(float delta);
	
	public void create()
	{
		model.create();
	}
	
	public void fixReferences()
	{
		if (holderUID != null)
		{
			holder = GameData.level.getActor(holderUID);
		}
		
		model.fixReferences();
		
		fixReferencesSuper();
	}
	
	protected abstract void fixReferencesSuper();

	public void dispose()
	{
		model.dispose();
	}
}
