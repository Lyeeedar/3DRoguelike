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
	
	public static final int NOISE_PERSISTANCE = 3;
	public static final int NOISE_OCTAVES = 5;
	
	public static final int ROOM_PLACE_ATTEMPTS = 10;
	public static final int STAIR_MIN = 3;
	public static final int STAIR_VAR = 3;
	public static final int MAIN_MIN = 10;
	public static final int MAIN_VAR = 5;
	public static final int SPECIAL_MIN = 6;
	public static final int SPECIAL_VAR = 6;
	public static final int OTHER_MIN = 5;
	public static final int OTHER_VAR = 3;

	final ArrayList<DungeonRoom> rooms = new ArrayList<DungeonRoom>();
	final AbstractTile[][] tiles;
	final Random ran = new Random();
	
	final int width;
	final int height;
	
	public SerkGenerator(AbstractTile[][] tiles)
	{
		this.tiles = tiles;
		this.width = tiles.length;
		this.height = tiles[0].length;
	}
	
	@Override
	public ArrayList<DungeonRoom> generate() {
		
		setInfluence();
		
		placeRoom(RoomType.START);

		placeRoom(RoomType.END);
		
		return rooms;
	}
	
	public void connectRooms()
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
	}
	
	public void calculatePaths(ArrayList<Pnt[]> paths, Triangle triangle)
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
	
    public void addPath(Pnt p1, Pnt p2, ArrayList<Pnt[]> paths)
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
    
    public boolean checkIgnored(Pnt p1, Pnt p2)
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
    
    public boolean checkAdded(Pnt p1, Pnt p2)
    {
    	for (Pnt[] p : addedPnts)
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

	public void setInfluence()
	{
		PerlinNoise noise = new PerlinNoise();
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				tiles[x][y].influence += noise.PerlinNoise_2D(x, y, NOISE_PERSISTANCE, NOISE_OCTAVES);
			}
		}
	}
	
	public boolean placeRoom(RoomType roomType)
	{
		int width = 0;
		int height = 0;
		int px = 0;
		int py = 0;
		
		if (roomType == RoomType.START)
		{
			width = STAIR_MIN+ran.nextInt(STAIR_VAR);
			height = STAIR_MIN+ran.nextInt(STAIR_VAR);
		}
		else if (roomType == RoomType.END)
		{
			width = STAIR_MIN+ran.nextInt(STAIR_VAR);
			height = STAIR_MIN+ran.nextInt(STAIR_VAR);
		}
		else if (roomType == RoomType.MAIN)
		{
			width = MAIN_MIN+ran.nextInt(MAIN_VAR);
			height = MAIN_MIN+ran.nextInt(MAIN_VAR);
		}
		else if (roomType == RoomType.SPECIAL)
		{
			width = SPECIAL_MIN+ran.nextInt(SPECIAL_VAR);
			height = SPECIAL_MIN+ran.nextInt(SPECIAL_VAR);
		}
		else if (roomType == RoomType.OTHER)
		{
			width = OTHER_MIN+ran.nextInt(OTHER_VAR);
			height = OTHER_MIN+ran.nextInt(OTHER_VAR);
		}
		
		for (int i = 0; i < ROOM_PLACE_ATTEMPTS; i++)
		{
			 px = ran.nextInt(width);
			 py = ran.nextInt(height);
			
			 if (checkRoom(px, py, width, height))
			 {
					addRoom(px, py, width, height, roomType);
					return true;
			 }
		}
		return false;
	}
	
	public void addRoom(int px, int py, int width, int height, RoomType roomType)
	{
		for (int x = px; x < px+width; x++)
		{
			for (int y = py; y < py+height; y++)
			{
				tiles[x][y].room = true;
				tiles[x][y].influence = 0;
			}
		}
		
		DungeonRoom room = new DungeonRoom(px, py, width, height, roomType);
		rooms.add(room);
	}
	
	public boolean checkRoom(int px, int py, int width, int height)
	{
		if (px+width > width)
		{
			return false;
		}
		
		if (py+height > height)
		{
			return false;
		}
		
		for (int x = px; x < px+width; x++)
		{
			for (int y = py; y < py+height; y++)
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
