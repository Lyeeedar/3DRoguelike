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
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameStats;
import com.lyeeedar.Roguelike3D.Game.Item.Component;
import com.lyeeedar.Roguelike3D.Game.Item.Recipe;

public class RecipeScreen extends UIScreen {
	
	public static final int NUM_RECIPES = 6;

	final Skin skin = new Skin(Gdx.files.internal("data/skins/uiskin.json"));
	
	
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
		recipeListMode = true;
		createRecipeMode();
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
	
	public void createCraftingMode()
	{
		table.clear();
		
		
		DragAndDrop dragAndDrop = new DragAndDrop();
		
		table.add(createCraftingView(dragAndDrop)).pad(50);
		table.add(createComponentView(dragAndDrop)).pad(50);
	}
	
	CraftButton[][] grid;
	private Table createCraftingView(DragAndDrop dragAndDrop)
	{
		Table inner = new Table();
		for (int x = 0; x < grid.length; x++)
		{
			for (int y = 0; y < grid[0].length; y++)
			{
				CraftButton cb = grid[x][y];
				inner.add(cb).width(50).height(50).pad(10);
				dragAndDrop.addTarget(new Target(cb) {
					public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
						getActor().setColor(Color.GREEN);
						return true;
					}

					public void reset (Source source, Payload payload) {
						getActor().setColor(Color.WHITE);
					}

					public void drop (Source source, Payload payload, float x, float y, int pointer) {
						System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
					}
				});
			}
			inner.row();
		}
		
