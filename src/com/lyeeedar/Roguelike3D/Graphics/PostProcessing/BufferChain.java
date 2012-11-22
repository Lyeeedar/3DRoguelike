package com.lyeeedar.Roguelike3D.Graphics.PostProcessing;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.lyeeedar.Roguelike3D.Graphics.PostProcessing.Effects.PostProcessingEffect;

public class BufferChain {
	
	public static final int NUM_BUFFERS = 2;
	
	FrameBuffer[] buffers;

	int currentBuffer;

	public BufferChain(Format format, int width, int height) {
		updateBuffers(format, width, height);
	}

	public void updateBuffers(Format format, int width, int height) {
		
		buffers = new FrameBuffer[NUM_BUFFERS];
		
		for (int i = 0; i < NUM_BUFFERS; i++)
		{
			buffers[i] = new FrameBuffer(format, width, height, false);
		}
		
		currentBuffer = 0;
	}
	
	public void applyEffect(PostProcessingEffect effect)
	{
		
	}
	
	public Texture getFinalImage()
	{
		return buffers[currentBuffer].getColorBufferTexture();
	}
}
