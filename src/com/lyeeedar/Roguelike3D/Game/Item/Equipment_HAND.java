package com.lyeeedar.Roguelike3D.Game.Item;

import com.badlogic.gdx.graphics.Camera;

public abstract class Equipment_HAND extends Equippable{

	public boolean two_handed = false;
	
	public Equipment_HAND() {
	}

	public abstract void draw(Camera cam);
	public abstract void update(float delta);
}
