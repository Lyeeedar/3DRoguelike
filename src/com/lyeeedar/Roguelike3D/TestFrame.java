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
import com.lyeeedar.Roguelike3D.Game.Level.XML.MonsterEvolver;
import com.lyeeedar.Roguelike3D.Game.Level.SerkGenerator;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;

public class TestFrame extends JFrame
{
	public static void main(String[] args)
	{
		MonsterEvolver me = new MonsterEvolver("group", 1);
		
		new TestFrame(me);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7985050102337369937L;

	public TestFrame(MonsterEvolver me)
	{
		this.setFocusable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(new DrawingCanvas(me));
		
		this.setSize(640, 480);
		this.setVisible(true);
	}
}

class DrawingCanvas extends JPanel implements KeyListener
{
	private static final long serialVersionUID = -1409207916893116457L;
	BufferedImage im = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
	MonsterEvolver me;
	
	int posx = 25;
	int posy = 25;
	
	public DrawingCanvas(MonsterEvolver me)
	{
		this.me = me;
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
		
		char[][] grid = me.getVisualGrid();
		
		for (int x = 0; x < grid.length; x++)
		{
			for (int y = 0; y < grid[0].length; y++)
			{
				g2.drawString(""+grid[x][y], x*20, y*20);
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
