/*******************************************************************************
 * Copyright (c) 2012 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Game.Actor;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND;
import com.lyeeedar.Roguelike3D.Game.Item.Equippable;
import com.lyeeedar.Roguelike3D.Game.Item.Item;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;


public abstract class GameActor extends GameObject{

	// ----- Actor Statistics START ----- //
	
	public HashMap<String, Item> INVENTORY = new HashMap<String, Item>();
	
	public Equippable HELMET;
	public Equippable SHIRT;
	public Equippable TROUSERS;
	public Equippable BOOTS;
	public Equippable GLOVES;
	
	public Equipment_HAND L_HAND;
	public Equipment_HAND R_HAND;
	
	public int HEALTH;
	public int BOOST_HEALTH;
	public HashMap<Element, Integer> ELE_DEFENSES;
	public HashMap<Element, Integer> BOOST_ELE_DEFENSES;
	
	public HashMap<Damage_Type, Integer> DEFENSES;
	public HashMap<Damage_Type, Integer> BOOST_DEFENSES;
	
	public int WEIGHT;
	public int BOOST_WEIGHT;
	
	public int STRENGTH;
	public int BOOST_STRENGTH;
	public int IQ;
	public int BOOST_IQ;
	
	public float ATTACK_SPEED;
	public float BOOST_ATTACK_SPEED;
	public float CAST_SPEED;
	public float BOOST_CAST_SPEED;
	
	public String FACTION;
	
	public boolean IMMORTAL;
	
	// ----- Actor Statistics END ----- //
	
	boolean alive = true;

	public GameActor(VisibleObject vo, float x, float y, float z, float scale) {
		super(vo, x, y, z, scale);
		
		setupDefenses();
	}
	
	public GameActor(String model, Color colour, String texture, float x, float y, float z, float scale)
	{
		super(model, colour, texture, x, y, z, scale);
		
		setupDefenses();
	}
	
	public GameActor(Mesh mesh, Color colour, String texture, float x, float y, float z, float scale)
	{
		super(mesh, colour, texture, x, y, z, scale);
		
		setupDefenses();
	}
	
	public void equipL_HAND(Equipment_HAND equip)
	{
		if (equip.two_handed)
		{
			L_HAND = equip;
			R_HAND = null;
		}
		else
		{
			L_HAND = equip;
		}
		
		calculateBoost();
	}
	
	public void equipR_HAND(Equipment_HAND equip)
	{
		if (equip.two_handed)
		{
			R_HAND = equip;
			L_HAND = null;
		}
		else
		{
			R_HAND = equip;
		}
		
		calculateBoost();
	}
	
	public void setupDefenses()
	{
		DEFENSES = new HashMap<Damage_Type, Integer>();
		
		DEFENSES.put(Damage_Type.PIERCE, 0);
		DEFENSES.put(Damage_Type.IMPACT, 0);
		DEFENSES.put(Damage_Type.TOUCH, 0);
		
		BOOST_DEFENSES = new HashMap<Damage_Type, Integer>();
		
		BOOST_DEFENSES.put(Damage_Type.PIERCE, 0);
		BOOST_DEFENSES.put(Damage_Type.IMPACT, 0);
		BOOST_DEFENSES.put(Damage_Type.TOUCH, 0);
		
		ELE_DEFENSES = new HashMap<Element, Integer>();
		
		ELE_DEFENSES.put(Element.FIRE, 0);
		ELE_DEFENSES.put(Element.AIR, 0);
		ELE_DEFENSES.put(Element.WATER, 0);
		ELE_DEFENSES.put(Element.WOOD, 0);
		ELE_DEFENSES.put(Element.METAL, 0);
		ELE_DEFENSES.put(Element.AETHER, 0);
		ELE_DEFENSES.put(Element.VOID, 0);
		
		BOOST_ELE_DEFENSES = new HashMap<Element, Integer>();
		
		BOOST_ELE_DEFENSES.put(Element.FIRE, 0);
		BOOST_ELE_DEFENSES.put(Element.AIR, 0);
		BOOST_ELE_DEFENSES.put(Element.WATER, 0);
		BOOST_ELE_DEFENSES.put(Element.WOOD, 0);
		BOOST_ELE_DEFENSES.put(Element.METAL, 0);
		BOOST_ELE_DEFENSES.put(Element.AETHER, 0);
		BOOST_ELE_DEFENSES.put(Element.VOID, 0);
	}
	
	public void calculateBoost()
	{
		BOOST_DEFENSES = new HashMap<Damage_Type, Integer>();
		
		BOOST_DEFENSES.put(Damage_Type.PIERCE, 0);
		BOOST_DEFENSES.put(Damage_Type.IMPACT, 0);
		BOOST_DEFENSES.put(Damage_Type.TOUCH, 0);
		
		BOOST_ELE_DEFENSES = new HashMap<Element, Integer>();
		
		BOOST_ELE_DEFENSES.put(Element.FIRE, 0);
		BOOST_ELE_DEFENSES.put(Element.AIR, 0);
		BOOST_ELE_DEFENSES.put(Element.WATER, 0);
		BOOST_ELE_DEFENSES.put(Element.WOOD, 0);
		BOOST_ELE_DEFENSES.put(Element.METAL, 0);
		BOOST_ELE_DEFENSES.put(Element.AETHER, 0);
		BOOST_ELE_DEFENSES.put(Element.VOID, 0);
		
		BOOST_HEALTH = 0;
		BOOST_WEIGHT = 0;
		BOOST_STRENGTH = 0;
		BOOST_IQ = 0;
		BOOST_ATTACK_SPEED = 0;
		BOOST_CAST_SPEED = 0;
		
		if (HELMET != null)
		{
			addBoost(HELMET);
		}
		
		if (SHIRT != null)
		{
			addBoost(SHIRT);
		}
		
		if (TROUSERS != null)
		{
			addBoost(TROUSERS);
		}
		
		if (BOOTS != null)
		{
			addBoost(BOOTS);
		}
		
		if (GLOVES != null)
		{
			addBoost(GLOVES);
		}
		
		if (L_HAND != null)
		{
			addBoost(L_HAND);
		}
		
		if (R_HAND != null)
		{
			addBoost(R_HAND);
		}
	}
	
	
	private void addBoost(Equippable e)
	{
		BOOST_HEALTH += e.HEALTH;
		for (Map.Entry<Element, Integer> entry : e.ELE_DEFENSES.entrySet())
		{
			int temp = BOOST_ELE_DEFENSES.get(entry.getKey());
			temp += entry.getValue();
			BOOST_ELE_DEFENSES.remove(entry.getKey());
			BOOST_ELE_DEFENSES.put(entry.getKey(), temp);
		}
		for (Map.Entry<Damage_Type, Integer> entry : e.DEFENSES.entrySet())
		{
			int temp = BOOST_DEFENSES.get(entry.getKey());
			temp += entry.getValue();
			BOOST_DEFENSES.remove(entry.getKey());
			BOOST_DEFENSES.put(entry.getKey(), temp);
		}
		
		BOOST_WEIGHT += e.WEIGHT;
		BOOST_STRENGTH += e.STRENGTH;
		BOOST_IQ += e.IQ;
		BOOST_ATTACK_SPEED += e.ATTACK_SPEED;
		BOOST_CAST_SPEED += e.CAST_SPEED;
	}
	
	@Override
	public void applyMovement()
	{
		float oldX = position.x/10;
		float oldZ = position.z/10;
		
		super.applyMovement();
		
		float newX = position.x/10;
		float newZ = position.z/10;
		
		//GameData.level.moveActor(oldX, oldZ, newX, newZ, UID);
	}
	
	public void damage(Damage_Type dam_type, Element ele_type, float amount)
	{
		if (!alive || IMMORTAL) return;
		
		int damDefense = DEFENSES.get(dam_type) + BOOST_DEFENSES.get(dam_type);
		int eleDefense = ELE_DEFENSES.get(ele_type) + BOOST_ELE_DEFENSES.get(ele_type);
		
		if (damDefense != 0) amount *= (100-damDefense)/100;
		if (eleDefense != 0) amount *= (100-eleDefense)/100;
		
		HEALTH -= amount;

		if (HEALTH+BOOST_HEALTH <= 0) death();

	}
	
	public void death()
	{
		alive = false;
	}
	
	@Override
	public void draw(Camera cam)
	{
		if (L_HAND != null) L_HAND.draw(cam);
		if (R_HAND != null) R_HAND.draw(cam);
	}
}
