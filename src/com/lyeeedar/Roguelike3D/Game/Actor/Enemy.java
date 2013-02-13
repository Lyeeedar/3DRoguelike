package com.lyeeedar.Roguelike3D.Game.Actor;

import com.lyeeedar.Roguelike3D.Graphics.Colour;

public class Enemy extends GameActor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3619579342432390931L;

	public Enemy(Colour colour, String texture, float x, float y, float z,
			float scale, int primitive_type, String... model) {
		super(colour, texture, x, y, z, scale, primitive_type, model);
	}

}
