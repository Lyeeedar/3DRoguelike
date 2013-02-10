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
	
	private static final long serialVersionUID = -5519914081029109584L;
	
	public enum Melee_Weapon_Style {
		SWING,
		STAB
	}

	public static Melee_Weapon_Style convertWeaponStyle(String wep_style)
	{
		for (Melee_Weapon_Style mws : Melee_Weapon_Style.values())
		{
			if (wep_style.equalsIgnoreCase(""+mws)) return mws;
		}

		return null;
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
				"Attack CD: " + attack_speed + "\n" +
				"Weight: " + WEIGHT + "\n" +
				"-----------------"
				;
	}
	
	final Attack_Style atk_style;
	private boolean swinging = false;
	boolean collided = false;

	public MeleeWeapon(Melee_Weapon_Style style,  
			int strength, HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam,
			float attack_speed, float weight, boolean two_handed, float range) {
		super(weight, strength, ele_dam, dam_dam, attack_speed, two_handed, range);
		
		if (style == Melee_Weapon_Style.SWING)
		{
			atk_style = new Circular_Attack(0.01f, range);
		}
		else atk_style = null;

	}

	@Override
	protected void used()
	{
		beginSwing();
	}
	
	// ----- Begin Visual Stuff ----- //
	public void beginSwing()
	{	
		if (atk_style.style == Melee_Weapon_Style.SWING)
		{
			float height = holder.getPosition().y;
			
			float startH = 0.2f + height;
			
			Vector3 base = new Vector3(holder.getRotation().x, 0, holder.getRotation().z);
			Vector3 up = new Vector3(0, 1, 0);
			Vector3 start = base.crs(up).mul(2);
			
			if (equippedSide == 1) start.mul(-1);
			
			start.add(holder.getRotation().x, startH, holder.getRotation().z);
			
			Vector3 rot = start.cpy();
			rot.mul(-2);
			rot.add(holder.getRotation().x, height-startH, holder.getRotation().z);
			
			
			beginSwingCircular(start, rot, Vector3.tmp3.set(0, -2, 0));
			
		}
		else System.err.println("Invalid wep type: "+atk_style.style);
	}
	
	public void beginSwingCircular(Vector3 startRot, Vector3 rotPerSecond, Vector3 offset)
	{
		if (atk_style.style != Melee_Weapon_Style.SWING)
		{
			System.err.println("Wrong method for weapon attack style!");
			return;
		}
		
		Circular_Attack c_a = (Circular_Attack) atk_style;
		c_a.reset(startRot, rotPerSecond, offset);

		swinging = true;
		collided = false;
	}

	@Override
	protected void updated(float delta)
	{
		if (!swinging) return;
		if (atk_style.cd <= 0) swinging = false;
		
		atk_style.update(delta, collided);
		
		if (collided) return;
		
		GameActor ga = GameData.level.checkCollisionEntity(atk_style.positionA, atk_style.positionB, holderUID);
		if (ga != null)
		{
			damage(ga);
			collided = true;
			return;
		}

		if (GameData.level.checkCollisionLevel(atk_style.positionA, atk_style.positionB, holderUID))
		{
			collided = true;
			return;
		}
	}
	
	protected void drawed(Camera cam)
	{
		if (!swinging) return;
		
		atk_style.draw(cam);
	}
	
	public void dispose()
	{
		atk_style.dispose();
	}

	@Override
	protected void fixReferencesSuper() {

		atk_style.fixReferences();
	}

	@Override
	protected void unequipped() {
		holder = null;
		holderUID = null;
		atk_style.unequip();
	}

	@Override
	protected void equipped(GameActor actor, int side) {
		holder = actor;
		holderUID = actor.UID;
		
		atk_style.equip(actor);
	}
}

abstract class Attack_Style implements Serializable
{
	private static final long serialVersionUID = 9139533290265415953L;

	public static final int TRAIL_STEPS = 60;
	
	Melee_Weapon_Style style;
	
	protected transient MotionTrail trail;
	protected transient GameActor center;
	protected String centerUID;
	
	final Vector3 positionA = new Vector3();
	final Vector3 positionB = new Vector3();
	
	final Vector3 tmpVec = new Vector3();
	
	final float step;
	final float range;
	
	public Attack_Style(float step, float range)
	{
		this.range = range;
		this.step = step;
		
		fixReferences();
	}
	
	public void fixReferences()
	{
		trail = new MotionTrail(TRAIL_STEPS, new Colour(0.7f, 0.7f, 0.7f, 1.0f), "data/textures/gradient.png");
		
		if (centerUID != null) center = GameData.level.getActor(centerUID);
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
	
	public void equip(GameActor actor)
	{
		center = actor;
		centerUID = actor.UID;
	}
	
	public void unequip()
	{
		center = null;
		centerUID = null;
	}
	
	protected abstract void updatePosition(float delta, boolean collided);
}

class Circular_Attack extends Attack_Style
{
	private static final long serialVersionUID = -7051328157650641931L;
	Vector3 startRotation = new Vector3();
	Vector3 rotPerSecond = new Vector3();
	Vector3 offset = new Vector3();
	
	Vector3 currentRotation = new Vector3();
	
	public Circular_Attack(float step, float range) 
	{
		super(step, range);
		
		this.style = Melee_Weapon_Style.SWING;
	}

	public void reset(Vector3 startRot, Vector3 rotPerSecond, Vector3 offset)
	{
		this.startRotation.set(startRot);
		this.rotPerSecond.set(rotPerSecond);
		this.offset.set(offset);
		
		this.currentRotation.set(startRotation);
		
		setPositions();
		
		trail.intialiseVerts(positionA, positionB);
		
		cd = TRAIL_STEPS;
	}

	protected void updatePosition(float delta, boolean collided)
	{
		if (collided) return;
		
		tmpVec.set(rotPerSecond);
		tmpVec.mul(delta);
		
		currentRotation.add(tmpVec);

		setPositions();
	}
	
	private void setPositions()
	{
		tmpVec.set(currentRotation).nor();
		
		positionA.set(tmpVec).mul(center.getRadius()).add(center.getPosition()).add(offset);
		
		positionB.set(tmpVec).mul(center.getRadius()+range).add(center.getPosition()).add(offset);
	}
}
