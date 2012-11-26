package com.lyeeedar.Roguelike3D.Game.Level;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import com.lyeeedar.Roguelike3D.Game.Level.AbstractTile.TileType;
import com.lyeeedar.Roguelike3D.Game.Level.DungeonRoom.RoomType;
import com.lyeeedar.Roguelike3D.Game.Level.DelaunayTriangulation.*;

/**
 * Dungeon generation inspired by: http://forums.tigsource.com/index.php?topic=5174.msg799973#msg799973
 * @author Philip
 *
 */
public class SerkGenerator implements AbstractGenerator{
	
	protected static final int NOISE_PERSISTANCE = 3;
	protected static final int NOISE_OCTAVES = 5;
	
	protected static final int ROOM_PLACE_ATTEMPTS = 150;
	protected final int ROOM_PLACE_PADDING;
	protected final int START_MIN;
	protected final int START_VAR;
	protected final int END_MIN;
	protected final int END_VAR;
	protected final int MAIN_MIN;
	protected final int MAIN_VAR;
	protected final int SPECIAL_MIN;
	protected final int SPECIAL_VAR;
	protected final int OTHER_MIN;
	protected final int OTHER_VAR;

	final ArrayList<DungeonRoom> rooms = new ArrayList<DungeonRoom>();
	final AbstractTile[][] tiles;
	final Random ran = new Random();
	
	final int width;
	final int height;
	
	public SerkGenerator(AbstractTile[][] tiles, BiomeReader biome)
	{
		this.tiles = tiles;
		this.width = tiles.length;
		this.height = tiles[0].length;
		
		ROOM_PLACE_PADDING = biome.getRoomPadding();
		START_MIN = biome.getRoomSizeMin(RoomType.START);
		START_VAR = biome.getRoomSizeVar(RoomType.START);
		END_MIN = biome.getRoomSizeMin(RoomType.END);
		END_VAR = biome.getRoomSizeVar(RoomType.END);
		MAIN_MIN = biome.getRoomSizeMin(RoomType.MAIN);
		MAIN_VAR = biome.getRoomSizeVar(RoomType.MAIN);
		SPECIAL_MIN = biome.getRoomSizeMin(RoomType.SPECIAL);
		SPECIAL_VAR = biome.getRoomSizeVar(RoomType.SPECIAL);
		OTHER_MIN = biome.getRoomSizeMin(RoomType.OTHER);
		OTHER_VAR = biome.getRoomSizeVar(RoomType.OTHER);
	}
	
	@Override
	public ArrayList<DungeonRoom> generate(BiomeReader biome) {
		
		setInfluence();
		
		int reps = biome.getRoomNumberMin(RoomType.START)+ran.nextInt(biome.getRoomNumberVar(RoomType.START));
		for (int i = 0 ; i < reps; i++)
		{
			placeRoom(RoomType.START);
		}
		
		reps = biome.getRoomNumberMin(RoomType.END)+ran.nextInt(biome.getRoomNumberVar(RoomType.END));
		for (int i = 0 ; i < reps; i++)
		{
			placeRoom(RoomType.END);
		}
		
		reps = biome.getRoomNumberMin(RoomType.MAIN)+ran.nextInt(biome.getRoomNumberVar(RoomType.MAIN));
		for (int i = 0 ; i < reps; i++)
		{
			placeRoom(RoomType.MAIN);
		}
		
		reps = biome.getRoomNumberMin(RoomType.SPECIAL)+ran.nextInt(biome.getRoomNumberVar(RoomType.SPECIAL));
		for (int i = 0 ; i < reps; i++)
		{
			placeRoom(RoomType.SPECIAL);
		}
		
		reps = biome.getRoomNumberMin(RoomType.OTHER)+ran.nextInt(biome.getRoomNumberVar(RoomType.OTHER));
		for (int i = 0 ; i < reps; i++)
		{
			placeRoom(RoomType.OTHER);
		}

		connectRooms();
		
		return rooms;
	}
	
