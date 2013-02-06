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

import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;

public class Tile implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2774877067050458423L;

	public char character;
	
	/**
	 * The floor of the tile
	 */
	public float floor;
	
	/**
	 * The roof of the tile (the very top)
	 */
	public float roof;
	
	/**
	 * The height of the tile (if this = roof then it a full height wall)
	 */
	public float height;
	public boolean visible = false;
	
	public LevelObject lo;
	
	public Tile (char character, float floor, float roof, float height)
	{
		this.character = character;
		this.floor = floor;
		this.roof = roof;
		this.height = height;
	}

}
