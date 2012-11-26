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
	
	public void createLevelGraphics(Tile[][] levelArray, HashMap<Character, Color> colours, BiomeReader biome)
	{
		int width = levelArray.length;
		int height = levelArray[0].length;
		
		VisibleObject[][] tempVOs = new VisibleObject[width][height];
		VisibleObject[][] tempRoofs = new VisibleObject[width][height];
		
		for (int x = 0; x < width; x++)
		{
			for (int z = 0; z < height; z++)
			{
				Tile t = levelArray[x][z];
				if (t.character == ' ') continue;

				VisibleObject vo = new VisibleObject(Shapes.genCuboid(5, t.height, 5), GL20.GL_TRIANGLES, colours.get(t.character), getTexture(t.character, biome));
				vo.attributes.getTransform().setToTranslation(x*10, t.height-5, z*10);
				tempVOs[x][z] = vo;
				
				if (t.height < t.roof)
				{
					VisibleObject voRf = new VisibleObject(Shapes.genCuboid(5, 1, 5), GL20.GL_TRIANGLES, colours.get('#'), getTexture('#', biome));
					voRf.attributes.getTransform().setToTranslation(x*10, t.roof, z*10);
					tempRoofs[x][z] = voRf;
				}
			}
		}
		
		int wBlocks = width/CHUNK_WIDTH;
		int hBlocks = height/CHUNK_HEIGHT;
		
		for (int x = 0; x < wBlocks; x++)
		{
			for (int y = 0; y < hBlocks; y++)
			{
				Chunk chunk = new Chunk();
				
				int startx = x*CHUNK_WIDTH;
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
		}
		
		for (int x = 0; x < width; x++)
		{
			for (int z = 0; z < height; z++)
			{
				if (tempVOs[x][z] != null) tempVOs[x][z].dispose();
				if (tempRoofs[x][z] != null) tempRoofs[x][z].dispose();
			}
		}
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