package com.lyeeedar.Roguelike3D.Graphics.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameStats;
import com.lyeeedar.Roguelike3D.Game.Item.Component;
import com.lyeeedar.Roguelike3D.Game.Item.Component.Component_Type;
import com.lyeeedar.Roguelike3D.Game.Item.Recipe;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RecipeReader;

public class MainMenuScreen extends AbstractScreen {
	
	Table table;

	public MainMenuScreen(Roguelike3DGame game) {
		super(game);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
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
	public void create() {
		
		Skin skin = new Skin(Gdx.files.internal("data/skins/uiskin.json"));
		
		Label lblTitle = new Label("Askey", skin);
		
		TextButton btnContinue = new TextButton("Continue", skin);
		
		TextButton btnNewGame = new TextButton("New Game", skin);
		btnNewGame.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				GameData.init(game);
				return false;
			}
		});

		
		TextButton btnLoadGame = new TextButton("Load Game", skin);
		TextButton btnOptions = new TextButton("Options", skin);
		TextButton btnCredits = new TextButton("Credits", skin);
		btnCredits.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				game.switchScreen(Roguelike3DGame.CREDITS);
				return false;
			}
		});
		
		TextButton btnExit = new TextButton("Exit", skin);
		btnExit.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				game.ANNIHALATE();
				return false;
			}
		});
		
		TextButton btnTest = new TextButton("Test", skin);
		btnTest.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				RecipeReader reader = new RecipeReader("sword");
				Recipe recipe;
				
				for (int i = 0; i < 10; i++)
				{
					recipe = new Recipe(reader);
					recipe.rarity += i;
					GameStats.addRecipe(recipe);
					recipe = new Recipe(reader);
					recipe.rarity += i;
					GameStats.addRecipe(recipe);
					recipe = new Recipe(reader);
					recipe.rarity += i;
					GameStats.addRecipe(recipe);
					recipe = new Recipe(reader);
					recipe.rarity += i;
					GameStats.addRecipe(recipe);
					
					recipe = new Recipe(reader);
					recipe.rarity += i;
					GameStats.addRecipe(recipe);
				}
				
				for (int i = 1; i < 11; i++)
				{
					Component c = new Component(Component_Type.CLAW, "Claw thingy 1"+i, i, i, "desc", i, 1, i, i, GameData.getElementMap());
					GameStats.addComponent(c);
					c = new Component(Component_Type.CLAW, "Claw thingy 2"+i, i, i, "desc", i, 2, i, i, GameData.getElementMap());
					GameStats.addComponent(c);
					c = new Component(Component_Type.CLAW, "Claw thingy 3"+i, i, i, "desc", i, 3, i, i, GameData.getElementMap());
					GameStats.addComponent(c);
				}
				
				game.switchScreen(Roguelike3DGame.RECIPES);
				return false;
			}
		});

		table = new Table();
		table.debug();
		table.add(lblTitle).center().padBottom(50);
		table.row();
		table.add(btnContinue).width(300).height(50).padBottom(25);
		table.row();
		table.add(btnNewGame).width(300).height(50).padBottom(25);
		table.row();
		table.add(btnLoadGame).width(300).height(50).padBottom(25);
		table.row();
		table.add(btnOptions).width(300).height(50).padBottom(25);
		table.row();
		table.add(btnCredits).width(300).height(50).padBottom(25);
		table.row();
		table.add(btnExit).width(300).height(50).padBottom(25);
		table.row();
		table.add(btnTest).width(300).height(50).padBottom(25);


		table.setFillParent(true);
		stage.addActor(table);	
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
	}

	@Override
	public void superDispose() {
	}

}
