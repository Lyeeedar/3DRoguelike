package com.lyeeedar.Roguelike3D.Game.Level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Item.Equippable;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * Class Used to evolve monsters to attempt to provide variety.
 * 
 * Also couples as the monsters.data xml reader.
 * @author Philip
 *
 */
public class MonsterEvolver {
	
	public static final String DEPTH_MIN = "DEPTH_MIN";
	public static final String DEPTH_MAX = "DEPTH_MAX";
	public static final String ACTORS = "ACTORS";
	public static final String ABSTRACT = "ABSTRACT";
	public static final String DESCRIPTION = "DESCRIPTION";
	public static final String MONSTER_TYPE = "MONSTER_TYPE";
	public static final String TYPE = "TYPE";
	
	final Random ran = new Random();
	
	final String monster;
	final Node root_node;

	public MonsterEvolver(String monster_type, int depth) {
		
		Document doc = null;
		DOMParser parser = new DOMParser();
		
		try {
			parser.parse(new InputSource(Gdx.files.internal("data/xml/monsters.data").read()));
			doc = parser.getDocument();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SortedMap<Integer, ArrayList<Node>> valid = new TreeMap<Integer, ArrayList<Node>>();
		
		for (int i = 0; i < doc.getFirstChild().getChildNodes().getLength(); i++)
		{
			Node n = doc.getFirstChild().getChildNodes().item(i);
			if (n.getNodeType() == Node.TEXT_NODE) continue;
			
			Node an = getNode(ABSTRACT, n.getChildNodes());
			Node tn = getNode(MONSTER_TYPE, an.getChildNodes());
			
			for (int j = 0; j < tn.getChildNodes().getLength(); j++)
			{
				Node in = tn.getChildNodes().item(j);
				if (in.getNodeName().equalsIgnoreCase(TYPE) && getNodeValue(in).equalsIgnoreCase(monster_type))
				{
					int d_min = Integer.parseInt(getNodeValue(getNode(DEPTH_MIN, an.getChildNodes())));
					int d_max = Integer.parseInt(getNodeValue(getNode(DEPTH_MAX, an.getChildNodes())));
					
					if (depth < d_min)
					{
						if (valid.containsKey(0)) valid.get(d_min-depth).add(n);
						else
						{
							ArrayList<Node> nodes = new ArrayList<Node>();
							nodes.add(n);
							valid.put(d_min-depth, nodes);
						}
					}
					else if (depth > d_max)
					{
						if (valid.containsKey(0)) valid.get(depth-d_max).add(n);
						else
						{
							ArrayList<Node> nodes = new ArrayList<Node>();
							nodes.add(n);
							valid.put(depth-d_max, nodes);
						}
					}
					else
					{
						if (valid.containsKey(0)) valid.get(0).add(n);
						else
						{
							ArrayList<Node> nodes = new ArrayList<Node>();
							nodes.add(n);
							valid.put(0, nodes);
						}
					}
				}
			}
		}
		
		if (valid.size() == 0)
		{
			root_node = null;
			monster = null;
			return;
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
		
		ArrayList<Node> ns = valid.get(valid.keySet().toArray()[pos]);
		
		root_node = ns.get(ran.nextInt(ns.size()));
		monster = root_node.getNodeName();
	}

	ArrayList<AbstractActor> actors;
	EvolverTile[][] grid;
	int actor_num;
	
	public void createGrid(int actor_num)
	{
		this.actor_num = actor_num;
		
		grid = new EvolverTile[actor_num][actor_num];
		
		for (int i = 0; i < actor_num; i++)
		{
			for (int j = 0; j < actor_num; j++)
			{
				grid[i][j] = new EvolverTile();
			}
		}
		
		for (int i = 0; i < ran.nextInt(actor_num/2)+10; i++)
		{
			addFood();
		}
		
		for (int i = 0; i < ran.nextInt(actor_num/2)+5; i++)
		{
			addBreed();
		}
	}
	
	private void addFood()
	{
		grid[ran.nextInt(actor_num)][ran.nextInt(actor_num)].food = true;
	}
	
	private void addBreed()
	{
		grid[ran.nextInt(actor_num)][ran.nextInt(actor_num)].breed = true;
	}
	
	public boolean update()
	{
		for (AbstractActor aa : actors)
		{
			aa.update(grid);
		}
		
		Iterator<AbstractActor> itr = actors.iterator();
		while (itr.hasNext())
		{
			AbstractActor aa = itr.next();
			if (!aa.alive) itr.remove();
		}
		
		if (actors.size() == 0) return false;
		else return true;
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

class EvolverTile
{
	public boolean food = false;
	public boolean breed = false;
	
	public AbstractActor actor = null;
}

class AbstractActor
{
	public final Equippable HELMET;
	public final Equippable SHIRT;
	public final Equippable TROUSERS;
	public final Equippable BOOTS;
	public final Equippable GLOVES;
	public final Equippable L_HAND;
	public final Equippable R_HAND;
	
	public final int HEALTH;
	public final HashMap<Element, Integer> DEFENSES;
	
	public final float SPEED;
	public final int STRENGTH;
	public final int IQ;
	public final int SIGHT;
	
	public int health;
	
	public boolean alive = true;
	
	public AbstractActor(Equippable HELMET, Equippable SHIRT,Equippable TROUSERS, Equippable BOOTS, Equippable GLOVES, Equippable L_HAND, Equippable R_HAND,
			int HEALTH, HashMap<Element, Integer> DEFENSES, float SPEED, int STRENGTH, int IQ, int SIGHT)
	{
		this.HELMET = HELMET;
		this.SHIRT = SHIRT;
		this.TROUSERS = TROUSERS;
		this.BOOTS = BOOTS;
		this.GLOVES = GLOVES;
		this.L_HAND = L_HAND;
		this.R_HAND = R_HAND;
		this.HEALTH = HEALTH;
		this.DEFENSES = DEFENSES;
		this.SPEED = SPEED;
		this.STRENGTH = STRENGTH;
		this.IQ = IQ;
		this.SIGHT = SIGHT;
		
		this.health = HEALTH;
	}
	
	public int x;
	public int y;
	
	public void placeActor(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void update(EvolverTile[][] grid)
	{
		damage(1, null);
		
		
		
	}
	
	public void damage(int amount, Element TYPE)
	{
		if (TYPE != null)
		{
			int eleDefense = DEFENSES.get(TYPE);
			
			if (eleDefense != 0) amount *= (100-eleDefense)/100;
		}
		
		health -= amount;
		
		if (health < 0) alive = false;
	}
}