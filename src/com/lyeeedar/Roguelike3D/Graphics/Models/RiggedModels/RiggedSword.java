package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

public class RiggedSword extends RiggedModelBehaviour {

	public RiggedSword(RiggedModelNode node) {
		super(node);
	}

	int stage = 0;
	
	float angle = 45;
	
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
				angle += delta*100;
				if (angle > 45)
					stage = 0;
			}
			else
			{
				angle -= delta*100;
				if (angle < 45)
					stage = 0;
			}
		}
		else if (stage == 1)
		{
			angle += delta*100;
			if (angle > 90)
				stage ++;
		}
		else if (stage == 2)
		{
			angle -= delta*700;
			if (angle < -45)
				stage++;
		}
		else if (stage == 3)
		{
			angle += delta*100;
			if (angle > 45)
				stage = 0;
		}
		
		node.rotation.setToRotation(0, 1, 0, 180).rotate(1, 0, 1, -angle);
	}

	@Override
	public void activate() {
		if (stage == 0) stage = 1;
	}

	@Override
	public void cancel() {
		stage = 4;
	}
	

}
