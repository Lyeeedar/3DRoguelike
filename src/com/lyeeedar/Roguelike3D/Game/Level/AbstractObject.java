package com.lyeeedar.Roguelike3D.Game.Level;

import com.badlogic.gdx.graphics.Color;

public class AbstractObject {
	
	public enum ObjectType {
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
		STAIR_UP,
		STAIR_DOWN
	}
	
	char character;
	ObjectType type;
	boolean visible;
	String description;
	
	String modelType;
	String modelName;
	
	float[] modelDimensions;
	
	String texture;

	Color colour;
	
	public AbstractObject(char character, String type, boolean visible, String description)
	{
		this.character = character;
		this.type = stringToObjectType(type);
		this.visible = visible;
		this.description = description;
	}
	
	private ObjectType stringToObjectType(String type)
	{
		ObjectType otype = null;
		
		if (type.equalsIgnoreCase("STATIC"))
		{
			otype = ObjectType.STATIC;
		}
		else if (type.equalsIgnoreCase("SPAWNER_0"))
		{
			otype = ObjectType.SPAWNER_0;
		}
		else if (type.equalsIgnoreCase("SPAWNER_1"))
		{
			otype = ObjectType.SPAWNER_1;
		}
		else if (type.equalsIgnoreCase("SPAWNER_2"))
		{
			otype = ObjectType.SPAWNER_2;
		}
		else if (type.equalsIgnoreCase("SPAWNER_3"))
		{
			otype = ObjectType.SPAWNER_3;
		}
		else if (type.equalsIgnoreCase("SPAWNER_4"))
		{
			otype = ObjectType.SPAWNER_4;
		}
		else if (type.equalsIgnoreCase("SPAWNER_5"))
		{
			otype = ObjectType.SPAWNER_5;
		}
		else if (type.equalsIgnoreCase("SPAWNER_6"))
		{
			otype = ObjectType.SPAWNER_6;
		}
		else if (type.equalsIgnoreCase("SPAWNER_7"))
		{
			otype = ObjectType.SPAWNER_7;
		}
		else if (type.equalsIgnoreCase("SPAWNER_8"))
		{
			otype = ObjectType.SPAWNER_8;
		}
		else if (type.equalsIgnoreCase("SPAWNER_9"))
		{
			otype = ObjectType.SPAWNER_9;
		}
		else if (type.equalsIgnoreCase("STAIR_UP"))
		{
			otype = ObjectType.STAIR_UP;
		}
		else if (type.equalsIgnoreCase("STAIR_DOWN"))
		{
			otype = ObjectType.STAIR_DOWN;
		}
		
		return otype;
	}
	
	public AbstractObject(char character, ObjectType type, boolean visible, String description)
	{
		this.character = character;
		this.type = type;
		this.visible = visible;
		this.description = description;
	}
	
	public void setModel(String modelType, String modelName, String texture, Color colour, float[] modelDimensions)
	{
		this.modelDimensions = modelDimensions;
		this.modelType = modelType;
		this.modelName = modelName;
		this.texture = texture;
		this.colour = colour;
	}
	
	public void apply(Level level)
	{
		
	}
}
