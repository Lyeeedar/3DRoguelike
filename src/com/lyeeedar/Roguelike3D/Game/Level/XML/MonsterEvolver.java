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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Actor.AI_Enemy_VFFG;
import com.lyeeedar.Roguelike3D.Game.Actor.AI_Player_Controlled;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Item.Component;
import com.lyeeedar.Roguelike3D.Game.Item.Component.Component_Type;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND.WeaponType;
import com.lyeeedar.Roguelike3D.Game.Item.Equippable;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.sun.org.apache.xerces.internal.parsers.DOMParser;

/**
 * Class Used to evolve monsters to attempt to provide variety.
 * 
 * Also couples as the monsters.data xml reader.
 * @author Philip
 *
 */
public class MonsterEvolver extends XMLReader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 935894653679977084L;
	public transient static final int EVOLVER_WIDTH = 20;
	public transient static final int EVOLVER_HEIGHT = 20;

	public transient static final int EVOLVER_CREATURE_NUM = 20;

	public transient static final int EVOLVER_CREATURE_TURNS = 100;

	public transient static final String DATA = "DATA";

	public transient static final String MONSTERS = "monsters";
	public transient static final String DEPTH_MIN = "depth_min";
	public transient static final String DEPTH_MAX = "depth_max";
	public transient static final String MONSTER_TYPE = "monster_type";
	public transient static final String CREATURES = "creatures";

	public transient static final String VISUAL = "visual";	
	public transient static final String DESCRIPTION = "description";
	public transient static final String MODEL = "model";
	public transient static final String MODEL_TYPE = "type";
	public transient static final String MODEL_NAME = "name";
	public transient static final String MODEL_SCALE = "scale";	
	public transient static final String TEXTURE = "texture";	
	public transient static final String COLOUR = "colour";
	public transient static final String RED = "red";
	public transient static final String GREEN = "green";
	public transient static final String BLUE = "blue";

	public transient static final String STATS = "stats";
	public transient static final String BASE_CALORIES = "base_calories";
	public transient static final String WEIGHT = "weight";
	public transient static final String HEALTH = "health";
	public transient static final String ELEMENT = "element";
	public transient static final String ELE_DEFENSES = "ele_defenses";
	public transient static final String FIRE = "FIRE";
	public transient static final String WATER = "WATER";
	public transient static final String AIR = "AIR";
	public transient static final String WOOD = "WOOD";
	public transient static final String METAL = "METAL";
	public transient static final String AETHER = "AETHER";
	public transient static final String VOID = "VOID";
	public transient static final String DEFENSES = "defenses";
	public transient static final String PIERCE = "PIERCE";
	public transient static final String IMPACT = "IMPACT";
	public transient static final String TOUCH = "TOUCH";
	public transient static final String STRENGTH = "strength";
	public transient static final String ATTACK_SPEED = "attack_speed";
	public transient static final String ATTACK_DIST_MIN = "attack_dist_min";
	public transient static final String ATTACK_DIST_MAX = "attack_dist_max";
	public transient static final String WEAPON_STYLE = "weapon_style";
	public transient static final String WEAPON_TYPE = "weapon_type";
	public transient static final String AI = "AI";

	public transient static final String MIND = "MIND";
	public transient static final String SKIN = "SKIN";
	public transient static final String BONES = "BONES";
	public transient static final String MUSCLES = "MUSCLES";
	public transient static final String ATTACK = "ATTACK";
	public transient static final String ATTACK_RIGHT = "ATTACK_RIGHT";
	public transient static final String ATTACK_LEFT = "ATTACK_LEFT";

	public transient static final String SCALING = "scaling";
	public transient static final String TYPE = "type";

	public transient static final String ABSTRACT = "abstract";
	public transient static final String CREATURE = "creature";
	public transient static final String CALORIE_USAGE = "calorie_usage";

	public transient static final String ATTRIBUTES = "attributes";

	public transient static final String CALORIES = "calories";

	public transient static final String DIFFICULTY = "difficulty_";
	
	public transient static final String DROP_LIST = "drop_list";
	public transient static final String DROP = "drop";
	public transient static final String CHANCE = "chance";
	public transient static final String WEIGHT_PER_AMOUNT = "weight_per_amount";
	public transient static final String SOFT_HARD = "soft_hard";
	public transient static final String FLEXIBLE_BRITTLE = "flexible_brittle";
	public transient static final String NAME = "name";
	public transient static final String AMOUNT = "amount";
	public transient static final String RARITY = "rarity";
	
	public transient static final String FACTIONS = "factions";
	public transient static final String FACTION = "faction";
	public transient static final String ICON = "icon";
	
	final transient Random ran = new Random();

	final String monster;
	final transient Node selected_monster;
	final transient Node attribute_root;

	final HashMap<String, AbstractCreature_Evolver> creatures = new HashMap<String, AbstractCreature_Evolver>();
	
	public final String UID;
	
	public final String type;

	public MonsterEvolver(String monster_type, int depth) {

		super("data/xml/monsters.data");
		
		this.type = monster_type;
		
		UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		
		System.out.println("Creating for type "+monster_type+" at depth "+depth);
		
		attribute_root = getNode(ATTRIBUTES, getNode(DATA, root_node.getChildNodes()).getChildNodes());

		SortedMap<Integer, ArrayList<Node>> valid = new TreeMap<Integer, ArrayList<Node>>();

		Node monsters = getNode(MONSTERS, getNode(DATA, root_node.getChildNodes()).getChildNodes());

		for (int i = 0; i < monsters.getChildNodes().getLength(); i++)
		{
			Node n = monsters.getChildNodes().item(i);
			if (n.getNodeType() == Node.TEXT_NODE) continue;

			String mon_type = getNodeValue(MONSTER_TYPE, n.getChildNodes());

			if (mon_type.equalsIgnoreCase(monster_type))
			{
				int d_min = Integer.parseInt(getNodeValue(getNode(DEPTH_MIN, n.getChildNodes())));
				int d_max = Integer.parseInt(getNodeValue(getNode(DEPTH_MAX, n.getChildNodes())));

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

		if (valid.size() == 0)
		{
			selected_monster = null;
			monster = null;
			System.err.println("Failed to select a monster!");
			return;
		}

		ArrayList<Node> ns = valid.get(valid.firstKey());

		selected_monster = ns.get(ran.nextInt(ns.size()));
		monster = selected_monster.getNodeName();
		
		ArrayList<String> factions = new ArrayList<String>();
		
		Node faction = getNode(FACTIONS, selected_monster.getChildNodes());
		
		for (int i = 0; i < faction.getChildNodes().getLength(); i++)
		{
			Node n = faction.getChildNodes().item(i);
			
			if (!n.getNodeName().equalsIgnoreCase(FACTION)) continue;
			
			factions.add(getNodeValue(n));
		}

		Node creatures = getNode(CREATURES, selected_monster.getChildNodes());

		for (int i = 0; i < creatures.getChildNodes().getLength(); i++)
		{
			Node n = creatures.getChildNodes().item(i);

			if (n.getNodeType() == Node.TEXT_NODE) continue;

			AbstractCreature_Evolver ac_e = new AbstractCreature_Evolver(n.getNodeName(), factions);

			// Add visual

			Node visual = getNode(VISUAL, n.getChildNodes());
			NodeList description = getNode(DESCRIPTION, visual.getChildNodes()).getChildNodes();

			Node model = getNode(MODEL, visual.getChildNodes());
			String model_type = getNodeValue(MODEL_TYPE, model.getChildNodes());
			String model_name = getNodeValue(MODEL_NAME, model.getChildNodes());
			String model_scale = getNodeValue(MODEL_SCALE, model.getChildNodes());

			String texture = getNodeValue(TEXTURE, visual.getChildNodes());

			Node colour = getNode(COLOUR, visual.getChildNodes());
			String red = getNodeValue(RED, colour.getChildNodes());
			String green = getNodeValue(GREEN, colour.getChildNodes());
			String blue = getNodeValue(BLUE, colour.getChildNodes());

			ac_e.addVisual(description, model_type, model_name, model_scale, texture, red, green, blue);

			// Add stats

			Node stats = getNode(STATS, n.getChildNodes());

			String base_calories = getNodeValue(BASE_CALORIES, stats.getChildNodes());
			String weight = getNodeValue(WEIGHT, stats.getChildNodes());
			String health = getNodeValue(HEALTH, stats.getChildNodes());
			String strength = getNodeValue(STRENGTH, stats.getChildNodes());

			ac_e.addStats(base_calories, weight, health, strength);

			// Add Elemental Defenses

			Node eleDef = getNode(ELE_DEFENSES, stats.getChildNodes());

			String f = getNodeValue(FIRE, eleDef.getChildNodes());
			String wa = getNodeValue(WATER, eleDef.getChildNodes());
			String ai = getNodeValue(AIR, eleDef.getChildNodes());
			String wo = getNodeValue(WOOD, eleDef.getChildNodes());
			String m = getNodeValue(METAL, eleDef.getChildNodes());
			String ae = getNodeValue(AETHER, eleDef.getChildNodes());
			String v = getNodeValue(VOID, eleDef.getChildNodes());

			ac_e.addEleDef(f, wa, ai, wo, m, ae, v);

			// Add Damage Defenses

			Node damDef = getNode(DEFENSES, stats.getChildNodes());

			String pierce = getNodeValue(PIERCE, damDef.getChildNodes());
			String impact = getNodeValue(IMPACT, damDef.getChildNodes());
			String touch = getNodeValue(TOUCH, damDef.getChildNodes());

			ac_e.addDamDef(pierce, impact, touch);

			// Add Mind info

			Node mind = getNode(MIND, stats.getChildNodes());

			String mind_scale = getNodeValue(SCALING, mind.getChildNodes());
			String mind_type = getNodeValue(TYPE, mind.getChildNodes());

			ac_e.addMind(mind_scale, mind_type);

			// Add Skin info

			Node skin = getNode(SKIN, stats.getChildNodes());

			String skin_scale = getNodeValue(SCALING, skin.getChildNodes());
			String skin_type = getNodeValue(TYPE, skin.getChildNodes());
			ArrayList<Component> skinDrops = getDrops(getNode(DROP_LIST, skin.getChildNodes()));

			ac_e.addSkin(skin_scale, skin_type, skinDrops);

			// Add Bones info

			Node bones = getNode(BONES, stats.getChildNodes());

			String bones_scale = getNodeValue(SCALING, bones.getChildNodes());
			String bones_type = getNodeValue(TYPE, bones.getChildNodes());
			ArrayList<Component> boneDrops = getDrops(getNode(DROP_LIST, bones.getChildNodes()));

			ac_e.addBones(bones_scale, bones_type, boneDrops);

			// Add Muscles info

			Node muscles = getNode(MUSCLES, stats.getChildNodes());

			String muscles_scale = getNodeValue(SCALING, muscles.getChildNodes());
			String muscles_type = getNodeValue(TYPE, muscles.getChildNodes());

			ac_e.addMuscles(muscles_scale, muscles_type);

			// Add Attack info

			Node attack_right = getNode(ATTACK_RIGHT, stats.getChildNodes());

			if (attack_right != null)
			{
				String attack_scale = getNodeValue(SCALING, attack_right.getChildNodes());
				String attack_type = getNodeValue(TYPE, attack_right.getChildNodes());
				ArrayList<Component> attRDrops = getDrops(getNode(DROP_LIST, attack_right.getChildNodes()));

				ac_e.addAttack_Right(attack_scale, attack_type, attRDrops);
			}

			Node attack_left = getNode(ATTACK_LEFT, stats.getChildNodes());

			if (attack_left != null)
			{
				String attack_scale = getNodeValue(SCALING, attack_left.getChildNodes());
				String attack_type = getNodeValue(TYPE, attack_left.getChildNodes());
				ArrayList<Component> attLDrops = getDrops(getNode(DROP_LIST, attack_left.getChildNodes()));

				ac_e.addAttack_Left(attack_scale, attack_type, attLDrops);
			}

			this.creatures.put(ac_e.name, ac_e);

		}
	}
	
	private ArrayList<Component> getDrops(Node dropList)
	{
		ArrayList<Component> drops = new ArrayList<Component>();
		
		if (dropList == null) return drops;
		
		for (int i = 0; i < dropList.getChildNodes().getLength(); i++)
		{
			Node n = dropList.getChildNodes().item(i);
			
			if (n.getNodeName().equalsIgnoreCase(DROP))
			{
				drops.add(getComponent(n));
			}
		}
		
		return drops;
	}
	
	private Component getComponent(Node dropNode)
	{
		String typeString = getNodeValue(TYPE, dropNode.getChildNodes());
		Component_Type type = Component.convertComponentType(typeString);
		
		String name = getNodeValue(NAME, dropNode.getChildNodes());
		
		String icon = getNodeValue(ICON, dropNode.getChildNodes());
		
		String rarityString = getNodeValue(RARITY, dropNode.getChildNodes());
		int rarity = Integer.parseInt(rarityString);
		
		String chanceString = getNodeValue(CHANCE, dropNode.getChildNodes());
		int chance = Integer.parseInt(chanceString);
		
		String description = getNodeValue(DESCRIPTION, dropNode.getChildNodes());
		
		String weightString = getNodeValue(WEIGHT_PER_AMOUNT, dropNode.getChildNodes());
		int weight_per_amount = Integer.parseInt(weightString);
		
		String amountString = getNodeValue(AMOUNT, dropNode.getChildNodes());
		int amount = Integer.parseInt(amountString);
		
		String softString = getNodeValue(SOFT_HARD, dropNode.getChildNodes());
		int soft_hard = Integer.parseInt(softString);
		
		String flexibleString = getNodeValue(FLEXIBLE_BRITTLE, dropNode.getChildNodes());
		int flexible_brittle = Integer.parseInt(flexibleString);
		
		Node ele = getNode(ELEMENT, dropNode.getChildNodes());
		String f = getNodeValue(FIRE, ele.getChildNodes());
		String wa = getNodeValue(WATER, ele.getChildNodes());
		String ai = getNodeValue(AIR, ele.getChildNodes());
		String wo = getNodeValue(WOOD, ele.getChildNodes());
		String m = getNodeValue(METAL, ele.getChildNodes());
		String ae = getNodeValue(AETHER, ele.getChildNodes());
		String v = getNodeValue(VOID, ele.getChildNodes());
		HashMap<Element, Integer> element = new HashMap<Element, Integer>();
		element.put(Element.FIRE, Integer.parseInt(f));
		element.put(Element.WATER, Integer.parseInt(wa));
		element.put(Element.AIR, Integer.parseInt(ai));
		element.put(Element.WOOD, Integer.parseInt(wo));
		element.put(Element.METAL, Integer.parseInt(m));
		element.put(Element.AETHER, Integer.parseInt(ae));
		element.put(Element.VOID, Integer.parseInt(v));
		
		Component drop = new Component(type, name, rarity, chance, description, weight_per_amount, amount, soft_hard, flexible_brittle, element, icon);
		
		return drop;
	}

	public GameActor getMonster(int difficulty)
	{
		Creature_Evolver ce = EVOLVED_CREATURES[difficulty];
		
		float scale = ce.creature.model_scale;
		Colour colour = ce.creature.colour;
		String texture = ce.creature.texture;
		
		GameActor ga = new GameActor(colour, texture, 0, 0, 0, scale, GL20.GL_TRIANGLES, "file", ce.creature.model_name);
		for (Component c : ce.creature.skinDrops)
		{
			ga.INVENTORY.put(c.drop_chance, c);
		}
		for (Component c : ce.creature.boneDrops)
		{
			ga.INVENTORY.put(c.drop_chance, c);
		}
		if (ce.attack_right != null)
		{
			ga.R_HAND = Equipment_HAND.getWeapon(
					ce.attack_right.wep_type,
					ce.attack_right.wep_style,
					ce.attack_right.strength,
					ce.attack_right.ele_amount,
					ce.attack_right.dam_amount,
					ce.attack_right.atk_speed,
					ce.attack_right.weight,
					false,
					2);
			ga.R_HAND.equip(ga, 2);
			for (Component c : ce.creature.attRDrops)
			{
				ga.INVENTORY.put(c.drop_chance, c);
			}
		}
		if (ce.attack_left != null)
		{
			ga.L_HAND = Equipment_HAND.getWeapon(
					ce.attack_left.wep_type,
					ce.attack_left.wep_style,
					ce.attack_left.strength,
					ce.attack_left.ele_amount,
					ce.attack_left.dam_amount,
					ce.attack_left.atk_speed,
					ce.attack_left.weight,
					false,
					2);
			ga.L_HAND.equip(ga, 1);
			for (Component c : ce.creature.attLDrops)
			{
				ga.INVENTORY.put(c.drop_chance, c);
			}
		}
		
		ga.setStats(ce.health, ce.weight, ce.strength, ce.ele_defenses, ce.dam_defenses, ce.creature.factions);
		
		ga.ai = new AI_Enemy_VFFG(ga);

		return ga;
	}
	
	transient EvolverTile[][] grid = new EvolverTile[EVOLVER_WIDTH][EVOLVER_HEIGHT];
	public void createMap()
	{
		for (int x = 0; x < EVOLVER_WIDTH; x++)
		{
			for (int y = 0; y < EVOLVER_HEIGHT; y++)
			{
				grid[x][y] = new EvolverTile(x, y);
			}
		}
	}

	public Creature_Evolver[] EVOLVED_CREATURES = new Creature_Evolver[10];

	public void Evolve_Creature()
	{
		for (int j = 0; j < 10; j++) {

			Creature_Evolver best = null;
			while (true)
			{

				for (int ii = 0; ii < EVOLVER_CREATURE_TURNS; ii++)
				{
					for (int x = 0; x < EVOLVER_WIDTH; x++)	
					{
						for (int y = 0; y < EVOLVER_HEIGHT; y++)
						{
							grid[x][y].food = false;
							grid[x][y].creature = null;
						}
					}
				}

				for (int i = 0; i < 25; i++)
				{
					grid[ran.nextInt(EVOLVER_WIDTH)][ran.nextInt(EVOLVER_HEIGHT)].food = true;
				}

				for (int ii = 0; ii < EVOLVER_CREATURE_NUM; ii++)
				{
					int x = ran.nextInt(EVOLVER_WIDTH);
					int y = ran.nextInt(EVOLVER_HEIGHT);
					if (j == 0)
						grid[x][y].creature = createCreature(j);
					else
						grid[x][y].creature = recreateCreature(j);

					grid[x][y].creature.x = x;
					grid[x][y].creature.y = y;
				}

				ArrayList<Creature_Evolver> creatures = new ArrayList<Creature_Evolver>();
				for (int ii = 0; ii < EVOLVER_CREATURE_TURNS; ii++)
				{
					for (int x = 0; x < EVOLVER_WIDTH; x++)
					{
						for (int y = 0; y < EVOLVER_HEIGHT; y++)
						{
							if (grid[x][y].creature != null)
							{
								creatures.add(grid[x][y].creature);
							}
						}
					}

					for (Creature_Evolver c_e : creatures)
					{
						c_e.update(grid);
					}
				}

				int best_points = 0;

				for (int x = 0; x < EVOLVER_WIDTH; x++)
				{
					for (int y = 0; y < EVOLVER_HEIGHT; y++)
					{
						if (grid[x][y].creature != null && grid[x][y].creature.points >= best_points)
						{
							best = grid[x][y].creature;
							best_points = best.points;
						}
					}
				}
				
				if (best != null) break;
			}

			EVOLVED_CREATURES[j] = best;
			System.out.println("Creature Evolved for Difficulty "+j);
			System.out.println(EVOLVED_CREATURES[j]);

		}
	}
	
	private Creature_Evolver createCreature(int difficulty)
	{
		Node abs = getNode(ABSTRACT, selected_monster.getChildNodes());
		Node abstractList = getNode(DIFFICULTY+difficulty, abs.getChildNodes());
		AbstractCreature_Evolver creature = creatures.get(getNodeValue(CREATURE, abstractList.getChildNodes()));
		int calorie_usage = Integer.parseInt(getNodeValue(CALORIE_USAGE, abstractList.getChildNodes()));

		Creature_Evolver c_e = new Creature_Evolver(creature, calorie_usage, 0, 0);
		c_e.addAttributes(
				getMind(creature),
				getSkin(creature),
				getBones(creature),
				getMuscles(creature),
				getAttackRight(creature),
				getAttackLeft(creature));
		
		while (c_e.calorie_usage_base < c_e.calorie_usage)
		{
			c_e = new Creature_Evolver(creature, calorie_usage, 0, 0);
			c_e.addAttributes(
					getMind(creature),
					getSkin(creature),
					getBones(creature),
					getMuscles(creature),
					getAttackRight(creature),
					getAttackLeft(creature));		
		}

		return c_e;
	}
	
	private Creature_Evolver recreateCreature(int difficulty)
	{
		Node abstractList = getNode(DIFFICULTY+difficulty, getNode(ABSTRACT, selected_monster.getChildNodes()).getChildNodes());
		AbstractCreature_Evolver creature = creatures.get(getNodeValue(CREATURE, abstractList.getChildNodes()));
		int calorie_usage = Integer.parseInt(getNodeValue(CALORIE_USAGE, abstractList.getChildNodes()));

		Creature_Evolver c_e = new Creature_Evolver(creature, calorie_usage, 0, 0);
		EVOLVED_CREATURES[difficulty-1].nextEvolution(c_e, this);
		
		while (c_e.calorie_usage_base < c_e.calorie_usage)
		{
			EVOLVED_CREATURES[difficulty-1].nextEvolution(c_e, this);
		}

		return c_e;
	}

	public Mind_Evolver getMind(AbstractCreature_Evolver creature)
	{
		Node mind = getNode(creature.mind_type, getNode(MIND, attribute_root.getChildNodes()).getChildNodes());

		ArrayList<Node> valid = new ArrayList<Node>();
		for (int i = 0; i < mind.getChildNodes().getLength(); i++)
		{
			Node n = mind.getChildNodes().item(i);

			if (n.getNodeType() == Node.TEXT_NODE) continue;

			valid.add(n);
		}

		Node selected = valid.get(ran.nextInt(valid.size()));

		Mind_Evolver m_e = new Mind_Evolver(selected.getNodeName());

		m_e.addAI(getNodeValue(AI, selected.getChildNodes()));

		return m_e;
	}

	public Skin_Evolver getSkin(AbstractCreature_Evolver creature)
	{
		Node skin = getNode(creature.skin_type, getNode(SKIN, attribute_root.getChildNodes()).getChildNodes());
		ArrayList<Node> valid = new ArrayList<Node>();
		for (int i = 0; i < skin.getChildNodes().getLength(); i++)
		{
			Node n = skin.getChildNodes().item(i);

			if (n.getNodeType() == Node.TEXT_NODE) continue;

			valid.add(n);
		}

		Node selected = valid.get(ran.nextInt(valid.size()));

		Skin_Evolver s_e = new Skin_Evolver(selected.getNodeName());

		s_e.addDescription(getNodeValue(DESCRIPTION, selected.getChildNodes()));

		s_e.addStats(
				getNodeValue(CALORIES, selected.getChildNodes()),
				getNodeValue(WEIGHT, selected.getChildNodes()),
				getNodeValue(HEALTH, selected.getChildNodes()),
				getNodeValue(STRENGTH, selected.getChildNodes())
				);

		Node ele_def = getNode(ELE_DEFENSES, selected.getChildNodes());
		s_e.addEleDef(
				getNodeValue(FIRE, ele_def.getChildNodes()), 
				getNodeValue(WATER, ele_def.getChildNodes()), 
				getNodeValue(AIR, ele_def.getChildNodes()),
				getNodeValue(WOOD, ele_def.getChildNodes()), 
				getNodeValue(METAL, ele_def.getChildNodes()), 
				getNodeValue(AETHER, ele_def.getChildNodes()), 
				getNodeValue(VOID, ele_def.getChildNodes())
				);

		Node dam_def = getNode(DEFENSES, selected.getChildNodes());
		s_e.addDamDef(
				getNodeValue(PIERCE, dam_def.getChildNodes()), 
				getNodeValue(IMPACT, dam_def.getChildNodes()), 
				getNodeValue(TOUCH, dam_def.getChildNodes())
				);

		return s_e;
	}

	public Bones_Evolver getBones(AbstractCreature_Evolver creature)
	{
		Node bones = getNode(creature.bones_type, getNode(BONES, attribute_root.getChildNodes()).getChildNodes());

		ArrayList<Node> valid = new ArrayList<Node>();
		for (int i = 0; i < bones.getChildNodes().getLength(); i++)
		{
			Node n = bones.getChildNodes().item(i);

			if (n.getNodeType() == Node.TEXT_NODE) continue;

			valid.add(n);
		}

		Node selected = valid.get(ran.nextInt(valid.size()));

		Bones_Evolver b_e = new Bones_Evolver(selected.getNodeName());

		b_e.addStats(
				getNodeValue(CALORIES, selected.getChildNodes()),
				getNodeValue(WEIGHT, selected.getChildNodes()),
				getNodeValue(HEALTH, selected.getChildNodes())
				);

		Node dam_def = getNode(DEFENSES, selected.getChildNodes());
		b_e.addDamDef(
				getNodeValue(PIERCE, dam_def.getChildNodes()), 
				getNodeValue(IMPACT, dam_def.getChildNodes()), 
				getNodeValue(TOUCH, dam_def.getChildNodes())
				);

		return b_e;
	}

	public Muscles_Evolver getMuscles(AbstractCreature_Evolver creature)
	{
		Node muscles = getNode(creature.muscles_type, getNode(MUSCLES, attribute_root.getChildNodes()).getChildNodes());

		ArrayList<Node> valid = new ArrayList<Node>();
		for (int i = 0; i < muscles.getChildNodes().getLength(); i++)
		{
			Node n = muscles.getChildNodes().item(i);

			if (n.getNodeType() == Node.TEXT_NODE) continue;

			valid.add(n);
		}

		Node selected = valid.get(ran.nextInt(valid.size()));

		Muscles_Evolver m_e = new Muscles_Evolver(selected.getNodeName());

		m_e.addStats(
				getNodeValue(CALORIES, selected.getChildNodes()),
				getNodeValue(WEIGHT, selected.getChildNodes()),
				getNodeValue(STRENGTH, selected.getChildNodes())
				);

		return m_e;
	}

	public Attack_Evolver getAttackLeft(AbstractCreature_Evolver creature)
	{
		Node attack = null;

		if (creature.left_equipped) attack = getNode(creature.attack_left_type, getNode(ATTACK, attribute_root.getChildNodes()).getChildNodes());

		if (attack == null) return null;

		ArrayList<Node> valid = new ArrayList<Node>();
		for (int i = 0; i < attack.getChildNodes().getLength(); i++)
		{
			Node n = attack.getChildNodes().item(i);

			if (n.getNodeType() == Node.TEXT_NODE) continue;

			valid.add(n);
		}

		Node selected = valid.get(ran.nextInt(valid.size()));

		Attack_Evolver a_e = new Attack_Evolver(selected.getNodeName());

		a_e.addDescription(getNodeValue(DESCRIPTION, selected.getChildNodes()));

		a_e.addStats(
				getNodeValue(CALORIES, selected.getChildNodes()),
				getNodeValue(WEIGHT, selected.getChildNodes()),
				getNodeValue(STRENGTH, selected.getChildNodes()),
				getNodeValue(ATTACK_SPEED, selected.getChildNodes()),
				getNodeValue(ATTACK_DIST_MIN, selected.getChildNodes()),
				getNodeValue(ATTACK_DIST_MAX, selected.getChildNodes()),
				getNodeValue(WEAPON_TYPE, selected.getChildNodes()),
				getNodeValue(WEAPON_STYLE, selected.getChildNodes())
				);

		Node element = getNode(ELEMENT, selected.getChildNodes());
		a_e.addElements(
				getNodeValue(PIERCE, selected.getChildNodes()),
				getNodeValue(IMPACT, selected.getChildNodes()),
				getNodeValue(TOUCH, selected.getChildNodes()),
				getNodeValue(FIRE, element.getChildNodes()),
				getNodeValue(WATER, element.getChildNodes()),
				getNodeValue(AIR, element.getChildNodes()),
				getNodeValue(WOOD, element.getChildNodes()),
				getNodeValue(METAL, element.getChildNodes()),
				getNodeValue(AETHER, element.getChildNodes()),
				getNodeValue(VOID, element.getChildNodes())
				);

		return a_e;
	}
	
	public Attack_Evolver getAttackRight(AbstractCreature_Evolver creature)
	{
		Node attack = null;

		if (creature.right_equipped) attack = getNode(creature.attack_right_type, getNode(ATTACK, attribute_root.getChildNodes()).getChildNodes());

		if (attack == null) return null;

		ArrayList<Node> valid = new ArrayList<Node>();
		for (int i = 0; i < attack.getChildNodes().getLength(); i++)
		{
			Node n = attack.getChildNodes().item(i);

			if (n.getNodeType() == Node.TEXT_NODE) continue;

			valid.add(n);
		}

		Node selected = valid.get(ran.nextInt(valid.size()));

		Attack_Evolver a_e = new Attack_Evolver(selected.getNodeName());

		a_e.addDescription(getNodeValue(DESCRIPTION, selected.getChildNodes()));

		a_e.addStats(
				getNodeValue(CALORIES, selected.getChildNodes()),
				getNodeValue(WEIGHT, selected.getChildNodes()),
				getNodeValue(STRENGTH, selected.getChildNodes()),
				getNodeValue(ATTACK_SPEED, selected.getChildNodes()),
				getNodeValue(ATTACK_DIST_MIN, selected.getChildNodes()),
				getNodeValue(ATTACK_DIST_MAX, selected.getChildNodes()),
				getNodeValue(WEAPON_TYPE, selected.getChildNodes()),
				getNodeValue(WEAPON_STYLE, selected.getChildNodes())
				);

		Node element = getNode(ELEMENT, selected.getChildNodes());
		a_e.addElements(
				getNodeValue(PIERCE, selected.getChildNodes()),
				getNodeValue(IMPACT, selected.getChildNodes()),
				getNodeValue(TOUCH, selected.getChildNodes()),
				getNodeValue(FIRE, element.getChildNodes()),
				getNodeValue(WATER, element.getChildNodes()),
				getNodeValue(AIR, element.getChildNodes()),
				getNodeValue(WOOD, element.getChildNodes()),
				getNodeValue(METAL, element.getChildNodes()),
				getNodeValue(AETHER, element.getChildNodes()),
				getNodeValue(VOID, element.getChildNodes())
				);

		return a_e;
	}
}

class EvolverTile
{
	int x;
	int y;

	public EvolverTile(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public boolean food = false;

	public Creature_Evolver creature = null;
}

class Creature_Evolver implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7925461195208826620L;
	public transient static final int FEED_DURATION = 5;
	public transient static final int FOOD_CALORIES = 200;

	public transient final Random ran = new Random();

	public int consumed_calories = 100;
	public int points = 0;

	public Mind_Evolver mind;
	public Skin_Evolver skin;
	public Bones_Evolver bones;
	public Muscles_Evolver muscles;
	public Attack_Evolver attack_right;
	public Attack_Evolver attack_left;

	public final AbstractCreature_Evolver creature;

	public final int calorie_usage_base;

	public int calorie_usage;
	public int health;
	public int weight;
	public int strength;

	public HashMap<Element, Integer> ele_defenses;
	public HashMap<Damage_Type, Integer> dam_defenses;

	public float atk_cooldown_right;
	public float atk_cooldown_left;
	public int current_health;

	/**
	 * up, down, left, right
	 */
	public transient boolean[] rotation = new boolean[4];

	public transient int x;
	public transient int y;

	public Creature_Evolver(AbstractCreature_Evolver creature, int calorie_usage, int x, int y)
	{
		this.x = x;
		this.y = y;
		this.calorie_usage_base = calorie_usage+creature.base_calories;
		this.creature = creature;

		rotation[ran.nextInt(4)] = true;
	}

	transient int feedCountdown = 0;
	public void update(EvolverTile[][] grid)
	{
//		if (calorie_usage/10 > consumed_calories)
//		{
//			consumed_calories = 0;
//			current_health -= calorie_usage/10;
//
//			if (current_health < 0)
//			{
//				//System.out.println("Creature died of Starvation!");
//				//System.out.println(this);
//				grid[x][y].creature = null;
//				return;
//			}
//		}
//		else
//		{
//			consumed_calories -= calorie_usage/10;
//		}

		if (feedCountdown > 0)
		{
			feedCountdown--;

			if (feedCountdown == 0)
			{
				points++;
				consumed_calories += FOOD_CALORIES;
				grid[x][y].food = false;
			}

			return;
		}

		if (grid[x][y].food)
		{
			feedCountdown = FEED_DURATION;
		}

		int[] move = mind.ai.evaluate(grid, this);

		if (move[0] == 0 && move[1] == 0) return;
		if (x+move[0] < 0 || x+move[0] >= grid.length ||
				y+move[1] < 0 || y+move[1] >= grid[0].length)
		{
			return;
		}

		grid[x][y].creature = null;

		x += move[0];
		y += move[1];

		if (move[0] == 1) {
			rotation[0] = false;
			rotation[1] = false;
			rotation[2] = false;
			rotation[3] = true;
		}
		else if (move[0] == -1) {
			rotation[0] = false;
			rotation[1] = false;
			rotation[2] = true;
			rotation[3] = false;
		}
		else if (move[1] == 1) {
			rotation[0] = true;
			rotation[1] = false;
			rotation[2] = false;
			rotation[3] = false;
		}
		else if (move[1] == -1) {
			rotation[0] = false;
			rotation[1] = true;
			rotation[2] = false;
			rotation[3] = false;
		}

		if (grid[x][y].creature == null) grid[x][y].creature = this;
		else
		{
			Evolver_Combat combat = new Evolver_Combat(this, grid[x][y].creature);

			if (combat.didC1WIN())
			{
				// kill
				if (grid[x][y].creature.current_health <= 0)
				{
					//System.out.println("Died!");
					//System.out.println(grid[x][y].creature);
					points += grid[x][y].creature.points;
					consumed_calories += grid[x][y].creature.consumed_calories;
					grid[x][y].creature = this;
				}
				// flee
				else
				{
					if (checkFree(grid, x+1, y))
					{
						grid[x][y].creature.x += 1;
						grid[x+1][y].creature = grid[x][y].creature;
						grid[x][y].creature = this;
					}
					else if (checkFree(grid, x-1, y))
					{
						grid[x][y].creature.x -= 1;
						grid[x-1][y].creature = grid[x][y].creature;
						grid[x][y].creature = this;
					}
					else if (checkFree(grid, x, y+1))
					{
						grid[x][y].creature.y += 1;
						grid[x][y+1].creature = grid[x][y].creature;
						grid[x][y].creature = this;
					}
					else if (checkFree(grid, x, y-1))
					{
						grid[x][y].creature.y -= 1;
						grid[x][y-1].creature = grid[x][y].creature;
						grid[x][y].creature = this;
					}
					else
					{
						points += grid[x][y].creature.points;
						consumed_calories += grid[x][y].creature.consumed_calories;
						grid[x][y].creature = this;
					}
				}
			}
			else
			{
				// kill
				if (current_health <= 0)
				{
					//System.out.println("Died!");
					//System.out.println(this);
					grid[x][y].creature.points += points;
					grid[x][y].creature.consumed_calories += consumed_calories;
				}
				else
				{
					if (checkFree(grid, x+1, y))
					{
						x += 1;
						grid[x][y].creature = this;
					}
					else if (checkFree(grid, x-1, y))
					{
						x -= 1;
						grid[x][y].creature = this;
					}
					else if (checkFree(grid, x, y+1))
					{
						y += 1;
						grid[x][y].creature = this;
					}
					else if (checkFree(grid, x, y-1))
					{
						y -= 1;
						grid[x][y].creature = this;
					}
					else
					{
						grid[x][y].creature.points += points;
						grid[x][y].creature.consumed_calories += consumed_calories;
					}
				}
			}
		}
	}

	private boolean checkFree(EvolverTile[][] grid, int x, int y)
	{
		//System.out.println("Check free evolver width/height "+ grid.length + "   " + grid[0].length);
		if (x < 0 || x >= grid.length-1) return false;
		if (y < 0 || y >= grid[0].length-1) return false;
		if (grid[x][y].creature != null) return false;

		return true;
	}

	public void addAttributes(Mind_Evolver mind, Skin_Evolver skin, Bones_Evolver bones, Muscles_Evolver muscles, Attack_Evolver attack_right, Attack_Evolver attack_left)
	{
		this.mind = mind;
		this.skin = skin;
		this.bones = bones;
		this.muscles = muscles;
		this.attack_right = attack_right;
		this.attack_left = attack_left;

		calculateStats();

		current_health = health;
		if (attack_right != null) atk_cooldown_right = attack_right.atk_speed;
		if (attack_left != null) atk_cooldown_left = attack_left.atk_speed;
	}

	public void calculateStats()
	{
		calorie_usage = (int) ((skin.calories*creature.skin_scale) + (bones.calories*creature.bones_scale) + (muscles.calories*creature.muscles_scale));
		if (attack_right != null) calorie_usage += (attack_right.calories*creature.attack_right_scale);
		if (attack_left != null) calorie_usage += (attack_left.calories*creature.attack_left_scale);

		health = creature.health + skin.health + bones.health;

		weight = creature.weight + skin.weight + bones.weight + muscles.weight;
		if (attack_right != null) weight += attack_right.weight;
		if (attack_left != null) weight += attack_left.weight;

		strength = creature.strength + skin.strength + muscles.strength;

		ele_defenses = new HashMap<Element, Integer>();
		Element element = Element.FIRE;
		ele_defenses.put(element, creature.eleDef.get(element) + skin.eleDef.get(element));
		element = Element.WATER;
		ele_defenses.put(element, creature.eleDef.get(element) + skin.eleDef.get(element));
		element = Element.AIR;
		ele_defenses.put(element, creature.eleDef.get(element) + skin.eleDef.get(element));
		element = Element.WOOD;
		ele_defenses.put(element, creature.eleDef.get(element) + skin.eleDef.get(element));
		element = Element.METAL;
		ele_defenses.put(element, creature.eleDef.get(element) + skin.eleDef.get(element));
		element = Element.AETHER;
		ele_defenses.put(element, creature.eleDef.get(element) + skin.eleDef.get(element));
		element = Element.VOID;
		ele_defenses.put(element, creature.eleDef.get(element) + skin.eleDef.get(element));

		dam_defenses = new HashMap<Damage_Type, Integer>();
		Damage_Type dam_type = Damage_Type.PIERCE;
		dam_defenses.put(dam_type, creature.damDef.get(dam_type) + skin.damDef.get(dam_type) + bones.damDef.get(dam_type));
		dam_type = Damage_Type.IMPACT;
		dam_defenses.put(dam_type, creature.damDef.get(dam_type) + skin.damDef.get(dam_type) + bones.damDef.get(dam_type));
		dam_type = Damage_Type.TOUCH;
		dam_defenses.put(dam_type, creature.damDef.get(dam_type) + skin.damDef.get(dam_type) + bones.damDef.get(dam_type));
	}

	public Creature_Evolver cpy()
	{
		Creature_Evolver c_e = new Creature_Evolver(creature, calorie_usage_base-creature.base_calories, 0, 0);
		c_e.addAttributes(mind, skin, bones, muscles, attack_right, attack_left);
		
		return c_e;
	}

	public void matchAttributes(Creature_Evolver creature)
	{
		creature.addAttributes(mind, skin, bones, muscles, attack_right, attack_left);
	}
	
	public void nextEvolution(Creature_Evolver c_e, MonsterEvolver evolver)
	{
		Skin_Evolver nSkin = skin;
		Bones_Evolver nBones = bones;
		Muscles_Evolver nMuscles = muscles;
		Attack_Evolver nLeft_Attack = attack_left;
		Attack_Evolver nRight_Attack = attack_right;
		
		for (int i = 0; i < 2; i++)
		{
		int change = ran.nextInt(4);
		
		if (change == 0)
		{
			nSkin = evolver.getSkin(c_e.creature);
		}
		else if (change == 1)
		{
			nBones = evolver.getBones(c_e.creature);
		}
		else if (change == 2)
		{
			nMuscles = evolver.getMuscles(c_e.creature);
		}
		else
		{
			nLeft_Attack = evolver.getAttackLeft(c_e.creature);
			nRight_Attack = evolver.getAttackRight(c_e.creature);
		}
		}
		
		c_e.addAttributes(mind, nSkin, nBones, nMuscles, nRight_Attack, nLeft_Attack);
	}

	@Override
	public String toString()
	{
		return 
				"--------------------------------------------" + "\n" +
				"Creature name: " + creature.name + "\n" +
				"Points earned: " + points + "\n" +
				"Total Calories: " + calorie_usage + "\n" +
				"Max Health: " + health + "\n" +
				"Weight: " + weight + "\n" +
				"Strength: " + strength + "\n" +
				"Mind: " + mind + "\n" +
				"Skin: " + skin + "\n" +
				"Bones: " + bones + "\n" +
				"Muscles: " + muscles + "\n" +
				"Attack Left: " + attack_left + "\n" +
				"Attack Right: " +attack_right + "\n" +
				"--------------------------------------------"
				;
	}
}

class AbstractCreature_Evolver implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5735799594872194576L;
	public final String name;
	public final ArrayList<String> factions;

	public AbstractCreature_Evolver(String name, ArrayList<String> factions)
	{
		this.factions = factions;
		this.name = name;
	}

	transient NodeList description; String model_type; String model_name; float model_scale; String texture; Colour colour;
	public void addVisual(NodeList description, String model_type, String model_name, String model_scale, String texture, String r, String g, String b)
	{
		this.description = description;
		this.model_type = model_type;
		this.model_name = model_name;
		this.model_scale = Float.parseFloat(model_scale);
		this.texture = texture;
		this.colour = new Colour(Float.parseFloat(r), Float.parseFloat(g), Float.parseFloat(b), 1.0f);
	}

	int base_calories; int weight; int health; int strength;
	public void addStats(String base_calories, String weight, String health, String strength)
	{
		this.base_calories = Integer.parseInt(base_calories);
		this.weight = Integer.parseInt(weight);
		this.health = Integer.parseInt(health);
		this.strength = Integer.parseInt(strength);
	}

	HashMap<Element, Integer> eleDef;
	public void addEleDef(String fire, String water, String air, String wood, String metal, String aether, String VOID)
	{
		eleDef = new HashMap<Element, Integer>();

		eleDef.put(Element.FIRE, Integer.parseInt(fire));
		eleDef.put(Element.WATER, Integer.parseInt(water));
		eleDef.put(Element.AIR, Integer.parseInt(air));
		eleDef.put(Element.WOOD, Integer.parseInt(wood));
		eleDef.put(Element.METAL, Integer.parseInt(metal));
		eleDef.put(Element.AETHER, Integer.parseInt(aether));
		eleDef.put(Element.VOID, Integer.parseInt(VOID));
	}

	HashMap<Damage_Type, Integer> damDef;
	public void addDamDef(String pierce, String impact, String touch)
	{
		damDef = new HashMap<Damage_Type, Integer>();

		damDef.put(Damage_Type.PIERCE, Integer.parseInt(pierce));
		damDef.put(Damage_Type.IMPACT, Integer.parseInt(impact));
		damDef.put(Damage_Type.TOUCH, Integer.parseInt(touch));

	}

	float mind_scale; String mind_type;
	public void addMind(String scale, String type)
	{
		this.mind_scale = Float.parseFloat(scale);
		this.mind_type = type;
	}

	float skin_scale; String skin_type; ArrayList<Component> skinDrops;
	public void addSkin(String scale, String type, ArrayList<Component> drops)
	{
		this.skin_scale = Float.parseFloat(scale);
		this.skin_type = type;
		this.skinDrops = drops;
	}

	float bones_scale; String bones_type; ArrayList<Component> boneDrops;
	public void addBones(String scale, String type, ArrayList<Component> drops)
	{
		this.bones_scale = Float.parseFloat(scale);
		this.bones_type = type;
		this.boneDrops = drops;
	}

	float muscles_scale; String muscles_type;
	public void addMuscles(String scale, String type)
	{
		this.muscles_scale = Float.parseFloat(scale);
		this.muscles_type = type;
	}

	float attack_right_scale; String attack_right_type; boolean right_equipped = false; ArrayList<Component> attRDrops;
	public void addAttack_Right(String scale, String type, ArrayList<Component> drops)
	{
		this.right_equipped = true;
		this.attack_right_scale = Float.parseFloat(scale);
		this.attack_right_type = type;
		this.attRDrops = drops;
	}

	float attack_left_scale; String attack_left_type; boolean left_equipped = false; ArrayList<Component> attLDrops;
	public void addAttack_Left(String scale, String type, ArrayList<Component> drops)
	{
		this.left_equipped = true;
		this.attack_left_scale = Float.parseFloat(scale);
		this.attack_left_type = type;
		this.attLDrops = drops;
	}
}

