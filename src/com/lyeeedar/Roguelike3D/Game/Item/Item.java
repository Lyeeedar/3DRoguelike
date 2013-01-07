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
package com.lyeeedar.Roguelike3D.Game.Item;

public class Item {

	public enum Item_Type {
		WEAPON,
		ARMOUR_HEAD,
		ARMOUR_BODY,
		ARMOUR_LEG,
		ARMOUR_HAND,
		COMPONENT
	}

	public static Item_Type convertItemType(String item_type)
	{
		Item_Type type = null;

		if (item_type.equalsIgnoreCase("weapon")) type = Item_Type.WEAPON;
		else if (item_type.equalsIgnoreCase("armour_head")) type = Item_Type.ARMOUR_HEAD;
		else if (item_type.equalsIgnoreCase("armour_body")) type = Item_Type.ARMOUR_BODY;
		else if (item_type.equalsIgnoreCase("armour_leg")) type = Item_Type.ARMOUR_LEG;
		else if (item_type.equalsIgnoreCase("armour_hand")) type = Item_Type.ARMOUR_HAND;
		else if (item_type.equalsIgnoreCase("component")) type = Item_Type.COMPONENT;

		return type;
	}
	
	public String NAME;
	public String DESCRIPTION;
	public int RARITY;

}
