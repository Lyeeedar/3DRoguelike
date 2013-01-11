package com.lyeeedar.Roguelike3D.Graphics.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;

public class CreditsScreen extends AbstractScreen {
	
	int countdown;
	
	Table table;

	public CreditsScreen(Roguelike3DGame game) {
		super(game);
	}

	@Override
	public void show() {
		countdown = 100;
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void create() {
		Texture tex = new Texture(Gdx.files.internal("data/textures/libgdx.png"));
		Image imglbl = new Image(tex);
		
		table = new Table();
		table.add(imglbl);
		
		
		table.setFillParent(true);
		stage.addActor(table);	
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void drawModels(float delta) {
	}

	@Override
	public void drawDecals(float delta) {
	}

	@Override
	public void drawOrthogonals(float delta) {
	}

	@Override
	public void update(float delta) {
		countdown -= delta;
		if (countdown < 0 && Gdx.input.isTouched())
		{
			game.switchScreen(Roguelike3DGame.MAINMENU);
		}
	}

	@Override
	public void superDispose() {
	}

}
