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

	@Override
	public void update(float delta) {	
		
		float move = delta * 10;
		
		velocity.y -= GameData.gravity*move;
		

		if (grounded)
		{
			if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A)) left_right(move);
			if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D)) left_right(-move);

			if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W)) forward_backward(move);
			if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S)) forward_backward(-move);

			if ( (grounded) && (Gdx.input.isKeyPressed(Keys.SPACE))) velocity.y += 0.4;
		}
		
		if (Gdx.input.isKeyPressed(Keys.B))
		{
//			float x = position.x;
//			float z = position.z;
//			
//			for (int i = 0; i < 20; i++)
//			{
//				TextureRegion t = new TextureRegion(new Texture(Gdx.files.internal("data/skins/loading_bar.png")));
//				Decal d = Decal.newDecal(5, 5, t, true);
//				
//				d.setPosition(x+(i/2f), 10-(i/2f), z+(i/10f));
//				
//				GameData.decals.add(d);
//			}
			
			ParticleEmitter p = new ParticleEmitter(position.x, position.y, position.z, 10, 10, 10, 1);
			
			p.setDecal("data/textures/texf.png", new Vector3(0.0f, 1.5f, 0.0f), 2, Color.YELLOW, Color.RED, 1, 1);
			GameData.particleEmitters.add(p);
		}
		
		applyMovement();

		float pitch = (float)Gdx.input.getDeltaY()*yrotate*move;
		Vector3 dir = rotation.cpy().nor();
		//System.out.println(dir.y);
		//if( (dir.y>-0.7) && (pitch<0) || (dir.y<+0.7) && (pitch>0) )
		//{
			//rotate(pitch, 1, 0, 0);
			//rotate(pitch, rotation.x, 0, rotation.y);
		//}

		if( (dir.y>-0.7) && (pitch<0) || (dir.y<+0.7) && (pitch>0) )
		{
			Vector3 localAxisX = rotation.cpy();
			localAxisX.crs(up.tmp()).nor();
			rotate(pitch, localAxisX.x, localAxisX.y, localAxisX.z);

		}

		rotate((float)Gdx.input.getDeltaX()*xrotate*move, 0, 1, 0);
		
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
