package com.lyeeedar.Roguelike3D.Game;
import java.util.ArrayList;
import java.util.HashMap;

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
		
		for (int i = 0; i < 3; i++)
		{
			Light l = new Light(new Vector3(0.5f+(i/10), 0.5f+(i/7), 0.5f+(i/9)), new Vector3(i*100, 5, i*100), new Vector3(i*100, 5, i*100), 100);
			lights.add(l);
		}
		
		currentLevel.setLevelLights(lights);
		
		VisibleObject vo = VisibleObject.createCuboid(3, 3, 3, new Vector3(0.6f, 0, 0), "blank");
		
		player = new Player("model@", new Vector3(0, 0.6f, 0), "blank", 0, 0, 0);
		//player = new Player(vo, 0, 0, 0);

		for (int x = 10; x < 50; x++)
		{
			for (int y = 10; y < 50; y++)
			{
				if (!currentLevel.checkCollision(x, 0, y, player.getCollisionBox(), ""))
				{
					currentLevel.getLevelArray()[x][y].actors.add(player);
					player.setPosition(new Vector3(x*10, 0, y*10));
					x = 51;
					y = 51;
				}
			}
		}
		
		for (int i = 1; i < 10; i++)
		{
			//Enemy e = new Enemy(vo, 0, 1, 0);
			Enemy e = new Enemy("model@", new Vector3(0.6f, 0, 0), "blank", 0, 0, 0);
			for (int x = 0; x < 50; x += i)
			{
				for (int y = 0; y < 50; y += i)
				{
					if (!currentLevel.checkCollision(x, 0, y, e.getCollisionBox(), ""))
					{
						currentLevel.getLevelArray()[x][y].actors.add(e);
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
