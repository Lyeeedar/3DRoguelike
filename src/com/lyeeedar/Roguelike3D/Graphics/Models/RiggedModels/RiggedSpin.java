package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;

public class RiggedSpin extends RiggedModelBehaviour {

	public RiggedSpin(RiggedModelNode node) {
		super(node);
	}

	float angle = 0;
	
	@Override
	public void held() {
		angle += 525;
		if (angle > 58450) angle = 58450;
	}

	@Override
	public void released() {
		angle -= 515;
		
		if (angle < 0) angle = 0;
	}

	@Override
	public void cancel() {
	}

	@Override
	public void update(float delta) {
		node.rotation.rotate(0, 1, 0, angle*delta);
	}

	@Override
	protected void equipped(GameActor holder, int side) {
	}

}
