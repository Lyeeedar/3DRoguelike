package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.Random;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Weapon_Style;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.MotionTrail;

public class MeleeWeapon extends Equipment_HAND {
	
	final Random ran = new Random();
	
	final GameActor holder;
	
	public enum Weapon_Style {
		SWING,
		STAB
	}
	
	final Attack_Style atk_style;
	boolean swinging = false;
	boolean collided = false;
	final int side;

	public MeleeWeapon(GameActor holder, Weapon_Style style, int side) {
		this.side = side;
		this.holder = holder;
		
		if (style == Weapon_Style.SWING)
		{
			atk_style = new Circular_Attack(new Vector3(0, -4, 0), 5, 10, holder);
		}
		else atk_style = null;
	}
	
	public void damage(GameActor ga)
	{
		System.out.println("HIT on "+ga.UID);
	}

	
	public void use()
	{
		beginSwing();
	}
	
	
	
	// ----- Begin Visual Stuff ----- //
	public void beginSwing()
	{
		if (atk_style.style == Weapon_Style.SWING)
		{
			float height = holder.getPosition().y;
			
			float startH = 1 + (ran.nextFloat()*height);
			
			Vector3 base = new Vector3(holder.getRotation().x, 0, holder.getRotation().z);
			Vector3 up = new Vector3(0, 1, 0);
			Vector3 start = base.crs(up);
			
			if (side == 1) start.mul(-1);
			
			start.add(holder.getRotation().x, startH, holder.getRotation().z);
			
			Vector3 rot = start.cpy();
			rot.mul(-2);
			rot.add(holder.getRotation().x, height-startH, holder.getRotation().z);
			
			
			beginSwingCircular(start, rot);
			
			System.out.println("Beginning swing");
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
	
	final Vector3 tmpPosA = new Vector3();
	final Vector3 tmpPosB = new Vector3();
	public void update(float delta)
	{
		if (!swinging) return;
		
		tmpPosA.set(atk_style.positionA);
		tmpPosB.set(atk_style.positionB);
		
		atk_style.update(delta, collided);
		
		if (collided) return;
		
		GameActor ga = checkCollisionEntity(tmpPosA, atk_style.positionA);
		if (ga != null)
		{
			System.out.println("Hit on entity. A");
			damage(ga);
			collided = true;
			return;
		}
		ga = checkCollisionEntity(tmpPosB, atk_style.positionB);
		if (ga != null)
		{
			System.out.println("Hit on entity. B");
			damage(ga);
			collided = true;
			return;
		}
		
		if (checkCollisionLevel(tmpPosA, atk_style.positionA))
		{
			System.out.println("Hit on level. A");
			collided = true;
			return;
		}

		if (checkCollisionLevel(tmpPosB, atk_style.positionB))
		{
			System.out.println("Hit on level. B");
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
		
		for (GameObject go : level.levelObjects)
		{
			if (Intersector.intersectRaySphere(ray, go.getPosition(), go.getRadius(), tmpVec))
			{
				if (start.dst2(tmpVec) > start.dst2(end)) continue;
				return true;
			}
		}
		
		return false;
	}
	
	public GameActor checkCollisionEntity(Vector3 start, Vector3 end)
	{
		Level level = GameData.level;
		
		ray.origin.set(start);
		ray.direction.set(end);
		ray.direction.sub(start);
		
		for (GameActor go : level.actors)
		{
			if (go.UID.equals(holder.UID)) continue;
			
			if (Intersector.intersectRaySphere(ray, go.getPosition(), go.getRadius(), tmpVec))
			{
				if (start.dst2(tmpVec) > start.dst2(end)) continue;
				
				return go;
			}
		}
		
		return null;
	}
	
	public void dispose()
	{
		atk_style.dispose();
	}
}

abstract class Attack_Style
{
	public static final int TRAIL_STEPS = 60;
	
	Weapon_Style style;
	
	final MotionTrail trail;
	
	final Vector3 positionA = new Vector3();
	final Vector3 positionB = new Vector3();
	
	final Vector3 tmpVec = new Vector3();
	final Vector3 tmpVec2 = new Vector3();
	final Vector3 tmpVec3 = new Vector3();
	
	public Attack_Style()
	{
		trail = new MotionTrail(TRAIL_STEPS);
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