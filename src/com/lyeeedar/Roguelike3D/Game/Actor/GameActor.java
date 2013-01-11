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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND;
import com.lyeeedar.Roguelike3D.Game.Item.Equippable;
import com.lyeeedar.Roguelike3D.Game.Item.Item;
import com.lyeeedar.Roguelike3D.Graphics.TextureDrawer;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;


public class GameActor extends GameObject{
	
	public static final float WHIPLASHCD = 0.1f;
	public static final float WHIPLASHAMOUNT = 0.1f;

	public final Vector3 offsetPos = new Vector3();
	public final Vector3 offsetRot = new Vector3();
	

	// ----- Actor Statistics START ----- //
	
	public HashMap<String, Item> INVENTORY = new HashMap<String, Item>();
	
	public int MAX_HEALTH;
	
	public int HEALTH;
	public int WEIGHT;
	public int STRENGTH;
	
	public HashMap<Element, Integer> ELE_DEF = new HashMap<Element, Integer>();
	public HashMap<Damage_Type, Integer> DAM_DEF = new HashMap<Damage_Type, Integer>();
	
	public String FACTION;
	public boolean IMMORTAL = false;
	public AI_Package ai;
	
	public Equipment_HAND L_HAND;
	public Equipment_HAND R_HAND;
	
	// ----- Actor Statistics END ----- //
	
	boolean alive = true;

	public ArrayList<Decal> textures = new ArrayList<Decal>();
	
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
	
	public void setStats(int health, int weight, int strength, HashMap<Element, Integer> ele_def, HashMap<Damage_Type, Integer> dam_def, String faction)
	{
		this.MAX_HEALTH = health;
		this.HEALTH = health;
		this.WEIGHT = weight;
		this.STRENGTH = strength;
		this.ELE_DEF = ele_def;
		this.DAM_DEF = dam_def;
		this.FACTION = faction;
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
	
	public void setupDefenses()
	{
		DAM_DEF = new HashMap<Damage_Type, Integer>();
		
		DAM_DEF.put(Damage_Type.PIERCE, 0);
		DAM_DEF.put(Damage_Type.IMPACT, 0);
		DAM_DEF.put(Damage_Type.TOUCH, 0);

		ELE_DEF = new HashMap<Element, Integer>();
		
		ELE_DEF.put(Element.FIRE, 0);
		ELE_DEF.put(Element.AIR, 0);
		ELE_DEF.put(Element.WATER, 0);
		ELE_DEF.put(Element.WOOD, 0);
		ELE_DEF.put(Element.METAL, 0);
		ELE_DEF.put(Element.AETHER, 0);
		ELE_DEF.put(Element.VOID, 0);
	}
	
	public void damage(int strength, 
			HashMap<Element, Integer> ele_dam,
			HashMap<Damage_Type, Integer> dam_dam)
	{
		if (!alive || IMMORTAL) return;
		
		HEALTH -= GameData.calculateDamage(strength, ele_dam, dam_dam, ELE_DEF, DAM_DEF);

		if (HEALTH <= 0) death();

	}
	
	public void death()
	{
		System.out.println(UID+" Died!!!!");
		alive = false;
		
		Matrix4 hinge = new Matrix4();
		hinge.setToRotation(0, 1, 1, 90);
		//hinge.mul(new Matrix4().setToTranslation(0, -1, 0));
		vo.attributes.getRotation().mul(hinge);
	}
	
	@Override
	public void update(float delta)
	{
		if (ai == null) return;
		ai.evaluateAI(delta);
	}
	

	@Override
	public void draw(Camera cam)
	{
		if (L_HAND != null) L_HAND.draw(cam);
		if (R_HAND != null) R_HAND.draw(cam);
	}
	@Override
	public void activate() {
	}

}
