package com.lyeeedar.Roguelike3D.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.TestFrame;
import com.lyeeedar.Roguelike3D.Graphics.Light;
import com.lyeeedar.Roguelike3D.Graphics.VisibleObject;


public class GameData {
	
	static final String FIRE = "burnyburnyouch";
	static final String WATER = "splashysplish";
	static final String AIR = "windyblowblow";
	
	public static Level currentLevel;
	
	public static Player player;
	
	public static ArrayList<GameActor> actors;
	
	public static TestFrame frame;
	
	public static float gravity = 0.01f;
	
	public static ShaderProgram collisionShader = new ShaderProgram(
			Gdx.files.internal("data/shaders/collision_box.vert").readString(),
            Gdx.files.internal("data/shaders/collision_box.frag").readString()
            );
	
	public static void createNewLevel()
	{
		currentLevel = new Level(50, 50);
		currentLevel.createLevelCave();
		currentLevel.createLevelCave();
		currentLevel.createLevelCave();
		
		currentLevel.clearWalls();
		
		currentLevel.createLevelGraphics();
		
		ArrayList<Light> lights = new ArrayList<Light>();
		
		Random ran = new Random();
		
		for (int i = 0; i < 5; i++)
		{		
			while (true)
			{
				int x = ran.nextInt(50);
				int z = ran.nextInt(50);
				Vector3 pos = new Vector3(x, 1, z);
				if (!currentLevel.checkLevelCollision(pos.x, pos.y, pos.z) && currentLevel.getTile(pos.x, pos.z).items.size() == 0)
				{
//					pos.x *= 10;
//					pos.z += 10;
					Light l = new Light(new Vector3(1.0f, 0.9f, 0.5f), pos, new Vector3(i*100, 5, i*100), 0.2f);
					lights.add(l);
					
					VisibleItem vi = new VisibleItem("model!", new Vector3(1.0f, 0.9f, 0.1f), "blank", pos.x*10, pos.y, pos.z*10, new Item());
					vi.boundLight = l;
					currentLevel.addItem(pos.x, pos.z, vi);
					
					break;
				}
			}
		}
		
		currentLevel.setLevelLights(lights);
		
		VisibleObject vo = VisibleObject.createCuboid(3, 3, 3, new Vector3(0.6f, 0, 0), "blank");
		
		player = new Player(null, new Vector3(0, 0.6f, 0), "blank", 0, 0, 0);
		//player = new Player(vo, 0, 0, 0);

		for (int x = 10; x < 50; x++)
		{
			for (int y = 10; y < 50; y++)
			{
				if (!currentLevel.checkCollision(x, 0, y, player.getCollisionBox(), ""))
				{
					currentLevel.addActor(x, y, player);
					player.setPosition(new Vector3(x*10, 0, y*10));
					lights.get(0).position = new Vector3(x*10, 0, y*10);
					x = 51;
					y = 51;
				}
			}
		}
		
		for (int i = 1; i < 10; i++)
		{
			//Enemy e = new Enemy(vo, 0, 1, 0);
			Enemy e = new Enemy("modelE", new Vector3(0.6f, 0.1f, 0.1f), "blank", 0, 0, 0);
			for (int x = 0; x < 50; x += i)
			{
				for (int y = 0; y < 50; y += i)
				{
					if (!currentLevel.checkCollision(x, 0, y, e.getCollisionBox(), ""))
					{
						//currentLevel.addActor(x, y, e);
						e.setPosition(new Vector3(x*10, 2, y*10));
						x = 51;
						y = 51;
					}
				}
			}
		}
		
		//frame = new TestFrame();
	}

}
