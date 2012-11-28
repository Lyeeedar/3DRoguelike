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
