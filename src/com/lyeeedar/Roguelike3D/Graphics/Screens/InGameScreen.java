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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.*;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.Item.VisibleItem;
import com.lyeeedar.Roguelike3D.Game.Level.LevelGraphics;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Game.Spell.Spell;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModelAttributes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;
import com.lyeeedar.Roguelike3D.Graphics.Screens.AbstractScreen;

public class InGameScreen extends AbstractScreen {
	
	public static final int VIEW_DISTANCE = 1000;
	public static final boolean SHOW_COLLISION_BOX = true;
	public static final int MAP_WIDTH = 300;
	public static final int MAP_HEIGHT = 300;
	public static final int MAP_X = 10;
	public static final int MAP_Y = 10;

	Texture crosshairs;
	Sprite arrow;
	
	public InGameScreen(Roguelike3DGame game) {
		super(game);
	}

	@Override
	public void drawModels(float delta) {

		for (VisibleObject vo : GameData.levelGraphics.graphics)
		{
			vo.render(protoRenderer);
		}
		
		for (LevelObject lo : GameData.level.levelObjects)
		{
			
			if (SHOW_COLLISION_BOX) {
				StillModelAttributes sma = lo.collisionAttributes;
				sma.getTransform().setToTranslation(lo.collisionBox.position);
				protoRenderer.draw(lo.collisionMesh, sma);
			}
			
			if (!lo.visible) continue;
			lo.vo.render(protoRenderer);
		}
		
		for (GameActor ga : GameData.level.actors)
		{
			
			if (SHOW_COLLISION_BOX) {
				StillModelAttributes sma = ga.collisionAttributes;
				sma.getTransform().setToTranslation(ga.collisionBox.position);
				protoRenderer.draw(ga.collisionMesh, sma);
			}
			
			if (!ga.visible) continue;
			ga.vo.render(protoRenderer);
		}
		
		for (VisibleItem vi : GameData.level.items)
		{
			
			if (SHOW_COLLISION_BOX) {
				StillModelAttributes sma = vi.collisionAttributes;
				sma.getTransform().setToTranslation(vi.collisionBox.position);
				protoRenderer.draw(vi.collisionMesh, sma);
			}
			
			if (!vi.visible) continue;
			vi.vo.render(protoRenderer);
		}
		
		for (Spell sp : GameData.level.spells)
		{
			sp.vo.render(protoRenderer);
		}

	}
	
	float time = 0;
	int particleNum = 0;
	@Override
	public void drawDecals(float delta) {
		particleNum = 0;
		for (ParticleEmitter pe : GameData.particleEmitters)
		{
			if (!cam.frustum.sphereInFrustum(pe.getPos(), pe.getRadius())) continue;
			pe.update(delta);
			pe.render(decalBatch, cam);
			particleNum += pe.particles;
		}
		time -= delta;
		if (time < 0)
		{
			System.out.println("Visible Particles: "+particleNum);
			time = 1;
		}
	}

	@Override
	public void drawOrthogonals(float delta) {
		
		spriteBatch.draw(crosshairs, screen_width/2f, screen_height/2f);
		
		int x = (int)( ( GameData.player.getPosition().x / 10f ) * (float)LevelGraphics.STEP );
		int y = (int)( ( GameData.player.getPosition().z / 10f ) * (float)LevelGraphics.STEP );

		spriteBatch.draw(GameData.levelGraphics.map, MAP_X, MAP_Y,
				x-MAP_WIDTH, y-MAP_HEIGHT, MAP_WIDTH*2, MAP_HEIGHT*2);
		
		// Work out angle
		float angle = 90 * GameData.player.getRotation().x;
		
		if (GameData.player.getRotation().z > 0)
		{
			angle = 180+angle;
		}
		else
		{
			angle = 0-angle;
		}
		
		arrow.setRotation(angle);
		arrow.setPosition(MAP_WIDTH+MAP_X, MAP_HEIGHT+MAP_Y);
		arrow.draw(spriteBatch);
		
		font.draw(spriteBatch, desc, 300, 20);
	}
	
	ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	int count = 1;
	float dist = VIEW_DISTANCE;
	float tempdist = 0;
	//GameObject lookedAtObject = null;
	StringBuilder desc = new StringBuilder();
	
	@Override
	public void update(float delta) {
		
		gameObjects.clear();
		for (LevelObject lo : GameData.level.levelObjects)
		{
			gameObjects.add(lo);
		}
		
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
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) game.ANNIHALATE();
		
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
			//label.setText(map);
		}
		
		cam.position.set(GameData.player.getPosition()).add(GameData.player.offsetPos);//.add(0, 5, 0);
		cam.direction.set(GameData.player.getRotation()).add(GameData.player.offsetRot);
		cam.update();
		
		Ray ray = new Ray(GameData.player.getPosition(), GameData.player.getRotation());//cam.getPickRay(screen_height/2f, screen_width/2f);
		dist = VIEW_DISTANCE;
		desc.delete(0, desc.length());
		desc.append("There is nothing there but empty space.");
	
		for (GameObject go : GameData.level.actors)
		{
			if (go.UID.equals(GameData.player.UID)) continue;
			tempdist = cam.position.dst2(go.getPosition());
			if (tempdist > dist) continue;
			else if (!go.collisionBox.intersectRay(ray)) continue;
			
			dist = tempdist;
			desc.delete(0, desc.length());
			desc.append(go.description);
		}
		for (GameObject go : GameData.level.levelObjects)
		{
			tempdist = cam.position.dst2(go.getPosition());
			if (tempdist > dist) continue;
			else if (!go.collisionBox.intersectRay(ray)) continue;
			
			dist = tempdist;
			desc.delete(0, desc.length());
			desc.append(go.description);
		}
		for (GameObject go : GameData.level.items)
		{
			tempdist = cam.position.dst2(go.getPosition());
			if (tempdist > dist) continue;
			else if (!go.collisionBox.intersectRay(ray)) continue;
			
			dist = tempdist;
			desc.delete(0, desc.length());
			desc.append(go.description);
		}
		
		dist = GameData.level.getDescription(ray, dist, desc);
		
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
		
		crosshairs = new Texture(Gdx.files.internal("data/textures/crosshairs.png"));
		arrow = new Sprite(new Texture(Gdx.files.internal("data/textures/arrow.png")));
	}

	@Override
	public void hide() {
		Gdx.input.setCursorCatched(false);
	}
	
	@Override
	public void show()
	{
		Gdx.input.setCursorCatched(true);
		
		
		Player p = GameData.player;
		ParticleEmitter pe = new ParticleEmitter(p.getPosition().x, p.getPosition().y+15, p.getPosition().z, 35, 0, 35, 0.75f, 1000);
		
		Color rain = new Color(0f, 0.345098f, 0.345098f, 1.0f);
		pe.setDecal("data/textures/texr.png", new Vector3(0.0f, -21.0f, 0.0f), 5, rain, rain, 1, 1, false);
		GameData.particleEmitters.add(pe);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void superDispose() {
		
		crosshairs.dispose();
	}

}
