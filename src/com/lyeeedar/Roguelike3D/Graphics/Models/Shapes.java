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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;

public class Shapes {

	public static Mesh genCuboid(Vector3 dimensions)
	{
		return genCuboid(dimensions.x, dimensions.y, dimensions.z);
	}

	public static float[] genCubeVertices(float x, float y, float z)
	{
		x /= 2.0f;
		y /= 2.0f;
		z /= 2.0f;

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
	
	public static Mesh insertColour(Mesh mesh, Colour colour)
	{
		VertexAttributes attributes = mesh.getVertexAttributes();
		final int vertCount = mesh.getNumVertices();
		final int vertexSize = attributes.vertexSize / 4;
		
		VertexAttribute[] newAttributes = new VertexAttribute[attributes.size()+1];
		for (int i = 0; i < attributes.size(); i++)
		{
			newAttributes[i] = attributes.get(i);
		}
		newAttributes[attributes.size()] = new VertexAttribute(Usage.Generic, 3, "a_colour");
		
		final int newVertexSize = vertexSize + 3;
		
		float[] verts = new float[vertexSize * vertCount]; 
		mesh.getVertices(verts);
		short[] indices = new short[mesh.getNumIndices()];
		mesh.getIndices(indices);
		float[] newVerts = new float[newVertexSize * vertCount];
		
		for (int i = 0; i < vertCount; i++)
		{
			int j = 0;
			for (; j < vertexSize; j++)
			{
				newVerts[ (i*newVertexSize) + j ] = verts[ (i*vertexSize) + j ];
			}
			newVerts[ (i*newVertexSize) + j ] = colour.r;
			newVerts[ (i*newVertexSize) + j + 1 ] = colour.g;
			newVerts[ (i*newVertexSize) + j + 2 ] = colour.b;
		}
		
		Mesh newMesh = new Mesh(true, mesh.getNumVertices(), mesh.getNumIndices(), newAttributes);
		newMesh.setVertices(newVerts);
		newMesh.setIndices(indices);
		
		return newMesh;
	}
	
	public static Mesh insertLight(Mesh mesh, LightManager lights, boolean bakeStatics, Vector3 meshPosition, Matrix4 meshRotation, boolean lit)
	{
		VertexAttributes attributes = mesh.getVertexAttributes();
		final int vertCount = mesh.getNumVertices();
		final int vertexSize = attributes.vertexSize / 4;
		
		VertexAttribute[] newAttributes = new VertexAttribute[attributes.size()+1];
		for (int i = 0; i < attributes.size(); i++)
		{
			newAttributes[i] = attributes.get(i);
		}
		newAttributes[attributes.size()] = new VertexAttribute(Usage.Generic, 3, "a_baked_light");
		
		final int newVertexSize = vertexSize + 3;
		
		float[] verts = new float[vertexSize * vertCount]; 
		mesh.getVertices(verts);
		short[] indices = new short[mesh.getNumIndices()];
		mesh.getIndices(indices);
		float[] newVerts = new float[newVertexSize * vertCount];
		
		int positionOffset = attributes.getOffset(Usage.Position);
		int normalOffset = attributes.getOffset(Usage.Normal);
		
		for (int i = 0; i < vertCount; i++)
		{
			int j = 0;
			for (; j < vertexSize; j++)
			{
				newVerts[ (i*newVertexSize) + j ] = verts[ (i*vertexSize) + j ];
			}
			
			Vector3 position = meshPosition.cpy();
			position.add(verts[(i*vertexSize)+positionOffset], verts[(i*vertexSize)+positionOffset+1], verts[(i*vertexSize)+positionOffset+2]);

			Vector3 normal = new Vector3(verts[(i*vertexSize)+normalOffset], verts[(i*vertexSize)+normalOffset+1], verts[(i*vertexSize)+normalOffset+2]);
			normal.mul(meshRotation);
			
			Vector3 light_colour = new Vector3(1.0f, 1.0f, 1.0f);
			
			if (lit) light_colour = lights.calculateLightAtPoint(position, normal.nor(), bakeStatics);
			
			newVerts[ (i*newVertexSize) + j ] = light_colour.x;
			newVerts[ (i*newVertexSize) + j + 1 ] = light_colour.y;
			newVerts[ (i*newVertexSize) + j + 2 ] = light_colour.z;
		}
		
		Mesh newMesh = new Mesh(true, mesh.getNumVertices(), mesh.getNumIndices(), newAttributes);
		newMesh.setVertices(newVerts);
		newMesh.setIndices(indices);
		
		return newMesh;
	}
	
	public static Mesh insertTangents(Mesh mesh)
	{
		VertexAttributes attributes = mesh.getVertexAttributes();
		final int vertCount = mesh.getNumVertices();
		final int vertexSize = attributes.vertexSize / 4;
		
		VertexAttribute[] newAttributes = new VertexAttribute[attributes.size()+1];
		for (int i = 0; i < attributes.size(); i++)
		{
			newAttributes[i] = attributes.get(i);
		}
		newAttributes[attributes.size()] = new VertexAttribute(Usage.Generic, 4, "a_tangent");
		
		final int newVertexSize = vertexSize + 4;
		
		float[] verts = new float[vertexSize * vertCount]; 
		mesh.getVertices(verts);
		short[] indices = new short[mesh.getNumIndices()];
		mesh.getIndices(indices);
		float[] newVerts = new float[newVertexSize * vertCount];
		
		int positionOffset = attributes.getOffset(Usage.Position);
		int normalOffset = attributes.getOffset(Usage.Normal);
		int textureOffset = attributes.getOffset(Usage.TextureCoordinates);
		
		int tangentOffset = 0;
		for (int i = 0; i < vertCount; i += 3)
		{
			int j = 0;
			for (; j < vertexSize; j++)
			{
				newVerts[ (i*newVertexSize) + j ] = verts[ (i*vertexSize) + j ];
				newVerts[ ((i+1)*newVertexSize) + j ] = verts[ ((i+1)*vertexSize) + j ];
				newVerts[ ((i+2)*newVertexSize) + j ] = verts[ ((i+2)*vertexSize) + j ];
			}
			tangentOffset = j;
		}
		for (int i = 0; i < mesh.getNumIndices(); i += 3)
		{
			int i1 = indices[i];
			int i2 = indices[i+1];
			int i3 = indices[i+2];
			
			Vector3 v1 = new Vector3(verts[(i1*vertexSize)+positionOffset], verts[(i1*vertexSize)+positionOffset]+1, verts[(i1*vertexSize)+positionOffset]+2);
			Vector3 v2 = new Vector3(verts[(i2*vertexSize)+positionOffset], verts[(i2*vertexSize)+positionOffset]+1, verts[(i2*vertexSize)+positionOffset]+2);
			Vector3 v3 = new Vector3(verts[(i3*vertexSize)+positionOffset], verts[(i3*vertexSize)+positionOffset]+1, verts[(i3*vertexSize)+positionOffset]+2);

			float[] w1 = {verts[(i1*vertexSize)+textureOffset], verts[(i1*vertexSize)+textureOffset]+1};
			float[] w2 = {verts[(i2*vertexSize)+textureOffset], verts[(i2*vertexSize)+textureOffset]+1};
			float[] w3 = {verts[(i3*vertexSize)+textureOffset], verts[(i3*vertexSize)+textureOffset]+1};
			
	        float x1 = v2.x - v1.x;
	        float x2 = v3.x - v1.x;
	        float y1 = v2.y - v1.y;
	        float y2 = v3.y - v1.y;
	        float z1 = v2.z - v1.z;
	        float z2 = v3.z - v1.z;
	        
	        float s1 = w2[0] - w1[0];
	        float s2 = w3[0] - w1[0];
	        float t1 = w2[1] - w1[1];
	        float t2 = w3[1] - w1[1];
	        
	        float div = s1 * t2 - s2 * t1;
	        float r = div == 0.0f ? 0.0f : 1.0f / div;
	        
	        Vector3 t = new Vector3((t2 * x1 - t1 * x2) * r, (t2 * y1 - t1 * y2) * r,
	                (t2 * z1 - t1 * z2) * r);
	        Vector3 n = new Vector3(verts[(i1*vertexSize)+normalOffset], verts[(i1*vertexSize)+normalOffset]+1, verts[(i1*vertexSize)+normalOffset]+2);
	        
	        //Vector3 tangent = t.cpy().sub(n).mul(n.tmp().dot(t)).nor();
	        Vector3 tangent = orthoNormalize(n, t);
	        
	        System.out.println(t2);
	        
            Vector3 tan2 = new Vector3((s1 * x2 - s2 * x1) * r, (s1 * y2 - s2 * y1) * r, (s1 * z2 - s2 * z1) * r);
            float handedness = ( n.tmp().crs(t).dot(tan2) < 0.0f ) ? -1.0f : 1.0f;
            
            Vector3 c1 = n.cpy().crs(0.0f, 0.0f, 1.0f); 
            Vector3 c2 = n.cpy().crs(0.0f, 1.0f, 0.0f); 

            if( c1.len2()>c2.len2() )
            {
            	tangent = c1;	
            }
            else
            {
            	tangent = c2;	
            }
			
			newVerts[ (i1*newVertexSize) + tangentOffset ] = tangent.x;
			newVerts[ (i1*newVertexSize) + tangentOffset + 1 ] = tangent.y;
			newVerts[ (i1*newVertexSize) + tangentOffset + 2 ] = tangent.z;
			newVerts[ (i1*newVertexSize) + tangentOffset + 3 ] = handedness;
			
			newVerts[ (i2*newVertexSize) + tangentOffset ] = tangent.x;
			newVerts[ (i2*newVertexSize) + tangentOffset + 1 ] = tangent.y;
			newVerts[ (i2*newVertexSize) + tangentOffset + 2 ] = tangent.z;
			newVerts[ (i2*newVertexSize) + tangentOffset + 3 ] = handedness;
			
			newVerts[ (i3*newVertexSize) + tangentOffset ] = tangent.x;
			newVerts[ (i3*newVertexSize) + tangentOffset + 1 ] = tangent.y;
			newVerts[ (i3*newVertexSize) + tangentOffset + 2 ] = tangent.z;
			newVerts[ (i3*newVertexSize) + tangentOffset + 3 ] = handedness;
		}
		
		Mesh newMesh = new Mesh(true, mesh.getNumVertices(), mesh.getNumIndices(), newAttributes);
		newMesh.setVertices(newVerts);
		newMesh.setIndices(indices);
		
		return newMesh;
	}
	
	public static Vector3 orthoNormalize(Vector3 vec1, Vector3 vec2)
	{
		vec1.nor();
		vec1.mul(vec2.dot(vec1));
		
		vec2.sub(vec1);
		
		vec2.nor();
		return vec2;
	}
}
