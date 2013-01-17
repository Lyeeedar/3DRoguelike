package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class PlayerPlacer extends LevelObject {

	public PlayerPlacer(boolean visible, float x, float y, float z,
			AbstractObject ao) {
		super(visible, x, y, z, ao);
	}

	@Override
	public void activate() {
	}

	@Override
	public String getActivatePrompt() {
		return "";
	}

}
