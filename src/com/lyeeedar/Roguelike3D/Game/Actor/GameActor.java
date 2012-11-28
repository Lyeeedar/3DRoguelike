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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.GameData.Elements;
import com.lyeeedar.Roguelike3D.Game.Item.Item;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;


public abstract class GameActor extends GameObject{
	
	public HashMap<String, Item> inventory = new HashMap<String, Item>();
	
	String faction;
	String name;
	
	int health;
	
	HashMap<Elements, Integer> defenses;
	int speed;
	
	boolean alive = true;

	public GameActor(VisibleObject vo, float x, float y, float z) {
		super(vo, x, y, z);
		
		setupDefenses();
	}
	
	public GameActor(String model, Color colour, String texture, float x, float y, float z)
	{
		super(model, colour, texture, x, y, z);
		
		setupDefenses();
	}
	
	public GameActor(Mesh mesh, Color colour, String texture, float x, float y, float z)
	{
		super(mesh, colour, texture, x, y, z);
		
		setupDefenses();
	}
	
	public void setupDefenses()
	{
		defenses = new HashMap<Elements, Integer>();
		
		defenses.put(Elements.FIRE, 0);
		defenses.put(Elements.AIR, 0);
		defenses.put(Elements.WATER, 0);
		defenses.put(Elements.PHYSICAL, 0);
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
	
	public void damage(Elements type, float amount)
	{
		if (!alive) return;
		
		int eleDefense = defenses.get(type);
		
		if (eleDefense != 0) amount *= (100-eleDefense)/100;
		
		health -= amount;

		if (health <= 0) death();

	}
	
	public void death()
	{
		alive = false;
	}
}
