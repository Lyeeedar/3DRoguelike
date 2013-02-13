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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Item.Component.Component_Type;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND.WeaponType;
import com.lyeeedar.Roguelike3D.Game.Item.Item.Item_Type;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Melee_Weapon_Style;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RecipeReader;

public class Recipe implements Comparable<Recipe>, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9091235160551539915L;
	// The recipe rarity
	public int rarity;
	// The type of the resulting item (armour, weapon etc)
	public Item_Type type;
	// The components required to create the item
	public HashMap<Character, Recipe_Component> components;
	// The layout of the visual recipe (will be converted by the UI into a usuable grid)
	public char[][] visualGrid;
	// The actual recipe
	private Recipe_Type recipe;
	
	public String recipeName;
	
	public Recipe(RecipeReader reader)
	{
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
		
		if (reader.getItemType() == Item_Type.WEAPON)
		{
			this.recipe = new Recipe_Melee_Weapon(reader);
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
	
	public Table getComponentDescription(char ref, Skin skin)
	{
		return components.get(ref).getUIDescription(skin);
	}
	
	public int getComponentAmount(char ref)
	{
		return components.get(ref).amount;
	}
	
	public Item craft(HashMap<Character, Component> components)
	{
		return recipe.finalise(components, this);
	}
}

class Recipe_Component implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3513926494850238989L;
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
	
	public Table getUIDescription(Skin skin)
	{
		Table t = new Table();
		
		t.add(new Label("Recipe Ingredient", skin));
		t.row();
		t.add(new Label("Name: ", skin));
		t.add(new Label(name, skin));
		t.row();
		t.add(new Label("Amount: ", skin));
		t.add(new Label(""+amount, skin));
		t.row();
		t.add(new Label("Allowed Types: ", skin));
		
		Table types = new Table();
		
		if (constraints.size() == 0) types.add(new Label("Any", skin));
		else {
			for (Component_Type ct : constraints)
			{
				types.add(new Label(""+ct, skin));
				types.row();
			}
		}
		
		t.add(types);
		
		return t;
	}
}

abstract class Recipe_Type implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5871516507153188509L;
	public Recipe_Type(RecipeReader reader)
	{
		read(reader);
	}

	public int calculate(ArrayList<ArrayList<String>> eqn, float scale, HashMap<Character, Component> components, Recipe recipe)
	{
		System.out.println("start calculating");
		System.out.println("eqn size = "+eqn.size());
		
		float value = 0;

		for (ArrayList<String> block : eqn)
		{
			float temp = 1;
			for (String s : block)
			{
				System.out.println(s);
				float temp2 = 0;
				try {
					temp2 = Float.parseFloat(s);
				}
				catch (Exception e)
				{
					Component c = components.get(s.charAt(0));
					char attribute = s.charAt(1);
					if (attribute == 'w') temp2 = c.weight_per_amount * recipe.getComponentAmount(s.charAt(0));
					else if (attribute == 'h') temp2 = c.soft_hard;
					else if (attribute == 'b') temp2 = c.flexible_brittle;
					else System.err.println("Equation error! Invalid pseudonym: " + attribute);
				}

				if (temp2 != 0) temp *= temp2;
				
				System.out.println("temp2="+temp2);
			}
			if (temp != 1) value += temp;
			System.out.println("temp="+temp);
		}

		if (value < 0) return 0;
		return (int) (value * scale);
	}

	public HashMap<Element, Integer> calculateElemental(ArrayList<ArrayList<String>> eqn, float scale, HashMap<Character, Component> components)
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

	private int calcEle(ArrayList<ArrayList<String>> eqn, float scale, Element element, HashMap<Character, Component> components)
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

		if (value < 0) return 0;
		return (int) (value * scale);
	}

	protected abstract void read(RecipeReader reader);
	public abstract Item finalise(HashMap<Character, Component> components, Recipe recipe);
}

/**
* A recipe for a weapon. It has components for the weapon style, the strength bonus,
* the Dam_type amounts, the elemental amounts and the attack speed
**/
class Recipe_Melee_Weapon extends Recipe_Type
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4282943162832212160L;
	Melee_Weapon_Style style;
	HashMap<String, String> styleMeta;
	
	String visualType;

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

	float attackSpeed;
	float attackSpeedScale;
	ArrayList<ArrayList<String>> attackSpeedEqn;
	
	float weight;

	public Recipe_Melee_Weapon(RecipeReader reader)
	{
		super(reader);
	}

	protected void read(RecipeReader reader)
	{
		this.style = reader.getWeaponStyle();
		this.styleMeta = reader.getWeaponStyleMeta();
		
		this.visualType = reader.getVisualType();

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

	public Item finalise(HashMap<Character, Component> components, Recipe recipe)
	{
		System.out.println("Crafting item");
		
		this.strength = calculate(strengthEqn, strengthScale, components, recipe);
		this.pierce = calculate(pierceEqn, pierceScale, components, recipe);
		this.impact = calculate(impactEqn, impactScale, components, recipe);
		this.touch = calculate(touchEqn, touchScale, components, recipe);
		this.attackSpeed = calculate(attackSpeedEqn, attackSpeedScale, components, recipe);

		this.elemental = calculateElemental(elementalEqn, elementalScale, components);
		
		HashMap<Damage_Type, Integer> damage = new HashMap<Damage_Type, Integer>();
		damage.put(Damage_Type.PIERCE, pierce);
		damage.put(Damage_Type.IMPACT, impact);
		damage.put(Damage_Type.TOUCH, touch);
		
		float weight = 0;
		for (Map.Entry<Character, Component> entry : components.entrySet())
		{
			weight += recipe.getComponentAmount(entry.getKey()) * entry.getValue().weight_per_amount;
		}
		
		return Equipment_HAND.getWeapon(WeaponType.MELEE, visualType, ""+style, strength, elemental, damage, attackSpeed, weight, false, 2);
	}
}
