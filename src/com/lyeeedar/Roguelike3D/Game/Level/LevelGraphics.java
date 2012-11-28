/*******************************************************************************
 * Copyright (c) 2012 Philip Collin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Philip Collin - initial API and implementation
 ******************************************************************************/
package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModel;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModelAttributes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class LevelGraphics {
	
	public static final int CHUNK_WIDTH = 10;
	public static final int CHUNK_HEIGHT = 10;
	
	public ArrayList<VisibleObject> graphics = new ArrayList<VisibleObject>();
	
	Tile[][] levelArray;
	HashMap<Character, Color> colours;
	BiomeReader biome;
	
	int width;
	int height;
	
	VisibleObject[][] tempVOs;
	VisibleObject[][] tempRoofs;
	
	int wBlocks;
	int hBlocks;
	
	public LevelGraphics(Tile[][] levelArray, HashMap<Character, Color> colours, BiomeReader biome)
	{
		this.levelArray = levelArray;
		this.colours = colours;
		this.biome = biome;
		
		width = levelArray.length;
		height = levelArray[0].length;
		
		tempVOs = new VisibleObject[width][height];
		tempRoofs = new VisibleObject[width][height];
		
		wBlocks = width/CHUNK_WIDTH;
		hBlocks = height/CHUNK_HEIGHT;
	}
	
	int tileX = 0;
	public boolean createTileRow()
	{
		if (tileX == width) return true;
		
		for (int z = 0; z < height; z++)
		{
			Tile t = levelArray[tileX][z];
			if (t.character == ' ') continue;

			VisibleObject vo = new VisibleObject(Shapes.genCuboid(5, t.height, 5), GL20.GL_TRIANGLES, colours.get(t.character), getTexture(t.character, biome));
			vo.attributes.getTransform().setToTranslation(tileX*10, t.height-5, z*10);
			tempVOs[tileX][z] = vo;
			
			if (t.height < t.roof)
			{
				VisibleObject voRf = new VisibleObject(Shapes.genCuboid(5, 1, 5), GL20.GL_TRIANGLES, colours.get('#'), getTexture('#', biome));
				voRf.attributes.getTransform().setToTranslation(tileX*10, t.roof, z*10);
				tempRoofs[tileX][z] = voRf;
			}
		}
		
		tileX++;
		
		return false;
	}
	
	int chunkX = 0;
	public boolean createChunkRow()
	{
		if (chunkX == wBlocks) return true;
		
		for (int y = 0; y < hBlocks; y++)
		{
			Chunk chunk = new Chunk();
			
			int startx = chunkX*CHUNK_WIDTH;
			int starty = y*CHUNK_HEIGHT;
			
			for (int ix = 0; ix < CHUNK_WIDTH; ix++)
			{
				for (int iy = 0; iy < CHUNK_HEIGHT; iy++)
				{
					chunk.addVO(tempVOs[startx+ix][starty+iy], levelArray[startx+ix][starty+iy].character);
					chunk.addVO(tempRoofs[startx+ix][starty+iy], '#');
				}
			}
			
			if (!chunk.isEmpty()) graphics.addAll(chunk.merge());
		}
		
		chunkX++;
		
		return false;
	}
	
	int disposeX = 0;
	public boolean disposeTileRow()
	{
		if (disposeX == width) return true;
		
		for (int z = 0; z < height; z++)
		{
			if (tempVOs[disposeX][z] != null) tempVOs[disposeX][z].dispose();
			if (tempRoofs[disposeX][z] != null) tempRoofs[disposeX][z].dispose();
		}
		
		disposeX++;
		
		return false;
	}
	
	public String getTexture(char c, BiomeReader biome)
	{
		String text = null;
		
		if (c == '#')
		{
			text = biome.getWallTexture();
		}
		else if (c == '.')
		{
			text = biome.getFloorTexture();
		}
			
		return text;
	}

	public void dispose()
	{
		for (VisibleObject vo : graphics)
		{
			vo.dispose();
		}
	}
}

class Chunk
{
	HashMap<Character, ArrayList<VisibleObject>> block = new HashMap<Character, ArrayList<VisibleObject>>();
	
	public boolean isEmpty()
	{
		for (Map.Entry<Character, ArrayList<VisibleObject>> entry : block.entrySet())
		{
			if (entry.getValue().size() != 0) return false;
		}
		
		return true;
	}
	
	public void addVO(VisibleObject vo, char c)
	{
		if (vo == null) return;
		
		if (block.containsKey(c))
		{
			ArrayList<VisibleObject> vos = block.get(c);
			vos.add(vo);
		}
		else
		{
			ArrayList<VisibleObject> vos = new ArrayList<VisibleObject>();
			vos.add(vo);
			block.put(c, vos);
		}
	}
	
	final Vector3 tempVec = new Vector3();
	public ArrayList<VisibleObject> merge()
	{
		ArrayList<VisibleObject> vos = new ArrayList<VisibleObject>();
		
		for (Map.Entry<Character, ArrayList<VisibleObject>> entry : block.entrySet())
		{
			//System.out.println("Merging meshes for -"+entry.getKey() + " Number="+entry.getValue().size());
			
			final Mesh[] meshes = new Mesh[entry.getValue().size()];
			
			final Vector3 baseVec = entry.getValue().get(0).attributes.getSortCenter().cpy();
			
			int i = 0;
			for (VisibleObject vo : entry.getValue())
			{
				Mesh mesh = vo.model.subMeshes[0].mesh;
				
				tempVec.set(vo.attributes.getSortCenter());
				tempVec.sub(baseVec);
				Shapes.translateCubeMesh(mesh, tempVec.x, tempVec.y, tempVec.z);
				
				meshes[i] = mesh;
				i++;
			}
			
			Mesh merged = Shapes.merge(meshes);
			StillModel mergedModel = new StillModel(merged, entry.getValue().get(0).model.subMeshes[0].primitiveType);
			StillModelAttributes attributes = new StillModelAttributes(entry.getValue().get(0).attributes.material,
					entry.getValue().get(0).attributes.radius*entry.getValue().size());
			attributes.getTransform().setToTranslation(baseVec);
			VisibleObject vo = new VisibleObject(mergedModel, attributes);
			vos.add(vo);
		}
		
		return vos;
	}
}
