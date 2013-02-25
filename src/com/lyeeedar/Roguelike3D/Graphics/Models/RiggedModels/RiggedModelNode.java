package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Models.SubMesh;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.ForwardRenderer;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer;

public class RiggedModelNode implements Serializable
{
	public static final Matrix4 tmpMat = new Matrix4();
	public static final Vector3 tmpVec = new Vector3();
	
	private static final long serialVersionUID = -3949208107618544807L;
	public final RiggedSubMesh[] submeshes;
	public final int[] submeshMaterials;
	public transient Matrix4[] meshMatrixes;
	
	public final Matrix4 position;
	public final Matrix4 rotation;
	
	public transient Matrix4 offsetPosition = new Matrix4();
	public transient Matrix4 offsetRotation = new Matrix4();
	
	public RiggedModelNode[] childNodes;
	public transient RiggedModelNode parent;
	
	public float radius;
	public float renderRadius;
	
	public final float rigidity;
	
	public transient Matrix4 composedMatrix = new Matrix4();
	
	public RiggedModelBehaviour behaviour;
	
	public final boolean collidable;
	
	public boolean collideMode = false;
	
	public transient ParticleEmitter particleEmitter;
	public String particleEmitterUID;
	
	public RiggedModelNode(RiggedSubMesh[] submeshes, int[] submeshMaterials, Matrix4 position, Matrix4 rotation, int rigidity, boolean collidable)
	{
		if (submeshes.length != submeshMaterials.length)
		{
			System.err.println("Invalid number of materials to submeshes in RiggedModel!");
		}
		
		this.submeshes = submeshes;
		this.submeshMaterials = submeshMaterials;
		this.position = position;
		this.rotation = rotation;
		this.rigidity = rigidity;
		this.collidable = collidable;
	}
	
	public void equip(GameActor holder, int side)
	{
		if (behaviour != null) behaviour.equip(holder, side);
		
		for (RiggedModelNode rmn : childNodes) rmn.equip(holder, side);
	}
	
	public void setParticleEmitter(ParticleEmitter emitter)
	{
		this.particleEmitter = emitter;
		this.particleEmitterUID = emitter.UID;
	}
	
	public void setBehaviour(RiggedModelBehaviour behaviour)
	{
		this.behaviour = behaviour;
	}
	
	public void setParent(RiggedModelNode parent)
	{
		this.parent = parent;
	}
	
	public void setChilden(RiggedModelNode... children)
	{
		this.childNodes = children;
	}
	
	public void composeMatrixes(Matrix4 composed)
	{
		composedMatrix.set(composed).mul(position).mul(rotation).mul(offsetPosition).mul(offsetRotation);
		
		for (int i = 0; i < submeshes.length; i++)
		{
			meshMatrixes[i].set(composed).scale(submeshes[i].scale, submeshes[i].scale, submeshes[i].scale);
		}
		
		for (RiggedModelNode rgn : childNodes)
		{
			rgn.composeMatrixes(composedMatrix);
		}
		
		if (particleEmitter != null) particleEmitter.setPosition(Vector3.tmp3.set(0, 0, 0).mul(composedMatrix));
	}
	
	public void render(RiggedModel model, Renderer renderer)
	{
		for (int i = 0; i < submeshes.length; i++)
		{
			renderer.draw(submeshes[i], meshMatrixes[i], model.materials[submeshMaterials[i]], renderRadius);
		}
		
		for (RiggedModelNode rgn : childNodes)
		{
			rgn.render(model, renderer);
		}
	}
	
