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

import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;

public abstract class Equipment_ARMOUR extends Equippable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7412331863128890744L;
	public int STRENGTH;
	public HashMap<Element, Integer> ELE_DEF = new HashMap<Element, Integer>();
	public HashMap<Damage_Type, Integer> DAM_DEF = new HashMap<Damage_Type, Integer>();

	public Equipment_ARMOUR(float WEIGHT, Item_Type type) {
		super(WEIGHT, type);
	}

}