class Mind_Evolver implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2447742508198331316L;
	String name;
	public Mind_Evolver(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name + "\n" + ai;
	}

	public enum AI_Evolver {
		VFFG
	}

	AI_Evolver ai_type;
	AI_Evolver_Package ai;

	public void addAI(String ai)
	{
		Random ran = new Random();
		if (ai.equalsIgnoreCase("VFFG"))
		{
			this.ai_type = AI_Evolver.VFFG;
			this.ai = new AI_Evolver_VFFG(ran.nextInt(101), ran.nextInt(101), ran.nextInt(101), ran.nextInt(101));
		}
	}
}

class Skin_Evolver implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8441346867369627207L;
	String name;
	public Skin_Evolver(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	String description;
	public void addDescription(String description)
	{
		this.description = description;
	}

	int calories; int weight; int health; int strength;
	public void addStats(String calories, String weight, String health, String strength)
	{
		this.calories = Integer.parseInt(calories);
		this.weight = Integer.parseInt(weight);
		this.health = Integer.parseInt(health);
		this.strength = Integer.parseInt(strength);
	}

	HashMap<Element, Integer> eleDef;
	public void addEleDef(String fire, String water, String air, String wood, String metal, String aether, String VOID)
	{
		eleDef = new HashMap<Element, Integer>();

		eleDef.put(Element.FIRE, Integer.parseInt(fire));
		eleDef.put(Element.WATER, Integer.parseInt(water));
		eleDef.put(Element.AIR, Integer.parseInt(air));
		eleDef.put(Element.WOOD, Integer.parseInt(wood));
		eleDef.put(Element.METAL, Integer.parseInt(metal));
		eleDef.put(Element.AETHER, Integer.parseInt(aether));
		eleDef.put(Element.VOID, Integer.parseInt(VOID));
	}

	HashMap<Damage_Type, Integer> damDef;
	public void addDamDef(String pierce, String impact, String touch)
	{
		damDef = new HashMap<Damage_Type, Integer>();

		damDef.put(Damage_Type.PIERCE, Integer.parseInt(pierce));
		damDef.put(Damage_Type.IMPACT, Integer.parseInt(impact));
		damDef.put(Damage_Type.TOUCH, Integer.parseInt(touch));

	}
}

