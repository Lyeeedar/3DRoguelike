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

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class Shapes {
	
	public static Mesh genCuboid(Vector3 dimensions)
	{
		return genCuboid(dimensions.x, dimensions.y, dimensions.z);
	}
	
	public static float[] genCubeVertices(float x, float y, float z)
	{
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
				
				x, y, // back
				x, 0.0f,
				0.0f, 0.0f,
				0.0f, y,
				
				x, y, // front
				x, 0.0f,
				0.0f, 0.0f,
				0.0f, y,
				
				z, y, // left
				0.0f, y,
				0.0f, 0.0f,
				z, 0.0f,
				
				z, y, // right
				0.0f, y,
				0.0f, 0.0f,
				z, 0.0f

				};

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
		
		return vertices;
	}
	
	public static short[] genCubeIndices()
	{
		return new short[] {
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
	}
	
	public static Mesh genCuboid (float x, float y, float z) {
		Mesh mesh = new Mesh(true, 24, 36, 
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Normal, 3, "a_normal"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0"));

		float[] vertices = genCubeVertices(x, y, z);

		short[] indices = genCubeIndices();

		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		return mesh;
	}
	
	public static TempMesh genTempCuboid (float x, float y, float z) {

		float[] vertices = genCubeVertices(x, y, z);

		short[] indices = genCubeIndices();

		VertexAttribute[] attributes = {
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.Normal, 3, "a_normal"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoord0")
				};
		TempMesh tmpMesh = new TempMesh(vertices, indices, 36, 8, 24, attributes);

		return tmpMesh;
	}
	
	public static void translateCubeMesh(Mesh mesh, float x, float y, float z)
	{
		final int vertexSize = mesh.getVertexAttributes().vertexSize / 4;
		float[] newPos = new float[mesh.getMaxVertices()*vertexSize];
		mesh.getVertices(newPos);
		for (int i = 0; i < mesh.getMaxVertices(); i++)
		{
			newPos[(i*vertexSize)] += x;
			newPos[(i*vertexSize)+1] += y;
			newPos[(i*vertexSize)+2] += z;
		}
		mesh.setVertices(newPos);
	}
	
	public static void translateCubeVertices(int vertexNum, int vertexSize, float[] vertices, float x, float y, float z)
	{
		for (int i = 0; i < vertexNum; i++)
		{
			vertices[(i*vertexSize)] += x;
			vertices[(i*vertexSize)+1] += y;
			vertices[(i*vertexSize)+2] += z;
		}
	}
	
	/**
	    * Create a new Mesh that is a combination of the supplied meshes. The meshes must have the same VertexAttributes signature.
	    * @param meshes the meshes to combine
	    * @return the combined mesh
	    */
	   public static Mesh merge(final Mesh[] meshes) {
		   
	      final VertexAttributes attributes = meshes[0].getVertexAttributes();
	      final int vertCount = meshes[0].getNumVertices() * meshes.length;
	      final int idxCount = meshes[0].getNumIndices() * meshes.length;
	      final int vertexSize = attributes.vertexSize / 4; 
	      
	      final float vertices[] = new float[vertCount * vertexSize];
	      final short indices[] = new short[idxCount];

	      int voffset = 0;
	      int ioffset = 0;
	      
	      for (int i = 0; i < meshes.length; i++) {
	         final Mesh mesh = meshes[i];
	         
	         final int vsize = mesh.getNumVertices() * vertexSize;
	         final int isize = mesh.getNumIndices();
	         
	         final float[] tempVerts = new float[vsize];
	         final short[] tempIdxs = new short[isize];
	         
	         mesh.getVertices(tempVerts);

	         for (int j = 0; j < vsize; j++)
	         {
	        	 vertices[voffset+j] = tempVerts[j];
	         }
	         
	         mesh.getIndices(tempIdxs);
	         
	         for (int j = 0; j < isize; j++)
	         {
	            indices[ioffset+j] = (short)(tempIdxs[j] + (i*mesh.getNumVertices()));
	         }
	         
	         voffset += vsize;
	         ioffset += isize;
	      }
	      
	      final Mesh result = new Mesh(true, vertCount, idxCount, attributes);
	      result.setVertices(vertices);
	      result.setIndices(indices);

	      return result;
	   }

	   public static Mesh merge(final TempMesh[] tmpMeshes) {
		   
		      final int vertCount = tmpMeshes[0].vertexNum * tmpMeshes.length;
		      final int idxCount = tmpMeshes[0].indiceNum * tmpMeshes.length;
		      final int vertexSize = tmpMeshes[0].vertexSize;
		      
		      final float vertices[] = new float[vertCount * vertexSize];
		      final short indices[] = new short[idxCount];

		      int voffset = 0;
		      int ioffset = 0;
		      
		      for (int i = 0; i < tmpMeshes.length; i++) {
		         final TempMesh tmpMesh = tmpMeshes[i];
		         
		         final int vsize = tmpMesh.vertexNum * vertexSize;
		         final int isize = tmpMesh.indiceNum;
		         
		         final float[] tempVerts = tmpMesh.vertices;
		         final short[] tempIdxs = tmpMesh.indices;

		         for (int j = 0; j < vsize; j++)
		         {
		        	 vertices[voffset+j] = tempVerts[j];
		         }
		         
		         for (int j = 0; j < isize; j++)
		         {
		            indices[ioffset+j] = (short)(tempIdxs[j] + (i*tmpMesh.vertexNum));
		         }
		         
		         voffset += vsize;
		         ioffset += isize;
		      }
		      
		      final Mesh result = new Mesh(true, vertCount, idxCount, tmpMeshes[0].attributes);
		      result.setVertices(vertices);
		      result.setIndices(indices);

		      return result;
		   }
}
