package com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillSubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Models.SubMesh;

public class RiggedSubMesh implements Serializable {

	private static final long serialVersionUID = -530256766516721412L;
	public final float scale;
	public final int primitiveType;
	
	public final String[] meshValues;
	
	private transient Mesh mesh;
	private transient BoundingBox box;
	public transient boolean created = false;
	
	public RiggedSubMesh(int primitiveType, float scale, String... meshValues) {
		this.primitiveType = primitiveType;
		this.scale = scale;
		this.meshValues = meshValues;
	}

	public BoundingBox getBoundingBox() {
		return box;
	}
	
	public void create()
	{
		if (created || mesh != null) return;
		
		created = true;
		
		if (meshValues[0].equalsIgnoreCase("cube"))
		{
			if (meshValues.length == 4)
				mesh = Shapes.genCuboid(Float.parseFloat(meshValues[1]), Float.parseFloat(meshValues[2]), Float.parseFloat(meshValues[3]));
			else
				mesh = Shapes.genCuboid(Float.parseFloat(meshValues[1]), Float.parseFloat(meshValues[2]), Float.parseFloat(meshValues[3]),
						Float.parseFloat(meshValues[4]), Float.parseFloat(meshValues[5]), Float.parseFloat(meshValues[6]));
		}
		else if (meshValues[0].equalsIgnoreCase("file"))
		{
			mesh = GameData.loadMesh(meshValues[1]);
			
			if (meshValues.length > 2)
			{
				float x = Float.parseFloat(meshValues[2]);
				float y = Float.parseFloat(meshValues[3]);
				float z = Float.parseFloat(meshValues[4]);
				
				mesh = Shapes.copyMesh(mesh);
				
				Shapes.translateMesh(mesh, x, y, z);
			}
		}
		else
		{
			System.err.println("Invalid mesh type "+meshValues[0]);
		}
		
		box = mesh.calculateBoundingBox();
		box.min.mul(scale);
		box.max.mul(scale);
	}
	
	public Mesh getMesh()
	{
		return mesh;
	}
	
	public void dispose()
	{
		if (!meshValues[0].equals("file") && created)
		{
			mesh.dispose();
			created = false;
		}
	}
	
	public void bakeLight(LightManager lights, boolean bakeStatics, Matrix4 mat)
	{
		Mesh newMesh = Shapes.insertLight(mesh, lights, bakeStatics, mat);
		
		if (!meshValues[0].equals("file"))
		{
			mesh.dispose();
		}
		
		mesh = newMesh;
	}
}
