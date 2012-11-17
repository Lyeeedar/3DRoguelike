package com.lyeeedar.Roguelike3D.Graphics.Models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.math.collision.BoundingBox;
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

	public VisibleObject(Mesh mesh, int primitive_type, Color colour, String textureName)
	{
		Texture texture = new Texture(Gdx.files.internal("data/textures/"+textureName+".png"), true);
		//texture = new Texture(Gdx.files.internal("data/textures/"+textureName+".png"));
		texture.setWrap( TextureWrap.Repeat, TextureWrap.Repeat );
		texture.setFilter( TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear );
		//texture.setFilter( TextureFilter.Nearest, TextureFilter.Nearest );

		create(mesh, primitive_type, colour, texture);
	}

	public VisibleObject(Mesh mesh, int primitive_type, Color colour, Texture texture)
	{
		create(mesh, primitive_type, colour, texture);
	}

	public void create(Mesh mesh, int primitive_type, Color colour, Texture texture)
	{
		SubMesh[] meshes = {new StillSubMesh("SubMesh1", mesh, primitive_type)};
		model = new StillModel(meshes);
		
		MaterialAttribute c = new ColorAttribute(colour, ColorAttribute.diffuse);
		MaterialAttribute t = new TextureAttribute(texture, 0, TextureAttribute.diffuseTexture);
		Material material = new Material("basic", c, t);
		
		BoundingBox box = mesh.calculateBoundingBox();
		
		attributes = new StillModelAttributes(material, box.getDimensions().x+ box.getDimensions().z);
	}

	public static VisibleObject createCuboid(float x, float y, float z, int primitive_type, Color colour, String textureName)
	{
		return new VisibleObject(Shapes.genCuboid(x, y, z), primitive_type, colour, textureName);
	}
	
	public void render(PrototypeRendererGL20 protoRenderer)
	{
		protoRenderer.draw(model, attributes);
	}

	public void dispose()
	{
		model.dispose();
	}

}
