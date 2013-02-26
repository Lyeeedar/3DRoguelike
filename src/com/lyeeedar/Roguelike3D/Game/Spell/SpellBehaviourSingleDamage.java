package com.lyeeedar.Roguelike3D.Game.Spell;

import java.io.Serializable;

import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;

public class SpellBehaviourSingleDamage implements SpellBehaviour, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 107121664009788997L;

	public SpellBehaviourSingleDamage() {
	}

	boolean damaged = false;
	float time = 0;
	@Override
	public void update(float delta, Spell spell) {
		
		if (time == 0) {
			spell.particleEmitter.speed /= 4;
			spell.particleEmitter.time = 0;
		}
		else if (time < 0.5f)
		{
			spell.particleEmitter.vx += delta*5;
			spell.particleEmitter.vy += delta*5;
			spell.particleEmitter.vz += delta*5;
			spell.radius += delta*5;
		}
		else if (time < 2)
		{
			spell.particleEmitter.speed += delta;
		}
		else spell.alive = false;
		
		time += delta;
		
		if (!damaged)
		{
			GameActor ga = GameData.level.checkActors(spell.getPosition(), spell.getRadius(), spell.casterUID);
			
			if (ga != null)
			{
				ga.damage(spell.damage, spell.ELE_DAM, spell.DAM_DAM);
				damaged = true;
			}
		}
	}

	@Override
	public SpellBehaviour copy() {
		return new SpellBehaviourSingleDamage();
	}

}
