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
package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData.Elements;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject.ObjectType;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractRoom;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;

public abstract class LevelObject extends GameObject{

	public final AbstractObject ao;
	
	public LevelObject(boolean visible, float x, float y, float z, AbstractObject ao)
	{
		super(Shapes.genCuboid(0.1f, 0.1f, 0.1f), Color.WHITE, "blank", x, y, z);
		this.visible = visible;
		this.ao = ao;
	}

	public LevelObject(VisibleObject vo, float x, float y, float z, AbstractObject ao) {
		super(vo, x, y, z);
		this.ao = ao;
	}
	
	public LevelObject(Mesh mesh, Color colour, String texture, float x, float y, float z, AbstractObject ao) {
		super(mesh, colour, texture, x, y, z);
		this.ao = ao;
	}

	public LevelObject(String model, Color colour, String texture, float x,	float y, float z, AbstractObject ao) {
		super(model, colour, texture, x, y, z);
		this.ao = ao;
	}
	
	public static LevelObject checkObject(AbstractObject ao, float x, float y, float z, Level level)
	{
		LevelObject lo = null;
		if (ao.type == ObjectType.DOOR_UNLOCKED)
		{
			lo = Door.create(ao, level, x, y, z);
		}
		else if (ao.type == ObjectType.FIRE_CAMP)
		{
			ParticleEmitter p = new ParticleEmitter(x-2.5f, y-2, z-2.5f, 5, 5, 5, 0.75f, 100);
			p.setDecal("data/textures/texf.png", new Vector3(0.0f, 2.0f, 0.0f), 2, Color.YELLOW, Color.RED, 1, 1, true);

			lo = new EmitterObject(Shapes.genCuboid(5, 5, 5), x, y, z, ao, p, false);
			lo.description = ao.description;
		}
		else if (ao.type == ObjectType.FIRE_TORCH)
		{
			ParticleEmitter p = new ParticleEmitter(x-0.3f, y+1.5f, z-0.3f, 1, 1, 1, 0.75f, 10);
			p.setDecal("data/textures/texf.png", new Vector3(0.0f, 2.0f, 0.0f), 0.5f, Color.YELLOW, Color.RED, 1, 1, true);

			lo = new EmitterObject(Shapes.genCuboid(0.5f, 3, 0.5f), new Color(0.8f, 0.6f, 0.4f, 1.0f), "texw", x, y, z, ao, p, true);
			lo.description = ao.description;
		}
		
		return lo;
	}
}
