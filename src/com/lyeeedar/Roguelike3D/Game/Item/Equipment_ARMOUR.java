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
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;

public abstract class Equipment_ARMOUR extends Equippable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7412331863128890744L;
	public int STRENGTH;
	public HashMap<Element, Integer> ELE_DEF;
	public HashMap<Damage_Type, Integer> DAM_DEF;

	public Equipment_ARMOUR(float WEIGHT, Item_Type type, int strength, HashMap<Element, Integer> ELE_DEF, HashMap<Damage_Type, Integer> DAM_DEF) {
		super(WEIGHT, type);
		this.ELE_DEF = ELE_DEF;
		this.DAM_DEF = DAM_DEF;
		this.STRENGTH = strength;
	}

	@Override
	public Table getDescriptionWidget(Skin skin)
	{
		Table table = new Table();

		table.add(new Label("Name: "+name, skin));
		table.row();
		table.add(new Label("Description: "+description, skin));
		table.row();
		table.add(new Label("Rarity: ", skin));
		table.add(GameData.getRarityLabel(rarity, skin));
		table.row();
		table.add(new Label("Weight: "+WEIGHT, skin));
		table.row();
		if (STRENGTH != 0) {
			table.add(new Label("Strength Bonus: "+STRENGTH, skin));
			table.row();
		}
		
		String dam = "Damage Defenses: \n";
		for (Damage_Type dt : Damage_Type.values())
		{
			dam += "   "+dt+": "+DAM_DEF.get(dt)+"\n";
		}
		table.add(new Label(dam, skin));
		table.row();
		
		String element = "Elemental Defenses: \n";
		for (Element e : Element.values())
		{
			element += "   "+e+": "+ELE_DEF.get(e)+"\n";
		}

		table.add(new Label(element, skin));

		return table;
	}
	
	@Override
	public Table getComparisonWidget(Equippable equip, Skin skin)
	{
		Equipment_ARMOUR other = (Equipment_ARMOUR) equip;
		Table table = new Table();

		table.add(new Label("Name: "+other.name, skin));
		table.row();
		table.add(new Label("Description: "+other.description, skin));
		table.row();
		table.add(new Label("Rarity: ", skin));
		table.add(GameData.getRarityLabel(other.rarity, skin));
		table.row();
		table.add(new Label("Weight: ", skin));
		table.add(getComparison(WEIGHT, other.WEIGHT, skin, true));
		table.row();
		if (STRENGTH != 0) {
			table.add(new Label("Strength Bonus: ", skin));
			table.add(getComparison(STRENGTH, other.STRENGTH, skin, false));
			table.row();
		}

		Table dam = new Table();
		dam.add(new Label("Damage Defenses: ", skin));
		dam.row();
		for (Damage_Type dt : Damage_Type.values())
		{
			dam.add(new Label("   "+dt+": ", skin));
			dam.add(getComparison(DAM_DEF.get(dt), other.DAM_DEF.get(dt), skin, false));
			dam.row();
		}
		table.add(dam);
		table.row();
		
		Table element = new Table();
		element.add(new Label("Elemental Defenses: ", skin));
		element.row();
		for (Element ele : Element.values())
		{
			element.add(new Label("   "+ele+": ", skin));
			element.add(getComparison(ELE_DEF.get(ele), other.ELE_DEF.get(ele), skin, false));
			element.row();
		}
		table.add(element);

		return table;
	}
}