class Bones_Evolver implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2354864208762217473L;
	String name;
	public Bones_Evolver(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	int calories; int weight; int health;
	public void addStats(String calories, String weight, String health)
	{
		this.calories = Integer.parseInt(calories);
		this.weight = Integer.parseInt(weight);
		this.health = Integer.parseInt(health);
	}

	HashMap<Damage_Type, Integer> damDef;
	public void addDamDef(String pierce, String impact, String touch)
	{
		damDef = new HashMap<Damage_Type, Integer>();

		damDef.put(Damage_Type.PIERCE, Integer.parseInt(pierce));
		damDef.put(Damage_Type.IMPACT, Integer.parseInt(impact));
		damDef.put(Damage_Type.TOUCH, Integer.parseInt(touch));

	}
}

class Muscles_Evolver implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4784108714291815658L;
	String name;
	public Muscles_Evolver(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	int calories; int weight; int strength;
	public void addStats(String calories, String weight, String strength)
	{
		this.calories = Integer.parseInt(calories);
		this.weight = Integer.parseInt(weight);
		this.strength = Integer.parseInt(strength);
	}
}

class Attack_Evolver implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6632103730316200474L;
	String name;
	public Attack_Evolver(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	String description;
	public void addDescription(String description)
	{
		this.description = description;
	}

	int calories; int weight; int strength; float atk_speed; float atk_dst_min; float atk_dst_max; WeaponType wep_type; String wep_style;
	public void addStats(String calories, String weight, String strength, String atk_speed, String atk_dst_min, String atk_dst_max, String wep_type, String wep_style)
	{
		this.calories = Integer.parseInt(calories);
		this.weight = Integer.parseInt(weight);
		this.strength = Integer.parseInt(strength);
		this.atk_speed = Float.parseFloat(atk_speed);
		this.atk_dst_min = Float.parseFloat(atk_dst_min);
		this.atk_dst_max = Float.parseFloat(atk_dst_max);
		this.wep_type = Equipment_HAND.convertStringtoWepType(wep_type);
		this.wep_style = wep_style;
	}

	public HashMap<Element, Integer> ele_amount = new HashMap<Element, Integer>();
	public HashMap<Damage_Type, Integer> dam_amount = new HashMap<Damage_Type, Integer>();
	public void addElements(String pierce, String impact, String touch, String FIRE, String WATER, String AIR, String WOOD, String METAL, String AETHER, String VOID)
	{
		dam_amount.put(Damage_Type.PIERCE, Integer.parseInt(pierce));
		dam_amount.put(Damage_Type.IMPACT, Integer.parseInt(impact));
		dam_amount.put(Damage_Type.TOUCH, Integer.parseInt(touch));
		ele_amount.put(Element.FIRE, Integer.parseInt(FIRE));
		ele_amount.put(Element.WATER, Integer.parseInt(WATER));
		ele_amount.put(Element.AIR, Integer.parseInt(AIR));
		ele_amount.put(Element.WOOD, Integer.parseInt(WOOD));
		ele_amount.put(Element.METAL, Integer.parseInt(METAL));
		ele_amount.put(Element.AETHER, Integer.parseInt(AETHER));
		ele_amount.put(Element.VOID, Integer.parseInt(VOID));
	}
}

