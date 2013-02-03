package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.XML.MonsterEvolver;

public class LevelContainer {
	
	public final Random ran = new Random();
	
	public final String UID;
	public final String name;
	
	public Level level = null;
	
	public final String biome;
	
	public final int depth;
	
	public final String[] up_levels;
	public final String[] down_levels;
	public final String[] other_levels;
	
	public HashMap<String, ArrayList<MonsterEvolver>> monsters = new HashMap<String, ArrayList<MonsterEvolver>>();

	public LevelContainer(String name, String biome, int depth, String[] up, String[] down, String[] other) {
		this.biome = biome;
		this.depth = depth;
		this.name = name;
		this.up_levels = up;
		this.down_levels = down;
		this.other_levels = other;
		
		this.UID = depth+biome+this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
	}
	
	public Level getLevel(BiomeReader biome)
	{
		if (level != null) return level;
		
		if (loadingStage == 0)
		{
			message = "Planning Everything";
			level = new Level(biome.getWidth(), biome.getHeight(), biome.getGenerator(), biome, GameData.currentLevel, false);
			loadingStage++;
		}
		else if (loadingStage == 1)
		{
			message = "Filling Rooms";
			boolean done = level.fillRoom(rReader, GameData.currentLevel);
			percent += taskSteps;
			
			if (done) loadingStage++;
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
}
