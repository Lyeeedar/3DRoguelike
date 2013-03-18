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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Pools;
import com.lyeeedar.Graphics.ParticleEffects.ParticleEmitter;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.Level.MapGenerator.GeneratorType;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.XML.MonsterEvolver;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RoomReader;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer;
import com.lyeeedar.Utils.Bag;


public class Level implements Serializable {
	
	private static final long serialVersionUID = 7198101723293369502L;
	
	private static final int VIEW_STEP = 10;

	public static final String MONSTER_TYPE = "monster_type";
	
	public final Tile[][] levelArray;
	private int bx; private int bz;
	private int tx; private int tz;
	private float radius2;
	
	private final Tile[][] block = new Tile[3][3];
	
	public final HashMap<Character, String> shortDescs = new HashMap<Character, String>();
	public final HashMap<Character, String> longDescs = new HashMap<Character, String>();
	
	public final HashMap<Character, Color> colours = new HashMap<Character, Color>();
	public final Bag<Character> opaques = new Bag<Character>();
	public final Bag<Character> solids = new Bag<Character>();
	
	private transient final Bag<DungeonRoom> rooms;

	public final int width;
	public final int height;
	public final boolean hasRoof;
	public final int depth;
	public final GeneratorType gtype;
	
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
				addLevelObject(lo);
			}
		}
	}
	
	public boolean fillRoom(RoomReader rReader, LevelContainer lc)
	{
		if (rooms.size == 0)
		{
			return true;
		}
		
		DungeonRoom room = rooms.remove(0);
		AbstractRoom aroom = rReader.getRoom(room.roomtype, room.width, room.height, (gtype != GeneratorType.STATIC));
		
		if (aroom == null) {
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
				addLevelObject(lo);
			}
			else
			{
				System.err.println("Failed at creating Object! Char=" + ao.character + " Type=" + ao.type);
			}
		}
		
		return false;
	}
	
	// ----- Add and Remove GameObjects ----- //
	
	public void addLevelObject(LevelObject lo)
	{
		Tile tile = getTile(lo.position.x, lo.position.z);
		if (tile == null) return;
		
		tile.levelObjects.add(lo);
	}
	
	public void addGameActor(GameActor ga)
	{
		Tile tile = getTile(ga.position.x, ga.position.z);
		if (tile == null) return;
		
		tile.actors.add(ga);
	}
	
	public void removeLevelObject(LevelObject lo)
	{
		Tile tile = getTile(lo.position.x, lo.position.z);
		if (tile == null) return;
		
		tile.removeLevelObject(lo.UID);
	}
	
	public void removeGameActor(GameActor ga)
	{
		Tile tile = getTile(ga.position.x, ga.position.z);
		if (tile == null) return;
		
		tile.removeGameActor(ga.UID);
	}
	
	// ----- 3D Game Actions (Creation, Destruction, Rendering etc) ----- //
	
	public void update(float delta, Camera cam)
	{
		for (Tile[] tt : levelArray)
		{
			for (Tile t : tt)
			{
				t.update(delta, cam);
			}
		}
	}
	
	public void render(Renderer renderer, Camera cam, ArrayList<ParticleEmitter> visibleEmitters)
	{
		for (Tile[] tt : levelArray)
		{
			for (Tile t : tt)
			{
				t.render(renderer, cam, visibleEmitters);
			}
		}
	}
	
	public Player getPlayer()
	{
		Player player = null;
		
		for (Tile[] tt : levelArray)
		{
			for (Tile t : tt)
			{
				player = t.getPlayer();
				if (player != null) return player;
			}
		}
		
		return null;
	}
	
	public void getLights(LightManager lightManager)
	{
		for (Tile[] tt : levelArray)
		{
			for (Tile t : tt)
			{
				t.getLights(lightManager);
			}
		}
	}
	
	public void positionPlayer(Player player, String prevLevel, String currentLevel)
	{
		for (Tile[] tt : levelArray)
		{
			for (Tile t : tt)
			{
				if (t.positionPlayer(player, prevLevel, currentLevel)) return;
			}
		}
	}
	
	public void evaluateUniqueBehaviour(LightManager lightManager)
	{
		for (Tile[] tt : levelArray)
		{
			for (Tile t : tt)
			{
				t.evaluateUniqueBehaviour(this, lightManager);
			}
		}
	}
	
	public void fixReferences()
	{
		for (Tile[] tt : levelArray)
		{
			for (Tile t : tt)
			{
				t.fixReferences();
			}
		}
	}
	
	public void create()
	{
		for (Tile[] tt : levelArray)
		{
			for (Tile t : tt)
			{
				t.create();
			}
		}
	}
	
	public void bakeLights(LightManager lightManager)
	{
		for (Tile[] tt : levelArray)
		{
			for (Tile t : tt)
			{
				t.bakeLights(lightManager);
			}
		}
	}
	
	public void dispose()
	{
		for (Tile[] tt : levelArray)
		{
			for (Tile t : tt)
			{
				t.dispose();
			}
		}
	}
	
	public float getDescription(Ray ray, float view, StringBuilder sB, boolean longDesc)
	{
		Vector3 pos = Pools.obtain(Vector3.class).set(ray.origin);
		Vector3 step = Pools.obtain(Vector3.class).set(ray.direction).mul(VIEW_STEP);
		
		float dist = 0;
		
		for (int i = 0; i < view; i += VIEW_STEP)
		{
			dist += VIEW_STEP;
			
			if (dist*dist > view) break;
			
			pos.add(step);
			
			Tile t = getTile(pos.x, pos.z);
			if (t == null) { dist=view; break; }
			
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
			else if (pos.y > t.roof)
			{
				if (hasRoof) {
					sB.delete(0, sB.length());
					if (longDesc)
					{
						sB.append(longDescs.get('R'));
					}
					else
					{
						sB.append(shortDescs.get('R'));
					}
				}
				else
				{
					
				}
				break;
			}
		}
		
		Pools.free(pos);
		Pools.free(step);
		
		return dist*dist;
	}
	
	// ----- Collision ----- //
	
