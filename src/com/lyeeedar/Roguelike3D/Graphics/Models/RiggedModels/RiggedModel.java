package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEffect;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer;

public class RiggedModel implements Serializable {

	private static final long serialVersionUID = -3089869808778076973L;
	public Material[] materials;
	public RiggedModelNode rootNode;
	
	public RiggedModel(RiggedModelNode node, Material[] materials) {
		this.rootNode = node;
		this.materials = materials;
	}
	
	public void equip(GameActor holder, int side)
	{
		rootNode.equip(holder, side);
	}
	
	boolean held = false;
	public void held()
	{
		if (!held) rootNode.held();
		held = true;
	}
	
	public void released()
	{
		if (held) rootNode.released();
		
		held = false;
	}
	
	public void update(float delta, GameActor holder)
	{
		rootNode.update(delta);
	}

	public void composeMatrixes(Matrix4 composed)
	{
		rootNode.composeMatrixes(composed);
	}
	
	public void draw(Renderer renderer)
	{
		rootNode.render(this, renderer);
	}
	
	public void create()
	{
		for (Material m : materials)
		{
			m.create();
		}
		
		rootNode.create();
	}
	
	public void fixReferences()
	{
		rootNode.fixReferences();
	}
	
	public void dispose()
	{
		System.out.println("dispose");
		
		rootNode.dispose();
		for (Material m : materials)
		{
			m.dispose();
		}
	}
	
	public void bakeLight(LightManager lights, boolean bakeStatics)
	{
		rootNode.bakeLight(lights, bakeStatics);
	}
	
	/**
	 * rootnode
	 *    |
	 *    hilt
	 *    |
	 *    blade
	 *    .
	 *    .
	 *    .
	 *    n
	 *    .
	 *    .
	 *    .
	 *    nodeblade
	 *    |
	 *    nodeTip
	 * @param length
	 * @return
	 */
	public static RiggedModel getSword(Level level, int length, float scale)
	{
		RiggedModelNode rootnode = new RiggedModelNode("Root", new RiggedSubMesh[]{}, new int[]{}, new Matrix4(), new Matrix4(), 0, false);
		rootnode.setParent(null);
		
		RiggedSubMesh[] meshes = {new RiggedSubMesh(GL20.GL_TRIANGLES, scale, "file", "model!"), new RiggedSubMesh(GL20.GL_TRIANGLES, scale, "file", "model(", "0", "0", ""+ scale*0.54f)};	
		
		RiggedModelNode hilt = new RiggedModelNode("Hilt", meshes, new int[]{0, 0}, new Matrix4().setToTranslation(0, 0, scale), new Matrix4(), 0, false);
		
		hilt.setParent(rootnode);
		rootnode.setChilden(hilt);
		
		RiggedSubMesh[] meshesblade = {new RiggedSubMesh(GL20.GL_TRIANGLES, scale, "file", "modelHBlade")};
		
		RiggedModelNode prevNode = hilt;
		for (int i = 0; i < length-1; i++)
		{
			RiggedModelNode node = new RiggedModelNode("Blade", meshesblade, new int[]{0}, new Matrix4().setToTranslation(0, 0, scale), new Matrix4(), 100, true);
			node.setParent(prevNode);
			prevNode.setChilden(node);
			
			prevNode = node;
		}
		
		RiggedModelNode nodeblade = new RiggedModelNode("Blade", meshesblade, new int[]{0}, new Matrix4().setToTranslation(0, 0, scale), new Matrix4(), 100, true);
		nodeblade.setParent(prevNode);
		prevNode.setChilden(nodeblade);

		RiggedSubMesh[] meshestip = {new RiggedSubMesh(GL20.GL_TRIANGLES, -scale, "file", "modelABlade")};
		
		RiggedModelNode nodeTip = new RiggedModelNode("Tip", meshestip, new int[]{0}, new Matrix4().setToTranslation(0, 0, scale*0.2f), new Matrix4(), 100, true);
		nodeTip.setParent(nodeblade);
		nodeblade.setChilden(nodeTip);
		nodeTip.setChilden();
		
		Material material = new Material("basic");
		material.setColour(new Color(1.0f, 1.0f, 1.0f, 1.0f));
		material.setTexture("rockmag");
		
		return new RiggedModel(rootnode, new Material[]{material});
	}
	
	public static RiggedModel getTorch(Level level)
	{
		RiggedModelNode rootnode = new RiggedModelNode("Root", new RiggedSubMesh[]{}, new int[]{}, new Matrix4(), new Matrix4(), 0, false);
		rootnode.setParent(null);
		
		RiggedSubMesh[] meshes = {new RiggedSubMesh(GL20.GL_TRIANGLES, 1, "cube", "0.1", "0.1", "3")};
		
		RiggedModelNode node = new RiggedModelNode("Torch", meshes, new int[]{0}, new Matrix4().setToTranslation(0, 0, 1.5f), new Matrix4(), 100, true);
		
		rootnode.setChilden(node);
		node.setParent(rootnode);
		
		RiggedSubMesh[] meshes1 = {new RiggedSubMesh(GL20.GL_TRIANGLES, 1, "cube", "0.01", "0.01", "0.01")};
		
		RiggedModelNode node1 = new RiggedModelNode("Tip", meshes1, new int[]{0}, new Matrix4().setToTranslation(0, -0.5f, -0.5f), new Matrix4(), 100, true);
		
		
		ParticleEffect effect = new ParticleEffect(5);
		ParticleEmitter flame = new ParticleEmitter();
		flame.setEmitterParameters(1, 1, 0.04f, 0.1f, 0.1f, 0.1f, 0, GL20.GL_ONE, GL20.GL_ONE);
		flame.setParticleParameters("f", 50, 50, Color.YELLOW, Color.RED, 0, 1.7f, 0);
		flame.addLight(false, 0.04f, 0.3f, Color.ORANGE, true);
		effect.addEmitter(flame, 
				0, 0, 0);
		effect.create();
		
		node.setChilden(node1);
		
		node1.setParticleEffect(effect);
		node1.setParent(node);
		node1.setChilden();
		
		level.addParticleEffect(effect);
		
		Material material = new Material("basic");
		material.setColour(new Color(1.0f, 1.0f, 1.0f, 1.0f));
		material.setTexture("wood");
		
		return new RiggedModel(rootnode, new Material[]{material});
		
	}
	
	public RiggedModelNode getNode(String ID)
	{
		return rootNode.getNode(ID);
	}
}
