package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;
import java.util.Random;

public class AbstractDungeon {

	Random ran = new Random();
	GridCell[][] grid;
	public DungeonRoom[][] rooms;
	
	int startx;
	int starty;
	int endx;
	int endy;
	
	public AbstractDungeon(int size, int cell_size, int startx, int starty, int endx, int endy)
	{
		this.startx = startx;
		this.starty = starty;
		this.endx = endx;
		this.endy = endy;
		
		GridGenerator gg = new GridGenerator(size, cell_size);
		grid = gg.getGrid();

		calculatePath(startx, starty, endx, endy);
		calculatePath(startx, starty, endx, endy);
		
//		while(connectRoom())
//		{
//			if (ran.nextInt(5) == 1)
//			{
//				invert();
//			}
//		}
		
		int division = size/cell_size;
		
		rooms = new DungeonRoom[division][division];
		
		for (int x = 0; x < grid.length; x++)
		{
			for (int y = 0; y < grid[0].length; y++)
			{
				GridCell gc = grid[x][y];
				rooms[x][y] = new DungeonRoom(x, y, gc.size, gc.size);
			}
		}
		
		for (int x = 0; x < rooms.length; x++)
		{
			for (int y = 0; y < rooms[0].length; y++)
			{
				GridCell gc = grid[x][y];
				rooms[x][y].addConnections(rooms, gc.up!=null, gc.down!=null, gc.left!=null, gc.right!=null);
			}
		}
	}
	
