package com.lyeeedar.Roguelike3D.Graphics.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.Level.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.LevelGraphics;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom.RoomType;
import com.lyeeedar.Roguelike3D.Game.Level.RoomReader;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
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
		
		float size = height*2.0f;
		
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
					GameData.player = new Player("model@", new Color(0, 0.6f, 0, 1.0f), "blank", room.x*10+(room.width/2)*10, 3, room.y*10+(room.height/2)*10);
					
					level.addActor(GameData.player);
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
	public void draw3D(float delta) {
		
		for (GameObject go : objects)
		{
			go.vo.render(protoRenderer);
		}
	}
	
	@Override
	public void draw2D(float delta) {
		
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
		
		VisibleObject vo = VisibleObject.createCuboid(1, 1, 1, GL20.GL_TRIANGLES, Color.WHITE, "icon");
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

}