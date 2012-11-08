package com.lyeeedar.Roguelike3D.Game;

import java.util.ArrayList;

public class Tile {
	
	public char character;
	public float floor;
	public float roof;
	public float height;
	public boolean visible = false;
	public GameObject floorGo;
	public GameObject roofGo;
	public ArrayList<GameActor> actors = new ArrayList<GameActor>();
	
	public Tile (char character, float floor, float roof, float height)
	{
		this.character = character;
		this.floor = floor;
		this.roof = roof;
		this.height = height;
	}

}
