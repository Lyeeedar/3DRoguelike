package com.lyeeedar.Roguelike3D.Graphics;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

public class TextureDrawer {
	
	public static final Format format = Format.RGBA4444;

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
		SpriteBatch sB = new SpriteBatch();
		
		sB.begin();
		for (int line = 0; line < text.length; line++)
		{
			for (int c = 0; c < text[line].length(); c++)
			{
				font.draw(sB, ""+text[line].charAt(c), c*xSpacing, line*ySpacing);
			}
		}
		sB.end();
	
		return fB.getColorBufferTexture();
	}

}
