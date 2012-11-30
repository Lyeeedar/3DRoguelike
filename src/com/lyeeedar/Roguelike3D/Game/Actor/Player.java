/*******************************************************************************
 * Copyright (c) 2012 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Game.Actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Elements;
import com.lyeeedar.Roguelike3D.Game.Spell.Spell;
import com.lyeeedar.Roguelike3D.Game.Spell.Spell.SpellBehaviour;
import com.lyeeedar.Roguelike3D.Graphics.Materials.GlowAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.Particle;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;

public class Player extends GameActor {

	public final Vector3 offsetPos = new Vector3();
	public final Vector3 offsetRot = new Vector3();
	
	public Player(VisibleObject vo, float x, float y, float z) {
		super(vo, x, y, z);
		// TODO Auto-generated constructor stub
	}
	
	public Player(String model, Color colour, String texture, float x, float y, float z)
	{
		super(model, colour, texture, x, y, z);
	}
	
	public Player(Mesh mesh, Color colour, String texture, float x, float y, float z)
	{
		super(mesh, colour, texture, x, y, z);
	}
	
	float cooldown = 0;

	float move = 0;
	float xR = 0;
	float yR = 0;
	@Override
	public void update(float delta) {	
		
		cooldown -= delta;
		
		move = delta * 10;
		
		velocity.y -= GameData.gravity*move;
		

		if (grounded)
		{
			if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) left_right(move*speed);
			if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) left_right(-move*speed);

			if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) forward_backward(move*speed);
			if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) forward_backward(-move*(speed/2));

			if ( (grounded) && (Gdx.input.isKeyPressed(Keys.SPACE))) velocity.y += 0.4;
		}
		
		if (Gdx.input.isKeyPressed(Keys.B) && cooldown < 0)
		{			
			ParticleEmitter p = new ParticleEmitter(position.x, position.y-5, position.z, 5, 5, 5, 0.75f, 100);
			
			p.setDecal("data/textures/texf.png", new Vector3(0.0f, 2.0f, 0.0f), 2, Color.YELLOW, Color.RED, 1, 1, true);
			GameData.particleEmitters.add(p);
			
			cooldown = 1;
		}
		
		applyMovement();

		xR = (float)Gdx.input.getDeltaX()*xrotate*move;
		yR = (float)Gdx.input.getDeltaY()*yrotate*move;
		
		if (xR < -5.0f) xR = -5.0f;
		else if (xR > 5.0f) xR = 5.0f;
		
		if (yR < -3.0f) yR = -3.0f;
		else if (yR > 3.0f) yR = 3.0f;
		
		Yrotate(yR);

		Xrotate(xR);
		
		if (Gdx.input.isKeyPressed(Keys.C)) {
			Spell spell = new Spell("modelf", Color.RED, "blank", position.x, position.y, position.z);
			spell.setData(Elements.FIRE, UID, 5, 1);
			spell.addBehaviour(SpellBehaviour.PROJECTILE);
			
			spell.getRotation().set(rotation);
			
			spell.vo.attributes.material.addAttributes(new GlowAttribute(0.2f, GlowAttribute.glow));
			
			GameData.level.addSpell(spell);
		}
	}
	
	

}
