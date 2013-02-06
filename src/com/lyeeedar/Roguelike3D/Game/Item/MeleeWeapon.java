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
package com.lyeeedar.Roguelike3D.Game.Item;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Melee_Weapon_Style;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.MotionTrail;

public class MeleeWeapon extends Equipment_HAND {
	
	//final Random ran = new Random();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5519914081029109584L;
	private transient GameActor holder;
	public final String holderUID;
	
	public enum Melee_Weapon_Style {
		SWING,
		STAB
	}

	public static Melee_Weapon_Style convertWeaponStyle(String wep_style)
	{
		Melee_Weapon_Style style = null;

		if (wep_style.equalsIgnoreCase("swing")) style = Melee_Weapon_Style.SWING;
		else if (wep_style.equalsIgnoreCase("stab")) style = Melee_Weapon_Style.STAB;

		return style;
	}
	
	@Override
	public String toString()
	{
		String elements = "";
		for (Element ele : Element.values())
		{
			elements += ele+": " + ele_dam.get(ele)  + "\n";
		}
		return 	"-----------------" +
				"Melee Weapon" + "\n" +
				"Strength: " + strength  + "\n" +
				"Pierce: " + dam_dam.get(Damage_Type.PIERCE)  + "\n" +
				"Impact: " + dam_dam.get(Damage_Type.IMPACT)  + "\n" +
				"Touch: " + dam_dam.get(Damage_Type.TOUCH)  + "\n" +
				elements +
				"Attack CD: " + CD + "\n" +
				"Weight: " + WEIGHT + "\n" +
				"-----------------"
				;
	}
	
	final Attack_Style atk_style;
	private boolean swinging = false;
	boolean collided = false;
	final int side;

	public MeleeWeapon(GameActor holder, Melee_Weapon_Style style, int side, 
			int strength, HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam, float attack_speed, float weight) {
		super(weight);
		this.side = side;
		this.holder = holder;
		this.holderUID = holder.UID;
		
		if (style == Melee_Weapon_Style.SWING)
		{
			atk_style = new Circular_Attack(new Vector3(0, -2, 0), 5, 6, holder, 0.01f, new Vector3());
		}
		else atk_style = null;
		
		this.strength = strength;
		this.ele_dam = ele_dam;
		this.dam_dam = dam_dam;
		this.CD = attack_speed;
	}
	
	
	// ----- Damage stats ----- //
	public int strength;
	public HashMap<Element, Integer> ele_dam;
	public HashMap<Damage_Type, Integer> dam_dam;
	
	public transient float attack_cd = 0;
	
	public void damage(GameActor ga)
	{
		System.out.println("HIT on "+ga.UID);
		ga.damage(strength, ele_dam, dam_dam);
	}

	
	public void use()
	{
		beginSwing();
	}
	
	
	
	// ----- Begin Visual Stuff ----- //
	public void beginSwing()
	{	
		if (atk_style.style == Melee_Weapon_Style.SWING)
		{
			float height = holder.getPosition().y;
			
			float startH = 0.2f + height;// + (ran.nextFloat()*2);
			
			Vector3 base = new Vector3(holder.getRotation().x, 0, holder.getRotation().z);
			Vector3 up = new Vector3(0, 1, 0);
			Vector3 start = base.crs(up).mul(2);//.add(ran.nextFloat()-ran.nextFloat(), 0, ran.nextFloat()-ran.nextFloat());
			
			if (side == 1) start.mul(-1);
			
			start.add(holder.getRotation().x, startH, holder.getRotation().z);
			
			Vector3 rot = start.cpy();
			rot.mul(-2);
			rot.add(holder.getRotation().x, height-startH, holder.getRotation().z);
			
			
			beginSwingCircular(start, rot);
			
		}
		else System.err.println("Invalid wep type: "+atk_style.style);
	}
	
	public void beginSwingCircular(Vector3 startRot, Vector3 rotPerSecond)
	{
		if (atk_style.style != Melee_Weapon_Style.SWING)
		{
			System.err.println("Wrong method for weapon attack style!");
			return;
		}
		
		Circular_Attack c_a = (Circular_Attack) atk_style;
		c_a.reset(startRot, rotPerSecond);
		c_a.moveOffset.set(holder.getRotation().tmp().div(100f));

		swinging = true;
		collided = false;
		
	}
	
	final transient Vector3 tmpVec = new Vector3();
	final transient Vector3 tmpVec2 = new Vector3();

	public void update(float delta)
	{
		if (!swinging) return;
		if (atk_style.cd <= 0) swinging = false;
		
		
		atk_style.update(delta, collided);
		
		if (collided) return;
		
		GameActor ga = checkCollisionEntity(atk_style.positionA, atk_style.positionB);
		if (ga != null)
		{
			damage(ga);
			collided = true;
			return;
		}

		if (checkCollisionLevel(atk_style.positionA, atk_style.positionB))
		{
			collided = true;
			return;
		}
	}
	
