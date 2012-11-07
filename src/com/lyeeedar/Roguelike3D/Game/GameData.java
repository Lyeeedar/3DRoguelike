package com.lyeeedar.Roguelike3D.Game;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.TestFrame;
import com.lyeeedar.Roguelike3D.Graphics.Light;


public class GameData {
	
	static final String FIRE = "burnyburnyouch";
	static final String WATER = "splashysplish";
	static final String AIR = "windyblowblow";
	
	public static Level currentLevel;
	
	public static Player player;
	
	public static ArrayList<GameActor> actors;
	
	public static HashMap<String, Boolean> keys = new HashMap<String, Boolean>();
	
	public static TestFrame frame;
	
	public static void createNewLevel()
	{
		currentLevel = new Level(50, 50);
		currentLevel.createLevelCave();
		currentLevel.createLevelCave();
		currentLevel.createLevelCave();
		
		currentLevel.clearWalls();
		
		currentLevel.createLevelGraphics();
		
		ArrayList<Light> lights = new ArrayList<Light>(); 
		
		for (int i = 0; i < 1; i++)
		{
			Light l = new Light(new Vector3(0.5f+(i/10), 0.5f+(i/7), 0.5f+(i/9)), new Vector3(i*100, 5, i*100), new Vector3(i*100, 5, i*100), 100);
			lights.add(l);
		}
		
		Light l = new Light(new Vector3(0.8f, 0.9f, 0.8f), new Vector3(60, 0, 60), new Vector3(0, 0, 0), 100);
		lights.add(l);
		
		currentLevel.setLevelLights(lights);
		
		player = new Player("model@", new Vector3(1, 1, 1), "blank", 0, 0, 0);
		
		
		currentLevel.getLevelGraphics().add(new Player("model@", new Vector3(1, 1, 1), "tex#", 30, 0, 30));
		
		for (int x = 0; x < 50; x++)
		{
			for (int y = 0; y < 50; y++)
			{
				if (!currentLevel.checkCollision(x, 0, y))
				{
					player.setPosition(new Vector3(x*10, 0, y*10));
					return;
				}
			}
		}
	}

}
