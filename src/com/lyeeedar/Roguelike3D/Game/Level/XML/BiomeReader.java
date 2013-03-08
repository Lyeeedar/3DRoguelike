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

import org.w3c.dom.Node;

import com.badlogic.gdx.graphics.Color;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom.RoomType;
import com.lyeeedar.Roguelike3D.Game.Level.MapGenerator.GeneratorType;

public class BiomeReader extends XMLReader {
	
	public static final String BIOMES = "biomes";
	
	public static final String NAME = "name";
	public static final String GENERATOR = "generator";
	public static final String DESCRIPTION = "description";
	public static final String SHORT_DESCRIPTION = "short_description";
	public static final String LONG_DESCRIPTION = "long_description";
	public static final String CHAR = "char";
	public static final String HEIGHT = "height";
	public static final String WIDTH = "width";
	public static final String ROOF = "roof";
	public static final String WALL = "wall";
	public static final String FLOOR = "floor";
	public static final String COLOUR_RED = "red";
	public static final String COLOUR_GREEN = "green";
	public static final String COLOUR_BLUE = "blue";
	public static final String TEXTURE = "texture";
	public static final String AMBIENT = "ambient_light";
	public static final String ROOM_SETTINGS = "room_settings";
	public static final String PADDING = "padding";
	public static final String START = "START";
	public static final String END = "END";
	public static final String MAIN = "MAIN";
	public static final String SPECIAL = "SPECIAL";
	public static final String OTHER = "OTHER";
	public static final String ROOM_NUMBER = "number";
	public static final String ROOM_SIZE = "size";
	public static final String MIN = "min";
	public static final String VAR = "var";
	public static final String SKYBOX = "skybox";
	
	public static final String SERK = "Serk";
	public static final String STATIC = "Static";
	
	Node biome;
	
	public BiomeReader(String biome)
	{
		
		super("data/xml/biomes.data");
		
		Node root = getNode(BIOMES, root_node.getChildNodes());
		
		this.biome = getNode(biome, root.getChildNodes());
		
		if (this.biome == null)
		{
			System.err.println("Biome loading failed!");
		}
	}

	
	public GeneratorType getGenerator()
	{
		GeneratorType gtype = null;
		
		if (getNodeValue(GENERATOR, biome.getChildNodes()).equalsIgnoreCase(SERK))
		{
			gtype = GeneratorType.SERK;
		}
		else if (getNodeValue(GENERATOR, biome.getChildNodes()).equalsIgnoreCase(STATIC))
		{
			gtype = GeneratorType.STATIC;
		}
		
		return gtype;
	}
	
	public String getShortDescription(char c) 
	{
		Node desc = getNode(DESCRIPTION, biome.getChildNodes());
		
		for (int i = 0; i < desc.getChildNodes().getLength(); i++)
		{
			Node n = desc.getChildNodes().item(i);
			
			if (!n.getNodeName().equalsIgnoreCase(CHAR)) continue;
			
			if (getNodeValue(CHAR, n.getChildNodes()).charAt(0) == c)
			{
				return getNodeValue(SHORT_DESCRIPTION, n.getChildNodes());
			}
		}
		
		return "";
	}
	
	public String getLongDescription(char c) 
	{
		Node desc = getNode(DESCRIPTION, biome.getChildNodes());
		
		for (int i = 0; i < desc.getChildNodes().getLength(); i++)
		{
			Node n = desc.getChildNodes().item(i);
			
			if (!n.getNodeName().equalsIgnoreCase(CHAR)) continue;
			
			if (getNodeValue(CHAR, n.getChildNodes()).charAt(0) == c)
			{
				return getNodeValue(LONG_DESCRIPTION, n.getChildNodes());
			}
		}
		
		return "";
	}
	
	public String getSkybox()
	{
		String skybox = getNodeValue(SKYBOX, biome.getChildNodes());
		
		if (skybox.equals("")) skybox = null;
		
		return skybox;
	}
	
	public int getWidth()
	{
		int width = Integer.parseInt(getNodeValue(WIDTH, biome.getChildNodes()));
		
		return width;
	}
	
