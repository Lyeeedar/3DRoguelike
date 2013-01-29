package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.HashMap;

import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;

public abstract class Equippable extends Item {

	float WEIGHT;

	public Equippable(float WEIGHT) {
		this.WEIGHT = WEIGHT;
	}
}
