package com.lyeeedar.Roguelike3D.Graphics.Screens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameStats;
import com.lyeeedar.Roguelike3D.Game.Item.Recipe;

public class RecipeScreen extends UIScreen {
	
	Table table;

	public RecipeScreen(Roguelike3DGame game) {
		super(game);
	}

	ArrayList<Recipe> recipes;
	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
		recipes = new ArrayList<Recipe>();
		for (Entry<Integer, Collection<Recipe>> entry : GameStats.recipes.asMap().entrySet())
		{
			for (Recipe r : entry.getValue())
			{
				recipes.add(r);
			}
		}
		
		recipeIndex = 0;
		createRecipeList();
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
	}
	
	int recipeIndex = 0;
	boolean recipeListMode = true;
	
	public void createRecipeList()
	{

		Skin skin = new Skin(Gdx.files.internal("data/skins/uiskin.json"));
		
		table.clear();
		
		Recipe recipe = recipes.get(recipeIndex);
		Label lblSelectedRecipe = getRecipeText(recipe, skin);
		Button btnSelectedRecipe = new Button(skin);
		btnSelectedRecipe.add(lblSelectedRecipe);
		btnSelectedRecipe.setDisabled(true);
		
		Button up = new Button(skin);
		up.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				recipeIndex--;
				if (recipeIndex < 0) recipeIndex = 0;
				createRecipeList();
				return false;
			}
		});
		ArrayList<Label> tmp = new ArrayList<Label>();
		for (int i = 1; i < 4 && recipeIndex-i >=0; i++)
		{
			recipe = recipes.get(recipeIndex-i);
			Label lbl= getRecipeText(recipe, skin);
			tmp.add(lbl);
		}
		
		for (int i = tmp.size()-1; i >= 0; i--)
		{
			up.add(tmp.get(i));
			up.row();
		}
		
		Button down = new Button(skin);
		down.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				recipeIndex++;
				if (recipeIndex >= recipes.size()) recipeIndex = recipes.size()-1;
				createRecipeList();
				return false;
			}
		});
		for (int i = 1; i < 4 && recipeIndex+i < recipes.size(); i++)
		{
			recipe = recipes.get(recipeIndex+i);
			Label lbl= getRecipeText(recipe, skin);
			down.add(lbl);
			down.row();
		}

		TextButton craft = new TextButton("Craft Recipe", skin);
		
		Table left = new Table();
		
		left.add(up).width(200).height(250);
		left.row();
		left.add(btnSelectedRecipe).width(250).height(50);
		left.row();
		left.add(down).width(200).height(250);
		
		table.add(left).padRight(100).padLeft(100).left();
		table.add(craft);
	}
	
	private Label getRecipeText(Recipe recipe, Skin skin)
	{
		String text = "";
		text += recipe.recipeName;
		text += "       ";
		text += recipe.type;
		
		Label label = new Label(text, skin);
		LabelStyle lblS = new LabelStyle();
		lblS.background = label.getStyle().background;
		lblS.font = label.getStyle().font;
		lblS.fontColor = GameData.getRarity(recipe.rarity).getColour();
		label.setStyle(lblS);
		
		return label;
	}

	@Override
	protected void superSuperDispose() {

	}

	boolean up = false;
	boolean down = false;
	@Override
	public void update(float delta) {
		if (Gdx.input.isKeyPressed(Keys.DOWN) && !down)
		{
			down = true;
			
			if (recipeListMode)
			{
				recipeIndex++;
				if (recipeIndex >= recipes.size()) recipeIndex = recipes.size()-1;
				createRecipeList();
			}
		}
		else if (!Gdx.input.isKeyPressed(Keys.DOWN))
		{
			down = false;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP) && !up)
		{
			if (recipeListMode)
			{
				up = true;
				recipeIndex--;
				if (recipeIndex < 0) recipeIndex = 0;
				createRecipeList();
			}
		}
		else if (!Gdx.input.isKeyPressed(Keys.UP))
		{
			up = false;
		}
	}


}
