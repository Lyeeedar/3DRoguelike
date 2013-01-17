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
package com.lyeeedar.Roguelike3D.Graphics.PostProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.lyeeedar.Roguelike3D.Graphics.PostProcessing.Effects.BloomEffect;
import com.lyeeedar.Roguelike3D.Graphics.PostProcessing.Effects.BlurEffect;
import com.lyeeedar.Roguelike3D.Graphics.PostProcessing.Effects.PostProcessingEffect;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;

public class PostProcessor {
	
	public enum Effect {
		BLUR,
		BLOOM
	}
	
	public static boolean ON = true;
	
	private Format format;
	private int width;
	private int height;
	
	private FrameBuffer captureBuffer;
	
	private final SpriteBatch batch = new SpriteBatch();
	
	private final ArrayList<Effect> effectChain = new ArrayList<Effect>();
	private final HashMap<Effect, PostProcessingEffect> effects = new HashMap<Effect, PostProcessingEffect>();
	
	private final BufferChain bufferChain;
	
	private final ShaderProgram shader;

	public PostProcessor(Format format, int width, int height) {
		width += 50;
		height += 50;
		this.format = format;
		this.width = width;
		this.height = height;
		
		captureBuffer = new FrameBuffer(format, width, height, true);
		bufferChain = new BufferChain(format, width, height);
		
		setupEffects();
		
		ShaderProgram.pedantic = false;
		shader = new ShaderProgram(
				Gdx.files.internal("data/shaders/postprocessing/default.vertex.glsl"),
				Gdx.files.internal("data/shaders/postprocessing/default.fragment.glsl")
				);
		if (!shader.isCompiled()) Gdx.app.log("Problem loading shader:", shader.getLog());
	}
	
	public void setEffectChain(Effect... effects)
	{
		effectChain.clear();
		for (int i = 0; i < effects.length; i++)
		{
			effectChain.add(effects[i]);
		}
	}
	
	public void addEffect(Effect effect)
	{
		effectChain.add(effect);
	}
	
	public void setupEffects()
	{
		effects.clear();
		effects.put(Effect.BLUR, new BlurEffect());
		effects.put(Effect.BLOOM, new BloomEffect());
	}
	
	public void updateBufferSettings(Format format, int f, int g) {
		this.format = format;
		this.width = f;
		this.height = g;
		
		captureBuffer.dispose();
		captureBuffer = new FrameBuffer(format, f, g, true);
		bufferChain.updateBuffers(format, f, g);
	}
	
	public void begin()
	{
		captureBuffer.begin();
	}
	
	public void end()
	{
		captureBuffer.end();
		
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		Gdx.graphics.getGL20().glDisable(GL20.GL_DEPTH_TEST);		
		Gdx.graphics.getGL20().glDisable(GL20.GL_CULL_FACE);
		
		Texture texture = applyEffectChain();

		batch.begin();
		batch.setShader(shader);
		batch.draw(captureBuffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				0, 0, captureBuffer.getColorBufferTexture().getWidth(), captureBuffer.getColorBufferTexture().getHeight(),
				false, true);
		batch.setShader(null);
		batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				0, 0, texture.getWidth(), texture.getHeight(),
				false, true);
		batch.end();
	}
	
	private Texture applyEffectChain()
	{
		bufferChain.begin(captureBuffer.getColorBufferTexture());
		
		for (Effect effect : effectChain)
		{
			bufferChain.applyEffect(effects.get(effect));
		}
		
		return bufferChain.getFinalImage();
	}

	public void dispose()
	{
		captureBuffer.dispose();
		bufferChain.dispose();
		
		for (Map.Entry<Effect, PostProcessingEffect> entry : effects.entrySet())
		{
			entry.getValue().dispose();
		}
	}

	public Format getFormat() {
		return format;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public FrameBuffer getCaptureBuffer() {
		return captureBuffer;
	}
}
