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

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.lyeeedar.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer;

public class Stair extends LevelObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -728691849739801350L;
	public final String level_UID;

	public Stair(boolean visible, float x, float y, float z, AbstractObject ao, String level_UID) {
		super(visible, x, y, z, ao);
		this.level_UID = level_UID;
		this.solid = true;
	}

	public Stair(AbstractObject ao, String level_UID, Color colour, String texture, float x, float y,
			float z, float scale, int primitive_type, String... model) {
		super(ao, colour, texture, x, y, z, scale, primitive_type, model);
		this.level_UID = level_UID;
		this.solid = true;
	}

	@Override
	public void activate() {
		System.out.println("Change level");
		GameData.changeLevel(level_UID);
	}

	@Override
	public String getActivatePrompt() {
		return "[E] Change Level";
	}

	@Override
	public void update(float delta, Camera cam) {
		if (particleEffect != null)
		{
			particleEffect.update(delta, cam);
		}
		positionYAbsolutely(getRadius());
	}

	@Override
	protected void disposed() {
	}

	@Override
	public void fixReferences() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void created() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void rendered(Renderer renderer,
			ArrayList<ParticleEmitter> emitters, Camera cam) {
		// TODO Auto-generated method stub
		
	}

}
