package com.lyeeedar.Roguelike3D.Graphics;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class Shapes {
	public static Mesh genCuboid (float x, float y, float z) {
		Mesh mesh = new Mesh(true, 24, 36, 
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Normal, 3, "a_normal"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texcoords"));

		float[] cubeVerts = {
				-x, -y, -z, // bottom
				-x, -y, z,
				x, -y, z,
				x, -y, -z,
				
				-x, y, -z, // top
				-x, y, z,
				x, y, z,
				x, y, -z,
				
				-x, -y, -z, // back
				-x, y, -z,
				x, y, -z,
				x, -y, -z,
				
				-x, -y, z, // front
				-x, y, z,
				x, y, z,
				x, -y, z,
				
				-x, -y, -z, // left
				-x, -y, z,
				-x, y, z,
				-x, y, -z,
				
				x, -y, -z, // right
				x, -y, z,
				x, y, z,
				x, y, -z};

		float[] cubeNormals = {
				0.0f, -1.0f, 0.0f, // bottom
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f,
				
				0.0f, 1.0f, 0.0f, // top
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				
				0.0f, 0.0f, -1.0f, // back
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f,	-1.0f,
				
				0.0f, 0.0f, 1.0f, //front
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				
				-1.0f, 0.0f, 0.0f, // left
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				
				1.0f, 0.0f, 0.0f, // right
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f};

		float[] cubeTex = {
				0.0f, 0.0f, // bottom
				0.0f, z,
				x, z,
				x, 0.0f,
				
				x, 0.0f, // top
				x, z,
				0.0f, z,
				0.0f, 0.0f,
				
				0.0f, 0.0f, // back
				0.0f, y,
				x, y,
				x, 0.0f,
				
				0.0f, 0.0f, // front
				0.0f, y,
				x, y,
				x, 0.0f,
				
				0.0f, 0.0f, // left
				0.0f, z,
				x, z,
				x, 0.0f,
				
				0.0f, 0.0f, // right
				0.0f, z,
				x, z,
				x, 0.0f};

		float[] vertices = new float[24 * 8];
		int pIdx = 0;
		int nIdx = 0;
		int tIdx = 0;
		for (int i = 0; i < vertices.length;) {
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeVerts[pIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeNormals[nIdx++];
			vertices[i++] = cubeTex[tIdx++];
			vertices[i++] = cubeTex[tIdx++];
		}

		short[] indices = {
				0, 2, 1, // bottom
				0, 3, 2,
				
				4, 5, 6, // top
				4, 6, 7,
				
				8, 9, 10, // back
				8, 10, 11,
				
				12, 15, 14, // front
				12, 14, 13,
				
				16, 17, 18, // left
				16, 18, 19,
				
				20, 23, 22, // right
				20, 22, 21
				};

		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		return mesh;
	}
	
}