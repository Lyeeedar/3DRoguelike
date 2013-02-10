package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

public abstract class RiggedModelBehaviour {
	
	RiggedModelNode node;
	
	public RiggedModelBehaviour(RiggedModelNode node)
	{
		this.node = node;
	}

	public abstract void activate();
	public abstract void cancel();
	
	public abstract void update(float delta);

}
