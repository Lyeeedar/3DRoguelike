package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class Door extends LevelObject {
	
	float hingex = 0;
	float hingez = 0;

	public Door(Mesh mesh, Color colour, String texture, float x, float y, float z, AbstractObject ao, float hingex, float hingez) {
		super(mesh, colour, texture, x, y, z, ao);
		this.hingex = hingex;
		this.hingez = hingez;
	}

	public Door(String model, Color colour, String texture, float x, float y, float z, AbstractObject ao, float hingex, float hingez) {
		super(model, colour, texture, x, y, z, ao);
		this.hingex = hingex;
		this.hingez = hingez;
	}

	public static Door create(AbstractObject ao, Level level, float x, float y, float z) {

		float lx = 0;
		float lz = 0;
		float ly = 10;
		
		float hingex = 0;
		float hingez = 0;
		
		if (level.checkSolid((int)(x/10f), (int)(z/10f)-1) && level.checkSolid((int)(x/10f), (int)(z/10f)+1))
		{
			lx = 1;
			lz = 5;
			
			hingex = 5;
			hingez = 0;
		}
		else if (level.checkSolid((int)(x/10f)-1, (int)(z/10f)) && level.checkSolid((int)(x/10f)+1, (int)(z/10f)))
		{
			lx = 5;
			lz = 1;
			
			hingex = 0;
			hingez = 5;
		}
		else return null;
		
		Color colour = Color.WHITE;
		Door door = new Door(Shapes.genCuboid(lx, ly, lz), colour, "tex+", x, y, z, ao, hingex, hingez);
		
		return door;
	}

}
