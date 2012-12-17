package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.HashMap;

import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;

public class Equippable extends Item {
	
	public int HEALTH = 0;
	public HashMap<Element, Integer> ELE_DEFENSES;
	public HashMap<Damage_Type, Integer> DEFENSES;
	
	public int WEIGHT = 0;
	
	public float SPEED = 0;
	public int STRENGTH = 0;
	public int IQ = 0;
	
	public int SIGHT = 0;
	public float ATTACK_SPEED = 0;
	public float CAST_SPEED = 0;

	public Equippable() {
		setupDefenses();
	}

	
	public void setupDefenses()
	{
		DEFENSES = new HashMap<Damage_Type, Integer>();
		
		DEFENSES.put(Damage_Type.PIERCE, 0);
		DEFENSES.put(Damage_Type.IMPACT, 0);
		DEFENSES.put(Damage_Type.TOUCH, 0);
		
		ELE_DEFENSES = new HashMap<Element, Integer>();
		
		ELE_DEFENSES.put(Element.FIRE, 0);
		ELE_DEFENSES.put(Element.AIR, 0);
		ELE_DEFENSES.put(Element.WATER, 0);
		ELE_DEFENSES.put(Element.WOOD, 0);
		ELE_DEFENSES.put(Element.METAL, 0);
		ELE_DEFENSES.put(Element.AETHER, 0);
		ELE_DEFENSES.put(Element.VOID, 0);
	}
}
