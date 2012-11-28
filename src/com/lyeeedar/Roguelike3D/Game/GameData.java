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
import com.lyeeedar.Roguelike3D.Game.Level.LevelGraphics;
import com.lyeeedar.Roguelike3D.Game.Level.MapGenerator.GeneratorType;
import com.lyeeedar.Roguelike3D.Game.Level.RoomReader;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Roguelike3D.Graphics.Materials.GlowAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.Particle;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;


public class GameData {

	public enum Elements {
		PHYSICAL,
		FIRE,
		WATER,
		AIR
	}
	
	public static LightManager lightManager;
	
	public static Level level;
	public static LevelGraphics levelGraphics;
	
	public static Player player;
	
	public static ArrayList<GameActor> actors;

	public static float gravity = 0.1f;
	
	public static ArrayList<Particle> particles = new ArrayList<Particle>();
	public static ArrayList<ParticleEmitter> particleEmitters = new ArrayList<ParticleEmitter>();
	
	public static void createNewLevel(final Roguelike3DGame game)
	{	
		BiomeReader biome = new BiomeReader("generic");
		RoomReader rReader = new RoomReader("generic");
		game.loadLevel(60, 60, biome, rReader, "InGame");

		lightManager = new LightManager(10, LightQuality.VERTEX);
		lightManager.ambientLight.set(biome.getAmbientLight());
//		System.out.println("Created Light Manager in: "+((System.nanoTime()-time)/1000000000.0f)+"s");
//		
//		time = System.nanoTime();

//		System.out.println("Created player in: "+((System.nanoTime()-time)/1000000000.0f)+"s");
//		
//		Random ran = new Random();
//		
//		time = System.nanoTime();
//		for (int i = 0; i < 15; i++)
//		{		
//			int x = ran.nextInt(55);
//			int z = ran.nextInt(55);
//			PointLight l = new PointLight(new Vector3(player.position.x+x, 5, player.position.z+z), new Color(0.9f, 0.9f, 0.1f, 1.0f), 0.2f, 8.0f);
//			lightManager.addLight(l);
//			
//			VisibleItem vi = new VisibleItem("model!", new Color(1.0f, 0.9f, 0.1f, 1.0f), "blank", player.position.x+x, 5, player.position.z+z, new Item());
//			vi.vo.attributes.material.addAttributes(new GlowAttribute(0.9f, GlowAttribute.glow));
//			vi.vo.attributes.material.affectedByLighting = false;
//			vi.boundLight = l;
//		
//			level.addItem(vi);
//
//		}
//		System.out.println("Placed Items in: "+((System.nanoTime()-time)/1000000000.0f)+"s");
//
//		
//		time = System.nanoTime();
//		for (int i = 1; i < 30; i++)
//		{
//			while (true)
//			{
//				int x = ran.nextInt(50);
//				int z = ran.nextInt(50);
//				Vector3 pos = new Vector3(x, 1, z);
//				if (!level.checkLevelCollision(pos.x*10, pos.y, pos.z*10))
//				{
//					Enemy e = new Enemy("modelE", new Color(0.6f, 0.1f, 0.1f, 1.0f), "blank", x*10, 0, z*10);
//					
//					level.addActor(e);
//					x = 51;
//					z = 51;
//					
//					break;
//				}
//			}
//		}
//		System.out.println("Placed enemies in: "+((System.nanoTime()-time)/1000000000.0f)+"s");
//		
//		time = System.nanoTime();
	}


}
