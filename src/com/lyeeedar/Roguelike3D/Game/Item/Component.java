package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.HashMap;

import com.lyeeedar.Roguelike3D.Game.GameData.Element;

public class Component extends Item implements Comparable<Component>{
	
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
	
	public Component_Type type;
	
	public int rarity;

	public Component(Component_Type type, String name, int rarity, int drop_chance,
			String description, int weight_per_amount, int amount, int soft_hard,
			int flexible_brittle, HashMap<Element, Integer> element) {
		
		this.type = type;
		this.name = name;
		this.rarity = rarity;
		this.drop_chance = drop_chance;
		this.description = description;
		this.weight_per_amount = weight_per_amount;
		this.amount = amount;
		this.weight = weight_per_amount * amount;
		this.soft_hard = soft_hard;
		this.flexible_brittle = flexible_brittle;
		this.element = element;

	}
	
	public static Component_Type convertComponentType(String typeName)
	{
		Component_Type type = null;
		
		if (typeName.equalsIgnoreCase("tooth"))
		{
			type = Component_Type.TOOTH;
		}
		else if (typeName.equalsIgnoreCase("claw"))
		{
			type = Component_Type.CLAW;
		}
		else if (typeName.equalsIgnoreCase("fur"))
		{
			type = Component_Type.FUR;
		}
		else if (typeName.equalsIgnoreCase("leather"))
		{
			type = Component_Type.LEATHER;
		}
		else if (typeName.equalsIgnoreCase("feather"))
		{
			type = Component_Type.FEATHER;
		}
		
		return type;
	}
	
	@Override
	public String toString()
	{
		return "--------------------" + "\n" +
				"Name: " +name+"\n" +
				"Type: "+type+"\n" +
				"Rarity: "+rarity+"\n" +
				"Chance: "+drop_chance+"\n"+
				"Description: "+description+"\n"+
				"WpA: "+weight_per_amount+"\n"+
				"Amount: "+amount+"\n"+
				"Weight: "+weight+"\n"+
				"Soft_Hard: "+soft_hard+"\n"+
				"Flexible_Brittle: "+flexible_brittle+"\n"+
				"Element: "+"\n"+
				"	FIRE: "+element.get(Element.FIRE)+"\n"+
				"	WATER: "+element.get(Element.WATER)+"\n"+
				"	AIR: "+element.get(Element.AIR)+"\n"+
				"	WOOD: "+element.get(Element.WOOD)+"\n"+
				"	METAL: "+element.get(Element.METAL)+"\n"+
				"	AETHER: "+element.get(Element.AETHER)+"\n"+
				"	VOID: "+element.get(Element.VOID)+"\n"+
				"--------------------"
				;
	}

	@Override
	public int compareTo(Component o) {
		if (o.hashCode() < this.hashCode()) return -1;
		else if (o.hashCode() > this.hashCode()) return 1;
		return 0;
	}

}
