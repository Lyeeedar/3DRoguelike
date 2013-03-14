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
package com.lyeeedar.Roguelike3D.Graphics.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;

public abstract class UIScreen extends AbstractScreen {
	
	String returnScreen;

	final Skin skin = new Skin(Gdx.files.internal("data/skins/uiskin.json"));
	
	public UIScreen(Roguelike3DGame game) {
		super(game);
	}
	
	public void setReturnScreen(String screen)
	{
		returnScreen = screen;
	}

	@Override
	public void create() {
		createSuper();
	}
	protected abstract void createSuper();

	@Override
	public void drawModels(float delta) {
	}

	@Override
	public void drawTransparent(float delta) {
	}

	@Override
	public void drawOrthogonals(float delta) {
		stage.draw();
	}

	@Override
	public void superDispose() {
		superSuperDispose();
	}
	
	protected abstract void superSuperDispose();
}
