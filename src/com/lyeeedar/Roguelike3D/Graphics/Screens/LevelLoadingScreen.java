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
	
	public void setSettings(int width, int height, BiomeReader biome, RoomReader rReader, String nextScreen)
	{
		this.width = width;
		this.height = height;
		
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
			message = "Positioning You";
			for (DungeonRoom room : level.rooms)
			{
				if (room.roomtype == RoomType.START)
				{
					GameData.player = new Player("model@", new Color(0, 0.6f, 0, 1.0f), "blank", room.x*10+(room.width/2)*10, 4, room.y*10+(room.height/2)*10);
					
					level.addActor(GameData.player);
				}
			}
			Random ran = new Random();
			for (int i = 1; i < 10; i++)
			{
				while (true)
				{
					int x = ran.nextInt(50);
					int z = ran.nextInt(50);
					Vector3 pos = new Vector3(x, 1, z);
					if (!level.checkLevelCollision(pos.x*10, pos.y, pos.z*10))
					{
						Enemy e = new Enemy("modelE", new Color(0.6f, 0.1f, 0.1f, 1.0f), "blank", x*10, 0, z*10);
						e.description = "This is a nasty horrible enemy. It has lots of horrible parts and its really red.";
						level.addActor(e);
						
						x = 51;
						z = 51;
						
						break;
					}
				}
			}
			loadingStage++;
		}
		else if (loadingStage == 7) 
		{
			GameData.level = level;
			GameData.levelGraphics = graphics;
			game.switchScreen(nextScreen);
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
		
		VisibleObject vo = VisibleObject.createCuboid(2, 2, 2, GL20.GL_TRIANGLES, Color.WHITE, "icon");
		vo.attributes.material.affectedByLighting = false;
		GameObject go = new GameObject(vo, 0, 0, -4);
		
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
