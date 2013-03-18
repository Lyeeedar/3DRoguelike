/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Game.Level;

import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Bag;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.Door;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.PlayerPlacer;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.Spawner;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.Stair;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.Static;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer;

public class Tile implements Serializable {
	private static final long serialVersionUID = -2774877067050458423L;

	public char character;
	
	/**
	 * The floor of the tile
	 */
	public float floor;
	
	/**
	 * The roof of the tile (the very top)
	 */
	public float roof;
	
	/**
	 * The height of the tile (if this = roof then it a full height wall)
	 */
	public float height;
	public boolean visible = false;
	public boolean seen = false;
	
	public Bag<GameActor> actors = new Bag<GameActor>();
	public Bag<LevelObject> levelObjects = new Bag<LevelObject>();
	
	public Tile (char character, float floor, float roof, float height)
	{
		this.character = character;
		this.floor = floor;
		this.roof = roof;
		this.height = height;
	}
	
	public void update(float delta, Camera cam)
	{
		for (LevelObject lo : levelObjects)
		{
			lo.update(delta, cam);
		}
		
		for (GameActor ga : actors)
		{
			ga.update(delta, cam);
		}
	}
	
	public void render(Renderer renderer, Camera cam, ArrayList<ParticleEmitter> visibleEmitters)
	{
		for (LevelObject lo : levelObjects)
		{	
			lo.render(renderer, visibleEmitters, cam);
		}
		
		for (GameActor ga : actors)
		{
			ga.render(renderer, visibleEmitters, cam);
		}
	}
	
	public Player getPlayer()
	{
		for (GameActor ga : actors)
		{
			if (ga instanceof Player)
			{
				return (Player) ga;
			}
		}
		return null;
	}
	
	public boolean isSolid()
	{
		for (LevelObject lo : levelObjects)
		{
			if (lo instanceof Static)
			{
				return true;
			}
		}
			
		return false;
	}
	
	public void getLights(LightManager lightManager)
	{
		for (GameObject go : actors) go.getLight(lightManager);
		for (GameObject go : levelObjects) go.getLight(lightManager);
	}
	
	public boolean positionPlayer(Player player, String prevLevel, String currentLevel)
	{
		for (LevelObject lo : levelObjects) {
			if (lo instanceof PlayerPlacer)
			{
				System.out.println("Player placed at Player Placer");
				if (prevLevel.equals(currentLevel))
				{
					player.positionAbsolutely(lo.getPosition().x, lo.getPosition().y+player.getRadius()+1, lo.getPosition().z);
					return true;
				}
			}
			else if (lo instanceof Stair)
			{
				System.out.println("Player placed at Stair");
				Stair s = (Stair) lo;
				
				if (s.level_UID.equals(prevLevel))
				{
					player.positionAbsolutely(s.getPosition().x, s.getPosition().y+s.getPosition().y+s.getRadius()+player.getRadius()+1, 0);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void evaluateUniqueBehaviour(Level level, LightManager lightManager)
	{
		for (LevelObject lo : levelObjects) {
			if (lo instanceof Spawner)
			{
				Spawner s = (Spawner) lo;
				
				s.spawn(level, lightManager);
			}
			else if (lo instanceof Door)
			{
				Door d = (Door) lo;
				
				d.orientate(level);
			}
		}
	}
	
	public void bakeLights(LightManager lightManager)
	{
		for (GameActor ga : actors)
		{
			ga.bakeLights(lightManager, false);
			if (ga.L_HAND != null) ga.L_HAND.model.bakeLight(lightManager, false);
			if (ga.R_HAND != null) ga.R_HAND.model.bakeLight(lightManager, false);
		}
		for (LevelObject lo : levelObjects)
		{
			lo.bakeLights(lightManager, true);
		}
	}
	
	public void create()
	{
		for (GameActor ga : actors)
		{
			ga.create();
		}
		for (LevelObject lo : levelObjects)
		{
			lo.create();
		}
	}
	
	public void dispose()
	{
		for (GameActor ga : actors)
		{
			ga.dispose();
		}
		for (LevelObject lo : levelObjects)
		{
			lo.dispose();
		}
	}
	
	public void fixReferences()
	{
		for (GameActor ga : actors)
		{
			ga.fixReferences();
		}
		for (LevelObject lo : levelObjects)
		{
			lo.fixReferences();
		}
	}
	
	public void removeLevelObject(String UID)
	{
		for (int i = 0; i < levelObjects.size(); i++)
		{
			if (UID.equals(levelObjects.get(i).UID)) {
				levelObjects.remove(i);
				return;
			}
		}
	}
	
	public void removeGameActor(String UID)
	{
		for (int i = 0; i < actors.size(); i++)
		{
			if (UID.equals(actors.get(i).UID)) {
				actors.remove(i);
				return;
			}
		}
	}
}
