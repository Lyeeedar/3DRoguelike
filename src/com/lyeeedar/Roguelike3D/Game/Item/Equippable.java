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
package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;

public abstract class Equippable extends Item{

	/**
	 * 
	 */
	private static final long serialVersionUID = -322654903027697702L;
	float WEIGHT;

	public Equippable(float WEIGHT, Item_Type type) {
		super(type);
		this.WEIGHT = WEIGHT;
	}
	
	public abstract void fixReferences(GameActor actor);
	
	public abstract Table getDescriptionWidget(Skin skin);
	public abstract Table getComparisonWidget(Equippable other, Skin skin);
	
	public Table getComparison(float a, float b, Skin skin, boolean reverse)
	{
		Table table = new Table();
		
		table.add(new Label(""+a+">", skin));
		
		Label l = new Label(""+b, skin);
		
		LabelStyle ls = l.getStyle();
		LabelStyle nls = new LabelStyle();
		nls.fontColor = 
				(a == b) ? new Color(1.0f, 1.0f, 1.0f, 1.0f) :
					(reverse) ? 
							(a > b) ? new Color(0.3f, 0.8f, 0.3f, 1.0f) : new Color(0.8f, 0.3f, 0.3f, 1.0f) : 
							(a < b) ? new Color(0.3f, 0.8f, 0.3f, 1.0f) : new Color(0.8f, 0.3f, 0.3f, 1.0f);
		nls.background = ls.background;
		nls.font = ls.font;
		
		l.setStyle(nls);
		
		table.add(l);
		
		return table;
	}
	
	public Table getComparison(int a, int b, Skin skin, boolean reverse)
	{
		Table table = new Table();
		
		table.add(new Label(""+a+">", skin));
		
		Label l = new Label(""+b, skin);
		
		LabelStyle ls = l.getStyle();
		LabelStyle nls = new LabelStyle();
		nls.fontColor = 
				(a == b) ? new Color(1.0f, 1.0f, 1.0f, 1.0f) :
					(reverse) ? 
							(a > b) ? new Color(0.3f, 0.8f, 0.3f, 1.0f) : new Color(0.8f, 0.3f, 0.3f, 1.0f) : 
							(a < b) ? new Color(0.3f, 0.8f, 0.3f, 1.0f) : new Color(0.8f, 0.3f, 0.3f, 1.0f);
		nls.background = ls.background;
		nls.font = ls.font;
		
		l.setStyle(nls);
		
		table.add(l);
		
		return table;
	}
}
