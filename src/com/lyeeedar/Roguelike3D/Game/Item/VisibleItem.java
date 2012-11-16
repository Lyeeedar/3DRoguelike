package com.lyeeedar.Roguelike3D.Game.Item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;

public class VisibleItem extends GameObject {

	Item item;
	
	public VisibleItem(String model, Color colour, String texture, float x,
			float y, float z, Item item) {
		super(model, colour, texture, x, y, z);
		
		this.item = item;
	}
	
	public void update(float delta)
	{
		applyMovement();
		
		GameActor actor = null;
		
		Vector3 cpos = this.getCPosition();
		actor = GameData.level.checkEntities(cpos.x, cpos.y, cpos.z, getCollisionBox(), UID);
		if (actor != null) {
			System.out.println("Pickup!");
			actor.inventory.put(item.name, item);
			GameData.level.removeItem(cpos.x, cpos.z, UID);
		}
	}
	
	@Override
	public void applyMovement()
	{
		Vector3 p = this.getCPosition();
		
		float oldX = p.x;
		float oldZ = p.z;
		
		//System.out.println("Start   "+p);
		
		super.applyMovement();
		
		p = this.getCPosition();
		
		float newX = p.x;
		float newZ = p.z;
		
		//System.out.println("End    "+p);
		
		GameData.level.moveItem(oldX, oldZ, newX, newZ, UID);
	}

}
