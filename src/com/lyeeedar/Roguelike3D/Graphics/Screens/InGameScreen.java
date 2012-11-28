/*******************************************************************************
 * Copyright (c) 2012 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Graphics.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.*;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Item.VisibleItem;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Game.Spell.Spell;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;
import com.lyeeedar.Roguelike3D.Graphics.Screens.AbstractScreen;

public class InGameScreen extends AbstractScreen {

	public InGameScreen(Roguelike3DGame game) {
		super(game);
	}

	@Override
	public void drawModels(float delta) {

		for (VisibleObject vo : GameData.levelGraphics.graphics)
		{
			vo.render(protoRenderer);
		}
		
		for (GameActor go : GameData.level.actors)
		{
			go.vo.render(protoRenderer);
		}
		
		for (VisibleItem vi : GameData.level.items)
		{
			vi.vo.render(protoRenderer);
		}
		
		for (Spell sp : GameData.level.spells)
		{
			sp.vo.render(protoRenderer);
		}

	}
	
	@Override
	public void drawDecals(float delta) {
		for (ParticleEmitter pe : GameData.particleEmitters)
		{
			if (!cam.frustum.sphereInFrustum(pe.getPos(), pe.getRadius())) continue;
			pe.update(delta);
			pe.render(decalBatch, cam);
		}
		
	}

	@Override
	public void drawOrthogonals(float delta) {
		
	}
	
	ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	int count = 1;
	@Override
	public void update(float delta) {
		
		gameObjects.clear();
		for (GameActor ga : GameData.level.actors)
		{
			gameObjects.add(ga);
		}
		
		for (VisibleItem vi : GameData.level.items)
		{
			gameObjects.add(vi);
		}
		
		for (Spell sp : GameData.level.spells)
		{
			gameObjects.add(sp);
		}
		
		for (GameObject ga : gameObjects)
		{
			ga.update(delta);
		}
		
		if (Gdx.input.justTouched()) game.switchScreen("LibGDXSplash");
		
		count--;
		if (count <= 0) {
			count = 10;
			//GameData.frame.paint(GameData.frame.getGraphics());
			String map = "";
			for (Tile[] row : GameData.level.getLevelArray()) {
				String r = "";
				for (Tile t : row) {
					r += t.character;
				}
				map += r + "\n";
			}
			label.setText(map);
		}
		
		cam.position.set(GameData.player.getPosition());
		cam.direction.set(GameData.player.getRotation());
		cam.update();
	}

	Label label;
	@Override
	public void create() {
		
		GameData.createNewLevel(game);
		
		protoRenderer = new PrototypeRendererGL20(GameData.lightManager);
		protoRenderer.cam = cam;
		
		Skin skin = new Skin(Gdx.files.internal( "data/skins/uiskin.json" ));	
	    
		label = new Label("", skin);
		
		LabelStyle defaultStyle = new LabelStyle();
	    defaultStyle.font = font;
	    defaultStyle.fontColor = Color.WHITE;
		label.setStyle(defaultStyle);
		
		stage.addActor(label);
	}

	@Override
	public void hide() {
		Gdx.input.setCursorCatched(false);
	}
	
	@Override
	public void show()
	{
		Gdx.input.setCursorCatched(true);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

}
