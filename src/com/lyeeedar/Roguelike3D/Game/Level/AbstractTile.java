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

public class AbstractTile {
	
	public enum TileType {
		FLOOR,
		WALL,
		DOOR
	}
	
	public int x;
	public int y;
	
	public int influence = 25;
	
	public TileType tileType;
	
	public boolean room = false;
	
	public AbstractTile(int x, int y, TileType tileType)
	{
		this.x = x;
		this.tileType = tileType;
		this.y = y;
	}

}
