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
package com.lyeeedar.Roguelike3D.Game;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.LevelContainer;
import com.lyeeedar.Roguelike3D.Game.Level.LevelGraphics;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.XML.MonsterEvolver;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RoomReader;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.PlayerPlacer;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.Stair;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;


public class GameData {
	
	/**
	 * Cycle of elements:
	 * 
	 * Destruction - 
	 * 		FIRE melts METAL
	 * 		METAL cuts WOOD
	 * 		WOOD funnels AIR
	 * 		AIR evaporates WATER
	 * 		WATER douses FIRE
	 * 
	 * 		VOID consumes ALL (except AETHER)
	 * 
	 * Creation - 
	 * 		FIRE excites AIR
	 * 		AIR polishes METAL
	 * 		METAL carries WATER
	 * 		WATER nourishes WOOD
	 * 		WOOD feeds FIRE
	 * 
	 * 		AETHER creates ALL (except VOID)
	 * 
	 * @author Philip
	 */
	public enum Element {
		FIRE,
		METAL,
		WOOD,
		AIR,
		WATER,
		AETHER,
		VOID
	}
	
	/**
	 * PIERCE = Armour piercing. (e.g. A Spear)
	 * IMPACT = Launching (e.g. A Hammer)
	 * TOUCH = Effect on touch (e.g. Elemental attacks)
	 * @author Philip
	 */
	public enum Damage_Type {
		PIERCE,
		IMPACT,
		TOUCH
	}
	
	public static LightQuality lightQuality = LightQuality.NORMALMAP;
	
	public static LightManager lightManager;
	
	public static Level level;
	public static LevelGraphics levelGraphics;
	
	public static Player player;
	
	public static float gravity = 0.1f;
	
	public static ArrayList<ParticleEmitter> particleEmitters = new ArrayList<ParticleEmitter>();
	
	public static ArrayList<LevelContainer> dungeon = new ArrayList<LevelContainer>();
	public static LevelContainer currentLevel;
	
	public static Roguelike3DGame game;
	
	public static void init(final Roguelike3DGame game)
	{	
		GameData.game = game;
		
		LevelContainer lc = new LevelContainer("start_town", 1);
		
		currentLevel = lc;

		changeLevel(lc);
	}
	
	static String prevLevel;
	public static void changeLevel(LevelContainer lc)
	{
		prevLevel = currentLevel.UID;
		currentLevel = lc;
		BiomeReader biome = new BiomeReader(lc.biome);
		RoomReader rReader = new RoomReader(lc.biome, lc.depth);
		
		lightManager = new LightManager(10, lightQuality);
		lightManager.ambientLight.set(biome.getAmbientLight());
		
		game.loadLevel(biome, rReader, Roguelike3DGame.INGAME);
	}
	
	public static void finishLoading(Level level, LevelGraphics graphics, String nextScreen)
	{
		System.out.println("Finishing loading.");
		
		if (player == null)
		{
			player = new Player("model@", new Color(0, 0.6f, 0, 1.0f), "blank", 0, 0, 0, 1.0f);
		}
		
		for (LevelObject lo : level.levelObjects)
		{
			if (lo instanceof PlayerPlacer)
			{
				player.positionAbsolutely(lo.position.cpy());
				break;
			}
			else if (lo instanceof Stair)
			{
				Stair s = (Stair) lo;
				
				if (s.level_UID.equals(prevLevel))
				{
					player.positionAbsolutely(s.position.cpy().add(0, 4, 0));
					break;
				}
			}
		}
		
		GameData.level = level;
		currentLevel.level = level;
		levelGraphics = graphics;
		
		level.addActor(player);
		
		game.switchScreen(nextScreen);
		
		PointLight l = new PointLight(player.position.cpy(), Color.WHITE, 0.06f, 1.4f);
		player.boundLight = l;
		lightManager.addDynamicLight(l);
	}

	public static LevelContainer getLevel(String UID)
	{
		for (LevelContainer lc : dungeon)
		{
			if (lc.UID.equals(UID)) return lc;
		}
		
		return null;
	}
	
