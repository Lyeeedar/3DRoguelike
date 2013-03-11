/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL11;
import com.badlogic.gdx.utils.BufferUtils;
import com.lyeeedar.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.LevelContainer;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RoomReader;
import com.lyeeedar.Roguelike3D.Graphics.Screens.*;


public class Roguelike3DGame extends Game {
	
	public enum GameScreen {
		INGAME,
		LEVELLOADING,
		MAINMENU,
		CREDITS,
		RECIPES,
		OPTIONS,
		GAMEMENU,
		INVENTORY
	}

	public HashMap<GameScreen, AbstractScreen> screens = new HashMap<GameScreen, AbstractScreen>();
	
	GameScreen currentScreen;
	
	@Override
	public void create() {

		loadScreens();
		switchScreen(GameScreen.MAINMENU);
	}
	
	private void loadScreens()
	{
		screens.put(GameScreen.LEVELLOADING, new LevelLoadingScreen(this));	
		screens.put(GameScreen.INGAME, new InGameScreen(this));
		screens.put(GameScreen.MAINMENU, new MainMenuScreen(this));
		screens.put(GameScreen.RECIPES, new RecipeScreen(this));
		screens.put(GameScreen.OPTIONS, new OptionsScreen(this));
		screens.put(GameScreen.INVENTORY, new InventoryScreen(this));
		
		for (Map.Entry<GameScreen, AbstractScreen> entry : screens.entrySet())
		{
			entry.getValue().create();
		}
	}
	
	public void loadLevel(BiomeReader biome, RoomReader rReader, GameScreen nextScreen)
	{
		LevelLoadingScreen screen = (LevelLoadingScreen) screens.get(GameScreen.LEVELLOADING);
		screen.setSettings(biome, rReader, nextScreen);
		setScreen(screen);
	}

	public void switchScreen(GameScreen screen)
	{
		currentScreen = screen;
		setScreen(screens.get(screen));
	}
	
	public void ANNIHALATE()
	{
		Gdx.app.exit();
	}
}
