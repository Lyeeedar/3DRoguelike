package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;
import java.util.HashMap;

public class LevelContainer {
	
	public String UID;
	
	public Level level = null;
	
	public String biome;
	
	public int depth;
	
	public ArrayList<LevelContainer> up_levels = new ArrayList<LevelContainer>();
	public ArrayList<LevelContainer> down_levels = new ArrayList<LevelContainer>();
	public ArrayList<LevelContainer> other_levels = new ArrayList<LevelContainer>();

	public LevelContainer(String biome, int depth) {
		this.biome = biome;
		this.depth = depth;
		
		this.UID = depth+biome+this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
	}
	
	public int up_index = 0;
	public void addLevel_UP(LevelContainer lc)
	{
		lc.down_levels.add(this);
		up_levels.add(lc);
	}
	
	public int down_index = 0;
	public void addLevel_DOWN(LevelContainer lc)
	{
		lc.up_levels.add(this);
		down_levels.add(lc);
	}

	public int other_index = 0;
	public void addLevel_OTHER(LevelContainer lc)
	{
		lc.other_levels.add(this);
		other_levels.add(lc);
	}
	
	public LevelContainer getUPLEVEL(String UID)
	{
		for (LevelContainer lc : up_levels)
		{
			if (lc.UID.equals(UID)) return lc;
		}
		
		return null;
	}
	
	public LevelContainer getDOWNLEVEL(String UID)
	{
		for (LevelContainer lc : down_levels)
		{
			if (lc.UID.equals(UID)) return lc;
		}
		
		return null;
	}
	
	public LevelContainer getOTHERLEVEL(String UID)
	{
		for (LevelContainer lc : other_levels)
		{
			if (lc.UID.equals(UID)) return lc;
		}
		
		return null;
	}
}
