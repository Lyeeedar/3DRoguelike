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
package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import com.badlogic.gdx.graphics.Camera;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.XML.MonsterEvolver;
import com.lyeeedar.Roguelike3D.Graphics.Colour;

public class Spawner extends LevelObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7727773756686518979L;
	
	int difficulty;
	String type;
	String monsterUID;
	transient MonsterEvolver me;

	public Spawner(boolean visible, float x, float y, float z, AbstractObject ao, int difficulty, MonsterEvolver me) {
		super(visible, x, y, z, ao);
		this.difficulty = difficulty;
		this.me = me;
		this.type = me.type;
		this.monsterUID = me.UID;
	}

	public Spawner(AbstractObject ao, int difficulty, MonsterEvolver me, Colour colour, String texture, float x,
			float y, float z, float scale, int primitive_type, String... model) {
		super(ao, colour, texture, x, y, z, scale, primitive_type, model);
		this.difficulty = difficulty;
		this.me = me;
		this.type = me.type;
		this.monsterUID = me.UID;
	}

	@Override
	public void fixReferencesSuper() {
		me = GameData.getCurrentLevelContainer().getMonsterEvolver(type, monsterUID);
	}

	public void spawn(Level level)
	{
		GameActor creature = me.getMonster(difficulty);
		creature.create();
		creature.positionAbsolutely(getPosition().x, getPosition().y+getRadius()+creature.getRadius()+1, getPosition().z);
		level.actors.add(creature);
	}
	
	@Override
	public void update(float delta) {
	}

	@Override
	public void draw(Camera cam) {
	}

	@Override
	public void activate() {
	}

	@Override
	public String getActivatePrompt() {
		return null;
	}

}