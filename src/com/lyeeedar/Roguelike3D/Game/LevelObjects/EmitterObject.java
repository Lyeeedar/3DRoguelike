package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;

public class EmitterObject extends LevelObject {
	
	ParticleEmitter emitter;
	
	public EmitterObject(Mesh mesh, float x, float y, float z, AbstractObject ao, ParticleEmitter emitter, boolean visible) {
		super(mesh, Color.WHITE, "blank", x, y, z, ao);
		
		this.visible = visible;
		
		this.emitter = emitter;
		
		GameData.particleEmitters.add(emitter);
	}
	
	public EmitterObject(Mesh mesh, Color colour, String texture, float x, float y, float z, AbstractObject ao, ParticleEmitter emitter, boolean visible) {
		super(mesh, colour, texture, x, y, z, ao);
		
		this.visible = visible;
		
		this.emitter = emitter;
		
		GameData.particleEmitters.add(emitter);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		Iterator<ParticleEmitter> pItr = GameData.particleEmitters.iterator();
		
		while (pItr.hasNext())
		{
			ParticleEmitter p = pItr.next();
			if (p.UID.equals(emitter.UID))
			{
				pItr.remove();
				return;
			}
		}
	}

	@Override
	public void activate() {
	}


}
