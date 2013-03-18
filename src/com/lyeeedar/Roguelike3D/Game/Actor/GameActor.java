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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.lyeeedar.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND;
import com.lyeeedar.Roguelike3D.Game.Item.Item;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer;


public abstract class GameActor extends GameObject{
	
	private static final long serialVersionUID = -4038143255858827889L;

	public HashMap<Integer, Item> INVENTORY = new HashMap<Integer, Item>();
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
	
	boolean alive = true;
	boolean looted = false;

	public GameActor(Color colour, String texture, float x, float y, float z, float scale, int primitive_type, String... model)
	{
		super(colour, texture, x, y, z, scale, primitive_type, model);
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
	
	@Override
	public void getLight(LightManager lightManager)
	{
		if (particleEffect != null) particleEffect.getLight(lightManager);
		if (L_HAND != null) L_HAND.model.getLight(lightManager);
		if (R_HAND != null) R_HAND.model.getLight(lightManager);
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
	
	protected void created()
	{
		if (L_HAND != null)
		{
			L_HAND.create();
			L_HAND.equip(this, 1);
		}
		if (R_HAND != null)
		{
			R_HAND.create();
			R_HAND.equip(this, 2);
		}
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
	public void update(float delta, Camera cam)
	{
		if (particleEffect != null)
		{
			particleEffect.update(delta, cam);
		}
		
		if (ai == null) return;
		if (!alive) return;
		ai.evaluateAI(delta);
		
		if (L_HAND != null) L_HAND.update(delta, cam);
		if (R_HAND != null) R_HAND.update(delta, cam);
		
	}
	
	@Override
	protected void rendered(Renderer renderer, ArrayList<ParticleEmitter> emitters, Camera cam)
	{
		if (L_HAND != null) L_HAND.render(renderer, emitters, cam);
		if (R_HAND != null) R_HAND.render(renderer, emitters, cam);
	}
	
	@Override
	public void activate() {
		if (!alive && !looted)
		{
			for (Map.Entry<Integer, Item> entry : INVENTORY.entrySet())
			{
				if (ran.nextInt(101) < entry.getKey())
				{
					System.out.println(entry.getValue());
				}
			}
			looted = true;
			solid = false;
			visible = false;
			
			dispose();
			GameData.level.removeGameActor(this);
		}
	}

	@Override
	public String getActivatePrompt() {
		
		if (!alive && !looted)
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
	public void fixReferences() {
		ai.fixReferences(this);
		
		if (L_HAND != null) L_HAND.fixReferences(this);
		if (R_HAND != null) R_HAND.fixReferences(this);
	}

	@Override
	protected void disposed()
	{
		if (L_HAND != null) L_HAND.dispose();
		if (R_HAND != null) R_HAND.dispose();
	}
	
	@Override
	public void changeTile(Tile src, Tile dst)
	{
		src.removeGameActor(UID);
		dst.actors.add(this);
	}
}
