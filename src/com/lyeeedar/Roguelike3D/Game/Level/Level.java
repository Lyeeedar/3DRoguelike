package com.lyeeedar.Roguelike3D.Game.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.CollisionBox;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Item.VisibleItem;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractTile.TileType;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;


public class Level {
	
	Tile[][] levelArray;
	
	HashMap<Character, String> descriptions = new HashMap<Character, String>();
	HashMap<Character, Color> colours = new HashMap<Character, Color>();
	ArrayList<Character> opaques = new ArrayList<Character>();
	ArrayList<Character> solids = new ArrayList<Character>();
	
	public ArrayList<GameActor> actors = new ArrayList<GameActor>();
	public ArrayList<VisibleItem> items = new ArrayList<VisibleItem>();
	
	public ArrayList<DungeonRoom> rooms;
	
	public int width;
	public int height;
	
	public Level(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		solids.add('#');
		solids.add(' ');
		
		opaques.add('#');
		opaques.add(' ');
		
		colours.put('#', new Color(0.3f, 0.2f, 0.1f, 1.0f));
		colours.put('.', new Color(0.3f, 0.6f, 0.1f, 1.0f));
		colours.put(' ', new Color(0.8f, 0.9f, 0.6f, 1.0f));
		
		MapGenerator generator = new MapGenerator(width, height, solids, opaques, colours);
		levelArray = generator.getLevel();
		rooms = generator.getRooms();
	}
	
	public Tile getTile(float x, float z)
	{
		int ix = (int)(x+0.5f);
		int iz = (int)(z+0.5f);
		
		return getLevelArray()[ix][iz];
	}
	
	public boolean checkCollision(CollisionBox box, String UID)
	{
		if (checkLevelCollision(box.position.x, box.position.y, box.position.z)) return true;
		if (checkLevelCollision(box.position.x+box.dimensions.x, box.position.y, box.position.z)) return true;
		if (checkLevelCollision(box.position.x, box.position.y+box.dimensions.y, box.position.z)) return true;
		if (checkLevelCollision(box.position.x+box.dimensions.x, box.position.y+box.dimensions.y, box.position.z)) return true;
		if (checkLevelCollision(box.position.x, box.position.y, box.position.z+box.dimensions.z)) return true;
		if (checkLevelCollision(box.position.x+box.dimensions.x, box.position.y, box.position.z+box.dimensions.z)) return true;
		if (checkLevelCollision(box.position.x, box.position.y+box.dimensions.y, box.position.z+box.dimensions.z)) return true;
		if (checkLevelCollision(box.position.x+box.dimensions.x, box.position.y+box.dimensions.y, box.position.z+box.dimensions.z)) return true;
		
		return checkEntities(box, UID) != null;
	}
	
	public boolean checkLevelCollision(float x, float y, float z)
	{					
		int ix = (int)(x+3)/10;
		int iz = (int)(z+3)/10;
		
		if (ix < 0 || ix > width) return true;
		if (iz < 0 || iz > height) return true;

		Tile t = null;
		
		t = getLevelArray()[ix][iz];
		if (y < t.floor || y > t.roof) return true;
		
		return checkSolid(ix, iz);
	}
	
	public boolean checkSolid(int x, int z)
	{
		Tile t = null;
		
		t = getLevelArray()[x][z];
		
		if (t.character == ' ') return true;

		for (Character c : solids)
		{
			if (t.character == c)
			{
				return true;
			}
		}
		return false;
	}
	
	public GameActor checkEntities(CollisionBox box, String UID)
	{
		for (GameActor ga : actors)
		{
			if (ga.UID.equals(UID)) continue;
			
			if (box.intersect(ga.getCollisionBox()))
			{
				return ga;
			}
		}
		
		return null;
	}
//	
	public void removeItem(String UID)
	{
		int i = 0;
		for (VisibleItem ga : items)
		{
			//System.out.println(ga.UID);
			if (ga.UID.equals(UID)) 
			{
				if (ga.boundLight!= null) GameData.lightManager.removeLight(ga.boundLight.UID);
				items.remove(i);
				return;
			}
			i++;
		}
		System.err.println("Failed to remove item!");
	}
	
