package com.lyeeedar.Roguelike3D;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.lyeeedar.Roguelike3D.Game.Level.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.RoomReader;
import com.lyeeedar.Roguelike3D.Graphics.Screens.*;


public class Roguelike3DGame extends Game {

	public HashMap<String, AbstractScreen> screens = new HashMap<String, AbstractScreen>();
	
	String currentScreen;
	
	LevelLoadingScreen loading_screen;
	
	@Override
	public void create() {
		loadScreens();
		//setScreen(screens.get("LoadingScreen"));
		//setScreen(screens.get("InGame"));
	}
	
	private void loadScreens()
	{
		loading_screen = new LevelLoadingScreen(this);
		loading_screen.create();
		screens.put("InGame", new InGameScreen(this));
		
		for (Map.Entry<String, AbstractScreen> entry : screens.entrySet())
		{
			entry.getValue().create();
		}
	}
	
	public void loadLevel(int width, int height, BiomeReader biome, RoomReader rReader, String nextScreen)
	{
		loading_screen.setSettings(width, height, biome, rReader, nextScreen);
		setScreen(loading_screen);
	}

	public void switchScreen(String screen)
	{
		currentScreen = screen;
		setScreen(screens.get(screen));
	}
}
