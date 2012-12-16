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
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModel;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModelAttributes;
import com.lyeeedar.Roguelike3D.Graphics.Models.TempMesh;
import com.lyeeedar.Roguelike3D.Graphics.Models.TempVO;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class LevelGraphics {
	
	public static final int CHUNK_WIDTH = 10;
	public static final int CHUNK_HEIGHT = 10;
	
	public ArrayList<VisibleObject> graphics = new ArrayList<VisibleObject>();
	
	Tile[][] levelArray;
	HashMap<Character, Color> colours;
	BiomeReader biome;
	
	public int width;
	public int height;
	
	TempVO[][] tempVOs;
	TempVO[][] tempRoofs;
	
	int wBlocks;
	int hBlocks;
	
	public Texture map;
	
	public LevelGraphics(Tile[][] levelArray, HashMap<Character, Color> colours, BiomeReader biome)
	{
		this.levelArray = levelArray;
		this.colours = colours;
		this.biome = biome;
		
		width = levelArray.length;
		height = levelArray[0].length;
		
		tempVOs = new TempVO[width][height];
		tempRoofs = new TempVO[width][height];
		
		wBlocks = width/CHUNK_WIDTH;
		hBlocks = height/CHUNK_HEIGHT;
		
		createMap(levelArray);
	}
	
	public static final int STEP = 10;
	public void createMap(Tile[][] levelArray)
	{
		BitmapFont font = new BitmapFont();
		SpriteBatch sB = new SpriteBatch();
		FrameBuffer fB = new FrameBuffer(Format.RGBA4444, width*STEP, height*STEP, false);
		
		System.out.println(width*STEP + "    " + height*STEP);
		
		fB.begin();
		sB.begin();
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < height; y++)
			{
				char c = levelArray[x][y].character;
				if (c == ' ') continue;
				font.setColor(colours.get(c));
				font.draw(sB, ""+c, x*STEP, y*STEP);

			}
		}
		sB.end();
		fB.end();
		
		map = fB.getColorBufferTexture();
		
		System.out.println(map.getWidth() + "    " + map.getHeight());
	}
	
	int tileX = 0;
	public boolean createTileRow()
	{
		if (tileX == width) return true;
		
		for (int z = 0; z < height; z++)
		{
			Tile t = levelArray[tileX][z];
			if (t.character == ' ') continue;

			TempVO vo = new TempVO(Shapes.genTempCuboid(10, t.height*2, 10), GL20.GL_TRIANGLES, colours.get(t.character), getTexture(t.character, biome), tileX*10, -1, z*10);
			tempVOs[tileX][z] = vo;
			
			if (t.height < t.roof)
			{
				TempVO voRf = new TempVO(Shapes.genTempCuboid(10, 2, 10), GL20.GL_TRIANGLES, colours.get('#'), getTexture('#', biome), tileX*10, t.roof, z*10);
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
		//if (disposeX == width)
		return true;
//		
//		for (int z = 0; z < height; z++)
//		{
//			//if (tempVOs[disposeX][z] != null) tempVOs[disposeX][z].dispose();
//			//if (tempRoofs[disposeX][z] != null) tempRoofs[disposeX][z].dispose();
//		}
//		
//		disposeX++;
//		
//		return false;
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
	HashMap<Character, ArrayList<TempVO>> block = new HashMap<Character, ArrayList<TempVO>>();
	
	public boolean isEmpty()
	{
		for (Map.Entry<Character, ArrayList<TempVO>> entry : block.entrySet())
		{
			if (entry.getValue().size() != 0) return false;
		}
		
		return true;
	}
	
	public void addVO(TempVO vo, char c)
	{
		if (vo == null) return;
		
		if (block.containsKey(c))
		{
			ArrayList<TempVO> vos = block.get(c);
			vos.add(vo);
		}
		else
		{
			ArrayList<TempVO> vos = new ArrayList<TempVO>();
			vos.add(vo);
			block.put(c, vos);
		}
	}
	
	final Vector3 tempVec = new Vector3();
	public ArrayList<VisibleObject> merge()
	{
		ArrayList<VisibleObject> vos = new ArrayList<VisibleObject>();
		
		for (Map.Entry<Character, ArrayList<TempVO>> entry : block.entrySet())
		{

			final TempMesh[] meshes = new TempMesh[entry.getValue().size()];
			
			final TempVO base = entry.getValue().get(0);
			final Vector3 baseVec = new Vector3(base.x, base.y, base.z);
			
			int i = 0;
			for (TempVO vo : entry.getValue())
			{
				TempMesh mesh = vo.mesh;
				
				tempVec.set(vo.x, vo.y, vo.z);
				tempVec.sub(baseVec);
				Shapes.translateCubeVertices(mesh.vertexNum, mesh.vertexSize, mesh.vertices, tempVec.x, tempVec.y, tempVec.z);
				
				meshes[i] = mesh;
				i++;
			}
			
			Mesh merged = Shapes.merge(meshes);
			
			VisibleObject vo = new VisibleObject(merged, base.primitive_type, base.colour, base.textureName, 1.0f);
			vo.attributes.getTransform().setToTranslation(baseVec);
			vos.add(vo);
		}
		
		return vos;
	}
}