abstract class AI_Evolver_Package implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6268009811093340625L;
	public transient static final int SIGHT_DIST = 4;

	public abstract int[] evaluate(EvolverTile[][] grid, Creature_Evolver entity);
	public abstract int evaluateCombatAttack();
	public abstract int evaluateCombatFlee();
	public abstract String getToString();

	@Override
	public String toString()
	{
		return getToString();
	}

	transient int[] array = new int[2];
	transient int view = SIGHT_DIST;
	transient int startx = 0;
	transient int starty = 0;
	transient int x = startx;
	transient int y = starty;
	public ArrayList<EvolverTile> getInterestingTiles(EvolverTile[][] grid, Creature_Evolver entity)
	{
		ArrayList<EvolverTile> interesting = new ArrayList<EvolverTile>();

		view = SIGHT_DIST;

		startx = entity.x;
		starty = entity.y;

		x = startx;
		y = starty;

		for (int i = 0; i < view; i++)
		{
			int[] move = iterateDir(entity.rotation, array);
			x += move[0];
			y += move[1];

			if (x < 0 || x >= grid.length
					|| y < 0 || y >= grid[0].length) break;

			EvolverTile tile = grid[x][y];

			if (tile.food) interesting.add(tile);
			else if (tile.creature != null) interesting.add(tile);
		}

		view = SIGHT_DIST;
		startx = entity.x;
		starty = entity.y;
		for (int j = 0; j < SIGHT_DIST; j++)
		{
			int[] offset = iterateFOVSide1(entity.rotation, array);

			startx += offset[0];
			starty += offset[1];

			x = startx;
			y = starty;

			view -= 1;

			for (int i = 0; i < view; i++)
			{
				int[] move = iterateDir(entity.rotation, array);
				x += move[0];
				y += move[1];

				if (x < 0 || x >= grid.length
						|| y < 0 || y >= grid[0].length) break;

				EvolverTile tile = grid[x][y];

				if (tile.food) interesting.add(tile);
				else if (tile.creature != null) interesting.add(tile);
			}
		}

		view = SIGHT_DIST;
		startx = entity.x;
		starty = entity.y;
		for (int j = 0; j < SIGHT_DIST; j++)
		{
			int[] offset = iterateFOVSide2(entity.rotation, array);

			startx += offset[0];
			starty += offset[1];

			x = startx;
			y = starty;

			view -= 1;

			for (int i = 0; i < view; i++)
			{
				int[] move = iterateDir(entity.rotation, array);
				x += move[0];
				y += move[1];

				if (x < 0 || x >= grid.length
						|| y < 0 || y >= grid[0].length) break;

				EvolverTile tile = grid[x][y];

				if (tile.food) interesting.add(tile);
				else if (tile.creature != null) interesting.add(tile);
			}
		}

		return interesting;
	}

	private int[] iterateFOVSide1(boolean[] rotation, int[] itr)
	{
		if (rotation[0]) {
			itr[1] = 1;
			itr[0] = 1;
		}
		else if (rotation[1]) {
			itr[1] = -1;
			itr[0] = 1;
		}
		else if (rotation[2]) {
			itr[0] = -1;
			itr[1] = 1;
		}
		else {
			itr[0] = 1;	
			itr[1] = 1;
		}

		return itr;
	}

	private int[] iterateFOVSide2(boolean[] rotation, int[] itr)
	{
		if (rotation[0]) {
			itr[1] = 1;
			itr[0] = -1;
		}
		else if (rotation[1]) {
			itr[1] = -1;
			itr[0] = -1;
		}
		else if (rotation[2]) {
			itr[0] = -1;
			itr[1] = -1;
		}
		else {
			itr[0] = 1;	
			itr[1] = -1;
		}

		return itr;
	}

	private int[] iterateDir(boolean[] rotation, int[] itr)
	{
		if (rotation[0]) itr[1] = 1;
		else if (rotation[1]) itr[1] = -1;
		else if (rotation[2]) itr[0] = -1;
		else itr[0] = 1;	

		return itr;
	}
}

