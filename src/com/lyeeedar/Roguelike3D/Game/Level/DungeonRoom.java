package com.lyeeedar.Roguelike3D.Game.Level;

public class DungeonRoom {
	
	public enum RoomType{
		START,
		END,
		MAIN,
		SPECIAL,
		OTHER
	}
	
	public final int width;
	public final int height;
	
	public final int x;
	public final int y;
	
	public final RoomType roomtype;
	
	public DungeonRoom(int x, int y, int width, int height, RoomType roomType)
	{
		this.roomtype = roomType;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
	}
}
