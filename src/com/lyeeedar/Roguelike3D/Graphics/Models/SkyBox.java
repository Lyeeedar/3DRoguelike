package com.lyeeedar.Roguelike3D.Graphics.Models;

import java.nio.IntBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;

public class SkyBox {
	
	private static final int SKYBOX_TEXTURE_UNIT = 0;
	private static final int SKYBOX_TEXTURE_ACTIVE_UNIT = GL20.GL_TEXTURE0 + SKYBOX_TEXTURE_UNIT;
	
	Mesh mesh;
	Pixmap[] texture = new Pixmap[6];
	ShaderProgram shader;
	
	int textureId;

	public SkyBox(String texture) {
		
		this.texture[0] = new Pixmap(Gdx.files.internal("data/textures/"+texture+"_px.png"));
		this.texture[1] = new Pixmap(Gdx.files.internal("data/textures/"+texture+"_nx.png"));
		this.texture[2] = new Pixmap(Gdx.files.internal("data/textures/"+texture+"_py.png"));
		this.texture[3] = new Pixmap(Gdx.files.internal("data/textures/"+texture+"_ny.png"));
		this.texture[4] = new Pixmap(Gdx.files.internal("data/textures/"+texture+"_pz.png"));
		this.texture[5] = new Pixmap(Gdx.files.internal("data/textures/"+texture+"_nz.png"));
		
		IntBuffer buffer = BufferUtils.newIntBuffer(1);
		buffer.position(0);
		buffer.limit(buffer.capacity());
		Gdx.gl20.glGenTextures(1, buffer);
		textureId = buffer.get(0);
		
		Gdx.gl20.glActiveTexture(SKYBOX_TEXTURE_ACTIVE_UNIT);
		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, textureId);
		
		for (int i = 0; i < 6; i++) {
			glTexImage2D(GL20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, this.texture[i]);
		}
		
		float[] vertices = {  
			    -1.0f, -1.0f,  1.0f,
			    1.0f, -1.0f,  1.0f,
			    -1.0f,  1.0f,  1.0f,
			    1.0f,  1.0f,  1.0f,
			    -1.0f, -1.0f, -1.0f,
			    1.0f, -1.0f, -1.0f,
			    -1.0f,  1.0f, -1.0f,
			    1.0f,  1.0f, -1.0f,
			};

		for (int i = 0; i < vertices.length; i++)
		{
			//vertices[i] *= 10;
		}
		
		short[] indices = {0, 1, 2, 3, 7, 1, 5, 4, 7, 6, 2, 4, 0, 1};
		
		mesh = new Mesh(true, 24, 14, new VertexAttribute(Usage.Position, 3, "a_position"));
		
		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		final String vertexShader = Gdx.files.internal("data/shaders/model/skybox.vertex.glsl").readString();
		final String fragmentShader = Gdx.files.internal("data/shaders/model/skybox.fragment.glsl").readString();
		ShaderProgram.pedantic = true;
		shader = new ShaderProgram(vertexShader, fragmentShader);

		Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP,
				GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
		Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP,
				GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);

		Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP,
				GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
		Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP,
				GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
		
	}
	
	public void render(Camera cam)
	{
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(false);
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);

		Gdx.gl20.glActiveTexture(SKYBOX_TEXTURE_ACTIVE_UNIT);
		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, textureId);
		
		shader.begin();
		
		shader.setUniformMatrix("u_pv", cam.combined);
		shader.setUniformf("u_pos", cam.position);
		shader.setUniformi("u_texture", 0);
		mesh.render(shader, GL20.GL_TRIANGLE_STRIP);
		
		shader.end();
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
	}
	
	private void glTexImage2D(int textureCubeMapIndex, Pixmap pixmap) {
		Gdx.gl20.glTexImage2D(textureCubeMapIndex, 0,
				pixmap.getGLInternalFormat(), pixmap.getWidth(),
				pixmap.getHeight(), 0, pixmap.getGLFormat(),
				pixmap.getGLType(), pixmap.getPixels());
	}
}
