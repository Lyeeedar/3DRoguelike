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
package com.lyeeedar.Roguelike3D;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.LevelContainer;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RoomReader;
import com.lyeeedar.Roguelike3D.Graphics.Screens.*;


public class Roguelike3DGame extends Game {
	
	public static final String INGAME = "InGame";
	public static final String LEVELLOADING = "LevelLoading";
	public static final String MAINMENU = "MainMenu";
	public static final String CREDITS = "Credits";
	public static final String RECIPES = "Recipes";

	public HashMap<String, AbstractScreen> screens = new HashMap<String, AbstractScreen>();
	
	String currentScreen;
	
	@Override
	public void create() {
		loadScreens();
		switchScreen(MAINMENU);
	}
	
	private void loadScreens()
	{
		screens.put(LEVELLOADING, new LevelLoadingScreen(this));	
		screens.put(INGAME, new InGameScreen(this));
		screens.put(MAINMENU, new MainMenuScreen(this));
		screens.put(CREDITS, new CreditsScreen(this));
		screens.put(RECIPES, new RecipeScreen(this));
		
		for (Map.Entry<String, AbstractScreen> entry : screens.entrySet())
		{
			entry.getValue().create();
		}
	}
	
	public void loadLevel(BiomeReader biome, RoomReader rReader, String nextScreen)
	{
		LevelLoadingScreen screen = (LevelLoadingScreen) screens.get(LEVELLOADING);
		screen.setSettings(biome, rReader, nextScreen);
		setScreen(screen);
	}

	public void switchScreen(String screen)
	{
		currentScreen = screen;
		setScreen(screens.get(screen));
	}
	
	public void ANNIHALATE()
	{
		Gdx.app.exit();
	}
}
