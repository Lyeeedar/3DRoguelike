/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Game.Level;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.lyeeedar.Roguelike3D.Graphics.Colour;

public class AbstractObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4933941182699148908L;

	public enum ObjectType {
		PLAYER_PLACER,
		DOOR_UNLOCKED,
		DOOR_LOCKED,
		STATIC,
		SPAWNER_0,
		SPAWNER_1,
		SPAWNER_2,
		SPAWNER_3,
		SPAWNER_4,
		SPAWNER_5,
		SPAWNER_6,
		SPAWNER_7,
		SPAWNER_8,
		SPAWNER_9,
		SPAWNER_B,
		STAIR_UP,
		STAIR_DOWN,
		FIRE_CAMP,
		FIRE_TORCH,
		TREASURE,
		ARTIFACT,
		ALTAR
	}
	
	public final String UID;
	
	public final char character;
	public final ObjectType type;
	public final boolean visible;
	public final String shortDesc;
	public final String longDesc;
	
	public String modelType;
	public String modelName;
	
	public float modelScale;
	
	public float[] modelDimensions;
	
	public String texture;

	public Colour colour;
	
	public HashMap<String, String> meta = new HashMap<String, String>();
	
	public AbstractObject(char character, String type, boolean visible, String shortDesc, String longDesc)
	{
		this.character = character;
		this.type = stringToObjectType(type);
		this.visible = visible;
		this.shortDesc = shortDesc;
		this.longDesc = longDesc;
		
		UID = character+"   "+type+System.currentTimeMillis()+toString();
	}
	
	public float x; public float y; public float z;
	public AbstractObject(char character, ObjectType type, boolean visible, String shortDesc, String longDesc, float x, float y, float z)
	{
		this.character = character;
		this.type = type;
		this.visible = visible;
		this.shortDesc = shortDesc;
		this.longDesc = longDesc;
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		UID = character+"   "+type+System.currentTimeMillis()+toString();
	}
	
	public void addMeta(String name, String contents)
	{
		meta.put(name, contents);
	}
	
	public void addMeta(String[]... data)
	{
		for (int i = 0; i < data.length; i++)
		{
			meta.put(data[i][0], data[i][1]);
		}
	}
	
	private ObjectType stringToObjectType(String type)
	{
		for (ObjectType o : ObjectType.values())
		{
			if (type.equalsIgnoreCase(""+o)) return o;
		}
		
		return null;
	}
	
	public AbstractObject(char character, ObjectType type, boolean visible, String shortDesc, String longDesc)
	{
		this.character = character;
		this.type = type;
		this.visible = visible;
		this.shortDesc = shortDesc;
		this.longDesc = longDesc;
		
		UID = character+"   "+type+System.currentTimeMillis()+toString();
	}
	
	public void setModel(String modelType, String modelName, float modelScale, String texture, Colour colour, float[] modelDimensions)
	{
		this.modelDimensions = modelDimensions;
		this.modelType = modelType;
		this.modelName = modelName;
		this.modelScale = modelScale;
		this.texture = texture;
		this.colour = colour;
	}
	
	public AbstractObject cpy()
	{
		AbstractObject ao = new AbstractObject(character, type, visible, shortDesc, longDesc);
		ao.setModel(modelType, modelName, modelScale, texture, colour, modelDimensions);
		
		for (Map.Entry<String, String> entry : meta.entrySet())
		{
			ao.addMeta(entry.getKey(), entry.getValue());
		}
		
		return ao;
	}
}
