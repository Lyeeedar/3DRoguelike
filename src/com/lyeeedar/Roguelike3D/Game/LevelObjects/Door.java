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
package com.lyeeedar.Roguelike3D.Game.LevelObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.Level.AbstractObject;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer;

public class Door extends LevelObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3585422494836092341L;
	float hingex;
	float hingez;

	public Door(AbstractObject ao, Color colour, String texture, float x, float y, float z, float scale, int primitive_type, String... model) {
		super(ao, colour, texture, x, y, z, scale, primitive_type, model);
		solid = true;
	}

	public void orientate(Level level) {

		float lx = 0;
		float lz = 0;
		float ly = 10;
		
		float hingex = 0;
		float hingez = 0;
		
		float x = getPosition().x;
		float y = getPosition().y;
		float z = getPosition().z;
		
		Tile up = level.getTile((int)(x/10f), (int)(z/10f)+1);
		Tile down = level.getTile((int)(x/10f), (int)(z/10f)-1);
		
		Tile left = level.getTile((int)(x/10f)-1, (int)(z/10f));
		Tile right = level.getTile((int)(x/10f)+1, (int)(z/10f));

		if ((level.checkSolid(up) && level.checkSolid(down)) ||
				((up.getLo() instanceof Static) && (down.getLo() instanceof Static)))
		{
			lx = 1;
			lz = GameData.BLOCK_SIZE;
			
			hingez = 5;
			hingex = 0.1f;
		}
		else if ((level.checkSolid(left) && level.checkSolid(right)) ||
				((left.getLo() instanceof Static) && (right.getLo() instanceof Static)))
		{
			lx = GameData.BLOCK_SIZE;
			lz = 1;
			
			hingez = 0.1f;
			hingex = 5;
		}
		else
		{
			solid = false;
			visible = false;
			return;
		}
		
		if (vo.modelData[1].equals(""+lx) && vo.modelData[2].equals(""+ly) && vo.modelData[3].equals(""+lz))
		{
			
		}
		else
		{
			this.vo.dispose();
			VisibleObject vo2 = new VisibleObject(vo.primitive_type, vo.colour, vo.texture, vo.scale, "cube", ""+lx, ""+ly, ""+lz);
			vo2.create();
			vo2.bakeLights(GameData.lightManager, true);
			this.vo = vo2;
			
			this.hingex = hingex;
			this.hingez = hingez;
		}
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
			if (GameData.level.checkCollisionGameActors(position, vo.attributes.box) != null) return;
			
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
	
	private transient boolean moving = false;
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
	public void draw(Renderer renderer) {
	}

	@Override
	public void fixReferencesSuper() {
	}

	@Override
	protected void disposed() {
	}

}
