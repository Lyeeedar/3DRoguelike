package com.lyeeedar.Roguelike3D.Game.Spell;

import java.io.Serializable;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;

public class Spell implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1635153582138165618L;
	private SpellBehaviour moveBehaviour;
	private SpellBehaviour damBehaviour;
	
	public boolean move = true;
	
	public float radius;
	
	private final Vector3 position = new Vector3();
	private final Vector3 rotation = new Vector3();
	private final Vector3 velocity = new Vector3();
	
	public ParticleEmitter particleEmitter;
	
	private final Vector3 tmpVec = new Vector3();
	private final Vector3 up = new Vector3(0, 1, 0);
	private final Matrix4 tmpMat = new Matrix4();
	
	private final Vector3 tmpVelocity = new Vector3();
	
	public String casterUID;
	
	HashMap<Damage_Type, Integer> DAM_DAM = new HashMap<Damage_Type, Integer>();
	HashMap<Element, Integer> ELE_DAM = new HashMap<Element, Integer>();
	int damage;
	
	public boolean alive = true;

	public Spell(String casterUID) {
		this.casterUID = casterUID;
	}
	
	public void initialise(Vector3 position, Vector3 rotation, ParticleEmitter particleEmitter, float radius)
	{
		this.rotation.set(rotation);
		this.particleEmitter = particleEmitter;
		this.radius = radius;
		
		positionAbsolutely(position);
	}
	
	public void setBehaviour(SpellBehaviour moveBehaviour, SpellBehaviour damBehaviour)
	{
		this.moveBehaviour = moveBehaviour;
		this.damBehaviour = damBehaviour;
	}
	
	public void setDamage(HashMap<Damage_Type, Integer> DAM_DAM, HashMap<Element, Integer> ELE_DAM, int damage)
	{
		this.DAM_DAM = DAM_DAM;
		this.ELE_DAM = ELE_DAM;
		this.damage = damage;
	}

	public boolean update(float delta, Camera cam)
	{
		if (!alive)
		{
			dispose();
			return true;
		}
		
		if (move) moveBehaviour.update(delta, this);
		else damBehaviour.update(delta, this);
		
		particleEmitter.update(delta, cam);

		return false;
	}
	
	public void render(Camera cam)
	{
		particleEmitter.render(cam);
	}
	
	public void dispose(){
		particleEmitter.dispose();
	}
	
	public void create()
	{
		particleEmitter.create();
	}
	
	public void applyMovement(float delta)
	{
		Level lvl = GameData.level;
		
		tmpVelocity.set(velocity).mul(delta*100);
		
		// Check for collision
		if (lvl.checkCollision(position.tmp().add(tmpVelocity), radius, casterUID))
		{
			move = false;
		}

		translate(tmpVelocity);
		
		velocity.set(0, 0, 0);
		
	}
	
	public void Yrotate (float angle) {
		
		Vector3 dir = rotation.cpy().nor();

		if( (dir.y>-0.7) && (angle<0) || (dir.y<+0.7) && (angle>0) )
		{
			Vector3 localAxisX = rotation.cpy();
			localAxisX.crs(up.tmp()).nor();
			rotate(localAxisX.x, localAxisX.y, localAxisX.z, angle);

		}
	}

	public void Xrotate (float angle) {
		rotate(tmpVec.set(0, 1, 0), angle);
	}
	
	public void rotate (float x, float y, float z, float angle) {
		rotate(tmpVec.set(x, y, z), angle);
	}

	/** Rotates the direction and up vector of this camera by the given angle around the given axis. The direction and up vector
	 * will not be orthogonalized.
	 *
	 * @param axis
	 * @param angle the angle */
	public void rotate (Vector3 axis, float angle) {
		tmpMat.setToRotation(axis, angle);
		rotation.mul(tmpMat).nor();
		up.mul(tmpMat).nor();
	}
	
	public void translate(float x, float y, float z)
	{
		translate(new Vector3(x, y, z));
	}
	
	public void translate(Vector3 vec)
	{
		position.add(vec);

		positionAbsolutely(position);
	}
	
	public void positionAbsolutely(Vector3 position)
	{
		this.position.set(position);
		if (particleEmitter != null) particleEmitter.setPosition(position);
	}

	public void positionYAbsolutely(float y)
	{
		positionAbsolutely(tmpVec.set(position.x, y, position.z));
	}
	
	public void positionAbsolutely(float x, float y, float z)
	{
		positionAbsolutely(tmpVec.set(x, y, z));
	}
	
	public void left_right(float mag)
	{
		velocity.x += (float)Math.sin(rotation.z) * mag;
		velocity.z += -(float)Math.sin(rotation.x) * mag;
	}

	public void forward_backward(float mag)
	{
		velocity.x += (float)Math.sin(rotation.x) * mag;
		velocity.z += (float)Math.sin(rotation.z) * mag;
		velocity.y += (float)Math.sin(rotation.y) * mag;
	}
	
	public Spell copy()
	{
		Spell spell = new Spell(casterUID);
		spell.setBehaviour(moveBehaviour.copy(), damBehaviour.copy());
		spell.setDamage(DAM_DAM, ELE_DAM, damage);
		
		return spell;
	}
}
