package com.lyeeedar.Roguelike3D.Game;
import java.util.HashMap;

import com.lyeeedar.Roguelike3D.Graphics.VisibleObject;


public abstract class GameActor extends GameObject{

	public GameActor(VisibleObject vo, float x, float y, float z) {
		super(vo, x, y, z);
	}

	float health;
	HashMap<String, Integer> defenses;
	int speed;
	
	
	public abstract void update(float delta);
}
