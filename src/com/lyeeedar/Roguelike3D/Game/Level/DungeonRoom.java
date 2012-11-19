package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;

public class DungeonRoom {
	
	public int width;
	public int height;
	
	public int x;
	public int y;
	
	public ArrayList<DungeonRoom> up = new ArrayList<DungeonRoom>();
	public ArrayList<DungeonRoom> down = new ArrayList<DungeonRoom>();
	public ArrayList<DungeonRoom> left = new ArrayList<DungeonRoom>();
	public ArrayList<DungeonRoom> right = new ArrayList<DungeonRoom>();
	
	public Tile[][] tiles;
	
	public DungeonRoom(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		tiles = new Tile[width][height];
		
		for (int tx = 0; tx < width; tx++)
		{
			for (int ty = 0; ty < height; ty++)
			{
				tiles[tx][ty] = new Tile('#', 0, 15, 15);
			}
		}
	}
	
	public void addConnections(DungeonRoom[][] rooms, boolean north, boolean south, boolean east, boolean west)
	{
		if (north)
		{
			up.add(rooms[x][y-1]);
			
			for (int i = 0; i < height/2; i++)
			{
				tiles[width/2][i].character = '.';
			}
		}
		if (south)
		{
			down.add(rooms[x][y+1]);
			
			for (int i = height/2; i < height; i++)
			{
				tiles[width/2][i].character = '.';
			}
		}
		if (east)
		{
			left.add(rooms[x-1][y]);
			
			for (int i = 0; i < width/2; i++)
			{
				tiles[i][height/2].character = '.';
			}
		}
		if (west)
		{
			right.add(rooms[x+1][y]);
			
			for (int i = width/2; i < width; i++)
			{
				tiles[i][height/2].character = '.';
			}
		}
	}
	
}
