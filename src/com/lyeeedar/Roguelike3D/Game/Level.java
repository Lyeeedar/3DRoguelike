package com.lyeeedar.Roguelike3D.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Graphics.Light;
import com.lyeeedar.Roguelike3D.Graphics.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.VisibleObject;


public class Level {
	
	private Tile[][] levelArray;
	private ArrayList<Light> levelLights = new ArrayList<Light>();
	private ArrayList<GameObject> levelGraphics = new ArrayList<GameObject>();
	
	Vector3 ambient = new Vector3(0.1f, 0.1f, 0.1f);
	Vector3 defColour = new Vector3(0.8f, 0.9f, 0.6f);
	HashMap<Character, String> descriptions = new HashMap<Character, String>();
	HashMap<Character, Vector3> colours = new HashMap<Character, Vector3>();
	ArrayList<Character> opaques = new ArrayList<Character>();
	ArrayList<Character> solids = new ArrayList<Character>();
	
	
	public Level(int width, int height)
	{
		solids.add('#');
		solids.add(' ');
		
		opaques.add('#');
		opaques.add(' ');
		
		colours.put('#', new Vector3(0.3f, 0.2f, 0.1f));
		colours.put('.', new Vector3(0.3f, 0.6f, 0.1f));
		
		setLevelArray(new Tile[width][height]);
		
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				getLevelArray()[x][y] = new Tile('#', 0, 8, 8);
			}
		}
		
	}
	
	public void createLevelGraphics()
	{
		HashMap<Float, Mesh> meshes = new HashMap<Float, Mesh>();
		
		for (int i = 0; i < 10; i++)
		{
			
		}
		
		for (int x = 0; x < levelArray.length; x++)
		{
			for (int z = 0; z < levelArray[0].length; z++)
			{
				Tile t = levelArray[x][z];
				if (t.character == ' ') continue;
				
				if (!meshes.containsKey(t.height))
				{
					meshes.put(t.height, Shapes.genCuboid(5, t.height, 5));
				}
				
				VisibleObject vo = new VisibleObject(meshes.get(t.height), getColour(t.character), "tex"+t.character);
				t.floorGo = new GameObject(vo, x*10, t.height-5, z*10);
				
				if (t.height < t.roof)
				{
					if (!meshes.containsKey(1))
					{
						meshes.put(1f, Shapes.genCuboid(5, 1, 5));
					}
					
					VisibleObject voRf = new VisibleObject(meshes.get(1f), getColour('#'), "tex#");
					t.roofGo = new GameObject(voRf, x*10, t.roof, z*10);
				}
			}
		}
		
		for (Tile[] ts : levelArray)
		{
			for (Tile t : ts)
			{
				GameObject g = t.floorGo;
				if (g != null) levelGraphics.add(g);
				g = t.roofGo;
				if (g != null) levelGraphics.add(g);
			}
		}
		
		
	}
	
	private Vector3 getColour(char c)
	{
		if (colours.containsKey(c))
		{
			return colours.get(c);
		}
		else
		{
			return defColour;
		}
	}
	
	public boolean checkCollision(float x, float y, float z, BoundingBox box, String UID)
	{	
		int ix = (int)(x+0.5f);
		int iz = (int)(z+0.5f);
		
		if (ix < 0 || ix > getLevelArray()[0].length-1) return true;
		if (iz < 0 || iz > getLevelArray().length-1) return true;

		Tile t = null;
		
		t = getLevelArray()[ix][iz];
		if (y < t.floor || y > t.roof) return true;
		if (checkTileCollision(t, box, UID)) return true;
		
//		if (iz-1 > -1)
//		{
//			t = getLevelArray()[ix][iz-1];
//			if (checkTileCollision(t, box, UID)) return true;
//		}
//
//		if (iz+1 < getLevelArray().length)
//		{
//			t = getLevelArray()[ix][iz+1];
//			if (checkTileCollision(t, box, UID)) return true;
//		}
//
//		if (ix-1 > -1 && iz-1 > -1)
//		{
//			t = getLevelArray()[ix-1][iz-1];
//			if (checkTileCollision(t, box, UID)) return true;
//		}
//
//		if (ix-1 > -1)
//		{
//			t = getLevelArray()[ix-1][iz];
//			if (checkTileCollision(t, box, UID)) return true;
//		}
//
//		if (ix-1 > -1 && iz+1 < getLevelArray().length)
//		{
//			t = getLevelArray()[ix-1][iz+1];
//			if (checkTileCollision(t, box, UID)) return true;
//		}
//
//		if (ix+1 < getLevelArray()[0].length && iz-1 > -1)
//		{
//			t = getLevelArray()[ix+1][iz-1];
//			if (checkTileCollision(t, box, UID)) return true;
//		}
//
//		if (ix+1 < getLevelArray()[0].length)
//		{
//			t = getLevelArray()[ix+1][iz];
//			if (checkTileCollision(t, box, UID)) return true;
//		}
//
//		if (ix+1 < getLevelArray()[0].length && iz+1 < getLevelArray().length)
//		{
//			t = getLevelArray()[ix+1][iz+1];
//			if (checkTileCollision(t, box, UID)) return true;
//		}
		
		
		return false;
	}
	
	public boolean checkTileCollision(Tile t, BoundingBox box, String UID)
	{
		if (t.character == ' ') return true;

		for (Character c : solids)
		{
			if (t.character == c)
			{
				return true;
			}
		}
		
		for (GameActor ga : t.actors)
		{
			if (ga.UID.equals(UID)) continue;
			
			if (intersectBoxes(box, ga.getBoundingBox())) return true;
		}
		
		return false;
	}
	
	public boolean intersectBoxes(BoundingBox b1, BoundingBox b2)
	{
		if (
			b1.min.x < b2.max.x &&
			b1.min.y < b2.max.y &&
			b1.min.z < b2.max.z &&
			b1.max.x > b2.min.x &&
			b1.max.y > b2.min.y &&
			b1.max.z > b2.min.z
			) return true;
		else return false;
	}
	
	public void moveActor(float oldX, float oldZ, float newX, float newZ, String UID)
	{
		int ox = (int)(oldX+0.5f);
		int oz = (int)(oldZ+0.5f);
		
		int nx = (int)(newX+0.5f);
		int nz = (int)(newZ+0.5f);
		
		if (ox == nx && oz == nz) return;
		
		GameActor actor = null;
		
		int i = 0;
		for (GameActor ga : levelArray[ox][oz].actors)
		{
			if (ga.UID.equals(UID)) 
			{ 
				actor = levelArray[ox][oz].actors.get(i);
				levelArray[ox][oz].actors.remove(i);
				break;
			}
			i++;
		}
		
		if (actor == null)
		{
			System.err.println("Error removing actor from tile:"+ox+" "+oz);
			return;
		}
		
		levelArray[nx][nz].actors.add(actor);
	}
	
	public boolean checkOpaque(int x, int z)
	{
		if (x < 0 || x > getLevelArray()[0].length-1) return true;
		if (z < 0 || z > getLevelArray().length-1) return true;
		
		boolean opaque = false;
		
		for (Character c : solids)
		{
			if (getLevelArray()[(int)x][(int)z].character == c)
			{
				opaque = true;
				break;
			}
		}
		
		return opaque;
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
			getLevelArray()[i][y].character = '.';
			getLevelArray()[i][y].height = 0;
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
				getLevelArray()[i][y].character = '.';
				getLevelArray()[i][y].height = 0;
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
		
		tree.fillRooms(getLevelArray());
		tree.joinRooms(getLevelArray());
	}
	
	public void clearWalls()
	{
		for (int x = 0; x < getLevelArray().length; x++)
		{
			for (int y = 0; y < getLevelArray()[0].length; y++)
			{
				if (x == 0 || x == getLevelArray().length-1
						|| y == 0 || y == getLevelArray()[0].length-1) 
				{
					Tile t = getLevelArray()[x][y];
					t.character = '#';
					t.height = 8;
				}
				
				if (chWl(x-1, y) && chWl(x, y-1)
						&& chWl(x-1, y-1) && chWl(x-1, y+1)
						&& chWl(x+1, y-1) && chWl(x+1, y+1)
						&& chWl(x+1, y) && chWl(x, y+1))
				{
					getLevelArray()[x][y].character = ' ';
				}
			}
		}
	}
	
	private boolean chWl(int x, int y)
	{
		if (x < 0 || x > getLevelArray().length-1 ||
				y < 0 || y > getLevelArray()[0].length-1)
			return true;
		
		if (getLevelArray()[x][y].character == '#' ||
				getLevelArray()[x][y].character == ' ')
			return true;
		else
			return false;
	}

	public Tile[][] getLevelArray() {
		return levelArray;
	}

	public void setLevelArray(Tile[][] levelArray) {
		this.levelArray = levelArray;
	}

	public ArrayList<Light> getLevelLights() {
		return levelLights;
	}

	public void setLevelLights(ArrayList<Light> levelLights) {
		this.levelLights = levelLights;
	}

	public HashMap<Character, String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(HashMap<Character, String> descriptions) {
		this.descriptions = descriptions;
	}

	public Vector3 getAmbient() {
		return ambient;
	}

	public void setAmbient(Vector3 ambient) {
		this.ambient = ambient;
	}
	
	
	public void dispose()
	{
		for (Tile[] ts : levelArray)
		{
			for (Tile t : ts)
			{
				GameObject g = t.floorGo;
				if (g != null) g.dispose();
				g = t.roofGo;
				if (g != null) g.dispose();
				
				for (GameActor ga : t.actors)
				{
					ga.dispose();
				}
			}
		}
	}

	public ArrayList<GameObject> getLevelGraphics() {
		return levelGraphics;
	}

	public void setLevelGraphics(ArrayList<GameObject> levelGraphics) {
		this.levelGraphics = levelGraphics;
	}

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
				t.character = '.';
				t.height = 0;
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
				t.character = '.';
				t.height = 0;
			}
		}
	}

}
