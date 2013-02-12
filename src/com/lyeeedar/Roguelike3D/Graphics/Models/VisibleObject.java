/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Graphics.Models;

import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Materials.ColorAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Materials.MaterialAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.TextureAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;

public class VisibleObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -455929496204091194L;
	
	public transient StillModel model;
	public transient StillModelAttributes attributes;
	
	public final String[] modelData;
	public final String texture;
	public final float scale;
	public final int primitive_type;
	public final Colour colour;
	
	public boolean disposed = true;
	
	public VisibleObject(int primitive_type, Colour colour, String textureName, float scale, String... modelData)
	{
		this.texture = textureName;
		this.modelData = modelData;
		this.primitive_type = primitive_type;
		this.colour = colour;
		this.scale = scale;
	}
	
	public VisibleObject(Mesh mesh, Colour colour, String texture, int primitive_type, float scale)
	{
		this.texture = texture;
		this.modelData = new String[]{"mesh"};
		this.primitive_type = primitive_type;
		this.colour = colour;
		this.scale = scale;

		loadGraphics(mesh);
	}

	public void create()
	{
		if (model != null && attributes != null) return;
		
		Mesh mesh = getMesh();
		
		loadGraphics(mesh);
		
	}
	
	private void loadGraphics(Mesh mesh)
	{
		//mesh = Shapes.insertColour(mesh, colour);
		//mesh = Shapes.insertTangents(mesh);
		
		SubMesh[] meshes = {new StillSubMesh("SubMesh1", mesh, primitive_type)};
		model = new StillModel(meshes);
		MaterialAttribute t = new TextureAttribute(texture, 0);
		MaterialAttribute c = new ColorAttribute(colour, ColorAttribute.colour);
		
		Material material = new Material("basic", t, c);
		material.create();
		
		BoundingBox box = mesh.calculateBoundingBox();
		
		attributes = new StillModelAttributes(material, (box.getDimensions().x > box.getDimensions().z) ? box.getDimensions().x : box.getDimensions().z, scale, box.getDimensions());
	
		disposed = false;
	}
	
	private Mesh getMesh()
	{
		if (modelData[0].equalsIgnoreCase("file"))
		{
			return GameData.loadMesh(modelData[1]);
		}
		else if (modelData[0].equalsIgnoreCase("cube"))
		{
			return Shapes.genCuboid(Float.parseFloat(modelData[1]), Float.parseFloat(modelData[2]), Float.parseFloat(modelData[3]));
		}
		else if (modelData[0].equalsIgnoreCase("mesh"))
		{
			System.err.println("Cannot recreate predefined mesh object!!");
		}
		
		System.err.println("Failed to create mesh for type "+modelData[0]);
		return null;
	}
	
	public void render(PrototypeRendererGL20 protoRenderer)
	{
		protoRenderer.draw(model, attributes);
	}

	public void dispose()
	{
		if (!modelData[0].equals("file"))
			model.dispose();
		model = null;
		attributes.dispose();
		attributes = null;
		
		disposed = true;
	}
	
	public void bakeLights(LightManager lights, boolean bakeStatics)
	{
		int primitive_type = model.subMeshes[0].primitiveType;
		
		Mesh oldMesh = model.subMeshes[0].mesh;
		
		Matrix4 mat = new Matrix4();
		mat.set(attributes.getTransform()).scale(attributes.scale, attributes.scale, attributes.scale).mul(attributes.getRotation());
		Mesh newMesh = Shapes.insertLight(oldMesh, lights, bakeStatics, mat, attributes.getMaterial().affectedByLighting);
		
		model.subMeshes[0] = new StillSubMesh("SubMesh1", newMesh, primitive_type);
	}

}
