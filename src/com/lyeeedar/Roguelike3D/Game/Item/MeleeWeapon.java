package com.lyeeedar.Roguelike3D.Game.Item;

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
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Weapon_Style;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.MotionTrail;

public class MeleeWeapon extends Equipment_HAND {
	
	final Random ran = new Random();
	
	final GameActor holder;
	
	public enum Weapon_Style {
		SWING,
		STAB
	}

	public static Weapon_Style convertWeaponStyle(String wep_style)
	{
		Weapon_Style style = null;

		if (wep_style.equalsIgnoreCase("swing")) style = Weapon_Style.SWING;
		else if (wep_style.equalsIgnoreCase("stab")) style = Weapon_Style.STAB;

		return style;
	}
	
	final Attack_Style atk_style;
	boolean swinging = false;
	boolean collided = false;
	final int side;

	public MeleeWeapon(GameActor holder, Weapon_Style style, int side, 
			int strength, HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam, int attack_speed) {
		this.side = side;
		this.holder = holder;
		
		if (style == Weapon_Style.SWING)
		{
			atk_style = new Circular_Attack(new Vector3(0, -2, 0), 5, 6, holder);
		}
		else atk_style = null;
		
		this.strength = strength;
		this.ele_dam = ele_dam;
		this.dam_dam = dam_dam;
		this.attack_speed = attack_speed;
	}
	
	
	// ----- Damage stats ----- //
	public int strength;
	public HashMap<Element, Integer> ele_dam;
	public HashMap<Damage_Type, Integer> dam_dam;
	public int attack_speed;
	
	public int attack_cd = 0;
	
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
		if (attack_cd > 0) return;
		
		attack_cd = attack_speed;
		
		if (atk_style.style == Weapon_Style.SWING)
		{
			float height = holder.getPosition().y;
			
			float startH = 0.2f + height + (ran.nextFloat()*2);
			
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
	}
	
	public void beginSwingCircular(Vector3 startRot, Vector3 rotPerSecond)
	{
		if (atk_style.style != Weapon_Style.SWING)
		{
			System.err.println("Wrong method for weapon attack style!");
			return;
		}
		
		Circular_Attack c_a = (Circular_Attack) atk_style;
		c_a.reset(startRot, rotPerSecond);
		
		swinging = true;
		collided = false;
	}
	
	final Vector3 tmpVec = new Vector3();
	final Vector3 tmpVec2 = new Vector3();

	public void update(float delta)
	{
		attack_cd -= delta;
		
		if (!swinging) return;
		
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
	
	Ray ray = new Ray(new Vector3(), new Vector3());
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
		ray.direction.set(end);
		ray.direction.sub(start);
		
		LevelObject lo = level.getClosestLevelObject(ray, start.dst2(end), holder.UID, null);
		
		return (lo != null);
	}
	
	public GameActor checkCollisionEntity(Vector3 start, Vector3 end)
	{
		Level level = GameData.level;
		
		ray.origin.set(start);
		ray.direction.set(end);
		ray.direction.sub(start);
		
		return level.getClosestActor(ray, start.dst2(end), holder.UID, null);
	}
	
	public void dispose()
	{
		atk_style.dispose();
	}
}

abstract class Attack_Style
{
	public static final int TRAIL_STEPS = 245;
	
	Weapon_Style style;
	
	final MotionTrail trail;
	
	final Vector3 positionA = new Vector3();
	final Vector3 positionB = new Vector3();
	
	final Vector3 tmpVec = new Vector3();
	final Vector3 tmpVec2 = new Vector3();
	final Vector3 tmpVec3 = new Vector3();
	
	public Attack_Style()
	{
		trail = new MotionTrail(TRAIL_STEPS, Color.LIGHT_GRAY, "data/textures/gradient.png");
	}
	
	public void update(float delta, boolean collided)
	{
		if (!collided) updatePosition(delta);
		
		trail.update(positionA, positionB);
	}
	
	public void draw(Camera cam)
	{
		trail.draw(cam);
	}
	
	public void dispose()
	{
		trail.dispose();
	}
	
	protected abstract void updatePosition(float delta);
}

class Circular_Attack extends Attack_Style
{
	Vector3 startRotation;
	Vector3 rotPerSecond;
	Vector3 currentRotation;
	
	final Vector3 offset;
	final float nearDist;
	final float farDist;
	final GameObject center;
	
	public Circular_Attack(Vector3 offset, float nearDist, float farDist, GameObject center) 
	{
		super();
		
		this.style = Weapon_Style.SWING;
		
		this.offset = offset;
		this.nearDist = nearDist;
		this.farDist = farDist;
		this.center = center;
	}
	
	public void reset(Vector3 startRot, Vector3 rotPerSecond)
	{
		this.startRotation = startRot;
		this.rotPerSecond = rotPerSecond;
		this.currentRotation = startRotation.cpy();
		
		setPositions();
		
		trail.intialiseVerts(positionA, positionB);
	}

	protected void updatePosition(float delta)
	{				
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