class AI_Evolver_VFFG extends AI_Evolver_Package
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2161904739892927372L;

	int violence; int flee; int feed; int guard;

	public AI_Evolver_VFFG(int violence, int flee, int feed, int guard)
	{
		this.violence = violence;
		this.flee = flee;
		this.feed = feed;
		this.guard = guard;
	}

	public String getToString()
	{
		return 
				"	Violence="+violence+"\n"+
						"	Flee="+flee+"\n"+
								"	Feed="+feed+"\n"+
										"	Guard="+guard+"\n";
	}

	transient int[] target = new int[2];
	transient int wantMagnitude;
	transient int[] returnMove = new int[2];
	transient int diffX = 0;
	transient int diffY = 0;
	@Override
	public int[] evaluate(EvolverTile[][] grid, Creature_Evolver entity) {
		ArrayList<EvolverTile> interesting = super.getInterestingTiles(grid, entity);
		returnMove[0] = 0;
		returnMove[1] = 0;

		if (interesting.size() == 0)
		{
			diffX = (grid.length/2) - entity.x;
			diffY = (grid[0].length/2) - entity.y;

			if (diffX > diffY)
			{
				if (diffX > 0)
				{
					returnMove[0] = 1;
				}
				else
				{
					returnMove[0] = -1;
				}
			}
			else
			{
				if (diffY > 0)
				{
					returnMove[1] = 1;
				}
				else
				{
					returnMove[1] = -1;
				}
			}

			return returnMove;
		}

		wantMagnitude = calculateWantMagnitude(grid[target[0]][target[1]], entity.x, entity.y);
		wantMagnitude += (wantMagnitude/100)*guard;

		for (EvolverTile t : interesting)
		{
			mag = calculateWantMagnitude(t, entity.x, entity.y);

			if (mag > wantMagnitude)
			{
				wantMagnitude = mag;
				target[0] = t.x;
				target[y] = t.y;
			}
		}

		diffX = target[0] - entity.x;
		diffY = target[1] - entity.y;

		if (diffX > diffY)
		{
			if (diffX > 0)
			{
				returnMove[0] = 1;
			}
			else
			{
				returnMove[0] = -1;
			}
		}
		else
		{
			if (diffY > 0)
			{
				returnMove[1] = 1;
			}
			else
			{
				returnMove[1] = -1;
			}
		}

		return returnMove;
	}

	transient int dist;
	transient int mag;
	public int calculateWantMagnitude(EvolverTile tile, int ex, int ey)
	{
		mag = 0;

		dist = ((tile.x - ex) * (tile.x - ex)) + ((tile.y - ey) * (tile.y - ey));

		if (tile.creature != null)
		{
			mag += (dist / 100) * violence;
		}
		if (tile.food)
		{
			mag += (dist / 100) * feed;
		}

		return mag;
	}
	@Override
	public int evaluateCombatAttack() {
		return violence;
	}
	@Override
	public int evaluateCombatFlee() {
		return flee;
	}

}

