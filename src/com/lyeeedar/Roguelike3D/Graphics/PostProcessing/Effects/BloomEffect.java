package com.lyeeedar.Roguelike3D.Graphics.PostProcessing.Effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class BloomEffect extends PostProcessingEffect {

	BlurEffect blur = new BlurEffect(0.9f, 1.0f);
	
	public static final int BUFFER_WIDTH = 800;
	public static final int BUFFER_HEIGHT = 600;
	
	FrameBuffer downsampleBuffer = new FrameBuffer(Format.RGBA4444, BUFFER_WIDTH, BUFFER_HEIGHT, false);
	public BloomEffect() {
	}

	@Override
	public void render(Texture texture, FrameBuffer buffer)
	{		
		downsampleBuffer.begin();
		
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Gdx.graphics.getGL20().glDisable(GL20.GL_DEPTH_TEST);		
		Gdx.graphics.getGL20().glDisable(GL20.GL_CULL_FACE);

		batch.setShader(shader);
		
		batch.begin();
		
		shader.setUniformf("u_threshold", 0.0f);
		
		batch.draw(texture, 0, 0, BUFFER_WIDTH, BUFFER_HEIGHT,
				0, 0, texture.getWidth(), texture.getHeight(),
				false, true);
		
		batch.end();
		
		downsampleBuffer.end();
		
		blur.render(downsampleBuffer.getColorBufferTexture(), downsampleBuffer);
		
		buffer.begin();
		
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Gdx.graphics.getGL20().glDisable(GL20.GL_DEPTH_TEST);		
		Gdx.graphics.getGL20().glDisable(GL20.GL_CULL_FACE);

		batch.setShader(null);
		
		batch.begin();
		batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE);
		
		batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				0, 0, texture.getWidth(), texture.getHeight(),
				false, true);
		batch.draw(downsampleBuffer.getColorBufferTexture(), 0, 0, buffer.getWidth(), buffer.getHeight(),
				0, 0, downsampleBuffer.getColorBufferTexture().getWidth(), downsampleBuffer.getColorBufferTexture().getHeight(),
				false, true);
		
		batch.end();

		buffer.end();
	}
	
	@Override
	public void create() {
		shader = new ShaderProgram(
				Gdx.files.internal("data/shaders/postprocessing/bloom.vertex.glsl"),
				Gdx.files.internal("data/shaders/postprocessing/bloom.fragment.glsl")
				);
		if (!shader.isCompiled()) Gdx.app.log("Problem loading shader:", shader.getLog());
	}

	@Override
	public void bindUniforms(ShaderProgram shader) {
	}

}
