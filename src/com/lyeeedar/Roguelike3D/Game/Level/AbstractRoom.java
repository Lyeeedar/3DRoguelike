package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.HashMap;

public class AbstractRoom {
	
	int width;
	int height;
	
	char[][] contents;
	
	HashMap<Character, AbstractObject> objects;
	
	public AbstractRoom(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		contents = new char[width][height];
		
		objects = new HashMap<Character, AbstractObject>();
	}
	
	public void addObject(AbstractObject ao)
	{
		objects.put(ao.character, ao);
	}

	public void setRow(int column, char[] row)
	{
		contents[column] = row;
	}
}
