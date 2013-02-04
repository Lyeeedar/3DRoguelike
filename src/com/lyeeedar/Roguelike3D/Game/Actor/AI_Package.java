package com.lyeeedar.Roguelike3D.Game.Actor;

import java.io.Serializable;

import com.lyeeedar.Roguelike3D.Game.GameData;

public abstract class AI_Package implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4386924950201938705L;
	protected transient GameActor actor;
	public final String actorUID;
	
	public AI_Package(GameActor actor) {
		this.actor = actor;
		this.actorUID = actor.UID;
	}
	
	public abstract void evaluateAI(float delta);

	public void fixReferences()
	{
		actor = GameData.level.getActor(actorUID);
	}
}
