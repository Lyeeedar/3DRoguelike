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
public class XMLData
{
	Node root;

	Node currentNode;

	public void setToRoot()
	{
		currentNode = root;
	}

	public void moveToNode(String name)
	{
		if (!currentNode.branch) 
		{
			System.err.println("Node "+currentNode.name+" is not a Branch!");
			return;
		}

		currentNode = ( (Branch) currentNode ).nodes.get(name);
	}

	public String getContents()
	{
		if (currentNode.branch)
		{
			System.err.println("Node "+currentNode.name+" is not a Leaf!");
			return;
		}

		return ( (Leaf) currentNode ).contents;
	}

}

class Node
{
	String name;
	boolean branch;
}

class Branch extends Node
{
	HashMap<String, Node> nodes;

	public Branch()
	{
		branch = true;
	}
}

class Leaf extends Node
{
	String contents;

	public Leaf()
	{
		branch = false;
	}
}
