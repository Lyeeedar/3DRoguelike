package com.lyeeedar.Roguelike3D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.lyeeedar.Roguelike3D.Game.GameData;

public class TestFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7985050102337369937L;

	public TestFrame()
	{
		this.setFocusable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(new DrawingCanvas());
		
		this.setSize(640, 480);
		this.setVisible(true);
	}
}

class DrawingCanvas extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1409207916893116457L;
	BufferedImage im = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
	
	public DrawingCanvas()
	{
		this.setFocusable(false);
		reload();
	}
	
	public void reload()
	{
		im = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
		Graphics g2 = im.createGraphics();
		g2.setColor(Color.WHITE);
		
		int px = (int)GameData.player.getPosition().x;
		int pz = (int)GameData.player.getPosition().z;
		
		int lx = (int)(GameData.player.getRotation().x);
		int lz = (int)(GameData.player.getRotation().z);
		
		for (int x = 0; x < GameData.level.getLevelArray().length; x++)
		{
			for (int y = 0; y < GameData.level.getLevelArray()[0].length; y++)
			{
				g2.drawString(""+GameData.level.getLevelArray()[x][y].character, (400-(px))+(x*10), (300-(pz))+(y*10));
			}
		}

		g2.drawString("@", 400, 300);
		g2.drawLine(400, 300, 400+lx, 300+lz);
	}
	
	@Override
	public void paint(Graphics g)
	{
		reload();
		g.drawImage(im, 0, 0, null);
	}
}
