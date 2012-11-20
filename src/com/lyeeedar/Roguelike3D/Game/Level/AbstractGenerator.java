package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;

/**
 * This interface models a level generator that accepts an array of tiles and returns a sorted ArrayList of DungeonRooms
 * @author Philip
 *
 */
public interface AbstractGenerator {
	
	public ArrayList<DungeonRoom> generate();

}
