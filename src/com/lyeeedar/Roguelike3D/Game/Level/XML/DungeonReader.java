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
package com.lyeeedar.Roguelike3D.Game.Level.XML;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Node;

import com.lyeeedar.Roguelike3D.Game.Level.LevelContainer;

public class DungeonReader extends XMLReader {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1167468362405663910L;
	
	public static final String DUNGEON = "dungeon";
	public static final String LEVEL = "level";
	public static final String NAME = "name";
	public static final String BIOME = "biome";
	public static final String DEPTH = "depth";
	public static final String UP = "up";
	public static final String DOWN = "down";

	Node dungeonNode;
	
	public DungeonReader() {
		super("data/xml/dungeon.data");
		
		dungeonNode = getNode(DUNGEON, root_node.getChildNodes());
	}

	public HashMap<String, LevelContainer> getDungeon()
	{
		HashMap<String, LevelContainer> dungeon = new HashMap<String, LevelContainer>();
		
		for (int i = 0; i < dungeonNode.getChildNodes().getLength(); i++)
		{
			Node n = dungeonNode.getChildNodes().item(i);
			
			if (!n.getNodeName().equalsIgnoreCase(LEVEL)) continue;
			
			String name = getNodeValue(NAME, n.getChildNodes());
			String biome = getNodeValue(BIOME, n.getChildNodes());
			String depthString = getNodeValue(DEPTH, n.getChildNodes());
			int depth = Integer.parseInt(depthString);
			
			Node upNode = getNode(UP, n.getChildNodes());
			ArrayList<String> upList = new ArrayList<String>();
			if (upNode != null) for (int j = 0; j < upNode.getChildNodes().getLength(); j++)
			{
				Node jn = upNode.getChildNodes().item(j);
				if (!jn.getNodeName().equalsIgnoreCase(LEVEL)) continue;
				upList.add(getNodeValue(jn));
			}
			
			Node downNode = getNode(DOWN, n.getChildNodes());
			ArrayList<String> downList = new ArrayList<String>();
			if (downNode != null) for (int j = 0; j < downNode.getChildNodes().getLength(); j++)
			{
				Node jn = downNode.getChildNodes().item(j);
				if (!jn.getNodeName().equalsIgnoreCase(LEVEL)) continue;
				downList.add(getNodeValue(jn));
			}
			
			String[] up = new String[upList.size()];
			upList.toArray(up);
			
			String[] down = new String[downList.size()];
			downList.toArray(down);
			
			LevelContainer lc = new LevelContainer(name, biome, depth, up, down);
			
			dungeon.put(name, lc);
		}
		
		return dungeon;
	}
}
