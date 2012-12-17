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
package com.lyeeedar.Roguelike3D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractTile;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractTile.TileType;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.MapGenerator.GeneratorType;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.SerkGenerator;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;

public class TestFrame extends JFrame
{
	public static void main(String[] args)
	{
		BiomeReader biome = new BiomeReader("generic");
		Level level = new Level(80, 80, GeneratorType.SERK, biome);
		
		new TestFrame(level);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7985050102337369937L;

	public TestFrame(Level level)
	{
		this.setFocusable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(new DrawingCanvas(level));
		
		this.setSize(640, 480);
		this.setVisible(true);
	}
}

class DrawingCanvas extends JPanel implements KeyListener
{
	AbstractTile[][] tiles = new AbstractTile[50][50];
	/**
	 * 
	 */
	private static final long serialVersionUID = -1409207916893116457L;
	BufferedImage im = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
	Level level;
	
	int posx = 25;
	int posy = 25;
	
	public DrawingCanvas(Level level)
	{
		for (int x = 0; x < 50; x++)
		{
			for (int y = 0; y < 50; y++)
			{
				tiles[x][y] = new AbstractTile(x, y, TileType.WALL);
			}
		}
		
		this.level = level;
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(this);
		//SerkGenerator sg = new SerkGenerator(tiles);
		//sg.generate();
		reload();
	}
	
	public void reload()
	{
		im = new BufferedImage(1200, 1200, BufferedImage.TYPE_INT_RGB);
		Graphics g2 = im.createGraphics();
		g2.setColor(Color.WHITE);
		
		for (int x = 0; x < tiles.length; x++)
		{
			for (int y = 0; y < tiles[0].length; y++)
			{
				if (tiles[x][y].tileType == TileType.FLOOR)
				{
					g2.setColor(Color.GREEN);
				}
				else if (tiles[x][y].tileType == TileType.DOOR)
				{
					g2.setColor(Color.RED);
				}
				else if (tiles[x][y].room)
				{
					g2.setColor(Color.BLUE);
				}
				else if (tiles[x][y].tileType == TileType.WALL)
				{
					g2.setColor(Color.WHITE);
				}
				
				g2.fillRect(x*20, y*20, 15, 15);
			}
		}
	}
	
	@Override
	public void paint(Graphics g)
	{
		reload();
		g.drawImage(im, 0, 0, null);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			posy -= 5;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			posy += 5;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			posx -= 5;
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			posx += 5;
		}
		
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
