package com.lyeeedar.Roguelike3D.Game.Level;

public class AbstractTile {
	
	public enum TileType {
		FLOOR,
		WALL,
		DOOR
	}
	
	public int x;
	public int y;
	
	public int influence = 25;
	
	public TileType tileType;
	
	public boolean room = false;
	
	public AbstractTile(int x, int y, TileType tileType)
	{
		this.x = x;
		this.tileType = tileType;
		this.y = y;
	}

}
