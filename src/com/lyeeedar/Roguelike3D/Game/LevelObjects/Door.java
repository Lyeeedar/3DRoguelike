package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.lyeeedar.Roguelike3D.Game.GameData;
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
			lz = 10;
			
			hingez = 5;
			hingex = 0.1f;
		}
		else if (level.checkSolid((int)(x/10f)-1, (int)(z/10f)) && level.checkSolid((int)(x/10f)+1, (int)(z/10f)))
		{
			lx = 10;
			lz = 1;
			
			hingez = 0.1f;
			hingex = 5;
		}
		else return null;
		
		Color colour = Color.WHITE;
		Door door = new Door(Shapes.genCuboid(lx, ly, lz), colour, "tex+", x, y+(ly/2), z, ao, hingex, hingez);
		door.shortDesc = ao.shortDesc;
		door.longDesc = ao.longDesc;
		
		door.solid = true;
		//door.vo.attributes.radius /= 2;
		
		return door;
	}

	@Override
	public void activate() {
		
		System.out.println("door activate");
		
		if (solid && !moving)
		{

			moving = true;
		}
		else if (!moving)
		{
			if (GameData.level.checkEntities(position, vo.attributes.radius, UID) != null) return;
			
			moving = true;
		}
	}

	@Override
	public String getActivatePrompt() {
		if (solid && !moving)
		{
			return "[E] Open Door";
		}
		else if (!moving)
		{
			return "[E] Close Door";
		}
		else return "";
	}
	
	private boolean moving = false;
	private boolean open = false;
	private float angle = 0;

	@Override
	public void update(float delta) {
		
		if (moving)
		{
			if (!open)
			{
				angle += delta*100;
				
				if (angle > 90)
				{
					angle = 90;
					solid = false;
					open = true;
					moving = false;
				}
				
				Matrix4 hinge = new Matrix4();
				hinge.translate(-hingex, 0, -hingez);
				hinge.rotate(0, 1, 0, angle);
				hinge.translate(hingex, 0, hingez);
				
				vo.attributes.getRotation().set(hinge);
			}
			else
			{
				solid = true;
				angle -= delta*100;
				
				if (angle < 0)
				{
					open = false;
					angle = 0;
					moving = false;
				}
				
				Matrix4 hinge = new Matrix4();
				hinge.translate(-hingex, 0, -hingez);
				hinge.rotate(0, 1, 0, angle);
				hinge.translate(hingex, 0, hingez);
				
				vo.attributes.getRotation().set(hinge);
			}
		}
		
	}

	@Override
	public void draw(Camera cam) {
	}

}
