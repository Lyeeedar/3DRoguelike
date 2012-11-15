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
	
	@Override
	public void applyMovement()
	{
		float oldX = position.x/10;
		float oldZ = position.z/10;
		
		super.applyMovement();
		
		float newX = position.x/10;
		float newZ = position.z/10;
		
		GameData.currentLevel.moveActor(oldX, oldZ, newX, newZ, UID);
	}

	float health;
	HashMap<String, Integer> defenses;
	int speed;
}
