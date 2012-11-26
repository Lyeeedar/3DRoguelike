package com.lyeeedar.Roguelike3D.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
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
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Roguelike3D.Graphics.Materials.GlowAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;


public class GameData {

	public enum Elements {
		PHYSICAL,
		FIRE,
		WATER,
		AIR
	}
	
	public static LightManager lightManager;
	
	public static Level level;
	public static LevelGraphics levelGraphics = new LevelGraphics();
	
	public static Player player;
	
	public static ArrayList<GameActor> actors;

	public static float gravity = 0.1f;
	
	public static void createNewLevel()
	{
		BiomeReader biome = new BiomeReader("Generic");
		
		long time = System.nanoTime();
		level = new Level(60, 60, biome.getGenerator(), biome);
		System.out.println("Created Level in: "+((System.nanoTime()-time)/1000000000.0f)+"s");
		
		time = System.nanoTime();
		levelGraphics.createLevelGraphics(level.getLevelArray(), level.getColours(), biome);
		System.out.println("Created Level Graphics in: "+((System.nanoTime()-time)/1000000000.0f)+"s");
		
		time = System.nanoTime();

		lightManager = new LightManager(10, LightQuality.VERTEX);
		lightManager.ambientLight.set(biome.getAmbientLight());
		System.out.println("Created Light Manager in: "+((System.nanoTime()-time)/1000000000.0f)+"s");
		

		time = System.nanoTime();
		for (DungeonRoom room : level.rooms)
		{
			if (room.roomtype == RoomType.START)
			{
				player = new Player("model@", new Color(0, 0.6f, 0, 1.0f), "blank", room.x*10+(room.width/2)*10, 3, room.y*10+(room.height/2)*10);
				
				level.addActor(player);
			}
		}
		System.out.println("Created player in: "+((System.nanoTime()-time)/1000000000.0f)+"s");
		
		Random ran = new Random();
		
		time = System.nanoTime();
		for (int i = 0; i < 15; i++)
		{		
			int x = ran.nextInt(55);
			int z = ran.nextInt(55);
			PointLight l = new PointLight(new Vector3(player.position.x+x, 5, player.position.z+z), new Color(0.9f, 0.9f, 0.1f, 1.0f), 0.4f, 8.0f);
			lightManager.addLight(l);
			
			VisibleItem vi = new VisibleItem("model!", new Color(1.0f, 0.9f, 0.1f, 1.0f), "blank", player.position.x+x, 5, player.position.z+z, new Item());
			vi.vo.attributes.material.addAttributes(new GlowAttribute(0.9f, GlowAttribute.glow));
			vi.vo.attributes.material.affectedByLighting = false;
			vi.boundLight = l;
		
			level.addItem(vi);

		}
		System.out.println("Placed Items in: "+((System.nanoTime()-time)/1000000000.0f)+"s");

		
		time = System.nanoTime();
		for (int i = 1; i < 30; i++)
		{
			while (true)
			{
				int x = ran.nextInt(50);
				int z = ran.nextInt(50);
				Vector3 pos = new Vector3(x, 1, z);
				if (!level.checkLevelCollision(pos.x*10, pos.y, pos.z*10))
				{
					Enemy e = new Enemy("modelE", new Color(0.6f, 0.1f, 0.1f, 1.0f), "blank", x*10, 0, z*10);
					
					level.addActor(e);
					x = 51;
					z = 51;
					
					break;
				}
			}
		}
		System.out.println("Placed enemies in: "+((System.nanoTime()-time)/1000000000.0f)+"s");
	}

}
