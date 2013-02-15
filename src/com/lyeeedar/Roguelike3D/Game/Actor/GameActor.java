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
package com.lyeeedar.Roguelike3D.Game.Actor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Item.Component;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND;
import com.lyeeedar.Roguelike3D.Game.Item.Equippable;
import com.lyeeedar.Roguelike3D.Game.Item.Item;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.TextureDrawer;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;


public abstract class GameActor extends GameObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4038143255858827889L;
	public static final float WHIPLASHCD = 0.1f;
	public static final float WHIPLASHAMOUNT = 0.1f;

	public final Vector3 offsetPos = new Vector3();
	public final Vector3 offsetRot = new Vector3();

	private Random ran = new Random();

	// ----- Actor Statistics START ----- //
	
	public HashMap<Integer, Component> INVENTORY = new HashMap<Integer, Component>();
	
	public int MAX_HEALTH;
	
	public int HEALTH;
	public int WEIGHT;
	public int STRENGTH;
	
	public HashMap<Element, Integer> ELE_DEF = new HashMap<Element, Integer>();
	public HashMap<Damage_Type, Integer> DAM_DEF = new HashMap<Damage_Type, Integer>();
	
	public ArrayList<String> FACTIONS;
	public boolean IMMORTAL = false;
	public AI_Package ai;
	
	public Equipment_HAND L_HAND;
	public Equipment_HAND R_HAND;
	
	// ----- Actor Statistics END ----- //
	
	boolean alive = true;
	boolean loot = false;

	public GameActor(Colour colour, String texture, float x, float y, float z, float scale, int primitive_type, String... model)
	{
		super(colour, texture, x, y, z, scale, primitive_type, model);
		
		setupDefenses();
	}

	public void setStats(int health, int weight, int strength, HashMap<Element, Integer> ele_def, HashMap<Damage_Type, Integer> dam_def, ArrayList<String> factions)
	{
		this.MAX_HEALTH = health;
		this.HEALTH = health;
		this.WEIGHT = weight;
		this.STRENGTH = strength;
		this.ELE_DEF = ele_def;
		this.DAM_DEF = dam_def;
		this.FACTIONS = factions;
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
	}
	
	public void created()
	{
		if (L_HAND != null)
		{
			L_HAND.equip(this, 1);
		}
		if (R_HAND != null)
		{
			R_HAND.equip(this, 2);
		}
	}
	
	public void setupDefenses()
	{
		DAM_DEF = GameData.getDamageMap();

		ELE_DEF = GameData.getElementMap();
	}
	
	public void damage(int strength, 
			HashMap<Element, Integer> ele_dam,
			HashMap<Damage_Type, Integer> dam_dam)
	{
		if (!alive || IMMORTAL) return;
		
		HEALTH -= GameData.calculateDamage(strength, ele_dam, dam_dam, ELE_DEF, DAM_DEF);
		
		System.out.println("Remaining Health: "+HEALTH);

		if (HEALTH <= 0) death();

	}
	
	public void death()
	{
		System.out.println(UID+" Died!!!!");
		alive = false;
		
		this.Yrotate(90);
	}
	
	@Override
	public void update(float delta)
	{
		if (L_HAND != null) L_HAND.update(delta);
		if (R_HAND != null) R_HAND.update(delta);
		
		if (ai == null) return;
		if (!alive) return;
		ai.evaluateAI(delta);
	}
	

	@Override
	public void draw(PrototypeRendererGL20 renderer)
	{
		if (L_HAND != null) L_HAND.draw(renderer);
		if (R_HAND != null) R_HAND.draw(renderer);
	}
	@Override
	public void activate() {
		if (!alive && !loot)
		{
			for (Map.Entry<Integer, Component> entry : INVENTORY.entrySet())
			{
				if (ran.nextInt(101) < entry.getKey())
				{
					System.out.println(entry.getValue());
				}
			}
			loot = true;
			solid = false;
			visible = false;
			
			dispose();
			GameData.level.removeActor(UID);
		}
	}

	@Override
	public String getActivatePrompt() {
		
		if (!alive && !loot)
		{
			return "[E] Loot";
		}
		return "";
	}
	
	public boolean checkFaction(ArrayList<String> factions)
	{
		for (String f : factions)
		{
			for (String f2 : FACTIONS)
			{
				if (f.equalsIgnoreCase(f2)) return true;
			}
		}
		
		return false;
	}

	@Override
	public void fixReferencesSuper() {
		ai.fixReferences();
		
		for (Map.Entry<Integer, Component> entry : INVENTORY.entrySet())
		{
			entry.getValue().fixReferences();
		}
		
		if (L_HAND != null) L_HAND.fixReferences();
		if (R_HAND != null) R_HAND.fixReferences();
	}

	protected void disposed()
	{
		if (L_HAND != null) L_HAND.dispose();
		if (R_HAND != null) R_HAND.dispose();
	} 
}
