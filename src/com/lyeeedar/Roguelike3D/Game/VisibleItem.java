package com.lyeeedar.Roguelike3D.Game;

import com.badlogic.gdx.math.Vector3;

public class VisibleItem extends GameObject {

	Item item;
	
	public VisibleItem(String model, Vector3 colour, String texture, float x,
			float y, float z, Item item) {
		super(model, colour, texture, x, y, z);
		
		this.item = item;
	}
	
	public void update(float delta)
	{
		applyMovement();
		
		GameActor actor = null;
		
		Vector3 cpos = this.getCPosition();
		actor = GameData.currentLevel.checkEntities(cpos.x, cpos.y, cpos.z, getCollisionBox(), UID);
		if (actor != null) {
			actor.inventory.put(item.name, item);
			GameData.currentLevel.removeItem(cpos.x, cpos.z, UID);
		}
	}

}
