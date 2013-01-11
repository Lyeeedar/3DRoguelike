/*******************************************************************************
 * Copyright (c) 2012 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Graphics.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Materials.ColorAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Materials.MaterialAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.TextureAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;

public class VisibleObject {
	
	public StillModel model;
	public StillModelAttributes attributes;
	
	public VisibleObject(StillModel model, StillModelAttributes attributes)
	{
		this.model = model;
		this.attributes = attributes;
	}

	public VisibleObject(Mesh mesh, int primitive_type, Color colour, String textureName, float scale)
	{
		Texture diffuseTexture = new Texture(Gdx.files.internal("data/textures/"+textureName+".png"), true);
		diffuseTexture.setWrap( TextureWrap.Repeat, TextureWrap.Repeat );
		diffuseTexture.setFilter( TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear );
		
		Texture normalTexture = null;
		if (Gdx.files.internal("data/textures/"+textureName+".map.png").exists())
		{
			normalTexture = new Texture(Gdx.files.internal("data/textures/"+textureName+".map.png"), true);
			normalTexture.setWrap( TextureWrap.Repeat, TextureWrap.Repeat );
			normalTexture.setFilter( TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear );
			System.out.println("Normal map found for "+textureName);
		}

		create(mesh, primitive_type, colour, diffuseTexture, normalTexture, scale);
	}

	private void create(Mesh mesh, int primitive_type, Color colour, Texture diffuseTexture, Texture normalTexture, float scale)
	{
		mesh = Shapes.insertColour(mesh, colour);
		
		SubMesh[] meshes = {new StillSubMesh("SubMesh1", mesh, primitive_type)};
		model = new StillModel(meshes);
		MaterialAttribute t = new TextureAttribute(diffuseTexture, normalTexture, null, 0);
		
		Material material = new Material("basic", t);
		
		BoundingBox box = mesh.calculateBoundingBox();
		
		attributes = new StillModelAttributes(material, box.getDimensions().x+ box.getDimensions().z, scale);
	}

	public static VisibleObject createCuboid(float x, float y, float z, int primitive_type, Color colour, String textureName, float scale)
	{
		return new VisibleObject(Shapes.genCuboid(x, y, z), primitive_type, colour, textureName, scale);
	}
	
	public void render(PrototypeRendererGL20 protoRenderer)
	{
		protoRenderer.draw(model, attributes);
	}

	public void dispose()
	{
		model.dispose();
	}
	
	public void bakeLights(LightManager lights, boolean bakeStatics)
	{
		int primitive_type = model.subMeshes[0].primitiveType;
		
		Mesh oldMesh = model.subMeshes[0].mesh;
		
		Mesh newMesh = Shapes.insertLight(oldMesh, lights, bakeStatics, attributes.getSortCenter(), attributes.getRotation(), attributes.getMaterial().affectedByLighting);
		
		model.dispose();
		
		SubMesh[] meshes = {new StillSubMesh("SubMesh1", newMesh, primitive_type)};
		model = new StillModel(meshes);
	}

}
