package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Materials.MaterialAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.TextureAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillSubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Models.SubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;

public class RiggedModel implements Serializable {

	private static final long serialVersionUID = -3089869808778076973L;
	public Material[] materials;
	public RiggedModelNode rootNode;
	
	static ShaderProgram shader;
	
	public RiggedModel(RiggedModelNode node, Material[] materials) {
		this.rootNode = node;
		this.materials = materials;
		
		if (shader == null) {
			final String vertexShader = Gdx.files.internal("data/shaders/model/rigged_model.vertex.glsl").readString();
			final String fragmentShader = Gdx.files.internal("data/shaders/model/rigged_model.fragment.glsl").readString();
			
			shader = new ShaderProgram(vertexShader, fragmentShader);
			if (!shader.isCompiled())
			{
				Gdx.app.error("Problem loading shader:", shader.getLog());
			}
		}
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
	
	public void draw(PrototypeRendererGL20 renderer)
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
	
	
	public void bakeLight(LightManager lights, boolean bakeStatics)
	{
		rootNode.bakeLight(lights, bakeStatics, this);
	}
	
	public void dispose()
	{
		rootNode.dispose();
		for (Material m : materials)
		{
			m.dispose();
		}
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
	public static RiggedModel getSword(int length)
	{
		RiggedModelNode rootnode = new RiggedModelNode(new RiggedSubMesh[]{}, new int[]{}, new Matrix4(), new Matrix4(), 0, false);
		rootnode.setParent(null);
		
		RiggedSubMesh[] meshes = {new RiggedSubMesh("Hilt", GL20.GL_TRIANGLES, 1.0f, "file", "model!"), new RiggedSubMesh("Guard", GL20.GL_TRIANGLES, 1.0f, "file", "model(", "0", "0", "0.4")};	
		
		RiggedModelNode hilt = new RiggedModelNode(meshes, new int[]{0, 0}, new Matrix4().setToTranslation(0, 0, 0.7f), new Matrix4(), 0, false);
		
		hilt.setParent(rootnode);
		rootnode.setChilden(new RiggedModelNode[]{hilt});
		
		RiggedSubMesh[] meshesblade = {new RiggedSubMesh("Blade", GL20.GL_TRIANGLES, 0.5f, "file", "modelHBlade")};
		
		RiggedModelNode prevNode = hilt;
		for (int i = 0; i < length-1; i++)
		{
			RiggedModelNode node = new RiggedModelNode(meshesblade, new int[]{0}, new Matrix4().setToTranslation(0, 0, 0.5f), new Matrix4(), 100, true);
			node.setParent(prevNode);
			prevNode.setChilden(new RiggedModelNode[]{node});
			
			prevNode = node;
		}
		
		RiggedModelNode nodeblade = new RiggedModelNode(meshesblade, new int[]{0}, new Matrix4().setToTranslation(0, 0, 0.34f), new Matrix4(), 100, true);
		nodeblade.setParent(prevNode);
		prevNode.setChilden(new RiggedModelNode[]{nodeblade});

		RiggedSubMesh[] meshestip = {new RiggedSubMesh("Tip", GL20.GL_TRIANGLES, -0.5f, "file", "modelABlade")};
		
		RiggedModelNode nodeTip = new RiggedModelNode(meshestip, new int[]{0}, new Matrix4().setToTranslation(0, 0, 0.2f), new Matrix4(), 100, true);
		nodeTip.setParent(nodeblade);
		nodeblade.setChilden(new RiggedModelNode[]{nodeTip});
		nodeTip.setChilden(new RiggedModelNode[]{});
		
		MaterialAttribute t = new TextureAttribute("blank", 0);
		Material material = new Material("basic", t);
		
		return new RiggedModel(rootnode, new Material[]{material});
	}
}
