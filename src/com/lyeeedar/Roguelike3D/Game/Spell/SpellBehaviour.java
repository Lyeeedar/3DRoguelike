package com.lyeeedar.Roguelike3D.Game.Spell;

public interface SpellBehaviour {

	public void update(float delta, Spell spell);
	
	public SpellBehaviour copy();
}
