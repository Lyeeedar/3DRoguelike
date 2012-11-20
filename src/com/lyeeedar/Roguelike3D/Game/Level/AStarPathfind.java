package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;
import java.util.Random;

import com.lyeeedar.Roguelike3D.Game.Level.PathFind.Node;

public class AStarPathfind
{
	Random ran = new Random();
	AbstractTile[][] grid;
	int startx;
	int starty;
	int endx;
	int endy;
	int currentx;
	int currenty;
	Node[][] nodes;
	
	ArrayList<Node> openList = new ArrayList<Node>();
	ArrayList<Node> closedList = new ArrayList<Node>();

	public AStarPathfind(AbstractTile[][] grid, int startx, int starty, int endx, int endy)
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