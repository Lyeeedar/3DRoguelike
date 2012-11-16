package com.lyeeedar.Roguelike3D.Graphics.Models;

import com.badlogic.gdx.graphics.Mesh;

public class StillModelLoader {
	
	public static StillModel createFromList(StillSubMesh... meshes)
	{
		return new StillModel(meshes);
	}
	
	public static StillModel createFromArray(StillSubMesh[] meshes)
	{
		return new StillModel(meshes);
	}
	
	public static StillSubMesh convertMeshtoSubMesh(Mesh mesh, String name, int primitive_type)
	{
		return new StillSubMesh(name, mesh, primitive_type);
	}

}
