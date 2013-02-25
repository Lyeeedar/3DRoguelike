package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Spell.Spell;

public class RiggedModelBehaviourCastSpell extends RiggedModelBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2461210296884237147L;
	
	final Spell spell;
	String holderUID;
	transient GameActor holder;

	public RiggedModelBehaviourCastSpell(RiggedModelNode node, Spell spell) {
		super(node);
		this.spell = spell;
	}

	@Override
	protected void equipped(GameActor holder, int side) {
		
		holderUID = holder.UID;
		spell.casterUID = holder.UID;
		
		this.holder = holder;
	}

	@Override
	public void held() {
	}

	@Override
	public void released() {
	}

	@Override
	public void cancel() {
	}

	@Override
	public void update(float delta) {
	}

	boolean collideMode = false;
	@Override
	public void proccessCollideMode(boolean mode) {
		if (collideMode && !mode)
		{	
			Spell spellCpy = spell.copy();
			spellCpy.initialise(Vector3.tmp.set(0, 0, 0).mul(node.composedMatrix), holder.getRotation(), spell.particleEmitter.copy(), spell.particleEmitter.getRadius());
			spellCpy.create();
			GameData.spells.add(spellCpy);
		}
		
		collideMode = mode;
	}

}