class Evolver_Combat
{
	public static final int COMBAT_STEPS = 25;
	final Random ran = new Random();
	public static final float STAGE_LENGTH = 25;
	public static final float TIME_STEP = 0.5f;

	Creature_Evolver c1;
	Creature_Evolver c2;

	float c1X = 0;
	float c2X = STAGE_LENGTH;

	public Evolver_Combat(Creature_Evolver c1, Creature_Evolver c2)
	{
		this.c1 = c1;
		this.c2 = c2;
	}

	public boolean didC1WIN()
	{
		for(int i = 0; i < COMBAT_STEPS; i++)
		{
			c1.atk_cooldown_left -= TIME_STEP;
			c2.atk_cooldown_right -= TIME_STEP;
			c1X += evaluateAI(c1, c2, c1X, c2X);
			c2X += evaluateAI(c2, c1, c2X, c1X);

			if (c1.current_health <= 0 )
			{
				//System.out.println("C1 died!");
				return false;
			}
			else if (c1X < 0 || c1X > STAGE_LENGTH)
			{
				//System.out.println("C1 fled!");
				return false;
			}
			else if (c2.current_health <= 0)
			{
				//System.out.println("C2 died!");
				return true;
			}
			else if (c2X < 0 || c2X > STAGE_LENGTH)
			{
				//System.out.println("C2 fled!");
				return true;
			}

		}
		
		return (c1.points > c2.points) ? true : false;
	}

