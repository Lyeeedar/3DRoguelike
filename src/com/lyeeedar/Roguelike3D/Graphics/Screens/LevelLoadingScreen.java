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
package com.lyeeedar.Roguelike3D.Graphics.Screens;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.lyeeedar.Graphics.ParticleEffects.ParticleEffect;
import com.lyeeedar.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Roguelike3DGame.GameScreen;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.GameStats;
import com.lyeeedar.Roguelike3D.Game.Actor.Enemy;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND;
import com.lyeeedar.Roguelike3D.Game.Item.Equipment_HAND.WeaponType;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.LevelGraphics;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RoomReader;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Models.SkyBox;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.ForwardRenderer;

public class LevelLoadingScreen extends AbstractScreen{
	
	Texture loading_bar;
	
	ArrayList<GameObject> objects = new ArrayList<GameObject>();
	
	float percent = 0;
	String message = "";
	
	float step;

	Level level;
	LevelGraphics graphics;
	BiomeReader biome;
	RoomReader rReader;
	
	float taskSteps;
	
	GameScreen nextScreen;
	
	int width;
	int height;
	
	public LevelLoadingScreen(Roguelike3DGame game) {
		super(game);
	}
	
	public void setSettings(BiomeReader biome, RoomReader rReader, GameScreen nextScreen)
	{
		this.width = biome.getWidth();
		this.height = biome.getHeight();
		
		this.rReader = rReader;
		this.biome = biome;
		this.nextScreen = nextScreen;
		
		float size = height*1.5f;
		
		taskSteps = 100f/size;
		
		percent = 0;
		message = "";
		
		loadingStage = 0;
	}
	
	long time;
	int loadingStage = 0;
	public void loadingTask()
	{
		if (loadingStage == 0)
		{
			time = System.nanoTime();
			message = "Disposing previous level";
			
			if (GameData.level != null) GameData.level.dispose();
			
			if (GameData.levelGraphics != null) GameData.levelGraphics.dispose();
			
			loadingStage++;
		}
		else if (loadingStage == 1)
		{
			message = "Loading Level";
			
			level = GameData.getCurrentLevelContainer().getLevel(biome, rReader);
			GameData.level = level;
			
			percent += taskSteps;
			
			if (level != null) loadingStage++;
		}
		else if (loadingStage == 2)
		{
			level.create();
			
			level.fixReferences();
			
			Player player = level.getPlayer();
			
			if (player == null)
			{
				player = new Player(new Color(0, 0.6f, 0, 1.0f), "blank", 0, 0, 0, 0.5f, GL20.GL_TRIANGLES, "file", "model@");
				
				HashMap<Damage_Type, Integer> DAM_DEF = new HashMap<Damage_Type, Integer>();
				DAM_DEF.put(Damage_Type.PIERCE, 50);
				DAM_DEF.put(Damage_Type.IMPACT, 50);
				DAM_DEF.put(Damage_Type.TOUCH, 0);

				HashMap<Element, Integer> ELE_DEF = new HashMap<Element, Integer>();
				ELE_DEF.put(Element.FIRE, 0);
				ELE_DEF.put(Element.AIR, 0);
				ELE_DEF.put(Element.WATER, 0);
				ELE_DEF.put(Element.WOOD, 0);
				ELE_DEF.put(Element.METAL, 0);
				ELE_DEF.put(Element.AETHER, 100);
				ELE_DEF.put(Element.VOID, 0);
				
				player.R_HAND = Equipment_HAND.getWeapon(WeaponType.MELEE, "sword", "SWING", 15, ELE_DEF, DAM_DEF, 20, 85, false, 3, 1, level);
				player.L_HAND = Equipment_HAND.getWeapon(WeaponType.RANGED, "torch", "FIREBALL", 15, ELE_DEF, DAM_DEF, 71, 13, false, 3, 1, level);
				
				player.create();
				player.visible = false;
				
				level.addGameActor(player);			
			}
			
			GameStats.setPlayerStats(player);
			
			GameData.player = player;
			
			level.positionPlayer(player, GameData.prevLevel, GameData.currentLevel);
			
			percent += taskSteps;
			loadingStage++;
			
		}
		else if (loadingStage == 3)
		{
			message = "Creating Fundamental Structure";
			graphics = new LevelGraphics(level.levelArray, level.colours, biome, level.hasRoof);
			
			if (GameData.getCurrentLevelContainer().skybox != null)
			{
				GameData.skyBox = new SkyBox(GameData.getCurrentLevelContainer().skybox);
			}
			else GameData.skyBox = null;
			
			loadingStage++;
			percent += taskSteps*5;
		}
		else if (loadingStage == 4)
		{
			message = "Forcing Matter Into Existence";
			boolean done = graphics.createTileRow();
			percent += taskSteps;
			
			if (done) loadingStage++;
		}
		else if (loadingStage == 5)
		{
			message = "Coalescing Matter";
			boolean done = graphics.createChunkRow();
			percent += taskSteps;
			
			if (done) loadingStage++;
		}
		else if (loadingStage == 6)
		{
			message = "Baking Lights";
			
			level.getLights(GameData.lightManager);
			level.evaluateUniqueBehaviour(lightManager);

			if (GameData.lightQuality != LightQuality.FORWARD_VERTEX)
			{
				loadingStage++;
				return;
			}
			
			graphics.bakeLights(GameData.lightManager, true);
			level.bakeLights(GameData.lightManager);
			
			loadingStage++;
		}
		else if (loadingStage == 7)
		{
			System.out.println("Level loading done in "+((float)(System.nanoTime()-time)/1000000000f)+"seconds");
			GameData.finishLoading(graphics, GameScreen.INGAME);
			loadingStage++;
		}
		
		if (percent > 100) percent = 100;
	}

	ArrayList<ParticleEmitter> emitters = new ArrayList<ParticleEmitter>();
	@Override
	public void drawModels(float delta) {
		renderer.begin();
		for (GameObject go : objects)
		{
			go.render(renderer, emitters, cam);
		}
		renderer.end(lightManager);
	}
	
	@Override
	public void drawOrthogonals(float delta) {

	}
	
	@Override
	public void drawTransparent(float delta) {
		
		spriteBatch.begin();
		
		spriteBatch.draw(loading_bar, 100, 200, percent*step, 25);
		
		font.drawMultiLine(spriteBatch, message, 250, 175);
		
		spriteBatch.end();
	}


	float xrotate = 360f/560f;
	@Override
	public void update(float delta) {
		
		loadingTask();

		cam.update();
	}
	
	LightManager lightManager;

	@Override
	public void create() {
		
		lightManager = new LightManager(4, LightQuality.FORWARD_VERTEX);
		
		renderer = new ForwardRenderer();
		renderer.createShader(lightManager);

		GameObject go = new Enemy(new Color(), "icon", 0, 0, -4, 0.5f, GL20.GL_TRIANGLES, "cube", "2", "2", "2");
		go.create();
		go.bakeLights(lightManager, true);

		objects.add(go);
		
		loading_bar = new Texture(Gdx.files.internal("data/skins/loading_bar.png"));
	}
	
	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);
		
		step = (width-200)/100;
	}

	@Override
	public void hide() {

	}
	
	@Override
	public void show()
	{
		Gdx.input.setCursorCatched(false);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
	}

	@Override
	public void superDispose() {
	}
}
