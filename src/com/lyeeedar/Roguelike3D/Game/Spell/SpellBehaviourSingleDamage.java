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
			spell.particleEffect.getFirstEmitter().emissionTime /= 4;
		}
		else if (time < 0.5f)
		{
			spell.particleEffect.getFirstEmitter().ex += delta*5;
			spell.particleEffect.getFirstEmitter().ey += delta*5;
			spell.particleEffect.getFirstEmitter().ez += delta*5;
			spell.particleEffect.getFirstEmitter().calculateRadius();
			spell.radius += delta*5;
		}
		else if (time < 2)
		{
			spell.particleEffect.getFirstEmitter().emissionTime += delta;
		}
		else spell.alive = false;
		
		time += delta;
		
		if (!damaged)
		{
			GameActor ga = GameData.level.collideSphereActorsAll(spell.getPosition().x, spell.getPosition().y, spell.getPosition().z, spell.getRadius(), spell.casterUID);
			
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