	int v;
	int f;
	int move;
	public int evaluateAI(Creature_Evolver activeCreature, Creature_Evolver opponentCreature, float positionActive, float positionOpponent)
	{
		if (activeCreature.current_health <= 0 || opponentCreature.current_health <= 0) return 0;

		v = ran.nextInt(101) * activeCreature.mind.ai.evaluateCombatAttack() * (opponentCreature.health / opponentCreature.current_health);
		f = ran.nextInt(101) * activeCreature.mind.ai.evaluateCombatFlee() * (activeCreature.health / activeCreature.current_health);

		move = 1;
		if (v > f)
		{
			if (activeCreature.attack_left != null && activeCreature.atk_cooldown_left < 0)
			{
				if (Math.abs(positionActive-positionOpponent) < activeCreature.attack_left.atk_dst_max)
				{
					if (Math.abs(positionActive-positionOpponent) > activeCreature.attack_left.atk_dst_min)
					{
						int damage = GameData.calculateDamage(activeCreature.strength+activeCreature.attack_left.strength,
								activeCreature.attack_left.ele_amount, activeCreature.attack_left.dam_amount,
								opponentCreature.ele_defenses, opponentCreature.dam_defenses);
						opponentCreature.current_health -= damage;

						activeCreature.atk_cooldown_left = activeCreature.attack_left.atk_speed;
					}
					else
					{
						if (positionActive > positionOpponent)
						{
							move = GameData.calculateSpeed(activeCreature.weight, activeCreature.strength);
						}
						else
						{
							move = -GameData.calculateSpeed(activeCreature.weight, activeCreature.strength);
						}
					}
				}
				else
				{
					if (positionActive < positionOpponent)
					{
						move = GameData.calculateSpeed(activeCreature.weight, activeCreature.strength);
					}
					else
					{
						move = -GameData.calculateSpeed(activeCreature.weight, activeCreature.strength);
					}
				}
			}

			if (activeCreature.attack_right != null && activeCreature.atk_cooldown_right < 0)
			{
				if (Math.abs(positionActive-positionOpponent) < activeCreature.attack_right.atk_dst_max)
				{
					if (Math.abs(positionActive-positionOpponent) > activeCreature.attack_right.atk_dst_min)
					{
						int damage = GameData.calculateDamage(activeCreature.strength+activeCreature.attack_right.strength,
								activeCreature.attack_right.ele_amount, activeCreature.attack_right.dam_amount,
								opponentCreature.ele_defenses, opponentCreature.dam_defenses);
						opponentCreature.current_health -= damage;

						activeCreature.atk_cooldown_right = activeCreature.attack_right.atk_speed;
					}
					else
					{
						if (positionActive > positionOpponent)
						{
							move = GameData.calculateSpeed(activeCreature.weight, activeCreature.strength);
						}
						else
						{
							move = -GameData.calculateSpeed(activeCreature.weight, activeCreature.strength);
						}
					}
				}
				else
				{
					if (positionActive < positionOpponent)
					{
						move = GameData.calculateSpeed(activeCreature.weight, activeCreature.strength);
					}
					else
					{
						move = -GameData.calculateSpeed(activeCreature.weight, activeCreature.strength);
					}
				}
			}
		}
		else
		{
			if (positionActive > positionOpponent)
			{
				return GameData.calculateSpeed(activeCreature.weight, activeCreature.strength);
			}
			else
			{
				return -GameData.calculateSpeed(activeCreature.weight, activeCreature.strength);
			}
		}

		return move;
	}
}
