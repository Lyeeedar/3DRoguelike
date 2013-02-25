package com.lyeeedar.Roguelike3D.Game.Spell;

import java.io.Serializable;

public class SpellBehaviourSingleDamage implements SpellBehaviour, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 107121664009788997L;

	public SpellBehaviourSingleDamage() {
	}

	@Override
	public void update(float delta, Spell spell) {
		
		spell.particleEmitter.speed += delta;
		
		if (spell.particleEmitter.speed > 2) spell.alive = false;
	}

	@Override
	public SpellBehaviour copy() {
		return new SpellBehaviourSingleDamage();
	}

}
