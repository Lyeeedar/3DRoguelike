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
package com.lyeeedar.Roguelike3D.Graphics.Screens;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.Enemy;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.Level.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.LevelContainer;
import com.lyeeedar.Roguelike3D.Game.Level.LevelGraphics;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom.RoomType;
import com.lyeeedar.Roguelike3D.Game.Level.RoomReader;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;

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
	
	String nextScreen;
	
	int width;
	int height;
	
	public LevelLoadingScreen(Roguelike3DGame game) {
		super(game);
	}
	
	public void setSettings(BiomeReader biome, RoomReader rReader, String nextScreen)
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
	}
	
	int loadingStage = 0;
	public void loadingTask()
	{
		if (loadingStage == 0)
		{
			message = "Planning Everything";
			level = new Level(width, height, biome.getGenerator(), biome);
			loadingStage++;
		}
		else if (loadingStage == 1)
		{
			message = "Filling Rooms";
			boolean done = level.fillRoom(rReader);
			percent += taskSteps;
			
			if (done) loadingStage++;
		}
		else if (loadingStage == 2)
		{
			message = "Creating Fundamental Structure";
			graphics = new LevelGraphics(level.getLevelArray(), level.getColours(), biome);
			loadingStage++;
			percent += taskSteps*5;
		}
		else if (loadingStage == 3)
		{
			message = "Forcing Matter Into Existence";
			boolean done = graphics.createTileRow();
			percent += taskSteps;
			
			if (done) loadingStage++;
		}
		else if (loadingStage == 4)
		{
			message = "Coalescing Matter";
			boolean done = graphics.createChunkRow();
			percent += taskSteps;
			
			if (done) loadingStage++;
		}
		else if (loadingStage == 5)
		{
			message = "Cleaning Up";
			boolean done = graphics.disposeTileRow();
			
			if (done) loadingStage++;
		}
		else if (loadingStage == 6)
		{
			GameData.finishLoading(level, graphics, game, "InGame");
			loadingStage++;
		}
		
		if (percent > 100) percent = 100;
	}

	@Override
	public void drawModels(float delta) {
		
		for (GameObject go : objects)
		{
			go.vo.render(protoRenderer);
		}
	}
	
	@Override
	public void drawOrthogonals(float delta) {

	}
	
	@Override
	public void drawDecals(float delta) {
		
		spriteBatch.begin();
		
		spriteBatch.draw(loading_bar, 100, 200, percent*step, 25);
		
		font.drawMultiLine(spriteBatch, message, 250, 175);
		
		spriteBatch.end();

	}


	float xrotate = 360f/560f;
	@Override
	public void update(float delta) {
		
		loadingTask();

		GameObject go = objects.get(0);
		
		go.positionAbsolutely(0.0f, 1.3f, -4.0f);

		cam.update();
	}

	@Override
	public void create() {
		
		LightManager lightManager = new LightManager(0, LightQuality.VERTEX);
		lightManager.ambientLight.set(1.0f, 1.0f, 1.0f, 1.0f);
		
		protoRenderer = new PrototypeRendererGL20(lightManager);
		protoRenderer.cam = cam;
		
		VisibleObject vo = VisibleObject.createCuboid(2, 2, 2, GL20.GL_TRIANGLES, Color.WHITE, "icon", 0.5f);
		vo.attributes.material.affectedByLighting = false;
		GameObject go = new GameObject(vo, 0, 0, -4, 0.5f);
		
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
