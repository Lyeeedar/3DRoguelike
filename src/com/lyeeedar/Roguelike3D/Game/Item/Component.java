package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.HashMap;

import com.lyeeedar.Roguelike3D.Game.GameData.Element;

public class Component extends Item {
	
	public enum Component_Type
	{
		// Attack related
		TOOTH,
		CLAW,
		
		// Skin related
		FUR,
		LEATHER,
		FEATHER,
		
		// Bone related
	}
	
	public String name;
	public int drop_chance;
	public String description;
	public int weight_per_amount;
	public int weight;
	public int amount;
	
	public int soft_hard;
	public int flexible_brittle;
	
	public HashMap<Element, Integer> element;

	public Component() {
		// TODO Auto-generated constructor stub
	}

}
