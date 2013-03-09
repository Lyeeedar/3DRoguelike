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
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEffect;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;

public class Spell implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1635153582138165618L;
	private SpellBehaviour moveBehaviour;
	private SpellBehaviour damBehaviour;
	
	public transient boolean move = true;
	
	public float radius;
	
	private transient final Vector3 position = new Vector3();
	private transient final Vector3 rotation = new Vector3();
	private transient final Vector3 velocity = new Vector3();
	
	public final ParticleEffect particleEffect;
	
	private transient final Vector3 tmpVec = new Vector3();
	private transient final Vector3 up = new Vector3(0, 1, 0);
	private transient final Matrix4 tmpMat = new Matrix4();
	
	private transient final Vector3 tmpVelocity = new Vector3();
	
	public String casterUID;
	
	HashMap<Damage_Type, Integer> DAM_DAM;
	HashMap<Element, Integer> ELE_DAM;
	int damage;
	
	public boolean alive = true;

	public Spell(String casterUID, ParticleEffect particleEffect, float radius) {
		this.casterUID = casterUID;
		
		this.particleEffect = particleEffect;
		this.radius = radius;
	}
	
	public void initialise(Vector3 position, Vector3 rotation)
	{
		this.rotation.set(rotation);
		
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
			delete();
			return true;
		}
		
		if (move) moveBehaviour.update(delta, this);
		else damBehaviour.update(delta, this);
		
		particleEffect.update(delta, cam);

		return false;
	}
	
	public void render()
	{
		particleEffect.render();
	}
	
	public void dispose(){
		particleEffect.dispose();
	}
	
	public void delete()
	{
		particleEffect.delete();
	}
	
	public void create()
	{
		particleEffect.create();
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
		if (particleEffect != null) particleEffect.setPosition(position);
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
		Spell spell = new Spell(casterUID, particleEffect.copy(), radius);
		spell.setBehaviour(moveBehaviour.copy(), damBehaviour.copy());
		spell.setDamage(DAM_DAM, ELE_DAM, damage);
		
		return spell;
	}

	/**
	 * @return the radius
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}

	/**
	 * @return the dAM_DAM
	 */
	public HashMap<Damage_Type, Integer> getDAM_DAM() {
		return DAM_DAM;
	}

	/**
	 * @param dAM_DAM the dAM_DAM to set
	 */
	public void setDAM_DAM(HashMap<Damage_Type, Integer> dAM_DAM) {
		DAM_DAM = dAM_DAM;
	}

	/**
	 * @return the eLE_DAM
	 */
	public HashMap<Element, Integer> getELE_DAM() {
		return ELE_DAM;
	}

	/**
	 * @param eLE_DAM the eLE_DAM to set
	 */
	public void setELE_DAM(HashMap<Element, Integer> eLE_DAM) {
		ELE_DAM = eLE_DAM;
	}

	/**
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}

	/**
	 * @param damage the damage to set
	 */
	public void setDamage(int damage) {
		this.damage = damage;
	}

	/**
	 * @return the alive
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * @param alive the alive to set
	 */
	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	/**
	 * @return the position
	 */
	public Vector3 getPosition() {
		return position;
	}

	/**
	 * @return the rotation
	 */
	public Vector3 getRotation() {
		return rotation;
	}

	/**
	 * @return the velocity
	 */
	public Vector3 getVelocity() {
		return velocity;
	}

	/**
	 * @return the casterUID
	 */
	public String getCasterUID() {
		return casterUID;
	}
}
