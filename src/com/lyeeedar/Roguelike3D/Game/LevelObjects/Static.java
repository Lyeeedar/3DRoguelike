package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class Static extends LevelObject {

	public Static(boolean visible, float x, float y, float z, AbstractObject ao) {
		super(visible, x, y, z, ao);
	}

	public Static(VisibleObject vo, float x, float y, float z, AbstractObject ao) {
		super(vo, x, y, z, ao);
	}

	public Static(Mesh mesh, Color colour, String texture, float x, float y,
			float z, AbstractObject ao) {
		super(mesh, colour, texture, x, y, z, ao);
	}

	public Static(String model, Color colour, String texture, float x, float y,
			float z, AbstractObject ao) {
		super(model, colour, texture, x, y, z, ao);
	}

}