	public int getHeight()
	{
		int height = Integer.parseInt(getNodeValue(HEIGHT, biome.getChildNodes()));
		
		return height;
	}
	
	public int getRoof()
	{
		int roof = Integer.parseInt(getNodeValue(ROOF, biome.getChildNodes()));
		
		return roof;
	}

	public String getWallTexture()
	{
		Node wall = getNode(WALL, biome.getChildNodes());
		return getNodeValue(TEXTURE, wall.getChildNodes());
	}
	
	public Color getWallColour()
	{
		Node wall = getNode(WALL, biome.getChildNodes());
		float red = Float.parseFloat(getNodeValue(COLOUR_RED, wall.getChildNodes()));
		float green = Float.parseFloat(getNodeValue(COLOUR_GREEN, wall.getChildNodes()));
		float blue = Float.parseFloat(getNodeValue(COLOUR_BLUE, wall.getChildNodes()));
		
		return new Color(red, green, blue, 1.0f);
	}
	
	public String getFloorTexture()
	{
		Node floor = getNode(FLOOR, biome.getChildNodes());
		return getNodeValue(TEXTURE, floor.getChildNodes());
	}
	
	public Color getFloorColour()
	{
		Node floor = getNode(FLOOR, biome.getChildNodes());
		float red = Float.parseFloat(getNodeValue(COLOUR_RED, floor.getChildNodes()));
		float green = Float.parseFloat(getNodeValue(COLOUR_GREEN, floor.getChildNodes()));
		float blue = Float.parseFloat(getNodeValue(COLOUR_BLUE, floor.getChildNodes()));
		
		return new Color(red, green, blue, 1.0f);
	}
	
	public Color getAmbientLight()
	{
		Node ambient = getNode(AMBIENT, biome.getChildNodes());
		float red = Float.parseFloat(getNodeValue(COLOUR_RED, ambient.getChildNodes()));
		float green = Float.parseFloat(getNodeValue(COLOUR_GREEN, ambient.getChildNodes()));
		float blue = Float.parseFloat(getNodeValue(COLOUR_BLUE, ambient.getChildNodes()));
		
		return new Color(red, green, blue, 1.0f);
	}
	
	public int getRoomPadding()
	{
		Node room = getNode(ROOM_SETTINGS, biome.getChildNodes());
		
		return Integer.parseInt(getNodeValue(PADDING, room.getChildNodes()));
	}
	
	private Node getRoomNode(RoomType rtype)
	{
		String rname = null;
		
		if (rtype == RoomType.START)
		{
			rname = START;
		}
		else if (rtype == RoomType.END)
		{
			rname = END;
		}
		else if (rtype == RoomType.MAIN)
		{
			rname = MAIN;
		}
		else if (rtype == RoomType.SPECIAL)
		{
			rname = SPECIAL;
		}
		else if (rtype == RoomType.OTHER)
		{
			rname = OTHER;
		}
		
		Node room = getNode(ROOM_SETTINGS, biome.getChildNodes());
		
		return getNode(rname, room.getChildNodes());
	}
	
	public int getRoomNumberMin(RoomType rtype)
	{
		Node room = getRoomNode(rtype);
		room = getNode(ROOM_NUMBER, room.getChildNodes());
		
		return Integer.parseInt(getNodeValue(MIN, room.getChildNodes())); 
	}
	
	public int getRoomNumberVar(RoomType rtype)
	{
		Node room = getRoomNode(rtype);
		room = getNode(ROOM_NUMBER, room.getChildNodes());
		
		return Integer.parseInt(getNodeValue(VAR, room.getChildNodes()))+1; 
	}
	
	public int getRoomSizeMin(RoomType rtype)
	{
		Node room = getRoomNode(rtype);
		room = getNode(ROOM_SIZE, room.getChildNodes());
		
		return Integer.parseInt(getNodeValue(MIN, room.getChildNodes())); 
	}
	
	public int getRoomSizeVar(RoomType rtype)
	{
		Node room = getRoomNode(rtype);
		room = getNode(ROOM_SIZE, room.getChildNodes());
		
		return Integer.parseInt(getNodeValue(VAR, room.getChildNodes()))+1; 
	}
}
