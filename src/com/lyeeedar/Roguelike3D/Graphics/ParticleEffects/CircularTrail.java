package com.lyeeedar.Roguelike3D.Graphics.ParticleEffects;

import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameObject;

public class CircularTrail extends MotionTrail{
	
	final Vector3 offset;
	
	final Vector3 startRotation;
	final Vector3 endRotation;
	
	Vector3 currentRotation;
	
	final float nearDist;
	final float farDist;
	
	final GameObject center;
	
	final Vector3 rotationStep;

	public CircularTrail(Vector3 offset, Vector3 startRot, Vector3 endRot, float nearDist, float farDist, GameObject center, int vertsNum, float timeToComplete) 
	{
		super(vertsNum, timeToComplete);
		
		this.offset = offset;
		this.startRotation = startRot;
		this.endRotation = endRot;
		this.nearDist = nearDist;
		this.farDist = farDist;
		this.center = center;
		
		rotationStep = endRotation.cpy();
		rotationStep.sub(startRotation);
		rotationStep.div(timeToComplete);
		
		currentRotation = startRotation.cpy();
		
		tmpVec.set(tmpVec.set(currentRotation).nor());
		
		for (int i = 0; i < vertsNum; i++)
		{
			tmpVec2.set(tmpVec).mul(nearDist).add(center.getPosition()).add(offset);
			addVert(tmpVec2);
			
			tmpVec2.set(tmpVec).mul(farDist).add(center.getPosition()).add(offset);
			addVert(tmpVec2);
		}
		
		updateVerts();
	}

	protected void updatePositions(float delta)
	{		
		tmpVec.set(rotationStep);
		tmpVec.mul(delta);
		
		currentRotation.add(tmpVec);
		
		tmpVec.set(currentRotation).nor();
		
		tmpVec2.set(tmpVec).mul(nearDist).add(center.getPosition()).add(offset);
		addVert(tmpVec2);
		
		tmpVec2.set(tmpVec).mul(farDist).add(center.getPosition()).add(offset);
		addVert(tmpVec2);
		
		updateVerts();
	}
}
