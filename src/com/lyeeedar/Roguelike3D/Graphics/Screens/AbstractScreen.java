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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.PostProcessing.PostProcessor;
import com.lyeeedar.Roguelike3D.Graphics.PostProcessing.PostProcessor.Effect;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;
 

public abstract class AbstractScreen implements Screen{
	
	int screen_width;
	int screen_height;

	protected final Roguelike3DGame game;

	protected final SpriteBatch spriteBatch;
	protected final DecalBatch decalBatch;
	protected BitmapFont font;
	protected final Stage stage;

	protected PrototypeRendererGL20 protoRenderer;
	protected final PostProcessor postProcessor;
	
	PerspectiveCamera cam;
	
	FPSLogger fps = new FPSLogger();

	public AbstractScreen(Roguelike3DGame game)
	{
		this.game = game;
		
		font = new BitmapFont(Gdx.files.internal("data/skins/default.fnt"), false);
		spriteBatch = new SpriteBatch();
		decalBatch = new DecalBatch();
		decalBatch.setGroupStrategy(new CameraGroupStrategy(cam));

		stage = new Stage(0, 0, true, spriteBatch);
		
		protoRenderer = new PrototypeRendererGL20(GameData.lightManager);
		
		postProcessor = new PostProcessor(Format.RGBA4444, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		postProcessor.addEffect(Effect.BLOOM);
	}

	@Override
	public void render(float delta) {
		
		update(delta);
		stage.act(delta);
	
		if (PostProcessor.ON) postProcessor.begin();
		
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		Gdx.graphics.getGL20().glEnable(GL20.GL_CULL_FACE);
		Gdx.graphics.getGL20().glCullFace(GL20.GL_BACK);
		
		Gdx.graphics.getGL20().glEnable(GL20.GL_DEPTH_TEST);
		Gdx.graphics.getGL20().glDepthMask(true);	
		
		protoRenderer.begin();
		drawModels(delta);
		protoRenderer.end();
		
		Gdx.graphics.getGL20().glDisable(GL20.GL_CULL_FACE);
		
		drawDecals(delta);
		decalBatch.flush();
		
		Gdx.graphics.getGL20().glDisable(GL20.GL_DEPTH_TEST);
		
		if (PostProcessor.ON) postProcessor.end();
		
		spriteBatch.begin();
		drawOrthogonals(delta);
		spriteBatch.end();

		stage.draw();
		
        fps.log();
		
	}

	@Override
	public void resize(int width, int height) {
		
		System.out.println("Screen resized to "+width+" by "+height);
		
		width = GameData.resolution[0];
		height = GameData.resolution[1];
		
		screen_width = width;
		screen_height = height;

        cam = new PerspectiveCamera(75, width, height);
        postProcessor.updateBufferSettings(Format.RGBA4444, width, height);
        cam.near = 0.1f;
        cam.far = 175;
        protoRenderer.cam = cam;
        
        decalBatch.setGroupStrategy(new CameraGroupStrategy(cam));
		
		stage.setViewport( width, height, true);
		
	}

	@Override
	public void dispose() {
		protoRenderer.dispose();
		postProcessor.dispose();

		spriteBatch.dispose();
		font.dispose();
		stage.dispose();
		
		superDispose();

	}
	
	/**
	 * Put all the creation of the objects used by the screen in here to avoid reloading everything on a screenswap
	 */
	public abstract void create();
	/**
	 * Draw models using {@link PrototypeRendererGL20}. Everything drawn in this method will also be passed through the post-processor
	 * @param delta
	 */
	public abstract void drawModels(float delta);
	/**
	 * Draw decals here. Everything drawn in this method will also be passed through the post-processor
	 * @param delta
	 */
	public abstract void drawDecals(float delta);
	/**
	 * Draw sprites using sprite batch. Everything drawn here will NOT be post-processed
	 * @param delta
	 */
	public abstract void drawOrthogonals(float delta);
	/**
	 * Update game logic
	 * @param delta
	 */
	public abstract void update(float delta);
	
	public abstract void superDispose();

}
