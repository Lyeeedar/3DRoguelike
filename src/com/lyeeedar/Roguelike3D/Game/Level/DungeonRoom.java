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

public class DungeonRoom {
	
	public enum RoomType{
		START,
		END,
		MAIN,
		SPECIAL,
		OTHER
	}
	
	public final int width;
	public final int height;
	
	public final int x;
	public final int y;
	
	public final RoomType roomtype;
	
	public DungeonRoom(int x, int y, int width, int height, RoomType roomType)
	{
		this.roomtype = roomType;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
	}
}
