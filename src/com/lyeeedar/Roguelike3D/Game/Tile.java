package com.lyeeedar.Roguelike3D.Game;

public class Tile {
	
	public char character;
	public float floor;
	public float roof;
	public float height;
	public boolean visible = false;
	
	public Tile (char character, float floor, float roof, float height)
	{
		this.character = character;
		this.floor = floor;
		this.roof = roof;
		this.height = height;
	}

}
