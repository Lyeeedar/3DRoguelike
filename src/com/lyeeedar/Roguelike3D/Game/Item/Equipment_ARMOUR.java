package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.HashMap;

import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;

public class Equipment_ARMOUR extends Equippable {
	
	public int STRENGTH;
	public HashMap<Element, Integer> ELE_DEF = new HashMap<Element, Integer>();
	public HashMap<Damage_Type, Integer> DAM_DEF = new HashMap<Damage_Type, Integer>();

	public Equipment_ARMOUR(float WEIGHT) {
		super(WEIGHT);
	}

}
