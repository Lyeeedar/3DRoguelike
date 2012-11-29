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
import java.util.ArrayList;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom.RoomType;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

public class RoomReader {
	
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String START = "START";
	public static final String END = "END";
	public static final String MAIN = "MAIN";
	public static final String SPECIAL = "SPECIAL";
	public static final String OTHER = "OTHER";
	public static final String DEFINITIONS = "definitions";
	public static final String SYMBOL = "symbol";
	public static final String CHAR = "char";
	public static final String TYPE = "type";
	public static final String VISIBLE = "visible";
	public static final String DESCRIPTION = "description";
	public static final String MODEL = "model";
	public static final String MODEL_TYPE = "type";
	public static final String MODEL_NAME = "name";
	public static final String DIMENSIONS = "dimensions";
	public static final String DIMENSIONS_NUMBER = "number";
	public static final String DIMENSIONS_D = "d";
	public static final String TEXTURE = "texture";
	public static final String COLOUR = "colour";
	public static final String RED = "red";
	public static final String GREEN = "green";
	public static final String BLUE = "blue";
	public static final String ROOM = "room";
	public static final String ROW = "row";
	
	Document doc;
	Node biome;
	
	public RoomReader(String biome)
	{
		loadBiome(biome);
	}
	
	private void loadBiome(String biome)
	{
		DOMParser parser = new DOMParser();
		try {
			parser.parse(new InputSource(Gdx.files.internal("data/dungeons/"+biome+".data").read()));
			doc = parser.getDocument();
			this.biome = doc.getFirstChild();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public AbstractRoom getRoom(RoomType rtype, int width, int height)
	{
		Node roomType = getRoomNode(rtype);

		SortedMap<Integer, Node> valid = new TreeMap<Integer, Node>();
		
		for (int i = 0; i < roomType.getChildNodes().getLength(); i++)
		{
			Node n = roomType.getChildNodes().item(i);
			if (n.getNodeType() == Node.TEXT_NODE) continue;

			int w = Integer.parseInt(getNodeValue(WIDTH, n.getChildNodes()));
			if (w <= width)
			{
				int h = Integer.parseInt(getNodeValue(HEIGHT, n.getChildNodes()));
				if (h <= height)
				{
					int priority = (width-w)+(height-h);
					valid.put(priority, n);
				}
			}
		}
		
		if (valid.size() == 0)
		{
			return null;
		}
		
		int pos = 0;
		Random ran = new Random();
		while (true)
		{
			if (pos == valid.size())
			{
				pos--;
				break;
			}
			
			if (ran.nextInt(100) < 50)
			{
				pos++;
			}
			else
			{
				break;
			}
		}
		
		Node chosen = (Node) valid.values().toArray()[pos];
		
		int rwidth = Integer.parseInt(getNodeValue(WIDTH, chosen.getChildNodes()));
		int rheight = Integer.parseInt(getNodeValue(HEIGHT, chosen.getChildNodes()));
		
		AbstractRoom room = new AbstractRoom(rwidth, rheight);
		
		Node roomGraphics = getNode(ROOM, chosen.getChildNodes());
		for (int i = 0; i < rheight; i++)
		{
			room.setRow(i, getNodeValue(ROW+(i+1), roomGraphics.getChildNodes()).toCharArray());
		}
		
		Node define = getNode(DEFINITIONS, chosen.getChildNodes());
		
		for (int i = 0; i < define.getChildNodes().getLength(); i++)
		{
			Node symbol = define.getChildNodes().item(i);
			if (symbol.getNodeType() == Node.TEXT_NODE) continue;
			
			char character = getNodeValue(CHAR, symbol.getChildNodes()).charAt(0);
			String type = getNodeValue(TYPE, symbol.getChildNodes());
			boolean visible = Boolean.parseBoolean(getNodeValue(VISIBLE, symbol.getChildNodes()));
			String description = getNodeValue(DESCRIPTION, symbol.getChildNodes());
			
			AbstractObject ao = new AbstractObject(character, type, visible, description);
			
			if (visible)
			{
				Node model = getNode(MODEL, symbol.getChildNodes());
				String modelType = getNodeValue(MODEL_TYPE, model.getChildNodes());
				String modelName = getNodeValue(MODEL_NAME, model.getChildNodes());
				
				Node dimensions = getNode(DIMENSIONS, model.getChildNodes());
				int number = Integer.parseInt(getNodeValue(DIMENSIONS_NUMBER, dimensions.getChildNodes()));
				float[] dim = new float[number];
				for (int j = 1; j < number+1; j++)
				{
					dim[j-1] = Float.parseFloat(getNodeValue(DIMENSIONS_D+j, dimensions.getChildNodes()));
				}
				
				String texture = getNodeValue(TEXTURE, symbol.getChildNodes());
				
				Node colour = getNode(COLOUR, symbol.getChildNodes());
				float red = Float.parseFloat(getNodeValue(RED, colour.getChildNodes()));
				float green = Float.parseFloat(getNodeValue(GREEN, colour.getChildNodes()));
				float blue = Float.parseFloat(getNodeValue(BLUE, colour.getChildNodes()));
				Color col = new Color(red, green, blue, 1.0f);
				
				ao.setModel(modelType, modelName, texture, col, dim);
			}
			
			room.addObject(ao);
		}
		
		return room;
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

		return getNode(rname, biome.getChildNodes());
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
