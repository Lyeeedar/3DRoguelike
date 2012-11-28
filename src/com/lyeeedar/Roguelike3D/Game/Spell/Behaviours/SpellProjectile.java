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
package com.lyeeedar.Roguelike3D.Game.Spell.Behaviours;

import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Actor.CollisionBox;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Spell.Spell;

/**
 * Models a projectile spell. <br>
 * Data requirements: <br>
 * 0 = Damage
 * 1 = speed
 * 
 * @author Philip
 *
 */
public class SpellProjectile implements SpellBehaviourPackage{

	@Override
	public void update(Spell spell, float delta) {

		float move = delta * spell.data[1] * 10;
		
		spell.forward_backward(move);
		
		CollisionBox box = new CollisionBox();
		spell.collisionBox.cpy(box);
		box.translate(spell.getVelocity());
		
		GameActor ga = GameData.level.checkEntities(box, spell.casterUID);
		
		if (ga != null)
		{
			ga.damage(spell.element, spell.data[0]);
			spell.dead = true;
			return;
		}
		
		boolean collide = GameData.level.checkBoxToLevelCollision(box);
		
		if (collide)
		{
			spell.dead = true;
			return;
		}
		
		spell.translate(spell.getVelocity());
		spell.getVelocity().set(0.0f,  0.0f,  0.0f);
	}


}
