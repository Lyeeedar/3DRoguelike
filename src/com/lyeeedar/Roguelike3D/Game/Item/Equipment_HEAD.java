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

public class Equipment_HEAD extends Equipment_ARMOUR {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6021770818631976746L;

	public Equipment_HEAD(float WEIGHT) {
		super(WEIGHT, Item_Type.ARMOUR_HEAD);
	}

	@Override
	public void fixReferences() {
	}

}
