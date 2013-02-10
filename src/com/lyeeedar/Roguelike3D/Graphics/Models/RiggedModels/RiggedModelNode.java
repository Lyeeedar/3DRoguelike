package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Models.SubMesh;

public class RiggedModelNode implements Serializable
{
	public static final Matrix4 tmpMat = new Matrix4();
	public static final Vector3 tmpVec = new Vector3();
	
	private static final long serialVersionUID = -3949208107618544807L;
	public final SubMesh[] submeshes;
	public final int[] submeshMaterials;
	
	public final Matrix4 position;
	public final Matrix4 rotation;
	public RiggedModelNode[] childNodes;
	public RiggedModelNode parent;
	
	public final float radius;
	
	public final float rigidity;
	
	public final Matrix4 composedMatrix = new Matrix4();
	
	public RiggedModelBehaviour behaviour;
	
	public final boolean collidable;
	
	public RiggedModelNode(SubMesh[] submeshes, int[] submeshMaterials, Matrix4 position, Matrix4 rotation, int rigidity, boolean collidable)
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
		
		BoundingBox box = new BoundingBox();
		
		for (SubMesh sm : submeshes)
		{
			box.ext(sm.mesh.calculateBoundingBox());
		}
		
		float longest = (box.getDimensions().x > box.getDimensions().z) ? box.getDimensions().x : box.getDimensions().z;
		longest = (box.getDimensions().y > longest) ? box.getDimensions().y : longest;
		this.radius = longest / 2.0f;
	}
	
	public void setBehaviour(RiggedModelBehaviour behaviour)
	{
		this.behaviour = behaviour;
	}
	
	public void setReferences(RiggedModelNode parent, RiggedModelNode[] children)
	{
		this.parent = parent;
		this.childNodes = children;
	}
	
	public void composeMatrixes(Matrix4 composed)
	{
		composedMatrix.set(composed).mul(position).mul(rotation);
		
		for (RiggedModelNode rgn : childNodes)
		{
			rgn.composeMatrixes(composedMatrix);
		}
	}
	
	public void render(RiggedModel model, Camera cam)
	{
		for (int i = 0; i < submeshes.length; i++)
		{
			RiggedModel.shader.begin();
			
			RiggedModel.shader.setUniformMatrix("u_pv", cam.combined);
			RiggedModel.shader.setUniformMatrix("u_model_matrix", composedMatrix);
			
			
			model.materials[submeshMaterials[i]].bind(RiggedModel.shader);
			
			submeshes[i].mesh.render(RiggedModel.shader, submeshes[i].primitiveType);
			
			RiggedModel.shader.end();
		}
		
		for (RiggedModelNode rgn : childNodes)
		{
			rgn.render(model, cam);
		}
	}
	
	public boolean checkCollision(String ignoreUID)
	{
		tmpVec.set(0, 0, 0).mul(composedMatrix);
		
		if (collidable && GameData.level.checkCollision(tmpVec, radius, ignoreUID)) {
			rotation.rotate(0, 1, 0, 10);
			return true;
		}
		
		for (RiggedModelNode rmn : childNodes)
		{
			if (rmn.checkCollision(ignoreUID)) return true;
		}
		
		return false;
	}
	
	public void update(float delta)
	{
		if (behaviour != null) behaviour.update(delta);
		
		for (RiggedModelNode rmn : childNodes)
		{
			rmn.update(delta);
		}
	}
	
	public void activate()
	{
		if (behaviour != null) behaviour.activate();
		
		for (RiggedModelNode rmn : childNodes)
		{
			rmn.activate();
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
}
