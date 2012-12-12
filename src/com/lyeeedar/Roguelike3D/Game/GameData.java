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
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.TestFrame;
import com.lyeeedar.Roguelike3D.Game.Actor.Enemy;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.Item.Item;
import com.lyeeedar.Roguelike3D.Game.Item.VisibleItem;
import com.lyeeedar.Roguelike3D.Game.Level.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom.RoomType;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.LevelContainer;
import com.lyeeedar.Roguelike3D.Game.Level.LevelGraphics;
import com.lyeeedar.Roguelike3D.Game.Level.MapGenerator.GeneratorType;
import com.lyeeedar.Roguelike3D.Game.Level.RoomReader;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.PlayerPlacer;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.Stair;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Roguelike3D.Graphics.Materials.GlowAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.Particle;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;


public class GameData {
	
	/**
	 * Cycle of elements:
	 * 
	 * Negation - 
	 * 		FIRE melts METAL
	 * 		METAL cuts WOOD
	 * 		WOOD funnels AIR
	 * 		AIR evaporates WATER
	 * 		WATER douses FIRE
	 * 
	 * Addition - 
	 * 		FIRE excites AIR
	 * 		AIR polishes METAL
	 * 		METAL carries WATER
	 * 		WATER nourishes WOOD
	 * 		WOOD feeds FIRE
	 * 
	 * @author Philip
	 */
	public enum Element {
		FIRE,
		METAL,
		WOOD,
		AIR,
		WATER
	}
	
	/**
	 * PIERCE = Armour piercing.
	 * IMPACT = Launching
	 * @author Philip
	 */
	public enum Damage_Type {
		PIERCE,
		IMPACT
	}
	
	public enum Weapon_Type {
		CLAW(0.5f, 0.5f),
		FIST(0.3f, 0.7f),
		BLADE(0.9f, 0.1f),
		CLUB(0.0f, 1.0f),
		TEETH(1.0f, 0.0f);
		
		public final float PIERCE;
		public final float IMPACT;
		
		private Weapon_Type(float PIERCE, float IMPACT)
		{
			this.PIERCE = PIERCE;
			this.IMPACT = IMPACT;
		}
	}
	
	public static LightManager lightManager;
	
	public static Level level;
	public static LevelGraphics levelGraphics;
	
	public static Player player;
	
	public static float gravity = 0.1f;
	
	public static ArrayList<ParticleEmitter> particleEmitters = new ArrayList<ParticleEmitter>();
	
	public static ArrayList<LevelContainer> dungeon = new ArrayList<LevelContainer>();
	public static LevelContainer currentLevel;
	
	public static void init(final Roguelike3DGame game)
	{	
		LevelContainer lc = new LevelContainer("start_town", 1);
		
		currentLevel = lc;

		createNewLevel(game, lc);
		
	}
	
	static String prevLevel;
	public static void createNewLevel(final Roguelike3DGame game, LevelContainer lc)
	{
		prevLevel = currentLevel.UID;
		currentLevel = lc;
		BiomeReader biome = new BiomeReader(lc.biome);
		RoomReader rReader = new RoomReader(lc.biome, lc.depth);
		game.loadLevel(biome, rReader, "InGame");

		lightManager = new LightManager(10, LightQuality.VERTEX);
		lightManager.ambientLight.set(biome.getAmbientLight());
	}
	
	public static void finishLoading(Level level, LevelGraphics graphics, Roguelike3DGame game, String nextScreen)
	{
		if (player == null)
		{
			player = new Player("model@", new Color(0, 0.6f, 0, 1.0f), "blank", 0, 0, 0);
			
			level.addActor(player);
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
					player.positionAbsolutely(s.position.cpy());
					break;
				}
			}
		}
		
		GameData.level = level;
		currentLevel.level = level;
		levelGraphics = graphics;
		game.switchScreen(nextScreen);
	}

	public static LevelContainer getLevel(String UID)
	{
		for (LevelContainer lc : dungeon)
		{
			if (lc.UID.equals(UID)) return lc;
		}
		
		return null;
	}
	
	public static String createLevelUP()
	{
		if (currentLevel.up_index == 0)
		{
			currentLevel.up_index++;
			return currentLevel.up_levels.get(0).UID;
		}
		currentLevel.up_index++;
		LevelContainer lc = new LevelContainer(currentLevel.biome, currentLevel.depth-1);
		currentLevel.addLevel_UP(lc);
		dungeon.add(lc);
		return lc.UID;
	}
	
	public static String createLevelDOWN()
	{
		if (currentLevel.down_index == 0)
		{
			currentLevel.down_index++;
			return currentLevel.down_levels.get(0).UID;
		}
		currentLevel.down_index++;
		LevelContainer lc = new LevelContainer(currentLevel.biome, currentLevel.depth+1);
		currentLevel.addLevel_DOWN(lc);
		dungeon.add(lc);
		return lc.UID;
	}

}
