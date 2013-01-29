package com.lyeeedar.Roguelike3D.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.lyeeedar.Roguelike3D.Game.GameData;

public class TextureDrawer {
	
	public static final Format format = Format.RGBA4444;
	private static final SpriteBatch sB = new SpriteBatch();

	public static Texture drawText(BitmapFont font, int xSpacing, int ySpacing, String... text)
	{
		int height = ySpacing * (text.length+2);
		
		int width = 0;
		
		int temp;
		for (int i = 0; i < text.length; i++)
		{
			temp = xSpacing * (text[i].length()+2);
			
			if (temp > width) width = temp;
		}
		
		FrameBuffer fB = new FrameBuffer(format, width, height, false);
		fB.begin();
		
		Gdx.graphics.getGL20().glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		font.setColor(Color.BLACK);
		
		sB.begin();
		for (int line = 0; line < text.length; line++)
		{
			for (int c = 0; c < text[line].length(); c++)
			{
				font.draw(sB, ""+text[line].charAt(c), c*xSpacing, (line-1)*ySpacing);
			}
		}
		sB.end();
	
		fB.end();
		return fB.getColorBufferTexture();
	}
	
	public static Texture combineTextures(Texture texture1, Color colour1, Texture texture2, Color colour2)
	{
		int width = texture1.getWidth();
		int height = texture1.getHeight();
		
		FrameBuffer buffer = new FrameBuffer(format, width, height, false);
		buffer.begin();
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);

		sB.begin();
		sB.setColor(colour1);
		sB.draw(texture1, 0, 0, GameData.resolution[0], GameData.resolution[1]);
		sB.end();
		
		if (texture2 != null) {
			sB.begin();
			sB.setColor(colour2);
			sB.draw(texture2, 0, 0, GameData.resolution[0], GameData.resolution[1]);
			sB.end();
		}
		
		buffer.end();
		
		Texture merged = buffer.getColorBufferTexture();
		
		return merged;

	}

}
