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

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom.RoomType;
import com.lyeeedar.Roguelike3D.Game.Level.MapGenerator.GeneratorType;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class BiomeReader {
	
	public static final String NAME = "name";
	public static final String GENERATOR = "generator";
	public static final String DESCRIPTION = "description";
	public static final String CHAR = "char";
	public static final String HEIGHT = "height";
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
	
	public static final String SERK = "Serk";
	
	Document doc;
	Node biome;
	
	public BiomeReader(String biome)
	{
		loadBiomes();
		setBiome(biome);
	}
	
	private void loadBiomes()
	{
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(Gdx.files.internal("data/dungeons/biomes.data").read()));
			doc = parser.getDocument();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setBiome(String biome)
	{
		NodeList root = doc.getChildNodes();
		
		this.biome = getNode(biome, root);
	}
	
	public GeneratorType getGenerator()
	{
		GeneratorType gtype = null;
		
		if (getNodeValue(GENERATOR, biome.getChildNodes()).equals(SERK))
		{
			gtype = GeneratorType.SERK;
		}
		
		return gtype;
	}
	
	public String getDescription(char c) 
	{
		Node desc = getNode(DESCRIPTION, biome.getChildNodes());
		
		for (int i = 0; i < desc.getChildNodes().getLength(); i++)
		{
			Node n = desc.getChildNodes().item(i);
			
			if (!n.getNodeName().equalsIgnoreCase(CHAR)) continue;
			
			if (getNodeValue(CHAR, n.getChildNodes()).charAt(0) == c)
			{
				return getNodeValue(DESCRIPTION, n.getChildNodes());
			}
		}
		
		return "";
	}
	
	public int getHeight()
	{
		int height = Integer.parseInt(getNodeValue(HEIGHT, biome.getChildNodes()));
		
		return height;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	protected Node getNode(String tagName, NodeList nodes) {
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                return node;
            }
        }

        return null;
    }

    protected String getNodeValue( Node node ) {
        NodeList childNodes = node.getChildNodes();
        for (int x = 0; x < childNodes.getLength(); x++ ) {
            Node data = childNodes.item(x);
            if ( data.getNodeType() == Node.TEXT_NODE )
                return data.getNodeValue();
        }
        return "";
    }

    protected String getNodeValue(String tagName, NodeList nodes ) {
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++ ) {
                    Node data = childNodes.item(y);
                    if ( data.getNodeType() == Node.TEXT_NODE )
                        return data.getNodeValue();
                }
            }
        }
        return "";
    }

    protected String getNodeAttr(String attrName, Node node ) {
        NamedNodeMap attrs = node.getAttributes();
        for (int y = 0; y < attrs.getLength(); y++ ) {
            Node attr = attrs.item(y);
            if (attr.getNodeName().equalsIgnoreCase(attrName)) {
                return attr.getNodeValue();
            }
        }
        return "";
    }

    protected String getNodeAttr(String tagName, String attrName, NodeList nodes ) {
        for ( int x = 0; x < nodes.getLength(); x++ ) {
            Node node = nodes.item(x);
            if (node.getNodeName().equalsIgnoreCase(tagName)) {
                NodeList childNodes = node.getChildNodes();
                for (int y = 0; y < childNodes.getLength(); y++ ) {
                    Node data = childNodes.item(y);
                    if ( data.getNodeType() == Node.ATTRIBUTE_NODE ) {
                        if ( data.getNodeName().equalsIgnoreCase(attrName) )
                            return data.getNodeValue();
                    }
                }
            }
        }

        return "";
    }
}