	public GameActor checkCollision(GameActor holder)
	{
		tmpVec.set(0, 0, 0).mul(composedMatrix);
		
		if (collidable && collideMode) {
			GameActor ga = GameData.level.checkEntities(tmpVec, radius, holder.UID);

			LevelObject lo = GameData.level.checkLevelObjects(tmpVec, radius);
			
			if (lo != null) ga = holder;
			
			if (GameData.level.checkLevelCollision(tmpVec, radius)) ga = holder;
			
			if (ga != null) {
				rotation.rotate(0, 1, 0, (100-rigidity)/50);
				return ga;
			}
		}
		
		for (RiggedModelNode rmn : childNodes)
		{
			GameActor ga = rmn.checkCollision(holder);
			if (ga != null) return ga;
		}
		
		return null;
	}
	
	public void update(float delta)
	{
		if (behaviour != null) behaviour.update(delta);
		
		for (RiggedModelNode rmn : childNodes)
		{
			rmn.update(delta);
		}
	}
	
	public void held()
	{
		if (behaviour != null) behaviour.held();
		
		for (RiggedModelNode rmn : childNodes)
		{
			rmn.held();
		}
	}
	
	public void released()
	{
		if (behaviour != null) behaviour.released();
		
		for (RiggedModelNode rmn : childNodes)
		{
			rmn.released();
		}
	}
	
	public void cancel()
	{
		if (behaviour != null) behaviour.cancel();
		
		for (RiggedModelNode rmn : childNodes)
		{
			rmn.cancel();
		}
	}

	public void setCollideMode(boolean mode, boolean propogateUp)
	{
		if (propogateUp)
		{
			if (parent != null)
			{
				parent.setCollideMode(mode, true);
			}
			else
			{
				for (RiggedModelNode rmn : childNodes)
				{
					rmn.setCollideMode(mode, false);
				}
			}
		}
		else
		{
			collideMode = mode;
			if (behaviour != null) behaviour.proccessCollideMode(mode);
			
			for (RiggedModelNode rmn : childNodes)
			{
				rmn.setCollideMode(mode, false);
			}
		}
	}
	
	public void create()
	{
		offsetPosition = new Matrix4();
		offsetRotation = new Matrix4();
		
		composedMatrix = new Matrix4();
		
		meshMatrixes = new Matrix4[submeshes.length];
		
		for (int i = 0; i < meshMatrixes.length; i++)
		{
			meshMatrixes[i] = new Matrix4();
		}
		
		for (RiggedSubMesh rsm : submeshes)
		{
			rsm.create();
		}
		
		BoundingBox box = new BoundingBox();
		
		for (RiggedSubMesh sm : submeshes)
		{
			box.ext(sm.getBoundingBox());
		}
		
		float longest = (box.getDimensions().x > box.getDimensions().z) ? box.getDimensions().x : box.getDimensions().z;
		longest = (box.getDimensions().y > longest) ? box.getDimensions().y : longest;
		this.radius = (longest / 2.0f);
		
		this.renderRadius = (radius > 1) ? radius : 1;
		

		for (RiggedModelNode rmn : childNodes)
		{
			rmn.create();
		}
		
		if (particleEmitter != null) particleEmitter.create();
		
	}
	
	public void fixReferences()
	{
		if (particleEmitterUID != null) particleEmitter = GameData.level.getParticleEmitter(particleEmitterUID);
		
		for (RiggedModelNode rmn : childNodes)
		{
			rmn.setParent(this);
			rmn.fixReferences();
		}
	}
	
	public void dispose()
	{
		for (RiggedSubMesh rsm : submeshes)
		{
			rsm.dispose();
		}

		for (RiggedModelNode rmn : childNodes)
		{
			rmn.dispose();
		}
		
		if (particleEmitter != null) {
			particleEmitter.dispose();
			GameData.level.removeParticleEmitter(particleEmitterUID);
		}
	}
	
	public void bakeLight(LightManager lights, boolean bakeStatics)
	{
		for (int i = 0; i < submeshes.length; i++)
		{
			submeshes[i].bakeLight(lights, bakeStatics, meshMatrixes[i]);
		}

		for (RiggedModelNode rmn : childNodes)
		{
			rmn.bakeLight(lights, bakeStatics);
		}
	}
}
