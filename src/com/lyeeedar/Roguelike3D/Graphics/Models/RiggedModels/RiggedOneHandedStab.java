package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;

public class RiggedOneHandedStab extends RiggedModelBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6507070860186900113L;
	
	final float weight;
	final float attack_speed;

	public RiggedOneHandedStab(RiggedModelNode node, float weight, float attack_speed) {
		super(node);
		this.weight = weight*5;
		this.attack_speed = attack_speed;
	}

	int stage = 0;
	
	float tilt = 55;
	float pos = 0;
	float speed = 0;
	
	@Override
	public void update(float delta) {

		if (stage == 0)
		{
			tilt = 55;
			pos = 0;
		}
		else if (stage == 4)
		{
			tilt += delta*attack_speed;
			if (tilt > 55)
				tilt = 55;
			if (pos < 0)
			{
				pos += delta*attack_speed;
				if (pos > 0) {
					pos = 0;
					
					if (tilt == 55) stage = 0;
				}
			}
			else
			{
				pos -= delta*attack_speed;
				if (pos < 0) {
					pos = 0;
					
					if (tilt == 55) stage = 0;
				}
			}
		}
		else if (stage == 1)
		{
			tilt -= delta*attack_speed;
			pos += delta*(attack_speed);
			if (tilt < 0)
				tilt = 0;
			if (pos > 45)
				pos = 45;
		}
		else if (stage == 2)
		{
			tilt -= delta*attack_speed;
			if (tilt < 0)
				tilt = 0;
			speed += delta*(attack_speed+weight);
			pos -= speed;
			if (pos < -45)
			{
				node.setCollideMode(false, true);
				stage++;
			}
		}
		else if (stage == 3)
		{
			tilt += delta*attack_speed;
			if (tilt > 55)
				tilt = 55;
			pos += delta*attack_speed;
			if (pos > 0) {
				pos = 0;
				stage = 0;
			}

		}
		
		node.offsetRotation.idt().rotate(0, 1, 0, 180).rotate(1, 0, sign, -tilt).rotate(0, 0, 1, 90);
		node.offsetPosition.idt().translate(0, 0.5f, pos/45);
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
