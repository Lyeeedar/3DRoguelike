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
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;

public class PlayerPlacer extends LevelObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1936382369794684932L;

	public PlayerPlacer(boolean visible, float x, float y, float z,
			AbstractObject ao) {
		super(visible, x, y, z, ao);
		this.solid = false;
	}

	@Override
	public void activate() {
	}

	@Override
	public String getActivatePrompt() {
		return "";
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void draw(PrototypeRendererGL20 renderer) {
	}

	@Override
	public void fixReferencesSuper() {
	}

}
