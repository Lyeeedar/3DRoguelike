package com.lyeeedar.Roguelike3D.Game.Actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class LevelObject extends GameObject{

	public LevelObject(VisibleObject vo, float x, float y, float z) {
		super(vo, x, y, z);
	}
	
	public LevelObject(Mesh mesh, Color colour, String texture, float x,
			float y, float z) {
		super(mesh, colour, texture, x, y, z);
	}
	
	public LevelObject(String model, Color colour, String texture, float x,
			float y, float z) {
		super(model, colour, texture, x, y, z);
	}


}
