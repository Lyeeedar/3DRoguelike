/*******************************************************************************
 * Copyright (c) 2013 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Game.Level;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractTile.TileType;
import com.lyeeedar.Roguelike3D.Game.Level.MapGenerator.GeneratorType;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.XML.MonsterEvolver;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RoomReader;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.Static;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;


public class Level implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7198101723293369502L;

	public static final String MONSTER_TYPE = "monster_type";
	
	Tile[][] levelArray;
	
	HashMap<Character, String> shortDescs = new HashMap<Character, String>();
	HashMap<Character, String> longDescs = new HashMap<Character, String>();
	
	HashMap<Character, Colour> colours = new HashMap<Character, Colour>();
	ArrayList<Character> opaques = new ArrayList<Character>();
	ArrayList<Character> solids = new ArrayList<Character>();
	
	public ArrayList<GameActor> actors = new ArrayList<GameActor>();
	public ArrayList<LevelObject> levelObjects = new ArrayList<LevelObject>();
	
	private transient ArrayList<DungeonRoom> rooms;

	public int width;
	public int height;
	
	public GeneratorType gtype;
	
	public boolean hasRoof;
	public int depth;
	
	public Level(int width, int height, GeneratorType gtype, BiomeReader biome, boolean hasRoof, int depth, int up, int down)
	{
		this.depth = depth;
		this.hasRoof = hasRoof;
		this.gtype = gtype;
		this.width = width;
		this.height = height;
		
		solids.add('#');
		solids.add(' ');
		
		opaques.add('#');
		opaques.add(' ');
		
		colours.put('#', biome.getWallColour());
		colours.put('.', biome.getFloorColour());
		colours.put(' ', biome.getWallColour());
		
		shortDescs.put('#', biome.getShortDescription('#'));
		longDescs.put('#', biome.getLongDescription('#'));
		shortDescs.put('.', biome.getShortDescription('.'));
		longDescs.put('.', biome.getLongDescription('.'));
		shortDescs.put('R', biome.getShortDescription('R'));
		longDescs.put('R', biome.getLongDescription('R'));
		
		MapGenerator generator = new MapGenerator(width, height, solids, opaques, colours, gtype, biome, up, down);
		levelArray = generator.getLevel();
		rooms = generator.getRooms();
		
		for (AbstractObject ao : generator.getObjects())
		{
			LevelObject lo = LevelObject.checkObject(ao, (ao.x)*10, 0, (ao.z)*10, this, null);
			
			if (lo != null)
			{
				levelObjects.add(lo);
			}
		}
	}
	
	public void fixReferences()
	{
		for (Tile[] tiles : levelArray)
		{
			for (Tile t : tiles)
			{
				t.fixReferences();
			}
		}
	}
	
	public GameActor getActor(String UID)
	{
		for (GameActor ga : actors)
		{
			if (ga.UID.equals(UID)) return ga;
		}
		
		System.err.println("Actor not found!");
		return null;
	}
	
	public LevelObject getLevelObject(String UID)
	{
		for (LevelObject lo : levelObjects)
		{
			if (lo.UID.equals(UID)) return lo;
		}
		
		System.err.println("Level Object not found!");
		return null;
	}
	
	public void createActors()
	{
		for (GameActor ga : actors) {
			ga.create();
		}
	}
	
	public void createLevelObjects()
	{
		for (LevelObject lo : levelObjects) {
			lo.create();
		}
	}
	
	private transient int fillRoomIndex = 0;
	public boolean fillRoom(RoomReader rReader, LevelContainer lc)
	{
		if (fillRoomIndex == rooms.size())
		{
			return true;
		}
		
		DungeonRoom room = rooms.get(fillRoomIndex);
		AbstractRoom aroom = rReader.getRoom(room.roomtype, room.width, room.height, (gtype != GeneratorType.STATIC));
		
		if (aroom == null) {
			fillRoomIndex++;
			System.err.println("Failed to place "+room.roomtype);
			return false;
		}
		System.out.println("Placed room "+room.roomtype);
		
		
		ArrayList<AbstractObject> abstractObjects = new ArrayList<AbstractObject>();
		MonsterEvolver evolver = null;
		
		if (aroom.meta.containsKey(MONSTER_TYPE))
		{
			evolver = lc.getMonsterEvolver(aroom.meta.get(MONSTER_TYPE));
		}
		
		for (int i = 0; i < aroom.width; i++)
		{
			for (int j = 0; j < aroom.height; j++)
			{
				if (aroom.contents[i][j] == '#')
				{
					levelArray[room.x+i][room.y+j].character = '#';
					levelArray[room.x+i][room.y+j].height = levelArray[room.x+i][room.y+j].roof;
				}
				else
				{
					levelArray[room.x+i][room.y+j].character = '.';
					levelArray[room.x+i][room.y+j].height = levelArray[room.x+i][room.y+j].floor;
					
					AbstractObject ao = aroom.objects.get(aroom.contents[i][j]);
					
					if (ao == null) continue;
					
					System.out.println("Placed object "+ao.type);
					
					ao = ao.cpy();
					
					ao.x = room.x+i;
					ao.z = room.y+j;
					ao.y = levelArray[room.x+i][room.y+j].floor;
					
					abstractObjects.add(ao);
					
				}
				
			}
		}
		
		for (AbstractObject ao : abstractObjects)
		{
			LevelObject lo = LevelObject.checkObject(ao, (ao.x)*10, 0, (ao.z)*10, this, evolver);
	
			if (lo != null)
			{
				lo.shortDesc = ao.shortDesc;
				lo.longDesc = ao.longDesc;
				levelObjects.add(lo);
				
				levelArray[(int) ao.x][(int) ao.z].setLo(lo);
			}
			else
			{
				System.err.println("Failed at creating Object! Char=" + ao.character + " Type=" + ao.type);
			}
		}
		
		fillRoomIndex++;
		return false;
	}
	
	
	private transient float tempdist2 = 0;

	public GameActor getClosestActor(Ray ray, float dist2, Vector3 p1, Vector3 p2, String ignoreUID, Vector3 collisionPoint)
	{
		GameActor chosen = null;
		for (GameActor go : GameData.level.actors)
		{
			if (go.UID.equals(ignoreUID)) continue;
			
			if (p1.dst2(go.getPosition()) < go.vo.attributes.radius*go.vo.attributes.radius) return go;
			if (p2.dst2(go.getPosition()) < go.vo.attributes.radius*go.vo.attributes.radius) return go;

			if (Intersector.intersectRaySphere(ray, go.getPosition(), go.getRadius(), tmpVec)) 
			{
				tempdist2 = tmpVec.dst2(ray.origin);
				if (tempdist2 > dist2) continue;
				else
				{
					if (collisionPoint != null) collisionPoint.set(tmpVec);
					dist2 = tempdist2;
					chosen = go;
				}
			}

		}
		return chosen;
	}
	
	/**
	 * 
	 * @param ray - The ray to be used for Intersecting
	 * @param dist2 - The square of the maximum distance (to remove square root operations)
	 * @param ignoreUID - The UID of the object to ignore
	 * @return
	 */
	public GameActor getClosestActor(Ray ray, float dist2, String ignoreUID, Vector3 collisionPoint)
	{
		GameActor chosen = null;
		for (GameActor go : GameData.level.actors)
		{
			if (go.UID.equals(ignoreUID)) continue;

			if (Intersector.intersectRaySphere(ray, go.getPosition(), go.getRadius(), tmpVec)) 
			{
				tempdist2 = tmpVec.dst2(ray.origin);
				if (tempdist2 > dist2) continue;
				else
				{
					if (collisionPoint != null) collisionPoint.set(tmpVec);
					dist2 = tempdist2;
					chosen = go;
				}
			}

		}
		return chosen;
	}
	/**
	 * 
	 * @param ray - The ray to be used for Intersecting
	 * @param dist2 - The square of the maximum distance (to remove square root operations)
	 * @param ignoreUID - The UID of the object to ignore
	 * @return
	 */
	public LevelObject getClosestLevelObject(Ray ray, float dist2, String ignoreUID, Vector3 collisionPoint)
	{
		LevelObject chosen = null;
		for (LevelObject go : GameData.level.levelObjects)
		{
			if (go.UID.equals(ignoreUID)) continue;

			if (Intersector.intersectRaySphere(ray, go.getPosition(), go.getRadius(), tmpVec)) 
			{
				tempdist2 = tmpVec.dst2(ray.origin);
				if (tempdist2 > dist2) continue;
				else
				{
					if (collisionPoint != null) collisionPoint.set(tmpVec);
					dist2 = tempdist2;
					chosen = go;
				}
			}
		}

		return chosen;
	}
	
	public boolean checkLevelCollisionRay(Ray ray, float view)
	{
		Vector3 pos = ray.origin.tmp2();
		Vector3 step = ray.direction.tmp2().mul(VIEW_STEP);
		
		float dist = 0;
		
		for (int i = 0; i < view; i += VIEW_STEP)
		{
			dist += VIEW_STEP;
			
			if (dist*dist > view) break;
			
			pos.add(step);
			
			if (pos.x < 0 || pos.x/10 > width) { dist=view; break; }
			if (pos.z < 0 || pos.z/10 > height) { dist=view; break; }
			
			Tile t = getTile((pos.x/10f)+0.5f, (pos.z/10f)+0.5f);
			
			if (pos.y < t.height)
			{
				return true;
			}
			else if (hasRoof && pos.y > t.roof)
			{
				return true;
			}
		}
		
		return false;
	}
	
	private static final int VIEW_STEP = 10;
	public float getDescription(Ray ray, float view, StringBuilder sB, boolean longDesc)
	{
		Vector3 pos = ray.origin.cpy();
		Vector3 step = ray.direction.cpy().mul(VIEW_STEP);
		
		float dist = 0;
		
		for (int i = 0; i < view; i += VIEW_STEP)
		{
			dist += VIEW_STEP;
			
			if (dist*dist > view) break;
			
			pos.add(step);
			
			if (pos.x < 0 || pos.x/10 > width) { dist=view; break; }
			if (pos.z < 0 || pos.z/10 > height) { dist=view; break; }
			
			Tile t = getTile((pos.x/10f)+0.5f, (pos.z/10f)+0.5f);
			
			if (pos.y < t.height)
			{
				sB.delete(0, sB.length());
				if (longDesc)
				{
					sB.append(longDescs.get(t.character));
				}
				else
				{
					sB.append(shortDescs.get(t.character));
				}
				break;
			}
			else if (hasRoof && pos.y > t.roof)
			{
				sB.delete(0, sB.length());
				if (longDesc)
				{
					sB.append(longDescs.get('R'));
				}
				else
				{
					sB.append(shortDescs.get('R'));
				}
				break;
			}
		}
		
		return dist*dist;
	}
	
	public Tile getTile(float x, float z)
	{
		int ix = (int)(x);
		int iz = (int)(z);
		
		return getLevelArray()[ix][iz];
	}
	
	public boolean checkCollision(Vector3 position, float radius, String UID)
	{
		if (checkLevelCollision(position, radius)) return true;
		if (checkLevelObjects(position, radius) != null) return true;
		return checkEntities(position, radius, UID) != null;
	}
	
	public boolean checkLevelCollision(Vector3 position, float radius)
	{
		if (checkCollisionTile(position.x+radius, position.y, position.z, radius)) return true;
		if (checkCollisionTile(position.x-radius, position.y, position.z, radius)) return true;
		if (checkCollisionTile(position.x, position.y, position.z+radius, radius)) return true;
		if (checkCollisionTile(position.x, position.y, position.z-radius, radius)) return true;
		
		if (checkCollisionTile(position.x+radius, position.y, position.z+radius, radius)) return true;
		if (checkCollisionTile(position.x-radius, position.y, position.z+radius, radius)) return true;
		if (checkCollisionTile(position.x-radius, position.y, position.z-radius, radius)) return true;
		if (checkCollisionTile(position.x+radius, position.y, position.z-radius, radius)) return true;

		
		return false;
	}
	
	private boolean checkCollisionTile(float fx, float fy, float fz, float radius)
	{
		int x = (int)((fx/10)+0.5f);
		int z = (int)((fz/10)+0.5f);
		
		if (x < 0 || x > width) return true;
		if (z < 0 || z > height) return true;
		
		Tile t = getLevelArray()[x][z];
		
		if ((fy-radius < t.floor) || (hasRoof && fy+radius > t.roof)) return true;

		return checkSolid(x, z);
	}
	
	private final Vector3 tmpVec = new Vector3();
	public GameActor checkEntities(Vector3 position, float radius, String UID)
	{
		for (GameActor ga : actors)
		{
			if (!ga.isSolid()) continue;
			
			if (ga.UID.equals(UID)) continue;
			
			if (position.dst2(ga.getPosition()) < (radius+ga.vo.attributes.radius)*(radius+ga.vo.attributes.radius))
			{
				return ga;
			}
		}
		
		return null;
	}
	
	public LevelObject checkLevelObjects(Vector3 position, float radius)
	{
		for (LevelObject ga : levelObjects)
		{
			if (!ga.isSolid()) continue;
			
			//if (position.dst2(ga.getPosition()) < (radius+ga.vo.attributes.radius)*(radius+ga.vo.attributes.radius))
			Vector3 box = ga.vo.attributes.box;
			Vector3 hbox = box.tmp2().mul(0.5f);
			if (GameData.SphereBoxIntersection(position.x, position.y, position.z, radius,
					ga.getPosition().x-hbox.x, ga.getPosition().y-hbox.y, ga.getPosition().z-hbox.z,
					box.x, box.y, box.z))
			{
				return ga;
			}
		}
		
		return null;
	}
	
	public GameActor checkCollisionGameActors(Vector3 position, Vector3 box)
	{
		for (GameActor ga : actors)
		{
			if (!ga.isSolid()) continue;
			
			Vector3 hbox = box.tmp2().mul(0.5f);
			if (GameData.SphereBoxIntersection(ga.getPosition().x, ga.getPosition().y, ga.getPosition().z, ga.radius,
					position.x-hbox.x, position.y-hbox.y, position.z-hbox.z,
					box.x, box.y, box.z))
			{
				return ga;
			}
		}
		
		return null;	
	}
	
	public boolean checkLevelCollision(float x, float y, float z)
	{					
		int ix = (int)((x/10f));
		int iz = (int)((z/10f));
		
		if (ix < 0 || ix >= width) return true;
		if (iz < 0 || iz >= height) return true;

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
	
	public boolean checkSolid(Tile t)
	{
		for (Character c : solids)
		{
			if (t.character == c)
			{
				return true;
			}
		}
		return false;
	}
	
	public void removeActor(String UID)
	{
		int i = 0;
		for (GameActor ga : actors)
		{
			if (ga.UID.equals(UID)) 
			{
				if (ga.boundLightUID != null) GameData.lightManager.removeDynamicLight(ga.boundLightUID);
				actors.remove(i);
				return;
			}
			i++;
		}
		System.err.println("Failed to remove actor!");
	}
	
	public void addActor(GameActor actor)
	{
		actors.add(actor);
	}
	
	public void addLvlObject(LevelObject lo)
	{
		levelObjects.add(lo);
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
	
	public void dispose()
	{
		for (GameActor ga : actors)
		{
			ga.dispose();
		}
		for (LevelObject lo : levelObjects)
		{
			lo.dispose();
		}
	}

	public HashMap<Character, Colour> getColours() {
		return colours;
	}

	public void setColours(HashMap<Character, Colour> colours) {
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

