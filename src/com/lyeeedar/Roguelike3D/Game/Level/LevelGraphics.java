package com.lyeeedar.Roguelike3D.Game.Level;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class LevelGraphics {
	
	private ArrayList<VisibleObject> levelGraphics = new ArrayList<VisibleObject>();
	
	public void createLevelGraphics(Tile[][] levelArray, Color roof)
	{
		HashMap<Float, Mesh> meshes = new HashMap<Float, Mesh>();
		
		for (int x = 0; x < levelArray.length; x++)
		{
			for (int z = 0; z < levelArray[0].length; z++)
			{
				Tile t = levelArray[x][z];
				if (t.character == ' ') continue;
				
				if (!meshes.containsKey(t.height))
				{
					meshes.put(t.height, Shapes.genCuboid(5, t.height, 5));
				}
				
				VisibleObject vo = new VisibleObject(meshes.get(t.height), GL20.GL_TRIANGLES, t.colour, "tex"+t.character);
				vo.attributes.getTransform().setToTranslation(x*10, t.height-5, z*10);
				levelGraphics.add(vo);
				
				if (t.height < t.roof)
				{
					if (!meshes.containsKey(1))
					{
						meshes.put(1f, Shapes.genCuboid(5, 1, 5));
					}
					
					VisibleObject voRf = new VisibleObject(meshes.get(1f), GL20.GL_TRIANGLES, roof, "tex#");
					voRf.attributes.getTransform().setToTranslation(x*10, t.roof, z*10);
					levelGraphics.add(voRf);
				}
			}
		}	
	}

}
