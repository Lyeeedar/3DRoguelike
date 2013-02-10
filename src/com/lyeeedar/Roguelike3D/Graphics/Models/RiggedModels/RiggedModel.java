package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Materials.MaterialAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.TextureAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillSubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Models.SubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;

public class RiggedModel implements Serializable {

	private static final long serialVersionUID = -3089869808778076973L;
	Material[] materials;
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
	
	public void update(float delta, String ignoreUID)
	{
		rootNode.update(delta);
		if (rootNode.checkCollision(ignoreUID)) {
			rootNode.cancel();
		}
	}

	public void composeMatrixes(Matrix4 composed)
	{
		rootNode.composeMatrixes(composed);
	}
	
	public void draw(Camera cam)
	{
		rootNode.render(this, cam);
	}
	
	public static RiggedModel getSword()
	{
		RiggedModelNode node = new RiggedModelNode(new SubMesh[]{}, new int[]{}, new Matrix4().setToTranslation(-0.5f, -1, 0), new Matrix4(), 0, false);
		node.setBehaviour(new RiggedSword(node));
		
		Mesh hilt = Shapes.genCuboid(0.1f, 0.1f, 1);
		Mesh guard = Shapes.genCuboid(0.7f, 0.1f, 0.1f, 0, 0, 0.5f);
		SubMesh[] meshes = {new StillSubMesh("Hilt", hilt, GL20.GL_TRIANGLES), new StillSubMesh("Guard", guard, GL20.GL_TRIANGLES)};	
		RiggedModelNode node1 = new RiggedModelNode(meshes, new int[]{0, 0}, new Matrix4().setToTranslation(0, 0, 0), new Matrix4(), 0, false);
		
		Mesh blade = Shapes.genCuboid(0.2f, 0.2f, 1f);
		SubMesh[] meshes2 = {new StillSubMesh("Blade", blade, GL20.GL_TRIANGLES)};
		RiggedModelNode node2 = new RiggedModelNode(meshes2, new int[]{0}, new Matrix4().setToTranslation(0, 0, 1f), new Matrix4(), 0, true);
		RiggedModelNode node3 = new RiggedModelNode(meshes2, new int[]{0}, new Matrix4().setToTranslation(0, 0, 1f), new Matrix4(), 0, true);
		RiggedModelNode node4 = new RiggedModelNode(meshes2, new int[]{0}, new Matrix4().setToTranslation(0, 0, 1f), new Matrix4(), 0, true);
		RiggedModelNode node5 = new RiggedModelNode(meshes2, new int[]{0}, new Matrix4().setToTranslation(0, 0, 1f), new Matrix4(), 0, true);
		
		node.setReferences(null, new RiggedModelNode[]{node1});
		node1.setReferences(node, new RiggedModelNode[]{node2});
		node2.setReferences(node1, new RiggedModelNode[]{node3});
		node3.setReferences(node2, new RiggedModelNode[]{node4});
		node4.setReferences(node3, new RiggedModelNode[]{node5});
		node5.setReferences(node4, new RiggedModelNode[]{});
		
		MaterialAttribute t = new TextureAttribute(GameData.loadTexture("blank"), null, null, 0);
		Material material = new Material("basic", t);
		
		return new RiggedModel(node, new Material[]{material});
	}
}
