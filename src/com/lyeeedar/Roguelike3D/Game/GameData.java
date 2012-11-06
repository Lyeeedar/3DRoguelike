package com.lyeeedar.Roguelike3D.Game;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.TestFrame;


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
		currentLevel.createLevelGraphics();
		
		player = new Player("model@", new float[]{0, 1, 0, 1}, "tex#", 0, 0, 0);
		frame = new TestFrame();
		
		for (int x = 0; x < 50; x++)
		{
			for (int y = 0; y < 50; y++)
			{
				if (!currentLevel.checkCollision(x, 0, y))
				{
					//player.setPosition(new Vector3(x*10, 0, y*10));
					return;
				}
			}
		}
	}

}
