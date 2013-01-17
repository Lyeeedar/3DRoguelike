package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.ArrayList;
import java.util.HashMap;

import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Item.Component.Component_Type;
import com.lyeeedar.Roguelike3D.Game.Item.Item.Item_Type;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Weapon_Style;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RecipeReader;

public class Recipe implements Comparable<Recipe>
{
	// The recipe rarity
	public int rarity;
	// The type of the resulting item (armour, weapon etc)
	public Item_Type type;
	// The components required to create the item
	public HashMap<Character, Recipe_Component> components;
	// The layout of the visual recipe (will be converted by the UI into a usuable grid)
	public char[][] visualGrid;
	// The actual recipe
	public Recipe_Type recipe;
	
	public String recipeName;
	
	RecipeReader reader;
	public Recipe(RecipeReader reader)
	{
		this.reader = reader;
		
		recipeName = reader.getRecipeName();
		type = reader.getItemType();
		rarity = reader.getRecipeRarity();
		visualGrid = reader.getVisual();
		
		components = new HashMap<Character, Recipe_Component>();
		
		for (int x = 0; x < visualGrid.length; x++)
		{
			for (int y = 0; y < visualGrid[0].length; y++)
			{
				char c = visualGrid[x][y];
				if (c == ' ') continue;
				
				components.put(c, new Recipe_Component(c, reader.getComponentName(c), reader.getComponentAmount(c), reader.getComponentConstraints(c)));
			}
		}
	}
	
	public boolean checkAll(Component c)
	{
		for (int x = 0; x < visualGrid.length; x++)
		{
			for (int y = 0; y < visualGrid[0].length; y++)
			{
				if (checkComponent(c, visualGrid[x][y])) return true;
			}
		}
		return false;
	}
	
	public boolean checkComponent(Component c, char slot)
	{
		if (slot == ' ') return false;
		
		if (!components.get(slot).check(c.type)) return false;
		
		return (c.amount >= components.get(slot).amount);
	}

	@Override
	public int compareTo(Recipe r) {
		if (r.hashCode() < this.hashCode()) return -1;
		else if (r.hashCode() > this.hashCode()) return 1;
		return 0;
	}
}

class Recipe_Component
{
	char tag;
	String name;
	int amount;
	// If components is size 0 it will take any type of material, otherwise only those in this list
	ArrayList<Component_Type> constraints = new ArrayList<Component_Type>();
	
	public Recipe_Component(char tag, String name, int amount, ArrayList<Component_Type> constraints)
	{
		this.tag = tag;
		this.name = name;
		this.amount = amount;
		this.constraints = constraints;
	}
	
	public boolean check(Component_Type type)
	{
		if (constraints.size() == 0) return true;
		
		for (Component_Type ct : constraints)
		{
			if (ct == type) return true;
		}
		
		return false;
	}
}

abstract class Recipe_Type
{
	RecipeReader reader;
	
	public Recipe_Type(RecipeReader reader)
	{
		this.reader = reader;
		read();
	}

	public int calculate(ArrayList<ArrayList<String>> eqn, float scale, HashMap<String, Component> components)
	{
		float value = 0;

		for (ArrayList<String> block : eqn)
		{
			float temp = 1;
			for (String s : block)
			{
				float temp2 = 0;
				try {
					temp2 = Float.parseFloat(s);
				}
				catch (Exception e)
				{
					Component c = components.get(s.charAt(0));
					char attribute = s.charAt(1);
					if (attribute == 'w') temp2 = c.weight;
					else if (attribute == 'h') temp2 = c.soft_hard;
					else if (attribute == 'b') temp2 = c.flexible_brittle;
					else System.err.println("Equation error! Invalid pseudonym: " + attribute);
				}

				if (temp2 != 0) temp *= temp2;
			}
			if (temp != 1) value += temp;
		}

		return (int) (value * scale);
	}

