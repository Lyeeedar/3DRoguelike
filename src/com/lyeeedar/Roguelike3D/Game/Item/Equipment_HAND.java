package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Camera;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Melee_Weapon_Style;

public abstract class Equipment_HAND extends Equippable{
	
	public enum WeaponType {
		MELEE,
		RANGED,
		SPELL
	}
	
	public static WeaponType convertStringtoWepType(String type)
	{
		WeaponType rType = null;
		if (type.equalsIgnoreCase("MELEE"))
		{
			rType = WeaponType.MELEE;
		}
		else if (type.equalsIgnoreCase("RANGED"))
		{
			rType = WeaponType.RANGED;
		}
		else if (type.equalsIgnoreCase("SPELL"))
		{
			rType = WeaponType.SPELL;
		}
		
		return rType;
	}
	
	public static Equipment_HAND getWeapon(WeaponType type, GameActor holder, String style, int side, 
			int strength, HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam, float attack_speed, float weight)
	{
		Equipment_HAND wep = null;
		
		if (type == WeaponType.MELEE)
		{
			wep = new MeleeWeapon(holder, MeleeWeapon.convertWeaponStyle(style), side, strength, ele_dam, dam_dam, attack_speed, weight);
		}
		else System.err.println("Failed at creating weapon: "+type);
		
		return wep;
	}
	
	public static Equipment_HAND getWeapon(WeaponType type, GameActor holder, Melee_Weapon_Style style, int side, 
			int strength, HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam, float attack_speed, float weight)
	{
		Equipment_HAND wep = null;
		
		if (type == WeaponType.MELEE)
		{
			wep = new MeleeWeapon(holder, style, side, strength, ele_dam, dam_dam, attack_speed, weight);
		}
		else System.err.println("Failed at creating weapon: "+type);
		
		return wep;
	}

	public boolean two_handed = false;
	public float CD;
	
	public Equipment_HAND(float WEIGHT) {
		super(WEIGHT);
	}


	public abstract void use();
	public abstract void draw(Camera cam);
	public abstract void update(float delta);
}
