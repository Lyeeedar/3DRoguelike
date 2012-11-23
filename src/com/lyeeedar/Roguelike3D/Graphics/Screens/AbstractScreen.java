package com.lyeeedar.Roguelike3D.Graphics.Screens;

import java.awt.Font;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ArrayMap;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.FullscreenQuad;
import com.lyeeedar.Roguelike3D.Graphics.PostProcessing.PostProcessor;
import com.lyeeedar.Roguelike3D.Graphics.PostProcessing.PostProcessor.Effect;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;
 

public abstract class AbstractScreen implements Screen{
	
	int screen_width;
	int screen_height;

	protected final Roguelike3DGame game;

	protected final SpriteBatch spriteBatch;
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
		stage = new Stage(0, 0, true, spriteBatch);
		
		protoRenderer = new PrototypeRendererGL20(GameData.lightManager);
		
		postProcessor = new PostProcessor(Format.RGBA4444, 800, 600);
		postProcessor.addEffect(Effect.GLOW);
	}

	@Override
	public void render(float delta) {
		
		update(delta);
	
		postProcessor.begin();
		
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		Gdx.graphics.getGL20().glEnable(GL20.GL_CULL_FACE);
		Gdx.graphics.getGL20().glCullFace(GL20.GL_BACK);
		
		Gdx.graphics.getGL20().glEnable(GL20.GL_DEPTH_TEST);
		Gdx.graphics.getGL20().glDepthMask(true);
		
		//Gdx.graphics.getGL20().glEnable(GL20.GL_POLYGON_OFFSET_FILL);
		//Gdx.graphics.getGL20().glPolygonOffset(1.0f, 1.0f);
		
		//Gdx.graphics.getGL20().glDisable(GL20.GL_DITHER);

		draw(delta);
		
		postProcessor.end();

		Gdx.graphics.getGL20().glDisable(GL20.GL_DEPTH_TEST);
		//Gdx.graphics.getGL20().glDepthMask(false);
		
		Gdx.graphics.getGL20().glDisable(GL20.GL_CULL_FACE);
		
		stage.draw();
		
        fps.log();
		
	}

	@Override
	public void resize(int width, int height) {
		screen_width = width;
		screen_height = height;

        cam = new PerspectiveCamera(90, width, height);
        postProcessor.updateBufferSettings(Format.RGBA4444, width, height);
        cam.near = 1.0f;
        cam.far = 200;
        protoRenderer.cam = cam;
		
		stage.setViewport( width, height, true );
	}

	@Override
	public void dispose() {
		protoRenderer.dispose();
		postProcessor.dispose();

		spriteBatch.dispose();
		font.dispose();
		stage.dispose();

	}
	
	@Override
	public void show()
	{
		
	}
	
	public abstract void create();
	public abstract void draw(float delta);
	public abstract void update(float delta);

}