	public void draw(Camera cam)
	{
		if (!swinging) return;
		
		
		atk_style.draw(cam);
	}
	
	private Ray ray = new Ray(new Vector3(), new Vector3());
	public boolean checkCollisionLevel(Vector3 start, Vector3 end)
	{
		Level level = GameData.level;
		
		Tile t = level.getTile((end.x/10f)+0.5f, (end.z/10f)+0.5f);
		
		if (end.y < t.height)
		{
			return true;
		}
		else if (end.y > t.roof)
		{
			return true;
		}
		
		ray.origin.set(start);
		ray.direction.set(end).sub(start).nor();
		
		LevelObject lo = level.getClosestLevelObject(ray, start.dst2(end), holder.UID, null);
		
		return (lo != null);
	}
	
	public GameActor checkCollisionEntity(Vector3 start, Vector3 end)
	{
		Level level = GameData.level;
		
		ray.origin.set(start);
		ray.direction.set(end).sub(start).nor();
		
		return level.getClosestActor(ray, start.dst2(end), start, end, holder.UID, null);
	}
	
	public void dispose()
	{
		atk_style.dispose();
	}

	@Override
	public void fixReferences() {
		holder = GameData.level.getActor(holderUID);
		atk_style.fixReferences();
	}
}

abstract class Attack_Style implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9139533290265415953L;

	public static final int TRAIL_STEPS = 60;
	
	Melee_Weapon_Style style;
	
	protected transient MotionTrail trail;
	
	final Vector3 positionA = new Vector3();
	final Vector3 positionB = new Vector3();
	
	final Vector3 tmpVec = new Vector3();
	
	final float step;
	
	public Attack_Style(float step)
	{
		this.step = step;
		trail = new MotionTrail(TRAIL_STEPS, new Colour(0.7f, 0.7f, 0.7f, 1.0f), "data/textures/gradient.png");
	}
	
	public void fixReferences()
	{
		trail = new MotionTrail(TRAIL_STEPS, new Colour(0.7f, 0.7f, 0.7f, 1.0f), "data/textures/gradient.png");
	}
	
	transient float cd = TRAIL_STEPS;
	transient float updateCD = 0;
	public void update(float delta, boolean collided)
	{
		if (collided) cd--;
		updateCD -= delta;
		if (updateCD > 0) return;
		
		while (updateCD < 0)
		{
			updateCD += step;
			
			updatePosition(delta, collided);
			
			trail.update(positionA, positionB);
		}
	}
	
	public void draw(Camera cam)
	{
		trail.draw(cam);
	}
	
	public void dispose()
	{
		trail.dispose();
	}
	
	protected abstract void updatePosition(float delta, boolean collided);
}

class Circular_Attack extends Attack_Style
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7051328157650641931L;
	Vector3 startRotation;
	Vector3 rotPerSecond;
	Vector3 currentRotation;
	
	final Vector3 offset;
	final float nearDist;
	final float farDist;
	transient GameActor center;
	final String centerUID;
	Vector3 moveOffset;
	
	public Circular_Attack(Vector3 offset, float nearDist, float farDist, GameActor center, float step, Vector3 moveOffset) 
	{
		super(step);
		
		this.moveOffset = moveOffset;
		this.style = Melee_Weapon_Style.SWING;
		
		this.offset = offset;
		this.nearDist = nearDist;
		this.farDist = farDist;
		this.center = center;
		this.centerUID = center.UID;
	}
	
	@Override
	public void fixReferences()
	{
		super.fixReferences();
		
		center = GameData.level.getActor(centerUID);
	}

	public void reset(Vector3 startRot, Vector3 rotPerSecond)
	{
		this.startRotation = startRot;
		this.rotPerSecond = rotPerSecond;
		this.currentRotation = startRotation.cpy();
		
		setPositions();
		
		trail.intialiseVerts(positionA, positionB);
		
		cd = TRAIL_STEPS;
	}

	protected void updatePosition(float delta, boolean collided)
	{
		
		trail.offsetAll(moveOffset);
		
		if (collided) return;
		
		tmpVec.set(rotPerSecond);
		tmpVec.mul(delta);
		
		currentRotation.add(tmpVec);

		setPositions();
	}
	
	private void setPositions()
	{
		tmpVec.set(currentRotation).nor();
		
		positionA.set(tmpVec).mul(nearDist).add(center.getPosition()).add(offset);
		
		positionB.set(tmpVec).mul(farDist).add(center.getPosition()).add(offset);
	}
}
