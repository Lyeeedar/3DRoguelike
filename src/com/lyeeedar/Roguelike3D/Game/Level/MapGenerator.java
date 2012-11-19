package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class MapGenerator {
	
	public Tile[][] levelArray;
	ArrayList<Character> solids;
	ArrayList<Character> opaques;
	HashMap<Character, Color> colours;
	
	public MapGenerator(int width, int height, ArrayList<Character> solids, ArrayList<Character> opaques, HashMap<Character, Color> colours)
	{
		this.solids = solids;
		this.opaques = opaques;
		this.colours = colours;

		levelArray = new Tile[width][height];
		
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				levelArray[x][y] = new Tile('#', 0, 15, 15);
			}
		}
	}
	
	public Tile[][] getLevel()
	{
		return levelArray;
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
					updateTile(levelArray[x][y], 15, '#');
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
	
	