		return inner;
	}
	
	private Table createComponentView(DragAndDrop dragAndDrop)
	{
		Table top = new Table();
		
		Table inner = new Table();
		final ScrollPane scroll = new ScrollPane(inner);
		scroll.setScrollingDisabled(true, false);
		//scroll.setForceOverscroll(false, true);
		System.out.println(scroll.getScrollY());
		
		//top.add(scroll).width(200).height(500).pad(50);
		top.add(inner).width(200).height(500).pad(50);

		for (Component c : components)
		{
			Label l = new Label(c.name+"     "+c.amount+"    "+c.type, skin);
			LabelStyle lblS = new LabelStyle();
			lblS.background = l.getStyle().background;
			lblS.font = l.getStyle().font;
			lblS.fontColor = GameData.getRarity(c.rarity).getColour();
			l.setStyle(lblS);
			
			inner.add(l).pad(10);
			inner.row();
			
			dragAndDrop.addSource(new Source(l) {
				public Payload dragStart (InputEvent event, float x, float y, int pointer) {
					
					//scroll.setScrollingDisabled(true, true);
					
					Payload payload = new Payload();
					payload.setObject("Some payload!");

					payload.setDragActor(new Label("Some payload!", skin));

					Label validLabel = new Label("Some payload!", skin);
					validLabel.setColor(0, 1, 0, 1);
					payload.setValidDragActor(validLabel);

					Label invalidLabel = new Label("Some payload!", skin);
					invalidLabel.setColor(1, 0, 0, 1);
					payload.setInvalidDragActor(invalidLabel);

					return payload;
				}
				
				public void dragStop (InputEvent event, float x, float y, int pointer, Target target) {
					//scroll.setScrollingDisabled(true, false);
				}
			});
		}
		
		return top;
	}
	
	ArrayList<Component> components;
	Recipe recipe;
	public void pickRecipe()
	{
		recipe = recipes.get(recipeIndex);
		recipeListMode = false;
		
		grid = new CraftButton[recipe.visualGrid.length][recipe.visualGrid[0].length];
		
		for (int x = 0; x < recipe.visualGrid[0].length; x++)
		{
			for (int y = 0; y < recipe.visualGrid.length; y++)
			{
				grid[y][x] = new CraftButton(skin, recipe.visualGrid[y][x]);
			}
		}
		
		components = new ArrayList<Component>();
		
		for (Entry<Integer, Collection<Component>> entry : GameStats.components.asMap().entrySet())
		{
			for (Component c : entry.getValue())
			{
				if (recipe.checkAll(c)) components.add(c);
			}
		}
		
		
		createCraftingMode();
	}
	
	int recipeIndex = 0;
	boolean recipeListMode = true;
	
	public void createRecipeMode()
	{
		table.clear();
		
		Recipe recipe = recipes.get(recipeIndex);
		Label lblSelectedRecipe = getRecipeText(recipe, skin);
		Button btnSelectedRecipe = new Button(skin);
		btnSelectedRecipe.add(lblSelectedRecipe);
		btnSelectedRecipe.setDisabled(true);
		
		Button up = new Button(skin);
		up.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				upRecipe();
				return false;
			}
		});
		ArrayList<Label> tmp = new ArrayList<Label>();
		for (int i = 1; i < NUM_RECIPES && recipeIndex-i >=0; i++)
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
				downRecipe();
				return false;
			}
		});
		for (int i = 1; i < NUM_RECIPES && recipeIndex+i < recipes.size(); i++)
		{
			recipe = recipes.get(recipeIndex+i);
			Label lbl= getRecipeText(recipe, skin);
			down.add(lbl);
			down.row();
		}

		final TextButton craft = new TextButton("Craft Recipe", skin);
		craft.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				pickRecipe();
				return false;
			}
		});
		
		Table left = new Table();
		
		left.add(up).width(200).height(250);
		left.row();
		left.add(btnSelectedRecipe).width(250).height(50);
		left.row();
		left.add(down).width(200).height(250);
		
		table.add(left).padRight(100).padLeft(100).left();
		table.add(craft).padRight(100).padLeft(100).right().width(150).height(100);
		
		
		DragAndDrop dragAndDrop = new DragAndDrop();
		dragAndDrop.addSource(new Source(btnSelectedRecipe) {
			public Payload dragStart (InputEvent event, float x, float y, int pointer) {
				Payload payload = new Payload();
				payload.setObject("Some payload!");

				payload.setDragActor(new Label("Some payload!", skin));

				Label validLabel = new Label("Some payload!", skin);
				validLabel.setColor(0, 1, 0, 1);
				payload.setValidDragActor(validLabel);

				Label invalidLabel = new Label("Some payload!", skin);
				invalidLabel.setColor(1, 0, 0, 1);
				payload.setInvalidDragActor(invalidLabel);

				return payload;
			}
		});
		dragAndDrop.addTarget(new Target(craft) {
			public boolean drag (Source source, Payload payload, float x, float y, int pointer) {
				getActor().setColor(Color.GREEN);
				return true;
			}

			public void reset (Source source, Payload payload) {
				getActor().setColor(Color.WHITE);
			}

			public void drop (Source source, Payload payload, float x, float y, int pointer) {
				craft.setDisabled(true);
				System.out.println("Accepted: " + payload.getObject() + " " + x + ", " + y);
			}
		});
	}
	
	private void upRecipe()
	{
		recipeIndex--;
		if (recipeIndex < 0) recipeIndex = 0;
		createRecipeMode();
	}
	
	private void downRecipe()
	{
		recipeIndex++;
		if (recipeIndex >= recipes.size()) recipeIndex = recipes.size()-1;
		createRecipeMode();
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
		skin.dispose();
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
				downRecipe();
			}
		}
		else if (!Gdx.input.isKeyPressed(Keys.DOWN))
		{
			down = false;
		}
		
		if (Gdx.input.isKeyPressed(Keys.UP) && !up)
		{
			up = true;
			if (recipeListMode)
			{
				upRecipe();
			}
		}
		else if (!Gdx.input.isKeyPressed(Keys.UP))
		{
			up = false;
		}
	}


}

class CraftButton extends Button
{
	Component component;
	char reference;
	
	public CraftButton(Skin skin, char reference) {
		super(skin);
		this.reference = reference;
		this.setDisabled(true);
		Label l = new Label(""+reference, skin);
		this.add(l);
	}
	
}
