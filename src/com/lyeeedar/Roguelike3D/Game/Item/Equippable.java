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
}
