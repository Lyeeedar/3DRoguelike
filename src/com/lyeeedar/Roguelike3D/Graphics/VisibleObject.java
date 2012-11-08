package com.lyeeedar.Roguelike3D.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;

public class VisibleObject {
	Mesh mesh;
	Vector3 colour;
	Texture texture;
	
	public VisibleObject(Mesh mesh, Vector3 colour, String textureName)
	{
		this.mesh = mesh;
		this.colour = colour;
		
		texture = new Texture(Gdx.files.internal("data/textures/"+textureName+".png"), true);
		//texture = new Texture(Gdx.files.internal("data/textures/"+textureName+".png"));
		texture.setWrap( TextureWrap.Repeat, TextureWrap.Repeat );
		texture.setFilter( TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear );
		//texture.setFilter( TextureFilter.Nearest, TextureFilter.Nearest );

	}
	
	public VisibleObject(Mesh mesh, Vector3 colour, Texture texture)
	{
		this.mesh = mesh;
		this.colour = colour;
		this.texture = texture;
	}
	
	public static VisibleObject createCuboid(float x, float y, float z, Vector3 colour, String textureName)
	{
		return new VisibleObject(Shapes.genCuboid(x, y, z), colour, textureName);
	}
	
	public void move(float x, float y, float z)
	{
		float[] verts = new float[mesh.getNumVertices()*8];
		mesh.getVertices(verts);
		for (int i = 0; i < mesh.getNumVertices(); i++)
		{
			verts[i*8] += x;
			verts[(i*8)+1] += y;
			verts[(i*8)+2] += z;
		}
		mesh.setVertices(verts);
	}
	
	public void dispose()
	{
		mesh.dispose();
		texture.dispose();
	}

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public Vector3 getColour() {
		return colour;
	}

	public void setColour(Vector3 colour) {
		this.colour = colour;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
