package com.lyeeedar.Roguelike3D.Graphics;

import java.awt.Font;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.ArrayMap;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;

public abstract class GameScreen implements Screen{
	
	int screen_width;
	int screen_height;

	Roguelike3DGame game;

	DecalBatch decalbatch = new DecalBatch();
	SpriteBatch spritebatch = new SpriteBatch();
	BitmapFont font = new BitmapFont();

	public GameScreen(Roguelike3DGame game)
	{
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.3f, 0.4f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

//		Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);
		
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(true);
//		Gdx.gl.glEnable(GL10.GL_COLOR_MATERIAL);
		//Gdx.graphics.getGL10().glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, 1f);

//		if (decals != null && decals.size() != 0)
//		{
//			for (DecalSprite sprite : decals)
//			{
//				batch.add(sprite.sprite);
//			}
//
//			batch.flush();
//		}

		draw(delta);

		update(delta);
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		
		spritebatch.begin();
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		font.draw(spritebatch, ""+Gdx.graphics.getFramesPerSecond(), 20, 580);
		font.draw(spritebatch, "Pos: "+GameData.player.getPosition(), 20, 550);
		font.draw(spritebatch, "Ang: "+GameData.player.getRotation(), 20, 520);
		spritebatch.end();
	}

	@Override
	public void resize(int width, int height) {
		screen_width = width;
		screen_height = height;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	abstract void draw(float delta);
	abstract void update(float delta);

}
