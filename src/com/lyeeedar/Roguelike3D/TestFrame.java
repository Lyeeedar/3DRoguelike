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
import com.lyeeedar.Roguelike3D.Game.Level.AbstractDungeon;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom;
import com.lyeeedar.Roguelike3D.Game.Level.Level;

public class TestFrame extends JFrame
{
	public static void main(String[] args)
	{
		Level level = new Level(50, 50);
		
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
	AbstractDungeon ad = new AbstractDungeon(50, 10, 1, 1, 4, 4);
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
		this.level = level;
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(this);
		reload();
	}
	
	public void reload()
	{
		im = new BufferedImage(1200, 1200, BufferedImage.TYPE_INT_RGB);
		Graphics g2 = im.createGraphics();
		g2.setColor(Color.WHITE);
		
		for (int x = 0; x < ad.rooms.length; x++)
		{
			for (int y = 0; y < ad.rooms[0].length; y++)
			{
				DungeonRoom dr = ad.rooms[x][y];
				
				if (dr.up.size()==0 && dr.down.size()==0 && dr.left.size()==0 && dr.right.size()==0) g2.setColor(Color.WHITE);
				else g2.setColor(Color.GREEN);
				
				for (int ix = 0; ix < dr.width; ix++)
				{
					for (int iy = 0; iy < dr.height; iy++)
					{
						g2.drawString(""+dr.tiles[ix][iy].character, (x*10*(dr.width+1)) + (ix*10), (y*10*(dr.height+1)) + (iy*10));
					}
				}
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
