package com.lyeeedar.Graphics.ParticleEffects;
///*******************************************************************************
// * Copyright (c) 2013 Philip Collin.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the GNU Public License v3.0
// * which accompanies this distribution, and is available at
// * http://www.gnu.org/licenses/gpl.html
// * 
// * Contributors:
// *     Philip Collin - initial API and implementation
// ******************************************************************************/
//package com.lyeeedar.Graphics.ParticleEffects;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.Camera;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.Mesh;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.Texture.TextureFilter;
//import com.badlogic.gdx.graphics.Texture.TextureWrap;
//import com.badlogic.gdx.graphics.VertexAttribute;
//import com.badlogic.gdx.graphics.VertexAttributes.Usage;
//import com.badlogic.gdx.graphics.glutils.ShaderProgram;
//import com.badlogic.gdx.math.Vector3;
//import com.lyeeedar.Roguelike3D.CircularArrayRing;
//
//public class MotionTrail {
//	
//	CircularArrayRing<Vector3> trailRing;
//	final int vertNum;
//	
//	final Mesh mesh;
//	
//	final ShaderProgram shader;
//	
//	Color colour;
//	
//	Texture texture;
//	
//	final float[] vertices;
//
//	public MotionTrail(int vertsNum, Color colour, String texture) 
//	{		
//		this.colour = colour;
//		this.texture = new Texture(Gdx.files.internal(texture));
//		this.texture.setWrap( TextureWrap.ClampToEdge, TextureWrap.ClampToEdge );
//		this.texture.setFilter( TextureFilter.Nearest, TextureFilter.Nearest);
//		
//		this.vertNum = vertsNum * 2;
//		
//		trailRing = new CircularArrayRing<Vector3>(this.vertNum);
//		
//		for (int i = 0; i < this.vertNum; i++)
//		{
//			trailRing.add(new Vector3());
//		}
//		
//		mesh = new Mesh(false, this.vertNum, 0, 
//				new VertexAttribute(Usage.Position, 3, "a_position"),
//				new VertexAttribute(Usage.Generic, 1, "a_texCoord"));
//		vertices = new float[this.vertNum * 4];
//		
//		shader = new ShaderProgram(
//				Gdx.files.internal("data/shaders/model/motion_trail.vertex.glsl"),
//				Gdx.files.internal("data/shaders/model/motion_trail.fragment.glsl")
//				);
//		
//		if (!shader.isCompiled())
//		{
//			Gdx.app.log("Problem loading shader:", shader.getLog());
//		}
//	}
//	
//	public void intialiseVerts(Vector3 bottom, Vector3 top)
//	{
//		for (int i = 0; i < vertNum/2; i++)
//		{
//			addVert(bottom);
//			addVert(top);
//		}
//	}
//	
//	protected void addVert(Vector3 vert)
//	{
//		trailRing.peek().set(vert);
//	}
//
//	private boolean up = false;
//	protected void updateVerts()
//	{
//		for (int i = 0; i < vertNum; i++)
//		{
//			Vector3 vert = trailRing.get(i);
//			vertices[i*4] = vert.x;
//			vertices[(i*4)+1] = vert.y;
//			vertices[(i*4)+2] = vert.z;
//			if (up) vertices[(i*4)+3] = 1.0f;
//			else vertices[(i*4)+3] = 0.0f;
//			
//			up = (!up);
//		}
//		
//		mesh.setVertices(vertices);
//	}
//	
//	public void offsetAll(Vector3 offset)
//	{
//		for (int i = 0; i < vertNum; i++)
//		{
//			Vector3 vert = trailRing.get(i);
//			vert.add(offset);
//		}
//	}
//	
//	public void draw(Camera cam)
//	{
//		Gdx.graphics.getGL20().glDisable(GL20.GL_CULL_FACE);
//		Gdx.gl.glEnable(GL20.GL_BLEND); 
//		
//		shader.begin();
//		
//		texture.bind();
//		
//		shader.setUniformMatrix("u_mv", cam.combined);
//		//shader.setUniformf("u_colour", colour.r, colour.g, colour.b, colour.a);
//		
//		mesh.render(shader, GL20.GL_TRIANGLE_STRIP);
//		
//		shader.end();
//		
//		Gdx.graphics.getGL20().glEnable(GL20.GL_CULL_FACE);
//		Gdx.gl.glDisable(GL20.GL_BLEND); 
//	}
//	
//	public void update(Vector3 bottom, Vector3 top)
//	{
//		addVert(bottom);
//		addVert(top);
//		
//		updateVerts();
//	}
//	
//	public void dispose()
//	{
//		mesh.dispose();
//	}
//
//}
