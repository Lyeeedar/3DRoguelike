package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import java.util.Iterator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Elements;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;

public class DamageField extends LevelObject {

	float damage;
	Elements element;
	
	ParticleEmitter emitter;
	
	public DamageField(Mesh mesh, float x, float y, float z, AbstractObject ao, float damage, Elements element, ParticleEmitter emitter) {
		super(mesh, Color.WHITE, "blank", x, y, z, ao);
		
		visible = false;
		
		this.damage = damage;
		this.element = element;
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


}
