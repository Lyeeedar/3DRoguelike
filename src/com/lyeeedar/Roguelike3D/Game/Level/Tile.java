package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Item.VisibleItem;

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
