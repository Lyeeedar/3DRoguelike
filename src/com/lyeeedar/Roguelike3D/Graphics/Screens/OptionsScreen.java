package com.lyeeedar.Roguelike3D.Graphics.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Roguelike3DGame.GameScreen;
import com.lyeeedar.Roguelike3D.Game.GameData;

public class OptionsScreen extends UIScreen {

	public OptionsScreen(Roguelike3DGame game) {
		super(game);
	}
	
	Table table;
	Table buttons;
	Table options;

	public void createVideo()
	{
		final Preferences prefs = GameData.getGamePreferences();
		
		options.clear();
		
		Label resolutionLabel = new Label("Resolution ", skin);
		final SelectBox resolutions = new SelectBox(GameData.applicationChanger.getSupportedDisplayModes(), skin);
		resolutions.setSelection(prefs.getString("resolutionX")+"x"+prefs.getString("resolutionY"));

		final CheckBox fullscreen = new CheckBox("Fullscreen", skin);
		fullscreen.setChecked(prefs.getBoolean("fullscreen"));
		
		final CheckBox vsync = new CheckBox("Enable vSync", skin);
		vsync.setChecked(prefs.getBoolean("vSync"));
		
		Label msaaLabel = new Label("MSAA Samples", skin);
		final SelectBox msaa = new SelectBox(new Integer[]{0, 2, 4, 8, 16, 32}, skin);
		msaa.setSelection(""+prefs.getInteger("MSAA-samples"));
		
		Label render = new Label("Renderer Type", skin);
		final SelectBox renderers = new SelectBox(new String[]{"Forward_Vertex", "Deferred"}, skin);
		renderers.setSelection(prefs.getString("Renderer"));
		
		TextButton apply = new TextButton("Apply", skin);
		apply.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

				String selectedResolution = resolutions.getSelection();
				
				int split = selectedResolution.indexOf("x");
				int rX = Integer.parseInt(selectedResolution.substring(0, split));
				int rY = Integer.parseInt(selectedResolution.substring(split+1, selectedResolution.length()));
				
				prefs.putInteger("resolutionX", rX);
				prefs.putInteger("resolutionY", rY);
				
				prefs.putBoolean("fullscreen", fullscreen.isChecked());
				prefs.putBoolean("vSync", vsync.isChecked());

				prefs.putInteger("MSAA-samples", Integer.parseInt(msaa.getSelection()));
				
				prefs.putString("Renderer", renderers.getSelection());
				
				prefs.flush();
				
				GameData.updateApplication();
				
				game.switchScreen(GameScreen.MAINMENU);
				
				return false;
			}
		});	
		TextButton restore = new TextButton("Restore", skin);
		restore.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				createVideo();	
				return false;
			}
		});
		TextButton defaults = new TextButton("Defaults", skin);
		defaults.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				
				prefs.putInteger("resolutionX", 800);
				prefs.putInteger("resolutionY", 600);
				prefs.putBoolean("fullscreen", false);
				prefs.putBoolean("vSync", true);
				prefs.putInteger("MSAA-samples", 16);
				prefs.putString("Renderer", "Deferred");
				
				prefs.flush();
				
				GameData.updateApplication();

				createVideo();
				
				return false;
			}
		});
		
		TextButton nativeRes = new TextButton("Use Native Resolution", skin);
		nativeRes.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				prefs.putBoolean("fullscreen", fullscreen.isChecked());
				prefs.putBoolean("vSync", vsync.isChecked());

				prefs.putInteger("MSAA-samples", Integer.parseInt(msaa.getSelection()));
				
				prefs.putString("Renderer", renderers.getSelection());
				GameData.applicationChanger.setToNativeResolution(prefs);
				createVideo();
				return false;
			}
		});
		
		options.add(resolutionLabel);
		options.add(resolutions);
		options.row();
		options.add(nativeRes);
		options.row();
		options.add(fullscreen);
		options.row();
		options.add(vsync);
		options.row();
		options.add(msaaLabel);
		options.add(msaa);
		options.row();
		options.add(render);
		options.add(renderers);
		options.row();
		options.add(apply);
		options.add(restore);
		options.add(defaults);
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		
		createVideo();
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	protected void createSuper() {
		
		table = new Table();
		stage.addActor(table);
		table.setFillParent(true);
		
		buttons = new Table();
		options = new Table();
		
		table.add(buttons);
		table.row();
		table.add(options);
	}

	@Override
	protected void superSuperDispose() {

	}

	@Override
	public void update(float delta) {
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) game.switchScreen(GameScreen.MAINMENU);
	}

}
