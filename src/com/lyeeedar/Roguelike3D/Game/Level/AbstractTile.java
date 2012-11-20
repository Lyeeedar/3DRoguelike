package com.lyeeedar.Roguelike3D.Game.Level;

public class AbstractTile {
	
	public enum TileType {
		FLOOR,
		WALL,
		DOOR
	}
	
	int x;
	int y;
	
	int influence = 25;
	
	TileType tileType;
	
	boolean room = false;
	
	public AbstractTile(int x, int y, TileType tileType)
	{
		this.x = x;
		this.tileType = tileType;
		this.y = y;
	}

}