	public static String createLevelUP(String biome)
	{
		if (currentLevel.up_levels.size() > 0 && currentLevel.up_index == 0)
		{
			currentLevel.up_index++;
			return currentLevel.up_levels.get(0).UID;
		}
		currentLevel.up_index++;
		LevelContainer lc = new LevelContainer(biome, currentLevel.depth-1);
		currentLevel.addLevel_UP(lc);
		dungeon.add(lc);
		return lc.UID;
	}
	
	public static String createLevelDOWN(String biome)
	{
		if (currentLevel.down_levels.size() > 0 && currentLevel.down_index == 0)
		{
			currentLevel.down_index++;
			return currentLevel.down_levels.get(0).UID;
		}
		currentLevel.down_index++;
		LevelContainer lc = new LevelContainer(biome, currentLevel.depth+1);
		currentLevel.addLevel_DOWN(lc);
		dungeon.add(lc);
		return lc.UID;
	}
	
	
	public static Element getElement(String eleName)
	{
		Element element = null;
		
		if (eleName.equalsIgnoreCase("FIRE"))
		{
			element = Element.FIRE;
		}
		else if (eleName.equalsIgnoreCase("WATER"))
		{
			element = Element.WATER;
		}
		else if (eleName.equalsIgnoreCase("AIR"))
		{
			element = Element.AIR;
		}
		else if (eleName.equalsIgnoreCase("WOOD"))
		{
			element = Element.WOOD;
		}
		else if (eleName.equalsIgnoreCase("METAL"))
		{
			element = Element.METAL;
		}
		else if (eleName.equalsIgnoreCase("AETHER"))
		{
			element = Element.AETHER;
		}
		else if (eleName.equalsIgnoreCase("VOID"))
		{
			element = Element.VOID;
		}
		
		return element;
	}
	
	public static Damage_Type getDamageType(String type)
	{
		Damage_Type damType = null;
		
		if (type.equalsIgnoreCase("PIERCE"))
		{
			damType = Damage_Type.PIERCE;
		}
		else if (type.equalsIgnoreCase("IMPACT"))
		{
			damType = Damage_Type.IMPACT;
		}
		else if (type.equalsIgnoreCase("TOUCH"))
		{
			damType = Damage_Type.TOUCH;
		}
		
		return damType;
	}
	
	public static int calculateDamage(int strength, 
			HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam, 
			HashMap<Element, Integer> ele_def, HashMap<Damage_Type, Integer> dam_def)
	{
		int damage = 
				(((strength/100)*dam_dam.get(Damage_Type.PIERCE)) * ((100-dam_def.get(Damage_Type.PIERCE))/100)) +
				(((strength/100)*dam_dam.get(Damage_Type.IMPACT)) * ((100-dam_def.get(Damage_Type.IMPACT))/100)) +
				(((strength/100)*dam_dam.get(Damage_Type.TOUCH)) * ((100-dam_def.get(Damage_Type.TOUCH))/100));
		
		int ele_damage = 
				(((damage/100)*ele_dam.get(Element.FIRE)) * ((100-ele_def.get(Element.FIRE))/100)) +
				(((damage/100)*ele_dam.get(Element.WATER)) * ((100-ele_def.get(Element.WATER))/100)) +
				(((damage/100)*ele_dam.get(Element.AIR)) * ((100-ele_def.get(Element.AIR))/100)) +
				(((damage/100)*ele_dam.get(Element.WOOD)) * ((100-ele_def.get(Element.WOOD))/100)) +
				(((damage/100)*ele_dam.get(Element.METAL)) * ((100-ele_def.get(Element.METAL))/100)) +
				(((damage/100)*ele_dam.get(Element.AETHER)) * ((100-ele_def.get(Element.AETHER))/100)) +
				(((damage/100)*ele_dam.get(Element.VOID)) * ((100-ele_def.get(Element.VOID))/100));

		return ele_damage;
	}
	
	public static int calculateSpeed(int weight, int strength)
	{
		return weight / strength;
	}

}