	protected void connectRooms()
	{
		ArrayList<Pnt> roomPnts = new ArrayList<Pnt>();
		
		for (DungeonRoom dr : rooms)
		{
			Pnt p = new Pnt(dr.x+(dr.width/2), dr.y+(dr.height/2));
			roomPnts.add(p);
		}

		Triangle initialTriangle = new Triangle(
				new Pnt(-10000, -10000),
				new Pnt(10000, -10000),
				new Pnt(0, 10000));
		Triangulation dt = new Triangulation(initialTriangle);
		
		for (Pnt p : roomPnts)
		{
			dt.delaunayPlace(p);
		}
		
		ArrayList<Pnt[]> paths = new ArrayList<Pnt[]>();
		
		for (Triangle tri : dt)
		{
			calculatePaths(paths, tri);
		}

		for (Pnt[] p : paths)
		{
			AStarPathfind pathFind = new AStarPathfind(tiles, (int)p[0].coord(0), (int)p[0].coord(1), (int)p[1].coord(0), (int)p[1].coord(1));
			carveCorridor(pathFind.getPath());
		}
	}
	
	protected void carveCorridor(int[][] path)
	{
		boolean room = tiles[path[0][0]][path[0][1]].room;
		if (!room) System.err.println("Error! Room Linking path did not start in a Room!");
		
		AbstractTile t = null;
		AbstractTile lt = null;
		for (int[] pos : path)
		{
			lt = t;
			t = tiles[pos[0]][pos[1]];
			if (!room && t.room)
			{
				lt.tileType = TileType.DOOR;
				t.tileType = TileType.FLOOR;
			}
			else if (room && !t.room)
			{
				t.tileType =TileType.DOOR;
			}
			else
			{
				t.tileType = TileType.FLOOR;
			}
			t.influence = 0;
			room = t.room;
		}
	}
	
	protected void calculatePaths(ArrayList<Pnt[]> paths, Triangle triangle)
	{
		Pnt[] vertices = triangle.toArray(new Pnt[0]);
		
		int ignore = 0;
        double dist = 0;
        
        dist = Math.pow(2, vertices[0].coord(0)-vertices[1].coord(0))+Math.pow(2, vertices[0].coord(1)-vertices[1].coord(1));
        
        double temp = Math.pow(2, vertices[0].coord(0)-vertices[2].coord(0))+Math.pow(2, vertices[0].coord(1)-vertices[2].coord(1));
        if (dist < temp)
        {
        	dist = temp;
        	ignore = 1;		
        }
        
        temp = Math.pow(2, vertices[1].coord(0)-vertices[2].coord(0))+Math.pow(2, vertices[1].coord(1)-vertices[2].coord(1));
        if (dist < temp)
        {
        	dist = temp;
        	ignore = 2;		
        }
        
        if (ignore != 0 && checkIgnored(vertices[0], vertices[1]) && !checkAdded(vertices[0], vertices[1]))
        {
        	addPath(vertices[0], vertices[1], paths);
        }
        else
        {
        	ignorePnts.add(new Pnt[]{vertices[0], vertices[1]});
        }
        if (ignore != 1 && checkIgnored(vertices[0], vertices[2]) && !checkAdded(vertices[0], vertices[2]))
        {
        	addPath(vertices[0], vertices[2], paths);
        }
        else
        {
        	ignorePnts.add(new Pnt[]{vertices[0], vertices[2]});
        }
        if (ignore != 2 && checkIgnored(vertices[1], vertices[2]) && !checkAdded(vertices[1], vertices[2]))
        {
        	addPath(vertices[1], vertices[2], paths);
        }
        else
        {
        	ignorePnts.add(new Pnt[]{vertices[1], vertices[2]});
        }
	}
	
    protected void addPath(Pnt p1, Pnt p2, ArrayList<Pnt[]> paths)
    {
    	if (p1.coord(0) < 0 || p2.coord(0) < 0)
    	{
    		ignorePnts.add(new Pnt[]{p1, p2});
    	}
    	else if (p1.coord(1) < 0 || p2.coord(1) < 0)
    	{
    		ignorePnts.add(new Pnt[]{p1, p2});
    	}
    	else if (p1.coord(0) > 1000 || p2.coord(0) > 1000)
    	{
    		ignorePnts.add(new Pnt[]{p1, p2});
    	}
    	else if (p1.coord(1) > 1000 || p2.coord(1) > 1000)
    	{
    		ignorePnts.add(new Pnt[]{p1, p2});
    	}
    	else
    	{
        	addedPnts.add(new Pnt[]{p1, p2});
        	paths.add(new Pnt[]{p1, p2});
    	}
    }
	
