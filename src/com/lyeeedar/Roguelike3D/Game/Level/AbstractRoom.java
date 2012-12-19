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
import java.util.Random;

public class AbstractRoom {
	
	int width;
	int height;
	
	char[][] contents;
	
	boolean flip = false;
	boolean rotate = false;
	
	HashMap<Character, AbstractObject> objects;
	
	public AbstractRoom(int width, int height, boolean rotate, boolean flippable)
	{
		
		if (width == height)
		{
			if (new Random().nextInt(100) < 50) rotate = true;
		}
		
		this.rotate = rotate;
		
		if (!rotate) {
			this.width = width;
			this.height = height;
		}
		else
		{
			this.width = height;
			this.height = width;
		}
		
		contents = new char[this.width][this.height];
		
		objects = new HashMap<Character, AbstractObject>();
		
		if (flippable && new Random().nextInt(100) < 50) flip = true;
	}
	
	public void addObject(AbstractObject ao)
	{
		objects.put(ao.character, ao);
	}

	public void setRow(int column, char[] row)
	{
		if (!rotate) contents[column] = row;
		else 
		{
			for (int i = 0; i < row.length; i++)
			{
				contents[i][column] = row[i];
			}
		}
	}
	
	public void finaliseContents()
	{
		if (flip)
		{
			for (int i = 0; i < contents.length; i++)
			{
				for (int j = 0; j < contents[i].length/2; j++)
				{
					char temp = contents[i][j];
					contents[i][j] = contents[i][contents[i].length-1-j];
					contents[i][contents[i].length-1-j] = temp;
				}
			}
		}
	}
}
