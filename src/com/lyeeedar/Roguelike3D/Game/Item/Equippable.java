package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.HashMap;

import com.lyeeedar.Roguelike3D.Game.GameData.Element;

public class Equippable extends Item {
	
	public int HEALTH = 0;
	public HashMap<Element, Integer> DEFENSES;
	
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
		DEFENSES = new HashMap<Element, Integer>();
		
		DEFENSES.put(Element.FIRE, 0);
		DEFENSES.put(Element.AIR, 0);
		DEFENSES.put(Element.WATER, 0);
		DEFENSES.put(Element.WOOD, 0);
		DEFENSES.put(Element.METAL, 0);
	}
}
