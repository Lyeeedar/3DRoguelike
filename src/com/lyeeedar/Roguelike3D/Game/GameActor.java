package com.lyeeedar.Roguelike3D.Game;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Graphics.VisibleObject;


public abstract class GameActor extends GameObject{
	
	HashMap<String, Item> inventory = new HashMap<String, Item>();

	public GameActor(VisibleObject vo, float x, float y, float z) {
		super(vo, x, y, z);
	}
	
	public GameActor(String model, Vector3 colour, String texture, float x, float y, float z)
	{
		super(model, colour, texture, x, y, z);
	}

	float health;
	HashMap<String, Integer> defenses;
	int speed;
	
	
	public abstract void update(float delta);
}