	public void removeActor(String UID)
	{
		int i = 0;
		for (GameActor ga : actors)
		{
			if (ga.UID.equals(UID)) 
			{
				if (ga.boundLight!= null) GameData.lightManager.removeLight(ga.boundLight.UID);
				actors.remove(i);
				return;
			}
			i++;
		}
		System.err.println("Failed to remove actor!");
	}
//
//	
//	public void moveActor(float oldX, float oldZ, float newX, float newZ, String UID)
//	{
//		int ox = (int)(oldX+0.5f);
//		int oz = (int)(oldZ+0.5f);
//		
//		int nx = (int)(newX+0.5f);
//		int nz = (int)(newZ+0.5f);
//		
//		if (ox == nx && oz == nz) return;
//		if (checkSolid(nx, nz)) return;
//		
//		GameActor actor = null;
//		
//		int i = 0;
//		for (GameActor ga : levelArray[ox][oz].actors)
//		{
//			if (ga.UID.equals(UID)) 
//			{ 
//				actor = levelArray[ox][oz].actors.get(i);
//				levelArray[ox][oz].actors.remove(i);
//				break;
//			}
//			i++;
//		}
//		
//		if (actor == null)
//		{
//			System.err.println("Error removing actor from tile:"+ox+" "+oz);
//			bruteForceMoveActor(newX, newZ, UID);
//			return;
//		}
//		
//		levelArray[nx][nz].actors.add(actor);
//	}
//	
//	public void bruteForceMoveActor(float newX, float newZ, String UID)
//	{
//		int nx = (int)(newX+0.5f);
//		int nz = (int)(newZ+0.5f);
//		
//		GameActor actor = null;
//		for (int x = 0; x < levelArray.length; x++)
//		{
//			for (int y = 0; y < levelArray[0].length; y++)
//			{
//				int i = 0;
//				for (GameActor ga : levelArray[x][y].actors)
//				{
//					if (ga.UID.equals(UID)) 
//					{ 
//						actor = levelArray[x][y].actors.get(i);
//						levelArray[x][y].actors.remove(i);
//						break;
//					}
//					i++;
//				}
//				if (actor != null)
//				{
//					levelArray[nx][nz].actors.add(actor);
//					return;
//				}
//			}
//		}
//		System.err.println("Failed brute force move actor!");
//	}
//	
//	public void moveItem(float oldX, float oldZ, float newX, float newZ, String UID)
//	{
//		int ox = (int)(oldX+0.5f);
//		int oz = (int)(oldZ+0.5f);
//		
//		int nx = (int)(newX+0.5f);
//		int nz = (int)(newZ+0.5f);
//		
//		//System.out.println("p   "+ ox + "  " + oz + "   " + nx + "   " + nz);
//		
//		if (ox == nx && oz == nz) return;
//		if (checkSolid(nx, nz)) return;
//		
//		VisibleItem item = null;
//		
//		int i = 0;
//		for (VisibleItem vi : levelArray[ox][oz].items)
//		{
//			if (vi.UID.equals(UID)) 
//			{ 
//				item = levelArray[ox][oz].items.get(i);
//				levelArray[ox][oz].items.remove(i);
//				break;
//			}
//			i++;
//		}
//		
//		if (item == null)
//		{
//			System.err.println("Error removing item from tile:"+ox+" "+oz);
//			return;
//		}
//		
//		levelArray[nx][nz].items.add(item);
//	}
//	
	public void addItem(VisibleItem item)
	{
		items.add(item);
	}
	
	public void addActor(GameActor actor)
	{
		actors.add(actor);
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

	public Tile[][] getLevelArray() {
		return levelArray;
	}

	public void setLevelArray(Tile[][] levelArray) {
		this.levelArray = levelArray;
	}

	public HashMap<Character, String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(HashMap<Character, String> descriptions) {
		this.descriptions = descriptions;
	}
	
	public void dispose()
	{
		for (GameActor ga : actors)
		{
			ga.dispose();
		}
		for (VisibleItem vi : items)
		{
			vi.dispose();
		}
	}

	public HashMap<Character, Color> getColours() {
		return colours;
	}

	public void setColours(HashMap<Character, Color> colours) {
		this.colours = colours;
	}

	public ArrayList<Character> getOpaques() {
		return opaques;
	}

	public void setOpaques(ArrayList<Character> opaques) {
		this.opaques = opaques;
	}

	public ArrayList<Character> getSolids() {
		return solids;
	}

	public void setSolids(ArrayList<Character> solids) {
		this.solids = solids;
	}
}