	public boolean connectRoom()
	{
		System.out.println("connecting");
		for (int x = 1; x < grid.length; x++)
		{
			for (int y = 1; y < grid[0].length; y++)
			{
				GridCell gc = grid[x][y];
				if (!gc.added)
				{
					calculatePath(startx, starty, x, y);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void invert()
	{
		for (int x = 0; x < grid.length; x++)
		{
			for (int y = 0; y < grid[0].length; y++)
			{
				GridCell gc = grid[x][y];
				if (gc.added)
				{
					gc.influence = 0;
				}
				else
				{
					gc.influence = 550;
				}
			}
		}
	}
	
	public void calculatePath(int sx, int sy, int ex, int ey)
	{
		PathFind path = new PathFind(grid, sx, sy, ex, ey);
		int[][] p = path.getPath();
		
		for (int i = 1; i < p.length; i++)
		{
			GridCell gc1 = grid[p[i-1][0]][p[i-1][1]];
			GridCell gc2 = grid[p[i][0]][p[i][1]];
			connectRooms(gc1, gc2);
			
			gc1.influence = 350;
			gc2.influence = 350;
			
			gc1.added = true;
			gc2.added = true;
		}
	}
	
	public void connectRooms(GridCell gc1, GridCell gc2)
	{
		if (gc1.x == gc2.x)
		{
			if (gc1.y < gc2.y)
			{
				gc1.down = gc2;
				gc2.up = gc1;
			}
			else
			{
				gc1.up = gc2;
				gc2.down = gc1;
			}
		}
		else
		{
			if (gc1.x < gc2.x)
			{
				gc1.right = gc2;
				gc2.left = gc1;
			}
			else
			{
				gc1.left = gc2;
				gc2.right = gc1;
			}
		
		}
	}
	
}

class PathFind
{
	Random ran = new Random();
	GridCell[][] grid;
	int startx;
	int starty;
	int endx;
	int endy;
	int currentx;
	int currenty;
	Node[][] nodes;
	
	ArrayList<Node> openList = new ArrayList<Node>();
	ArrayList<Node> closedList = new ArrayList<Node>();

	public PathFind(GridCell[][] grid, int startx, int starty, int endx, int endy)
	{
		this.grid = grid;
		this.startx = startx;
		this.starty = starty;
		this.currentx = startx;
		this.currenty = starty;
		this.endx = endx;
		this.endy = endy;
		
		nodes = new Node[grid.length][grid.length];
		
		nodes[startx][starty] = new Node(startx, starty, 0, 0, 0);
		openList.add(nodes[startx][starty]);
		
		while(currentx != endx || currenty != endy)
		{
			path();
		}
			
		fillArray();
	}
	
	public int[][] getPath()
	{
		int length = nodes[endx][endy].distance+1;
		int[][] path = new int[length][2];
		
		path[length-1][0] = endx;
		path[length-1][1] = endy;

		int cx = endx;
		int cy = endy;
		
		for (int i = length-1; i > 0; i--)
		{
			if (cx-1 > 0 && nodes[cx-1][cy] != null && nodes[cx-1][cy].distance <= i)
			{
				cx--;
			}
			else if (cx+1 < grid.length && nodes[cx+1][cy] != null && nodes[cx+1][cy].distance <= i)
			{
				cx++;
			}
			else if (cy-1 > 0 && nodes[cx][cy-1] != null && nodes[cx][cy-1].distance <= i)
			{
				cy--;
			}
			else if (cy+1 < grid.length && nodes[cx][cy+1] != null && nodes[cx][cy+1].distance <= i)
			{
				cy++;
			}
			
			path[i-1][0] = cx;
			path[i-1][1] = cy;
		}
		
		return path;
	}
	
	public void fillArray()
	{
		for (Node n : closedList)
		{
			nodes[n.x][n.y] = n;
		}
	}
	
	private void path()
	{
		Node current = findBestNode();
		currentx = current.x;
		currenty = current.y;

		if (currentx-2 > 0)
		{
			int tempx = currentx-1;
			int tempy = currenty;
			int heuristic = Math.abs(tempx-endx)+Math.abs(tempy-endy);
			Node tempn = new Node(tempx, tempy, heuristic, current.distance+1, grid[tempx][tempy].influence);
			addNodeToOpenList(tempn);
		}
		if (currentx+1 < grid.length)
		{
			int tempx = currentx+1;
			int tempy = currenty;
			int heuristic = Math.abs(tempx-endx)+Math.abs(tempy-endy);
			Node tempn = new Node(tempx, tempy, heuristic, current.distance+1, grid[tempx][tempy].influence);
			addNodeToOpenList(tempn);
		}
		if (currenty-2 > 0)
		{
			int tempx = currentx;
			int tempy = currenty-1;
			int heuristic = Math.abs(tempx-endx)+Math.abs(tempy-endy);
			Node tempn = new Node(tempx, tempy, heuristic, current.distance+1, grid[tempx][tempy].influence);
			addNodeToOpenList(tempn);
		}
		if (currenty+1 < grid.length)
		{
			int tempx = currentx;
			int tempy = currenty+1;
			int heuristic = Math.abs(tempx-endx)+Math.abs(tempy-endy);
			Node tempn = new Node(tempx, tempy, heuristic, current.distance+1, grid[tempx][tempy].influence);
			addNodeToOpenList(tempn);
		}
	}
	
	public boolean isNodeInClosedList(Node n)
	{
		for (Node nn : closedList)
		{
			if (nn.x == n.x && nn.y == n.y) return true;
		}
		return false;
	}
	
	public void addNodeToOpenList(Node n)
	{
		if (isNodeInClosedList(n)) return;
		
		if (openList.size() == 0)
		{
			openList.add(n);
			return;
		}
		Node less = openList.get(0);
		
		for (int i = 0; i < openList.size(); i++)
		{
			if (n.cost < less.cost)
			{
				openList.add(i, n);
				return;
			}
		}
		openList.add(n);
	}
	
	public void removeNodeFromOpenList(Node n)
	{
		int i = 0;
		for (Node nn : openList)
		{
			if (nn.x == n.x && nn.y == n.y)
			{
				openList.remove(i);
				return;
			}
			i++;
		}
	}
	
	public Node findBestNode()
	{
		if (openList.size() == 0)
		{
			System.err.println("No nodes in list!");
			for (Node n : closedList)
			{	
				System.err.printf(" %d %d ", n.x, n.y);
			}
			System.err.printf(" \n ");
		}
		Node best = openList.get(0);
		openList.remove(0);
		closedList.add(best);
		return best;
	}
	
	class Node
	{
		int x;
		int y;
		int cost;
		int heuristic;
		int distance;
		int influence;

		public Node(int x, int y, int heuristic, int distance, int influence)
		{
			this.influence = influence;
			this.x = x;
			this.y = y;
			this.heuristic = heuristic;
			this.distance = distance;
			this.cost = (heuristic*2) + distance + influence;
		}
	}

}

class GridCell
{
	int x;
	int y;
	int size;
	GridCell up = null;
	GridCell down = null;
	GridCell left = null;
	GridCell right = null;
	
	int influence = 0;
	
	boolean added = false;
	
	public GridCell(int x, int y, int size)
	{
		this.x = x;
		this.y = y;
		this.size = size;
	}
}


class GridGenerator {
	Random ran = new Random();
	
	int size;
	int cell_size;
	
	GridCell[][] grid;
	
	int division;
	
	public GridGenerator(int size, int cell_size)
	{
		this.size = size;
		this.cell_size = cell_size;
		
		if (size % cell_size != 0)
		{
			System.err.println("Grid not a multiple of Cell Size! Grid="+size+"  Cell Size="+cell_size);
			return;
		}
		
		division = size / cell_size;
		grid = new GridCell[division][division];
		for (int x = 0; x < division; x++)
		{
			for (int y = 0; y < division; y++)
			{
				grid[x][y] = new GridCell(x*cell_size, y*cell_size, cell_size);
			}
		}
		
		for (int i = 0; i < ran.nextInt(division*division); i++)
		{
			int x = ran.nextInt(division-1);
			int y = ran.nextInt(division-1);
			
			grid[x][y].influence += ran.nextInt(50);
		}
	}
	
	public GridCell[][] getGrid()
	{
		return grid;
	}
}