	public HashMap<Element, Integer> calculateElemental(ArrayList<ArrayList<String>> eqn, float scale, HashMap<String, Component> components)
	{
		HashMap<Element, Integer> elemental = new HashMap<Element, Integer>();

		elemental.put(Element.FIRE, calcEle(eqn, scale, Element.FIRE, components));
		elemental.put(Element.WATER, calcEle(eqn, scale, Element.WATER, components));
		elemental.put(Element.AIR, calcEle(eqn, scale, Element.AIR, components));
		elemental.put(Element.WOOD, calcEle(eqn, scale, Element.WOOD, components));
		elemental.put(Element.METAL, calcEle(eqn, scale, Element.METAL, components));
		elemental.put(Element.AETHER, calcEle(eqn, scale, Element.AETHER, components));
		elemental.put(Element.VOID, calcEle(eqn, scale, Element.VOID, components));

		return elemental;
	}

	private int calcEle(ArrayList<ArrayList<String>> eqn, float scale, Element element, HashMap<String, Component> components)
	{
		float value = 0;

		for (ArrayList<String> block : eqn)
		{
			float temp = 1;
			for (String s : block)
			{
				float temp2 = 0;
				try {
					temp2 = Float.parseFloat(s);
				}
				catch (Exception e)
				{
					Component c = components.get(s.charAt(0));
					temp2 = c.element.get(element);
				}

				if (temp2 != 0) temp *= temp2;
			}
			if (temp != 1) value += temp;
		}

		return (int) (value * scale);
	}

	protected abstract void read();
	public abstract void finalise(HashMap<String, Component> components);
}

/**
* A recipe for a weapon. It has components for the weapon style, the strength bonus,
* the Dam_type amounts, the elemental amounts and the attack speed
**/
class Recipe_Weapon extends Recipe_Type
{
	Weapon_Style style;
	HashMap<String, String> styleMeta;

	int strength;
	float strengthScale;
	ArrayList<ArrayList<String>> strengthEqn;

	int pierce;
	float pierceScale;
	ArrayList<ArrayList<String>> pierceEqn;

	int impact;
	float impactScale;
	ArrayList<ArrayList<String>> impactEqn;

	int touch;
	float touchScale;
	ArrayList<ArrayList<String>> touchEqn;

	HashMap<Element, Integer> elemental;
	float elementalScale;
	ArrayList<ArrayList<String>> elementalEqn;

	int attackSpeed;
	float attackSpeedScale;
	ArrayList<ArrayList<String>> attackSpeedEqn;

	public Recipe_Weapon(RecipeReader reader)
	{
		super(reader);
	}

	protected void read()
	{
		this.style = reader.getWeaponStyle();
		this.styleMeta = reader.getWeaponStyleMeta();

		this.strengthScale = reader.getScale(RecipeReader.STRENGTH);
		this.strengthEqn = reader.getEqn(RecipeReader.STRENGTH);

		this.pierceScale = reader.getScale(RecipeReader.PIERCE);
		this.pierceEqn = reader.getEqn(RecipeReader.PIERCE);

		this.impactScale = reader.getScale(RecipeReader.IMPACT);
		this.impactEqn = reader.getEqn(RecipeReader.IMPACT);

		this.touchScale = reader.getScale(RecipeReader.TOUCH);
		this.touchEqn = reader.getEqn(RecipeReader.TOUCH);

		this.elementalScale = reader.getScale(RecipeReader.ELEMENTAL);
		this.elementalEqn = reader.getEqn(RecipeReader.ELEMENTAL);

		this.attackSpeedScale = reader.getScale(RecipeReader.ATTACK_SPEED);
		this.attackSpeedEqn = reader.getEqn(RecipeReader.ATTACK_SPEED);
	}

	public void finalise(HashMap<String, Component> components)
	{
		this.strength = calculate(strengthEqn, strengthScale, components);
		this.pierce = calculate(pierceEqn, pierceScale, components);
		this.impact = calculate(impactEqn, impactScale, components);
		this.touch = calculate(touchEqn, touchScale, components);
		this.attackSpeed = calculate(attackSpeedEqn, attackSpeedScale, components);

		this.elemental = calculateElemental(elementalEqn, elementalScale, components);
	}
}