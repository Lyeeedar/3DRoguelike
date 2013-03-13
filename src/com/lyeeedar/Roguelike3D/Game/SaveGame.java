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
package com.lyeeedar.Roguelike3D.Game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Item.Component;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_BODY;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_BOOTS;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HEAD;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_LEGS;
import com.lyeeedar.Roguelike3D.Game.Item.Equippable;
import com.lyeeedar.Roguelike3D.Game.Item.Item.Item_Type;
import com.lyeeedar.Roguelike3D.Game.Item.Recipe;
import com.lyeeedar.Roguelike3D.Game.Level.LevelContainer;

public class SaveGame implements Serializable {

	public static final String SAVE_FILE = "game.sav";
	/**
	 * 
	 */
	private static final long serialVersionUID = -1149905866207926970L;
	
	public HashMap<String, LevelContainer> dungeon;
	public String currentLevel;
	
	
	public int MAX_HEALTH;
	public int HEALTH;
	public int WEIGHT;
	public int STRENGTH;
	public HashMap<Element, Integer> ELE_DEF;
	public HashMap<Damage_Type, Integer> DAM_DEF;
	public ArrayList<String> FACTIONS;
	
	public Equipment_HEAD head;
	public Equipment_BODY body;
	public Equipment_LEGS legs;
	public Equipment_BOOTS boots;
	public Equipment_HAND l_hand;
	public Equipment_HAND r_hand;
	
//	public TreeMultimap<Integer, Recipe> recipes;
//	public TreeMultimap<Integer, Component> components;
//	public TreeMultimap<Item_Type, Equippable> carryEquipment = TreeMultimap.create();
//	public TreeMultimap<Item_Type, Equippable> baseEquipment = TreeMultimap.create();
//	

	public SaveGame() {
	}

	public void setDungeon(HashMap<String, LevelContainer> dungeon, String currentLevel)
	{
		this.dungeon = dungeon;
		this.currentLevel = currentLevel;
	}
	
	public void setStats(
			int MAX_HEALTH,
			int HEALTH,
			int WEIGHT,
			int STRENGTH,
			HashMap<Element, Integer> ELE_DEF,
			HashMap<Damage_Type, Integer> DAM_DEF,
			ArrayList<String> FACTIONS,
			Equipment_HEAD head,
			Equipment_BODY body,
			Equipment_LEGS legs,
			Equipment_BOOTS boots,
			Equipment_HAND l_hand,
			Equipment_HAND r_hand
//			TreeMultimap<Integer, Recipe> recipes,
//			TreeMultimap<Integer, Component> components,
//			TreeMultimap<Item_Type, Equippable> carryEquipment,
//			TreeMultimap<Item_Type, Equippable> baseEquipment
			)
	{
		this.MAX_HEALTH = MAX_HEALTH;
		this.HEALTH = HEALTH;
		this.WEIGHT = WEIGHT;
		this.STRENGTH = STRENGTH;
		this.ELE_DEF = ELE_DEF;
		this.DAM_DEF = DAM_DEF;
		this.FACTIONS = FACTIONS;
		this.head = head;
		this.body = body;
		this.legs = legs;
		this.boots = boots;
		this.l_hand = l_hand;
		this.r_hand = r_hand;
//		this.recipes = recipes;
//		this.components = components;
//		this.carryEquipment = carryEquipment;
//		this.baseEquipment = baseEquipment;
	}
	
	public static void save(SaveGame save)
	{
		FileHandle file = Gdx.files.local(SAVE_FILE);
		
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(file.write(false));
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			out.writeObject(save);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static SaveGame load()
	{
		FileHandle file = Gdx.files.local(SAVE_FILE);
		
		SaveGame save = null;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(file.read());
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		try {
			save = (SaveGame) in.readObject();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return save;
	}
}
