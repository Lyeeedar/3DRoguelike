package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class Stair extends LevelObject {
	
	public String level_UID;

	public Stair(boolean visible, float x, float y, float z, AbstractObject ao, String level_UID) {
		super(visible, x, y, z, ao);
		this.level_UID = level_UID;
	}

	public Stair(VisibleObject vo, float x, float y, float z, AbstractObject ao, String level_UID) {
		super(vo, x, y, z, ao);
		this.level_UID = level_UID;
	}

	public Stair(Mesh mesh, Color colour, String texture, float x, float y,
			float z, AbstractObject ao, String level_UID) {
		super(mesh, colour, texture, x, y, z, ao);
		this.level_UID = level_UID;
	}

	public Stair(String model, Color colour, String texture, float x, float y,
			float z, AbstractObject ao, String level_UID) {
		super(model, colour, texture, x, y, z, ao);
		this.level_UID = level_UID;
	}

}
