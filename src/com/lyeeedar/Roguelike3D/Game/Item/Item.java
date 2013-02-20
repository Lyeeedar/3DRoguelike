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

import java.io.Serializable;

public abstract class Item implements Serializable, Comparable<Item> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7122062237284900314L;
	
	public final String UID;
	
	public int rarity;
	public final Item_Type item_type;
	public String name;
	public String description;
	
	public Item(Item_Type type)
	{
		this.item_type = type;
		
		UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
	}

	public enum Item_Type {
		WEAPON,
		ARMOUR_HEAD,
		ARMOUR_BODY,
		ARMOUR_LEGS,
		ARMOUR_BOOTS,
		COMPONENT
	}

	public static Item_Type convertItemType(String item_type)
	{
		for (Item_Type it : Item_Type.values())
		{
			if (item_type.equalsIgnoreCase(""+it)) return it;
		}

		return null;
	}
	
	public abstract void fixReferences();
	

	@Override
	public int compareTo(Item i) {
		
		int rarity = this.rarity - i.rarity;
		if (rarity != 0) return rarity;
		else if (i.toString().equals(this.toString())) return 0;
		else if (i.hashCode() < this.hashCode()) return -1;
		else if (i.hashCode() > this.hashCode()) return 1;
		return 0;
	}
}
