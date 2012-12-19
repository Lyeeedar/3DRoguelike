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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.Level.LevelGraphics;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;

public class InGameScreen extends AbstractScreen {
	
	public boolean paused = false;
	Texture pausedTint;
	
	public static final int VIEW_DISTANCE = 500;
	public static final int ACTIVATE_DISTANCE = 500;
	public static final boolean SHOW_COLLISION_BOX = false;
	public static final int MAP_WIDTH = 100;
	public static final int MAP_HEIGHT = 100;
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
			if (!lo.visible) continue;
			lo.vo.render(protoRenderer);
			lo.draw(cam);
		}
		
		for (GameActor ga : GameData.level.actors)
		{
			if (!ga.visible) continue;
			ga.vo.render(protoRenderer);
			ga.draw(cam);
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
			pe.render(decalBatch, cam);
			particleNum += pe.particles;
		}
		time -= delta;
		if (time < 0)
		{
			//System.out.println("Player position = " + GameData.player.getPosition());
			System.out.println("Visible Particles: "+particleNum);
			time = 1;
		}
		
	}

	@Override
	public void drawOrthogonals(float delta) {
		
		if (paused)
		{
			spriteBatch.draw(pausedTint, 0, 0, screen_width, screen_height);	
			
			int x = (int)( ( (GameData.player.getPosition().x / 10f) + 0.5f) * LevelGraphics.STEP );
			int y = (int)( ( (GameData.player.getPosition().z / 10f) + 0.5f) * LevelGraphics.STEP );
			
			spriteBatch.draw(GameData.levelGraphics.map, MAP_X, MAP_Y, MAP_WIDTH*2, MAP_HEIGHT*2,
					x-MAP_WIDTH, y-MAP_HEIGHT, MAP_WIDTH*2, MAP_HEIGHT*2,
					false, false);
			
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
		}
		else
		{			
			spriteBatch.draw(crosshairs, screen_width/2f, screen_height/2f);
		}
		
		font.draw(spriteBatch, desc, 300, 20);
	}
	
	int count = 1;
	float dist = VIEW_DISTANCE;
	float tempdist = 0;
	//GameObject lookedAtObject = null;
	StringBuilder desc = new StringBuilder();
	
	Ray ray = new Ray(new Vector3(), new Vector3());
	boolean tabCD = false;
	float activateCD = 0;
	@Override
	public void update(float delta) {
		activateCD -= delta;
		if (!paused)
		{
			for (LevelObject lo : GameData.level.levelObjects)
			{
				lo.update(delta);
			}
			
			for (GameActor ga : GameData.level.actors)
			{
				ga.update(delta);
			}
			
			for (ParticleEmitter pe : GameData.particleEmitters)
			{
				if (!cam.frustum.sphereInFrustum(pe.getPos(), pe.getRadius())) continue;
				pe.update(delta);
			}
			
			cam.position.set(GameData.player.getPosition()).add(GameData.player.offsetPos);
			cam.direction.set(GameData.player.getRotation()).add(GameData.player.offsetRot);
			cam.update();
		}
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) game.ANNIHALATE();
		if (Gdx.input.isKeyPressed(Keys.TAB) && !tabCD) 
		{
			if (paused)
			{
				paused = false;
				Gdx.input.setCursorCatched(true);
				tabCD = true;
			}
			else
			{
				paused = true;
				Gdx.input.setCursorCatched(false);
				Gdx.input.setCursorPosition(screen_width/2, screen_height/2);
				tabCD = true;
			}
		}
		else if (!Gdx.input.isKeyPressed(Keys.TAB))
		{
			tabCD = false;
		}
		
		if (paused)
		{
			Ray ray2 = cam.getPickRay(Gdx.input.getX(), Gdx.input.getY());
			dist = cam.far*cam.far;
			ray.set(ray2);
		}
		else
		{	
			ray.origin.set(GameData.player.getPosition());
			ray.direction.set(GameData.player.getRotation());
			dist = VIEW_DISTANCE;
		}

		getDescription(dist, ray, paused);
		
		
		if (!paused && Gdx.input.isKeyPressed(Keys.E) && activateCD < 0)
		{
			ray.origin.set(GameData.player.getPosition());
			ray.direction.set(GameData.player.getRotation());
			dist = ACTIVATE_DISTANCE;
			
			GameObject go = GameData.level.getClosestActor(ray, dist, GameData.player.UID, tmpVec);
			
			if (go != null)
			{			
				System.out.println("actor collision");
				dist = tmpVec.dst2(ray.origin);
			}

			go = GameData.level.getClosestLevelObject(ray, dist, GameData.player.UID, tmpVec);
			
			if (go != null) 
			{
				go.activate();
			}
			
			activateCD = 1;
		}
		
	}
	
	final Vector3 tmpVec = new Vector3();
	public void getDescription(float dist, Ray ray, boolean longDesc)
	{
		desc.delete(0, desc.length());
		desc.append("There is nothing there but empty space.");
		
		GameObject go = GameData.level.getClosestActor(ray, dist, GameData.player.UID, tmpVec);
		
		if (go != null)
		{
			desc.delete(0, desc.length());
			if (longDesc) {
				desc.append(go.longDesc);
			}
			else
			{
				desc.append(go.shortDesc);
			}
			
			dist = tmpVec.dst2(ray.origin);
		}

		go = GameData.level.getClosestLevelObject(ray, dist, GameData.player.UID, tmpVec);
		
		if (go != null)
		{
			desc.delete(0, desc.length());
			if (longDesc) {
				desc.append(go.longDesc);
			}
			else
			{
				desc.append(go.shortDesc);
			}
			
			dist = tmpVec.dst2(ray.origin);
		}
		
		dist = GameData.level.getDescription(ray, dist, desc, paused);
	}

	@Override
	public void create() {
		
		GameData.init(game);
		
		protoRenderer = new PrototypeRendererGL20(GameData.lightManager);
		protoRenderer.cam = cam;

		crosshairs = new Texture(Gdx.files.internal("data/textures/crosshairs.png"));
		arrow = new Sprite(new Texture(Gdx.files.internal("data/textures/arrow.png")));
		
		pausedTint = new Texture(Gdx.files.internal("data/textures/pausedScreenTint.png"));
		
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

	@Override
	public void superDispose() {
		pausedTint.dispose();
		crosshairs.dispose();
	}

}
