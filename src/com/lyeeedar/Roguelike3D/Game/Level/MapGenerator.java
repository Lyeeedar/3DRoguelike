package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class MapGenerator {
	
	private Tile[][] levelArray;
	ArrayList<Character> solids;
	ArrayList<Character> opaques;
	HashMap<Character, Color> colours;
	
	public MapGenerator(int width, int height, ArrayList<Character> solids, ArrayList<Character> opaques, HashMap<Character, Color> colours)
	{
		this.solids = solids;
		this.opaques = opaques;
		this.colours = colours;

		levelArray = new Tile[width][height];
		
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				levelArray[x][y] = new Tile('#', 0, 15, 15, colours.get(' '));
			}
		}
		
	}
	
	public Tile[][] getLevel()
	{
		return levelArray;
	}

	public void updateTile(Tile t, int height, char c)
	{
		t.height = height;
		t.character = c;
		t.colour = colours.get(c);
	}
	
	public void createLevelCave()
	{
		int width = levelArray.length;
		int height = levelArray[0].length;
		
		Random ran = new Random();
		
		int length = height + ran.nextInt(height);
		int roughness = 30+ran.nextInt(70);
		int windyness = 50+ran.nextInt(50);
		
		int x = ran.nextInt(5);
		int y = ran.nextInt(10);
		
		int cwidth = 3 + ran.nextInt(7);
		
		for (int i = x; i < x+cwidth; i++)
		{
			if (x > width-1) break;
			updateTile(levelArray[i][y], 0, '.');
		}
		
		for (; y < height; y++)
		{
			length --;
			if (length < 0) break;
			
			if (ran.nextInt(100) < roughness)
			{
				boolean positive = (1 == ran.nextInt(2));
				int val = ran.nextInt(2)+1;
				if (!positive)
				{
					val *= -1;
				}
				cwidth += val;
				if (cwidth < 3) cwidth = 3;
				if (cwidth > 5) cwidth = 5;
			}
			
			if (ran.nextInt(100) < windyness)
			{
				boolean positive = (1 == ran.nextInt(2));
				int val = ran.nextInt(2)+1;
				if (!positive)
				{
					val *= -1;
				}
				x += val;
				if (x < 0) x = 0;
				if (x > width-1) x = width-1;
			}
			
			if (ran.nextInt(200) < windyness)
			{
				int val = ran.nextInt(3)+1;
				y -= val;
				if (y < 1) y = 1;
				if (y > height-1) y = height-3;
			}
			
			for (int i = x; i < x+cwidth; i++)
			{
				if (i+cwidth > width) break;
				updateTile(levelArray[i][y], 0, '.');
			}
		}
	}
	
	public void createLevelComplex()
	{
		int width = levelArray.length;
		int height = levelArray[0].length;
		
		BSPTree tree = new BSPTree(new int[]{0, 0, width, height}, null);
		for (int i = 0; i < 5; i++)
		{
			tree.calculateRooms();
		}
		
		tree.fillRooms(levelArray);
		tree.joinRooms(levelArray);
	}
	
	public void clearWalls()
	{
		for (int x = 0; x < levelArray.length; x++)
		{
			for (int y = 0; y < levelArray[0].length; y++)
			{
				if (x == 0 || x == levelArray.length-1
						|| y == 0 || y == levelArray[0].length-1) 
				{
					Tile t = levelArray[x][y];
					updateTile(levelArray[x][y], 15, '#');
				}
				
				if (chWl(x-1, y) && chWl(x, y-1)
						&& chWl(x-1, y-1) && chWl(x-1, y+1)
						&& chWl(x+1, y-1) && chWl(x+1, y+1)
						&& chWl(x+1, y) && chWl(x, y+1))
				{
					levelArray[x][y].character = ' ';
				}
			}
		}
	}
	
	private boolean chWl(int x, int y)
	{
		if (x < 0 || x > levelArray.length-1 ||
				y < 0 || y > levelArray[0].length-1)
			return true;
		
		if (levelArray[x][y].character == '#' ||
				levelArray[x][y].character == ' ')
			return true;
		else
			return false;
	}
	
	class BSPTree
	{
		int[] size;
		
		BSPTree parent;
		BSPTree left;
		BSPTree right;
		
		int[] room;
		
		public BSPTree(int[] size, BSPTree parent)
		{
			this.size = size;
			this.parent = parent;
		}
		
		public void calculateRooms()
		{
			if (left == null)
			{
				split();
			}
			else
			{
				left.calculateRooms();
				right.calculateRooms();
			}
		}
		
		private void split()
		{
			if (size[2] < 5 || size[3] < 5)
				return;
			
			Random ran = new Random();
			boolean vertical = (1==ran.nextInt(2));
			int split = ran.nextInt(6)+2;
			
			if (vertical)
			{
				float splitVal = (float)split/10.0f;
				int block = (int) (size[2]*splitVal);
				int[] top = {size[0], size[1], block, size[3]};
				int[] bottom = {size[0]+block, size[1], size[2]-block, size[3]};
				
				left = new BSPTree(top, this);
				right = new BSPTree(bottom, this);
			}
			else
			{
				float splitVal = (float)split/10.0f;
				int block = (int) (size[3]*splitVal);
				int[] left = {size[0], size[1], size[2], block};
				int[] right = {size[0], size[1]+block, size[2], size[3]-block};
				
				this.left = new BSPTree(left, this);
				this.right = new BSPTree(right, this);
			}
		}
		
		public void fillRooms(Tile[][] levelArray)
		{
			if (left == null)
			{
				createRooms(levelArray);
			}
			else
			{
				left.fillRooms(levelArray);
				right.fillRooms(levelArray);
			}
		}
		
		private void createRooms(Tile[][] levelArray)
		{
			if (size[2] < 2) size[2] = 2;
			if (size[3] < 2) size[3] = 2;
			
			Random ran = new Random();
			int mx = size[0] + (size[2]/2);
			int my = size[1] + (size[3]/2);
			
			int x1 = mx - ((size[2]-2)/4 + ran.nextInt((size[2])/2));
			int x2 = mx + ((size[2]-2)/4 + ran.nextInt((size[2])/2));
			
			int y1 = my - ((size[3]-2)/4 + ran.nextInt((size[3])/2));
			int y2 = my + ((size[3]-2)/4 + ran.nextInt((size[3])/2));
			
			if (x1 < size[0]) x1 = size[0];
			if (x2 > size[0]+size[2]) x2 = size[0]+size[2];
			if (y1 < size[1]) y1 = size[1];
			if (y2 > size[1]+size[3]) y2 = size[1]+size[3];
			
			room = new int[]{0, x1, y1, x2, y2};
			
			for (int x = x1; x < x2; x++)
			{
				for (int y = y1; y < y2; y++)
				{
					Tile t = levelArray[x][y];
					updateTile(t, 0, '.');
				}
			}
		}
		
		public void joinRooms(Tile[][] levelArray)
		{
			if (left == null)
			{
				return;
			}
			else
			{
				joinRoom(levelArray);
				left.joinRooms(levelArray);
				right.joinRooms(levelArray);
			}
		}
		
		private void joinRoom(Tile[][] levelArray)
		{
			int x1 = 0;
			int x2 = 0;
			int y1 = 0;
			int y2 = 0;
			
			if (left.size[0] == right.size[0])
			{
				x1 = left.size[0] + (left.size[2]/2);
				x2 = x1 + 1;
				
				y1 = left.size[1] + (left.size[3]/2);
				y2 = right.size[1]+ (right.size[3]/2);
			}
			else
			{
				x1 = left.size[0] + (left.size[2]/2);
				x2 = right.size[0] + (right.size[2]/2);
				
				y1 = left.size[1] + (left.size[3]/2);
				y2 = y1 + 1;
			}
			
			for (int x = x1; x < x2; x++)
			{
				for (int y = y1; y < y2; y++)
				{
					Tile t = levelArray[x][y];
					updateTile(t, 0, '.');
				}
			}
		}

	}


}

