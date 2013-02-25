package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import java.io.Serializable;

import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;

public abstract class RiggedModelBehaviour implements Serializable {
	
	private static final long serialVersionUID = 1085264939806223799L;
	RiggedModelNode node;
	
	int side;
	
	public RiggedModelBehaviour(RiggedModelNode node)
	{
		this.node = node;
	}

	public void equip(GameActor holder, int side)
	{
		this.side = side;
		equipped(holder, side);
	}
	protected abstract void equipped(GameActor holder, int side);
	
	public abstract void held();
	public abstract void released();
	
	public abstract void cancel();
	
	public abstract void update(float delta);
	
	public abstract void proccessCollideMode(boolean mode);

}
