package com.lyeeedar.Roguelike3D.Game.Spell;

import java.io.Serializable;

public class SpellBehaviourBolt implements SpellBehaviour, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4116762507621516061L;

	public SpellBehaviourBolt() {
	}

	@Override
	public void update(float delta, Spell spell) {
		spell.forward_backward(0.5f);
		spell.applyMovement(delta);
	}

	@Override
	public SpellBehaviour copy() {
		return new SpellBehaviourBolt();
	}

}
