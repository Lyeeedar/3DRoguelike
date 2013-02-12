package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;

public class RiggedOneHandedSwing extends RiggedModelBehaviour {

	private static final long serialVersionUID = -3210144783845821123L;

	final float weight;
	final float attack_speed;
	
	public RiggedOneHandedSwing(RiggedModelNode node, float weight, float attack_speed) {
		super(node);
		
		this.weight = weight/10;
		this.attack_speed = 50+(attack_speed/10);
	}

	int stage = 0;
	
	float angle = 45;
	float speed = 0;
	
	@Override
	public void update(float delta) {

		if (stage == 0)
		{
			angle = 45;
		}
		else if (stage == 4)
		{
			if (angle < 45)
			{
				angle += delta*attack_speed;
				if (angle > 45)
					stage = 0;
			}
			else
			{
				angle -= delta*(attack_speed+weight);
				if (angle < 45)
					stage = 0;
			}
		}
		else if (stage == 1)
		{
			angle += delta*attack_speed;
			if (angle > 90)
				angle = 90;
		}
		else if (stage == 2)
		{
			speed += delta*(attack_speed+weight);
			angle -= speed;
			if (angle < -45)
			{
				node.setCollideMode(false, true);
				stage++;
			}
		}
		else if (stage == 3)
		{
			angle += delta*attack_speed;
			if (angle > 45)
				stage = 0;
		}
		
		node.offsetRotation.idt().rotate(0, 1, 0, 180).rotate(1, 0, sign, -45).rotate(0, 0, 1, 90).rotate(0, 1, 0, angle);
		node.offsetPosition.idt().translate(0, angle/45, 0);
	}

	@Override
	public void held() {
		if (stage == 0) {
			stage = 1;
		}
	}
	
	@Override
	public void released() {
		if (stage == 1) {
			node.setCollideMode(true, true);
			speed = 0;
			stage = 2;
		}
	}

	@Override
	public void cancel() {
		if (stage != 0 || stage != 4)
		{
			node.setCollideMode(false, true);
			stage = 4;
		}
	}

	int sign;
	
	@Override
	protected void equipped(GameActor holder, int side) {
		if (side == 1) sign = 1;
		else if (side == 2) sign = -1;
	}
	

}
