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

import java.util.ArrayList;
import java.util.Random;

import com.lyeeedar.Roguelike3D.Game.Level.AbstractTile.TileType;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom.RoomType;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Utils.Bag;

public class StaticGenerator implements AbstractGenerator {
	
	final AbstractTile[][] tiles;
	final Random ran = new Random();
	
	final int width;
	final int height;

	public StaticGenerator(AbstractTile[][] tiles, BiomeReader biome) {
		this.tiles = tiles;
		this.width = tiles.length;
		this.height = tiles[0].length;
	}

	@Override
	public Bag<DungeonRoom> generate(BiomeReader biome) {
		
		for (int x = 1; x < width-1;x++)
		{
			for (int y = 1; y < height-1; y++)
			{
				tiles[x][y].room = true;
				tiles[x][y].tileType = TileType.FLOOR;
			}
		}

		Bag<DungeonRoom> rooms = new Bag<DungeonRoom>();
		DungeonRoom room = new DungeonRoom(1, 1, width-2, height-2, RoomType.MAIN);
		rooms.add(room);
		
		return rooms;
	}

}
