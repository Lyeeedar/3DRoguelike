package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Spell.Spell;
import com.lyeeedar.Roguelike3D.Game.Spell.SpellBehaviourBolt;
import com.lyeeedar.Roguelike3D.Game.Spell.SpellBehaviourSingleDamage;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Materials.MaterialAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.TextureAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillSubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Models.SubMesh;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.ForwardRenderer;
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
		RiggedModelNode rootnode = new RiggedModelNode(new RiggedSubMesh[]{}, new int[]{}, new Matrix4(), new Matrix4(), 0, false);
		rootnode.setParent(null);
		
		RiggedSubMesh[] meshes = {new RiggedSubMesh("Hilt", GL20.GL_TRIANGLES, scale, "file", "model!"), new RiggedSubMesh("Guard", GL20.GL_TRIANGLES, scale, "file", "model(", "0", "0", ""+ scale*0.54f)};	
		
		RiggedModelNode hilt = new RiggedModelNode(meshes, new int[]{0, 0}, new Matrix4().setToTranslation(0, 0, scale), new Matrix4(), 0, false);
		
		hilt.setParent(rootnode);
		rootnode.setChilden(hilt);
		
		RiggedSubMesh[] meshesblade = {new RiggedSubMesh("Blade", GL20.GL_TRIANGLES, scale, "file", "modelHBlade")};
		
		RiggedModelNode prevNode = hilt;
		for (int i = 0; i < length-1; i++)
		{
			RiggedModelNode node = new RiggedModelNode(meshesblade, new int[]{0}, new Matrix4().setToTranslation(0, 0, scale), new Matrix4(), 100, true);
			node.setParent(prevNode);
			prevNode.setChilden(node);
			
			prevNode = node;
		}
		
		RiggedModelNode nodeblade = new RiggedModelNode(meshesblade, new int[]{0}, new Matrix4().setToTranslation(0, 0, scale), new Matrix4(), 100, true);
		nodeblade.setParent(prevNode);
		prevNode.setChilden(nodeblade);

		RiggedSubMesh[] meshestip = {new RiggedSubMesh("Tip", GL20.GL_TRIANGLES, -scale, "file", "modelABlade")};
		
		RiggedModelNode nodeTip = new RiggedModelNode(meshestip, new int[]{0}, new Matrix4().setToTranslation(0, 0, scale*0.2f), new Matrix4(), 100, true);
		nodeTip.setParent(nodeblade);
		nodeblade.setChilden(nodeTip);
		nodeTip.setChilden();
		
		Material material = new Material("basic");
		material.setColour(new Colour());
		material.setTexture("rockmag");
		
		return new RiggedModel(rootnode, new Material[]{material});
	}
	
	public static RiggedModel getTorch(Level level)
	{
		RiggedModelNode rootnode = new RiggedModelNode(new RiggedSubMesh[]{}, new int[]{}, new Matrix4(), new Matrix4(), 0, false);
		rootnode.setParent(null);
		
		RiggedSubMesh[] meshes = {new RiggedSubMesh("Torch", GL20.GL_TRIANGLES, 1, "cube", "0.1", "0.1", "3")};
		
		RiggedModelNode node = new RiggedModelNode(meshes, new int[]{0}, new Matrix4().setToTranslation(0, 0, 1.5f), new Matrix4(), 100, true);
		
		rootnode.setChilden(node);
		node.setParent(rootnode);
		
		RiggedSubMesh[] meshes1 = {new RiggedSubMesh("Flame", GL20.GL_TRIANGLES, 1, "cube", "0.01", "0.01", "0.01")};
		
		RiggedModelNode node1 = new RiggedModelNode(meshes1, new int[]{0}, new Matrix4().setToTranslation(0, -0.5f, -0.5f), new Matrix4(), 100, true);
		
		ParticleEmitter p = new ParticleEmitter(0, 0, 0, 0.1f, 0.1f, 0.1f, 0.04f, 350);
		p.setTexture("texf", new Vector3(0.0f, 1.7f, 0.0f), 2.0f, new Colour(0.8f, 1.0f, 0.3f, 1.0f), new Colour(0.9f, 0.2f, 0.0f, 1.0f), true, 0.01f, 0.9f, true, false);
		p.create();
		
		node.setChilden(node1);
		
		node1.setParticleEmitter(p);
		node1.setParent(node);
		node1.setChilden();
		
		level.getParticleEmitters().add(p);
		
		Material material = new Material("basic");
		material.setColour(new Colour());
		material.setTexture("wood");
		
		ParticleEmitter pp = new ParticleEmitter(0, 0, 0, 0.5f, 0.5f, 0.5f, 0.001f, 1050);
		pp.setTexture("texf", new Vector3(0.0f, 1.7f, 0.0f), 2.0f, new Colour(0.8f, 1.0f, 0.3f, 1.0f), new Colour(0.9f, 0.2f, 0.0f, 1.0f), true, 0.03f, 0.5f, true, false);
		
		Spell spell = new Spell("");
		spell.setDamage(GameData.getDamageMap(), GameData.getElementMap(), 1);
		spell.setBehaviour(new SpellBehaviourBolt(), new SpellBehaviourSingleDamage());
		spell.initialise(Vector3.tmp, Vector3.tmp2, pp, pp.getRadius());
		
		RiggedModelBehaviourCastSpell cast = new RiggedModelBehaviourCastSpell(node1, spell);
		node1.setBehaviour(cast);
		
		return new RiggedModel(rootnode, new Material[]{material});
		
	}
}