	ArrayList<Pnt[]> ignorePnts = new ArrayList<Pnt[]>();
	ArrayList<Pnt[]> addedPnts = new ArrayList<Pnt[]>();
    
    protected boolean checkIgnored(Pnt p1, Pnt p2)
    {
    	for (Pnt[] p : ignorePnts)
    	{
    		if (p[0].equals(p1) && p[1].equals(p2))
    		{
    			return false;
    		}
    		else if (p[0].equals(p2) && p[1].equals(p1))
    		{
    			return false;
    		}
    	}
    	return true;
    }
    
    protected boolean checkAdded(Pnt p1, Pnt p2)
    {
    	for (Pnt[] p : addedPnts)
    	{
    		if (p[0].equals(p1) && p[1].equals(p2))
    		{
    			return true;
    		}
    		else if (p[0].equals(p2) && p[1].equals(p1))
    		{
    			return true;
    		}
    	}
    	return false;
    }

	protected void setInfluence()
	{
		PerlinNoise noise = new PerlinNoise();
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				tiles[x][y].influence += noise.PerlinNoise_2D(x, y, NOISE_PERSISTANCE, NOISE_OCTAVES)*10;
			}
		}
	}
	
	protected boolean placeRoom(RoomType roomType)
	{
		int rwidth = 0;
		int rheight = 0;
		int px = 0;
		int py = 0;
		
		if (roomType == RoomType.START)
		{
			rwidth = START_MIN+ran.nextInt(START_VAR);
			rheight = START_MIN+ran.nextInt(START_VAR);
		}
		else if (roomType == RoomType.END)
		{
			rwidth = END_MIN+ran.nextInt(END_VAR);
			rheight = END_MIN+ran.nextInt(END_VAR);
		}
		else if (roomType == RoomType.MAIN)
		{
			rwidth = MAIN_MIN+ran.nextInt(MAIN_VAR);
			rheight = MAIN_MIN+ran.nextInt(MAIN_VAR);
		}
		else if (roomType == RoomType.SPECIAL)
		{
			rwidth = SPECIAL_MIN+ran.nextInt(SPECIAL_VAR);
			rheight = SPECIAL_MIN+ran.nextInt(SPECIAL_VAR);
		}
		else if (roomType == RoomType.OTHER)
		{
			rwidth = OTHER_MIN+ran.nextInt(OTHER_VAR);
			rheight = OTHER_MIN+ran.nextInt(OTHER_VAR);
		}
		
		for (int i = 0; i < ROOM_PLACE_ATTEMPTS; i++)
		{
			 px = ROOM_PLACE_PADDING+ran.nextInt(width-rwidth-ROOM_PLACE_PADDING-ROOM_PLACE_PADDING);
			 py = ROOM_PLACE_PADDING+ran.nextInt(height-rheight-ROOM_PLACE_PADDING-ROOM_PLACE_PADDING);
			
			 if (checkRoom(px, py, rwidth, rheight))
			 {
					addRoom(px, py, rwidth, rheight, roomType);
					return true;
			 }
		}
		System.err.println("Failed to place room!");
		return false;
	}
	
	protected void addRoom(int px, int py, int width, int height, RoomType roomType)
	{
		for (int x = px; x < px+width; x++)
		{
			for (int y = py; y < py+height; y++)
			{
				tiles[x][y].room = true;
				tiles[x][y].influence = 0;
				tiles[x][y].tileType = TileType.FLOOR;
			}
		}
		
		DungeonRoom room = new DungeonRoom(px, py, width, height, roomType);
		rooms.add(room);
	}
	
	protected boolean checkRoom(int px, int py, int rwidth, int rheight)
	{
		if (px+rwidth+ROOM_PLACE_PADDING > width)
		{
			return false;
		}
		else if (py+rheight+ROOM_PLACE_PADDING > height)
		{
			return false;
		}
		else if (px-ROOM_PLACE_PADDING < 0)
		{
			return false;
		}
		else if (py-ROOM_PLACE_PADDING < 0)
		{
			return false;
		}
		
		for (int x = px-ROOM_PLACE_PADDING; x < px+rwidth+ROOM_PLACE_PADDING; x++)
		{
			for (int y = py-ROOM_PLACE_PADDING; y < py+rheight+ROOM_PLACE_PADDING; y++)
			{
				if (tiles[x][y].room)
				{
					return false;
				}
			}
		}
		return true;
	}
}
