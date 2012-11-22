package com.lyeeedar.Roguelike3D.Graphics.PostProcessing;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.lyeeedar.Roguelike3D.Graphics.PostProcessing.Effects.PostProcessingEffect;

public class PostProcessor {
	
	public enum Effect {
		BLUR,
		GLOW
	}
	
	private Format format;
	private int width;
	private int height;
	
	private FrameBuffer captureBuffer;
	
	private final SpriteBatch batch = new SpriteBatch();
	
	private final ArrayList<Effect> effectChain = new ArrayList<Effect>();
	private final HashMap<Effect, PostProcessingEffect> effects = new HashMap<Effect, PostProcessingEffect>();
	
	private final BufferChain bufferChain;

	public PostProcessor(Format format, int width, int height) {
		this.format = format;
		this.width = width;
		this.height = height;
		
		captureBuffer = new FrameBuffer(format, width, height, true);
		bufferChain = new BufferChain(format, width, height);
	}
	
	public void setupEffects()
	{
		
	}
	
	public void updateBufferSettings(Format format, int width, int height) {
		this.format = format;
		this.width = width;
		this.height = height;
		
		captureBuffer = new FrameBuffer(format, width, height, true);
		bufferChain.updateBuffers(format, width, height);
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
		batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
				0, 0, texture.getWidth(), texture.getHeight(),
				false, true);
		batch.end();
	}
	
	private Texture applyEffectChain()
	{
		for (Effect effect : effectChain)
		{
			bufferChain.applyEffect(effects.get(effect));
		}
		
		return bufferChain.getFinalImage();
	}

	public void dispose()
	{
		captureBuffer.dispose();
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
