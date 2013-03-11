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
package com.lyeeedar.Roguelike3D.Game.Level;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.lyeeedar.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.XML.MonsterEvolver;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RoomReader;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;

public class LevelContainer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1485045667222532500L;

	public final Random ran = new Random();
	
	public final String UID;
	public final String name;
	
	public Level level = null;
	public LightManager lightManager = null;

	public final String biome;
	
	public final int depth;
	
	public final String[] up_levels;
	public final String[] down_levels;
	
	public HashMap<String, ArrayList<MonsterEvolver>> monsters = new HashMap<String, ArrayList<MonsterEvolver>>();
	
	public String skybox;

	public LevelContainer(String name, String biome, int depth, String[] up, String[] down) {
		this.biome = biome;
		this.depth = depth;
		this.name = name;
		this.up_levels = up;
		this.down_levels = down;
		
		this.UID = depth+biome+this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
	}
	
	public LightManager getLightManager()
	{
		if (lightManager == null)
		{
			lightManager = new LightManager(1, GameData.lightQuality);
		}
		
		return lightManager;
	}
	
	int loadingStage = 0;
	public Level getLevel(BiomeReader biome, RoomReader rReader)
	{
		if (loadingStage == 0)
		{
			skybox = biome.getSkybox();
			level = new Level(biome.getWidth(), biome.getHeight(), biome.getGenerator(), biome, (skybox == null), depth, up_levels.length, down_levels.length);
			loadingStage++;
		}
		else if (loadingStage == 1)
		{
			boolean done = level.fillRoom(rReader, this);
			
			if (done) loadingStage++;
		}
		else if (loadingStage == 2)
		{
			return level;
		}
		
		return null;
	}
	
	public MonsterEvolver getMonsterEvolver(String type, String UID)
	{
		ArrayList<MonsterEvolver> m = monsters.get(type);
		
		for (MonsterEvolver me : m)
		{
			if (me.UID.equals(UID)) return me;
		}
		
		return null;
	}
	
	public MonsterEvolver getMonsterEvolver(String type)
	{
		if (!monsters.containsKey(type))
		{
			ArrayList<MonsterEvolver> monsterType = new ArrayList<MonsterEvolver>();
			
			MonsterEvolver evolver = new MonsterEvolver(type, depth);
			evolver.createMap();
			evolver.Evolve_Creature();
			monsterType.add(evolver);
			
			monsters.put(type, monsterType);
			
			return evolver;
		}
		
		ArrayList<MonsterEvolver> monsterType = monsters.get(type);
		
		int pos = ran.nextInt(monsterType.size()+1);
		
		if (pos < monsterType.size())
		{
			return monsterType.get(pos);
		}
		else
		{
			MonsterEvolver evolver = new MonsterEvolver(type, depth);
			evolver.createMap();
			evolver.Evolve_Creature();
			monsterType.add(evolver);
			
			return evolver;
		}
	}
	
	int upIndex = 0;
	public String getUpLevel()
	{
		if (upIndex == up_levels.length) return null;
		
		String rs = up_levels[upIndex];
		upIndex++;
		
		return rs;
	}
	
	int downIndex = 0;
	public String getDownLevel()
	{
		if (downIndex == down_levels.length) return null;
		
		String rs = down_levels[downIndex];
		downIndex++;
		
		return rs;
	}
}
