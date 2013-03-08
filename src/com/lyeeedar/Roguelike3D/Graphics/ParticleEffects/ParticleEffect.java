package com.lyeeedar.Roguelike3D.Graphics.ParticleEffects;

import java.io.Serializable;

import com.lyeeedar.Roguelike3D.Bag;

public class ParticleEffect implements Serializable {

	private static final long serialVersionUID = -5746609278217754852L;
	
	
	Bag<ParticleEmitter> emitters = new Bag<ParticleEmitter>();

	public ParticleEffect() {
	}

}
