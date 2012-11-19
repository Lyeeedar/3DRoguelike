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
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.LevelGraphics;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
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

	public static float gravity = 0.01f;
	
	public static void createNewLevel()
	{
		level = new Level(50, 50);
		
		levelGraphics.createLevelGraphics(level.getLevelArray(), level.getColours());
		
		ArrayList<PointLight> lights = new ArrayList<PointLight>();
		lightManager = new LightManager(0, LightQuality.VERTEX);
		lightManager.ambientLight.set(0.5f, 0.5f, 0.5f, 1);
		
		Random ran = new Random();
		
		for (int i = 0; i < 15; i++)
		{		
			while (true)
			{
				int x = ran.nextInt(50);
				int z = ran.nextInt(50);
				Vector3 pos = new Vector3(x, 1, z);
				if (!level.checkLevelCollision(pos.x, pos.y, pos.z) && level.getTile(pos.x, pos.z).items.size() == 0)
				{
					PointLight l = new PointLight(new Vector3(i*10, 1, i*10), new Color(1.0f, 0.9f, 0.5f, 1.0f), 0.1f);
					lightManager.addLight(l);
					
					VisibleItem vi = new VisibleItem("model!", new Color(1.0f, 0.9f, 0.1f, 1.0f), "blank", pos.x*10, pos.y, pos.z*10, new Item());
					//vi.boundLight = l;
					level.addItem(pos.x, pos.z, vi);
					
					break;
				}
			}
		}

		player = new Player(Shapes.genCuboid(0.1f, 0.1f, 0.1f), new Color(0, 0.6f, 0, 1.0f), "blank", 0, 0, 0);

		for (int x = 10; x < 50; x++)
		{
			for (int y = 10; y < 50; y++)
			{
				if (!level.checkCollision(x, 0, y, player.getCollisionBox(), ""))
				{
					level.addActor(x, y, player);
					player.positionAbsolutely(new Vector3(x*10, 0, y*10));
//					
//					PointLight l = new PointLight(player.position, new Color(1.0f, 0.9f, 1.0f, 1.0f), 0.1f);
//					lightManager.addLight(l);
					
					//player.boundLight = l;
					
					PointLight l2 = new PointLight(new Vector3(x*10, 1, y+10*10), new Color(0.9f, 0.3f, 0.5f, 1.0f), 0.1f);
					lightManager.addLight(l2);
					
					PointLight l3 = new PointLight(new Vector3(x*10, 1, y+50*10), new Color(0.1f, 0.9f, 0.5f, 1.0f), 0.1f);
					lightManager.addLight(l3);
					
					x = 51;
					y = 51;
					
					
				}
			}
		}
		
		for (int i = 1; i < 10; i++)
		{
			//Enemy e = new Enemy(vo, 0, 1, 0);
			Enemy e = new Enemy("modelE", new Color(0.6f, 0.1f, 0.1f, 1.0f), "blank", 0, 0, 0);
			for (int x = 0; x < 50; x += i)
			{
				for (int y = 0; y < 50; y += i)
				{
					if (!level.checkCollision(x, 0, y, e.getCollisionBox(), ""))
					{
						//level.addActor(x, y, e);
						e.positionAbsolutely(new Vector3(x*10, 2, y*10));
						x = 51;
						y = 51;
					}
				}
			}
		}
	}

}
