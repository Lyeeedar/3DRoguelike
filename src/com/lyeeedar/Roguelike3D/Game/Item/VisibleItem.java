/*******************************************************************************
 * Copyright (c) 2012 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
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
		actor = GameData.level.checkEntities(getCollisionBox(), UID);
		if (actor != null) {
			System.out.println("Pickup!");
			actor.INVENTORY.put(item.NAME, item);
			GameData.level.removeItem(UID);
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
		
		//GameData.level.moveItem(oldX, oldZ, newX, newZ, UID);
	}

}
