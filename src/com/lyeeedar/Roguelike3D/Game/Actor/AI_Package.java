package com.lyeeedar.Roguelike3D.Game.Actor;

public abstract class AI_Package {

	final GameActor actor;
	
	public AI_Package(GameActor actor) {
		this.actor = actor;
	}
	
	public abstract void evaluateAI(float delta);

}
