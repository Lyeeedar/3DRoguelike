package com.lyeeedar.Roguelike3D.Graphics.ParticleEffects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.CircularArrayRing;

public class MotionTrail {
	
	CircularArrayRing<Vector3> trailRing;
	final int vertNum;
	
	final Mesh mesh;
	
	final ShaderProgram shader;

	public MotionTrail(int vertsNum) 
	{		
		this.vertNum = vertsNum * 2;
		
		trailRing = new CircularArrayRing<Vector3>(this.vertNum);
		
		for (int i = 0; i < this.vertNum; i++)
		{
			trailRing.add(new Vector3());
		}
		
		mesh = new Mesh(false, this.vertNum, 0, new VertexAttribute(Usage.Position, 3, "a_position"));
		vertices = new float[this.vertNum * 3];
		
		shader = new ShaderProgram(
				Gdx.files.internal("data/shaders/model/basic_movement.vertex.glsl"),
				Gdx.files.internal("data/shaders/model/basic_movement.fragment.glsl")
				);
		
		if (!shader.isCompiled())
		{
			Gdx.app.log("Problem loading shader:", shader.getLog());
		}
	}
	
	public void intialiseVerts(Vector3 bottom, Vector3 top)
	{
		for (int i = 0; i < vertNum/2; i++)
		{
			addVert(bottom);
			addVert(top);
		}
	}
	
	protected void addVert(Vector3 vert)
	{
		trailRing.peek().set(vert);
	}
	
	final float[] vertices;
	protected void updateVerts()
	{
		for (int i = 0; i < vertNum; i++)
		{
			Vector3 vert = trailRing.get(i);
			vertices[i*3] = vert.x;
			vertices[(i*3)+1] = vert.y;
			vertices[(i*3)+2] = vert.z;
		}
		
		mesh.setVertices(vertices);
	}
	
	public void draw(Camera cam)
	{
		Gdx.graphics.getGL20().glDisable(GL20.GL_CULL_FACE);
		
		shader.begin();
		
		shader.setUniformMatrix("u_mv", cam.combined);
		
		mesh.render(shader, GL20.GL_TRIANGLE_STRIP);
		
		shader.end();
		
		Gdx.graphics.getGL20().glEnable(GL20.GL_CULL_FACE);
	}
	
	public void update(Vector3 bottom, Vector3 top)
	{
		addVert(bottom);
		addVert(top);
		
		updateVerts();
	}
	
	public void dispose()
	{
		mesh.dispose();
	}

}
