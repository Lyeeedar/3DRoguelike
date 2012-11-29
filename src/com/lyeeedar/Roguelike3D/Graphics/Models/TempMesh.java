package com.lyeeedar.Roguelike3D.Graphics.Models;

import com.badlogic.gdx.graphics.VertexAttribute;

public class TempMesh {
	
	VertexAttribute[] attributes;
	
	public final float[] vertices;
	public final short[] indices;
	
	public final int indiceNum;
	public final int vertexSize;
	public final int vertexNum;

	public TempMesh(float[] vertices, short[] indices, int indiceNum, int vertexSize, int vertexNum, VertexAttribute... attributes)
	{
		this.vertices = vertices;
		this.indices = indices;
		
		this.indiceNum = indiceNum;
		this.vertexSize = vertexSize;
		this.vertexNum = vertexNum;
		
		this.attributes = attributes;
	}
}
