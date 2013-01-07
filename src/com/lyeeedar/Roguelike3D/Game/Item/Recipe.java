package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.ArrayList;
import java.util.HashMap;

import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Item.Component.Component_Type;
import com.lyeeedar.Roguelike3D.Game.Item.Item.Item_Type;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Weapon_Style;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RecipeReader;

public class Recipe
{
	// The type of the resulting item (armour, weapon etc)
	Item_Type type;
	// The components required to create the item
	HashMap<Character, Recipe_Component> components;
	// The layout of the visual recipe (will be converted by the UI into a usuable grid)
	char[][] visualGrid;
	// The actual recipe
	Recipe_Type recipe;
}

class Recipe_Component
{
	char tag;
	String name;
	int amount;
	// If components is size 0 it will take any type of material, otherwise only those in this list
	ArrayList<Component_Type> components = new ArrayList<Component_Type>();
}

abstract class Recipe_Type
{
	HashMap<Character, Component> components;
	RecipeReader reader;
	public Recipe_Type(RecipeReader reader)
	{
		components = new HashMap<Character, Component>();
		this.reader = reader;
		read();
	}

	public int calculate(ArrayList<ArrayList<String>> eqn, float scale)
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

	public HashMap<Element, Integer> calculateElemental(ArrayList<ArrayList<String>> eqn, float scale)
	{
		HashMap<Element, Integer> elemental = new HashMap<Element, Integer>();

		elemental.put(Element.FIRE, calcEle(eqn, scale, Element.FIRE));
		elemental.put(Element.WATER, calcEle(eqn, scale, Element.WATER));
		elemental.put(Element.AIR, calcEle(eqn, scale, Element.AIR));
		elemental.put(Element.WOOD, calcEle(eqn, scale, Element.WOOD));
		elemental.put(Element.METAL, calcEle(eqn, scale, Element.METAL));
		elemental.put(Element.AETHER, calcEle(eqn, scale, Element.AETHER));
		elemental.put(Element.VOID, calcEle(eqn, scale, Element.VOID));

		return elemental;
	}

	private int calcEle(ArrayList<ArrayList<String>> eqn, float scale, Element element)
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

	abstract void read();
	public abstract void finalise();
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

	void read()
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

	public void addComponent(Component component, char character)
	{
		components.remove(character);
		components.put(character, component);
	}

	public void finalise()
	{
		this.strength = calculate(strengthEqn, strengthScale);
		this.pierce = calculate(pierceEqn, pierceScale);
		this.impact = calculate(impactEqn, impactScale);
		this.touch = calculate(touchEqn, touchScale);
		this.attackSpeed = calculate(attackSpeedEqn, attackSpeedScale);

		this.elemental = calculateElemental(elementalEqn, elementalScale);
	}
}