//	public GameActor collideRayActors(Ray ray, float dist2, Vector3 p1, Vector3 p2, String ignoreUID, Vector3 collisionPoint)
//	{
//		GameActor chosen = null;
//		for (GameActor go : GameData.level.actors)
//		{
//			if (go.UID.equals(ignoreUID)) continue;
//			
//			if (p1.dst2(go.position) < go.radius*go.radius) return go;
//			if (p2.dst2(go.position) < go.radius*go.radius) return go;
//
//			if (Intersector.intersectRaySphere(ray, go.getTruePosition(), go.radius, tmpVec)) 
//			{
//				tempdist2 = tmpVec.dst2(ray.origin);
//				if (tempdist2 > dist2) continue;
//				else
//				{
//					if (collisionPoint != null) collisionPoint.set(tmpVec);
//					dist2 = tempdist2;
//					chosen = go;
//				}
//			}
//
//		}
//		return chosen;
//	}
//	
//	public LevelObject collideRayLevelObjects(Ray ray, float dist2, String ignoreUID, Vector3 collisionPoint)
//	{
//		LevelObject chosen = null;
//		for (LevelObject go : GameData.level.levelObjects)
//		{
//			if (go.UID.equals(ignoreUID)) continue;
//
//			if (Intersector.intersectRaySphere(ray, go.getTruePosition(), go.radius, tmpVec)) 
//			{
//				tempdist2 = tmpVec.dst2(ray.origin);
//				if (tempdist2 > dist2) continue;
//				else
//				{
//					if (collisionPoint != null) collisionPoint.set(tmpVec);
//					dist2 = tempdist2;
//					chosen = go;
//				}
//			}
//		}
//
//		return chosen;
//	}
	
	public boolean collideRayLevel(Ray ray, float view)
	{
		Vector3 pos = Pools.obtain(Vector3.class).set(ray.origin);
		Vector3 step = Pools.obtain(Vector3.class).set(ray.direction).mul(VIEW_STEP);
		
		float dist = 0;
		boolean collide = false;
		
		for (int i = 0; i < view; i += VIEW_STEP)
		{
			dist += VIEW_STEP;
			
			if (dist*dist > view) break;
			
			pos.add(step);

			Tile t = getTile(pos.x, pos.z);
			if (t == null) { dist=view; break; }
			
			if (pos.y < t.height)
			{
				collide = true;
				break;
			}
			else if (hasRoof && pos.y > t.roof)
			{
				collide = true;
				break;
			}
		}
		
		Pools.free(pos);
		Pools.free(step);
		return collide;
	}
	
	public boolean collideSphereAll(float x, float y, float z, float radius, String UID)
	{
		Tile tile = null;
		tile = getTile(x+radius, z);
		if (tile != null && checkSolid(tile)) return true;
		
		tile = getTile(x-radius, z);
		if (tile != null && checkSolid(tile)) return true;
		
		tile = getTile(x, z+radius);
		if (tile != null && checkSolid(tile)) return true;
		
		tile = getTile(x, z-radius);
		if (tile != null && checkSolid(tile)) return true;
		
		tile = getTile(x+radius, z+radius);
		if (tile != null && checkSolid(tile)) return true;
		
		tile = getTile(x+radius, z-radius);
		if (tile != null && checkSolid(tile)) return true;
		
		tile = getTile(x-radius, z+radius);
		if (tile != null && checkSolid(tile)) return true;
		
		tile = getTile(x-radius, z-radius);
		if (tile != null && checkSolid(tile)) return true;

		tile = getTile(x, z);
		if (tile != null && checkSolid(tile)) return true;
		if (tile == null) return true;
		
		if (y-radius < tile.height)
		{
			return true;
		}
		else if (hasRoof && y+radius > tile.roof)
		{
			return true;
		}
		
		if (collideSphereLevelObjectsAll(x, y, z, radius) != null) return true;
		if (collideSphereActorsAll(x, y, z, radius, UID) != null) return true;
		
		return false;
	}
	
	public LevelObject collideSphereLevelObjectsAll(float x, float y, float z, float radius)
	{
		tx = (int)((x/10f)+0.5f);
		tz = (int)((z/10f)+0.5f);
		radius2 = radius * radius;
		final Tile[][] block = getBlock(tx, tz);
		
		LevelObject lo = null;
		
		lo = collideSphereLevelObjectsTile(block[0][0], x, y, z, radius2);
		if (lo != null) return lo;
		
		lo = collideSphereLevelObjectsTile(block[1][0], x, y, z, radius2);
		if (lo != null) return lo;
		
		lo = collideSphereLevelObjectsTile(block[2][0], x, y, z, radius2);
		if (lo != null) return lo;
		
		lo = collideSphereLevelObjectsTile(block[0][1], x, y, z, radius2);
		if (lo != null) return lo;
		
		lo = collideSphereLevelObjectsTile(block[1][1], x, y, z, radius2);
		if (lo != null) return lo;
		
		lo = collideSphereLevelObjectsTile(block[2][1], x, y, z, radius2);
		if (lo != null) return lo;
		
		lo = collideSphereLevelObjectsTile(block[0][2], x, y, z, radius2);
		if (lo != null) return lo;
		
		lo = collideSphereLevelObjectsTile(block[1][2], x, y, z, radius2);
		if (lo != null) return lo;
		
		lo = collideSphereLevelObjectsTile(block[2][2], x, y, z, radius2);
		if (lo != null) return lo;
		
		return null;
	}
	
	public LevelObject collideSphereLevelObjectsTile(Tile tile, float x, float y, float z, float radius2)
	{
		for (LevelObject lo : tile.levelObjects)
		{
			if (!lo.solid) continue;
			if (lo.position.dst2(x, y, z) <= radius2+(lo.radius*lo.radius)) return lo;
		}	
		return null;
	}
	
	public GameActor collideSphereActorsAll(float x, float y, float z, float radius, String UID)
	{
		tx = (int)((x/10f)+0.5f);
		tz = (int)((z/10f)+0.5f);
		radius2 = radius * radius;
		final Tile[][] block = getBlock(tx, tz);
		
		GameActor ga = null;
		
		ga = collideSphereActorsTile(block[0][0], x, y, z, radius2, UID);
		if (ga != null) return ga;
		
		ga = collideSphereActorsTile(block[1][0], x, y, z, radius2, UID);
		if (ga != null) return ga;
		
		ga = collideSphereActorsTile(block[2][0], x, y, z, radius2, UID);
		if (ga != null) return ga;
		
		ga = collideSphereActorsTile(block[0][1], x, y, z, radius2, UID);
		if (ga != null) return ga;
		
		ga = collideSphereActorsTile(block[1][1], x, y, z, radius2, UID);
		if (ga != null) return ga;
		
		ga = collideSphereActorsTile(block[2][1], x, y, z, radius2, UID);
		if (ga != null) return ga;
		
		ga = collideSphereActorsTile(block[0][2], x, y, z, radius2, UID);
		if (ga != null) return ga;
		
		ga = collideSphereActorsTile(block[1][2], x, y, z, radius2, UID);
		if (ga != null) return ga;
		
		ga = collideSphereActorsTile(block[2][2], x, y, z, radius2, UID);
		if (ga != null) return ga;
		
		return null;
	}
	
	public GameActor collideBoxActorsAll(float x, float y, float z, Vector3 box, String UID)
	{
		tx = (int)((x/10f)+0.5f);
		tz = (int)((z/10f)+0.5f);
		final Tile[][] block = getBlock(tx, tz);
		
		GameActor ga = null;
		
		ga = collideBoxActorsTile(block[0][0], x, y, z, box, UID);
		if (ga != null) return ga;
		
		ga = collideBoxActorsTile(block[1][0], x, y, z, box, UID);
		if (ga != null) return ga;
		
		ga = collideBoxActorsTile(block[2][0], x, y, z, box, UID);
		if (ga != null) return ga;
		
		ga = collideBoxActorsTile(block[0][1], x, y, z, box, UID);
		if (ga != null) return ga;
		
		ga = collideBoxActorsTile(block[1][1], x, y, z, box, UID);
		if (ga != null) return ga;
		
		ga = collideBoxActorsTile(block[2][1], x, y, z, box, UID);
		if (ga != null) return ga;
		
		ga = collideBoxActorsTile(block[0][2], x, y, z, box, UID);
		if (ga != null) return ga;
		
		ga = collideBoxActorsTile(block[1][2], x, y, z, box, UID);
		if (ga != null) return ga;
		
		ga = collideBoxActorsTile(block[2][2], x, y, z, box, UID);
		if (ga != null) return ga;
		
		return null;
	}
	
	public GameActor collideSphereActorsTile(Tile tile, float x, float y, float z, float radius2, String UID)
	{
		for (GameActor ga : tile.actors)
		{
			if (!ga.solid) continue;
			if (UID != null)
			{
				if (ga.UID.equals(UID)) continue;
			}
			
			if (ga.position.dst2(x, y, z) <= radius2+(ga.radius*ga.radius)) return ga;
		}
		
		return null;
	}
	
	public GameActor collideBoxActorsTile(Tile tile, float x, float y, float z, Vector3 box, String UID)
	{
		for (GameActor ga : tile.actors)
		{
			if (!ga.solid) continue;
			if (UID != null)
			{
				if (ga.UID.equals(UID)) continue;
			}
			
			if (GameData.SphereBoxIntersection(ga.position.x, ga.position.y, ga.position.z, ga.radius, x, y, z, box.x, box.y, box.z)) return ga;
		}
		
		return null;
	}
	
	public Tile[][] getBlock(int tx, int tz)
	{
		Tile t = null;
		
		bx = tx-1; bz = tz+1;
		if (checkBounds(bx, bz)) t = null;
		else t = levelArray[bx][bz];
		block[0][0] = t;
		
		bx = tx; bz = tz+1;
		if (checkBounds(bx, bz)) t = null;
		else t = levelArray[bx][bz];
		block[1][0] = t;
		
		bx = tx+1; bz = tz+1;
		if (checkBounds(bx, bz)) t = null;
		else t = levelArray[bx][bz];
		block[2][0] = t;
		
		bx = tx-1; bz = tz;
		if (checkBounds(bx, bz)) t = null;
		else t = levelArray[bx][bz];
		block[0][1] = t;
		
		bx = tx; bz = tz;
		if (checkBounds(bx, bz)) t = null;
		else t = levelArray[bx][bz];
		block[1][1] = t;
		
		bx = tx+1; bz = tz;
		if (checkBounds(bx, bz)) t = null;
		else t = levelArray[bx][bz];
		block[2][1] = t;
		
		bx = tx-1; bz = tz-1;
		if (checkBounds(bx, bz)) t = null;
		else t = levelArray[bx][bz];
		block[0][2] = t;
		
		bx = tx; bz = tz-1;
		if (checkBounds(bx, bz)) t = null;
		else t = levelArray[bx][bz];
		block[1][2] = t;
		
		bx = tx+1; bz = tz-1;
		if (checkBounds(bx, bz)) t = null;
		else t = levelArray[bx][bz];
		block[2][2] = t;
		
		return block;
	}
	
	// ----- Check mappings ----- //
	
	public boolean checkBounds(int x, int z)
	{
		if (x < 0 || x >= width || z < 0 || z >= height) return true;
		return false;
	}
	
	public Tile getTile(float x, float z)
	{
		tx = (int)((x/10f)+0.5f);
		tz = (int)((z/10f)+0.5f);
		
		if (checkBounds(tx, tz)) return null;
		
		return levelArray[tx][tz];
	}
	
	public boolean checkSolid(Tile tile)
	{
		for (Character c : solids) if (tile.character == c) return true;
		return false;
	}
	
	public boolean checkOpaque(Tile tile)
	{
		for (Character c : solids) if (tile.character == c) return true;
		return false;
	}
}

