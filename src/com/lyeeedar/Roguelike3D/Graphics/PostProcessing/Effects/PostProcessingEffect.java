package com.lyeeedar.Roguelike3D.Graphics.PostProcessing.Effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * This class models an effect to be applied in Post Processing. <br>
 * It should be used with a PostProcessor class.
 * @author Philip
 *
 */
public abstract class PostProcessingEffect {
	
	SpriteBatch batch = new SpriteBatch();
	
	ShaderProgram shader;

	public PostProcessingEffect()
	{
		create();
	}
	
	public void render(Texture texture)
	{		
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Gdx.graphics.getGL20().glDisable(GL20.GL_DEPTH_TEST);		
		Gdx.graphics.getGL20().glDisable(GL20.GL_CULL_FACE);

		batch.setShader(shader);
		
		batch.begin();
		
		bindUniforms();
		
		batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				0, 0, texture.getWidth(), texture.getHeight(),
				false, true);
		
		batch.end();
		
		batch.setShader(null);
	}
	
	public void dispose()
	{
		shader.dispose();
	}
	
	public abstract void create();
	
	public abstract void bindUniforms();

}
