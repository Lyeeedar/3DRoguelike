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
package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject.ObjectType;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractTile.TileType;

public class MapGenerator {
	
	public enum GeneratorType {
		SERK,
		STATIC
	}

	private Tile[][] levelArray;
	ArrayList<Character> solids;
	ArrayList<Character> opaques;
	HashMap<Character, Color> colours;
	ArrayList<DungeonRoom> rooms;
	ArrayList<AbstractObject> objects;
	
	int ceiling;

	public MapGenerator(int width, int height, ArrayList<Character> solids, ArrayList<Character> opaques, HashMap<Character, Color> colours, GeneratorType gtype, BiomeReader biome)
	{
		this.ceiling = biome.getRoof();
		
		this.solids = solids;
		this.opaques = opaques;
		this.colours = colours;
		
		objects = new ArrayList<AbstractObject>();

		AbstractTile[][] tiles = new AbstractTile[width][height];
		levelArray = new Tile[width][height];
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				tiles[x][y] = new AbstractTile(x, y, TileType.WALL);
				levelArray[x][y] = new Tile('#', 0, ceiling, ceiling);
			}
		}
		
		AbstractGenerator generator = getGenerator(gtype, tiles, biome);
		rooms = generator.generate(biome);
		
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				if (tiles[x][y].tileType == TileType.WALL)
				{
					
				}
				else if (tiles[x][y].tileType == TileType.DOOR)
				{
					levelArray[x][y] = new Tile('.', 0, ceiling, 0);
					objects.add(new AbstractObject('+', ObjectType.DOOR_UNLOCKED, true, biome.getDescription('+'), (float)x, 0.0f, (float)y));
				}
				else
				{
					levelArray[x][y] = new Tile('.', 0, ceiling, 0);
				}
			}
		}
		
		clearWalls();
	}
	
	public ArrayList<AbstractObject> getObjects()
	{
		return objects;
	}
	
	private AbstractGenerator getGenerator(GeneratorType gtype, AbstractTile[][] tiles, BiomeReader biome)
	{
		if (gtype == GeneratorType.SERK)
		{
			return new SerkGenerator(tiles, biome);
		}
		else if (gtype == GeneratorType.STATIC)
		{
			return new StaticGenerator(tiles, biome);
		}
		return null;
	}

	public Tile[][] getLevel()
	{
		return levelArray;
	}
	
	public ArrayList<DungeonRoom> getRooms()
	{
		return rooms;
	}

	public void updateTile(Tile t, int height, char c)
	{
		t.height = height;
		t.character = c;
	}

	public void clearWalls()
	{
		for (int x = 0; x < levelArray.length; x++)
		{
			for (int y = 0; y < levelArray[0].length; y++)
			{
				if (x == 0 || x == levelArray.length-1
						|| y == 0 || y == levelArray[0].length-1)
				{
					Tile t = levelArray[x][y];
					updateTile(levelArray[x][y], ceiling, '#');
				}

				if (chWl(x-1, y) && chWl(x, y-1)
						&& chWl(x-1, y-1) && chWl(x-1, y+1)
						&& chWl(x+1, y-1) && chWl(x+1, y+1)
						&& chWl(x+1, y) && chWl(x, y+1))
				{
					levelArray[x][y].character = ' ';
				}
			}
		}
	}

	private boolean chWl(int x, int y)
	{
		if (x < 0 || x > levelArray.length-1 ||
				y < 0 || y > levelArray[0].length-1)
			return true;

		if (levelArray[x][y].character == '#' ||
				levelArray[x][y].character == ' ')
			return true;
		else
			return false;
	}
}
