package com.lyeeedar.Roguelike3D.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;

public class VisibleObject {
	Mesh mesh;
	float[] colour;
	Texture texture;
	
	public VisibleObject(Mesh mesh, float[] colour, String textureName)
	{
		this.mesh = mesh;
		this.colour = colour;
		
		texture = new Texture(Gdx.files.internal("Data/Textures/"+textureName+".png"));
		texture.setWrap( TextureWrap.Repeat, TextureWrap.Repeat );
		texture.setFilter( TextureFilter.Nearest, TextureFilter.Nearest );
	}
	
	public VisibleObject(Mesh mesh, float[] colour, Texture texture)
	{
		this.mesh = mesh;
		this.colour = colour;
		this.texture = texture;
	}
	
	public static VisibleObject createCuboid(float x, float y, float z, float[] colour, String textureName)
	{
		Mesh mesh = Shapes.genCuboid(x, y, z);
		Texture texture = new Texture(Gdx.files.internal("Data/Textures/"+textureName+".png"));
		texture.setWrap( TextureWrap.Repeat, TextureWrap.Repeat );
		texture.setFilter( TextureFilter.Nearest, TextureFilter.Nearest );
		
		return new VisibleObject(mesh, colour, texture);
		
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

	public Mesh getMesh() {
		return mesh;
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	public float[] getColour() {
		return colour;
	}

	public void setColour(float[] colour) {
		this.colour = colour;
	}

	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}
}
