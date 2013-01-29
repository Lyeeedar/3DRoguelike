package com.lyeeedar.Roguelike3D.Graphics.Screens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import com.lyeeedar.Roguelike3D.Game.Item.Item;
import com.lyeeedar.Roguelike3D.Game.Item.Recipe;

public class RecipeScreen extends UIScreen {
	
	public static final int NUM_RECIPES = 6;

	final Skin skin = new Skin(Gdx.files.internal("data/skins/uiskin.json"));
	
	Table table;
	Table left;
	Table leftBot;
	Table mid;
	Table right;
	
	ButtonGroup bg;
	boolean recipeListMode = true;

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
		
		recipeListMode = true;
		
		createUI();
	}
	
	public void createUI()
	{
		left.clear();
		mid.clear();
		right.clear();
		
		if (recipeListMode)
		{
			left.add(getRecipeList()).width(200);
			setRecipeDesc();
			TextButton craft = new TextButton("Craft Recipe", skin);
			craft.addListener(new InputListener() {
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					recipeListMode = false;
					pickRecipe();
					createUI();
					return false;
				}
			});
			right.add(craft);
		}
		else
		{
			left.add(createCraftingLeftView());
			left.row();
			left.add(leftBot);
		}
		
	}
	
	ArrayList<RecipeButton> recipeButtons;
	private ScrollPane getRecipeList()
	{
		Table rlist = new Table();
		bg = new ButtonGroup();
		recipeButtons = new ArrayList<RecipeButton>();
		
		boolean test = true;
		for (Recipe r : recipes)
		{
			RecipeButton rb = new RecipeButton(skin, r, bg);
			recipeButtons.add(rb);
			
			if (test) { test = false; rb.toggle(); }
			
			rlist.add(rb).width(200).height(30).padBottom(10);
			rlist.row();
		}
		
		ScrollPane scroll = new ScrollPane(rlist, skin);
		scroll.setColor(0, 0, 0, 1.0f);
		scroll.setScrollingDisabled(true, false);
		
		return scroll;
	}
	
	private void setRecipeDesc()
	{
		mid.clear();
		
		Recipe r = null;
		for (RecipeButton rb : recipeButtons)
		{
			if (rb.isChecked()) { r = rb.recipe; break; }
		}
		
		mid.add(new Label("Name: "+r.recipeName, skin));
		mid.row();
		Table rarity = new Table();
		rarity.add(new Label("Rarity: ", skin));
		rarity.add(GameData.getRarityLabel(r.rarity, skin));
		mid.add(rarity);
		mid.row();
	}

	Recipe chosenRecipe;
	public void pickRecipe()
	{
		for (RecipeButton rb : recipeButtons)
		{
			if (rb.isChecked()) { chosenRecipe = rb.recipe; break; }
		}
		
		recipeListMode = false;
		createUI();
	}
	
	ArrayList<CraftButton> craftButtons;
	CraftButton selected = null;
	private Table createCraftingLeftView()
	{
		craftButtons = new ArrayList<CraftButton>();
		Table view = new Table();
		
		for (char[] row : chosenRecipe.visualGrid)
		{
			for (char c : row)
			{
				final CraftButton cb = new CraftButton(skin, c);
				if (c != ' ') {
					craftButtons.add(cb);
					cb.addListener(new InputListener() {
						public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
							if (selected != null) selected.setColor(1.0f, 1.0f, 1.0f, 1.0f);
							selected = cb;
							selected.setColor(0.3f, 1.0f, 0.3f, 1.0f);
							createCraftingCenterView();
							createCraftingRightView();
							return false;
						}
					});
				}
				view.add(cb).width(20).height(20);
			}
			view.row();
		}
		return view;
	}
	
	private void createCraftingLeftButton()
	{
		
		leftBot.clear();
		
		for (CraftButton cb : craftButtons)
		{
			if (cb.component == null) return;
		}
		
		TextButton tb = new TextButton("Craft Item", skin);
		tb.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				HashMap<Character, Component> components = new HashMap<Character, Component>();
				for (CraftButton cb : craftButtons)
				{
					components.put(cb.reference, cb.component);
					GameStats.removeComponent(cb.component, chosenRecipe.getComponentAmount(cb.reference));
				}
				Item i = chosenRecipe.craft(components);
				System.out.println(i);
				return false;
			}
		});

		leftBot.add(tb);
	}
	
	private void createCraftingCenterView()
	{
		mid.clear();
		
		if (selected == null) return;
		
		mid.add(chosenRecipe.getComponentDescription(selected.reference, skin));
		mid.row();
		
		if (selected.component == null) return;
		Component c = selected.component;
		
		Table tc = new Table();
		
		tc.add(new Label("Crafting Component", skin));
		tc.row();
		tc.add(new Label("Name: "+c.name, skin));
		tc.row();
		Table rarity = new Table();
		rarity.add(new Label("Rarity: ", skin));
		rarity.add(GameData.getRarityLabel(c.rarity, skin));
		tc.add(rarity);
		tc.row();
		tc.add(new Label("Soft/Hard: "+c.soft_hard, skin));
		tc.row();
		tc.add(new Label("Flexible/Brittle: "+c.flexible_brittle, skin));
		tc.row();
		
		mid.add(tc);
	}
	
	private void createCraftingRightView()
	{
		right.clear();
		
		bg = new ButtonGroup();
		Table t = new Table();

		for (Component c : GameStats.components.values())
		{
			// Check valid type
			if (!chosenRecipe.checkComponent(c, selected.reference)) continue;
			
			// Check amounts
			int usedAmount = 0;
			for (CraftButton cb : craftButtons)
			{
				if (cb.component != null && cb.component.toString().equals(c.toString()))
				{
					usedAmount += chosenRecipe.getComponentAmount(cb.reference);
				}
			}
			
			boolean check = false;
			if (selected.component != null && selected.component.toString().equals(c.toString())) check = true;
			else if (usedAmount+chosenRecipe.getComponentAmount(selected.reference) > c.amount) continue;
			
			// Valid!
			ComponentButton cb = new ComponentButton(skin, c, bg);
			t.add(cb);
			t.row();
			if (check) cb.cbox.toggle();
		}
		
		ScrollPane scroll = new ScrollPane(t, skin);
		scroll.setColor(0, 0, 0, 1.0f);
		scroll.setScrollingDisabled(true, false);
		
		right.add(scroll);
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
		
		left = new Table();
		leftBot = new Table();
		mid = new Table();
		right = new Table();
		
		table.add(left).width(200);
		table.add(mid).width(200);
		table.add(right).width(200);
	}

	@Override
	protected void superSuperDispose() {
		skin.dispose();
	}

	@Override
	public void update(float delta) {

	}
	
	class CraftButton extends Button
	{
		char reference;
		Component component;
		
		public CraftButton(Skin skin, char reference)
		{
			super(skin);
			this.reference = reference;
		}
		
		public void setComponent(Component c)
		{
			this.component = c;
			this.clear();
			
			this.add(new Image(c.icon));
		}
	}

	class RecipeButton extends Button
	{
		Recipe recipe;
		CheckBox cbox;
		boolean clicked = false;
		public RecipeButton(Skin skin, Recipe recipe, ButtonGroup bg)
		{
			super(skin);
			this.recipe = recipe;
			
			cbox = new CheckBox("     " + recipe.recipeName, skin);
			bg.add(cbox);
			
			add(cbox);
			addListener(new InputListener() {
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					clicked = true;
					return true;
				}
				
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					if (clicked) {
						clicked = false;
						cbox.toggle();
						setRecipeDesc();
					}
				}
				
				public void touchDragged (InputEvent event, float x, float y, int pointer) {
					clicked = false;
				}
			});
			
		}
		
		public boolean isChecked()
		{
			return cbox.isChecked();
		}
	}
	
	class ComponentButton extends Button
	{
		Component component;
		CheckBox cbox;
		boolean clicked = false;
		public ComponentButton(Skin skin, final Component component, ButtonGroup bg)
		{
			super(skin);
			this.component = component;
			
			cbox = new CheckBox("     " + component.name, skin);
			bg.add(cbox);
			
			add(cbox);
			add(new Image(component.icon));
			addListener(new InputListener() {
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					clicked = true;
					return true;
				}
				
				public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
					if (clicked) {
						clicked = false;
						cbox.toggle();
						selected.setComponent(component);
						createCraftingCenterView();
						createCraftingLeftButton();
					}
				}
				
				public void touchDragged (InputEvent event, float x, float y, int pointer) {
					clicked = false;
				}
			});
			
		}
		
		public boolean isChecked()
		{
			return cbox.isChecked();
		}
	}

}
