package com.lyeeedar.Roguelike3D.Game.Spell;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.lyeeedar.Roguelike3D.Game.GameData.Elements;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Spell.Behaviours.SpellBehaviourPackage;
import com.lyeeedar.Roguelike3D.Game.Spell.Behaviours.SpellProjectile;
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;

public class Spell extends GameObject{
	
	public enum SpellBehaviour {
		PROJECTILE
	}
	
	public Elements element;
	
	public float[] data;
	
	public String casterUID;
	
	public boolean dead = false;
	
	public Spell(String model, Color colour, String texture, float x, float y,
			float z) {
		super(model, colour, texture, x, y, z);
		
		setupBehaviours();
	}
	
	public Spell(Mesh mesh, Color colour, String texture, float x, float y,
			float z) {
		super(mesh, colour, texture, x, y, z);
		
		setupBehaviours();
	}

	public Spell(VisibleObject vo, float x, float y, float z) {
		super(vo, x, y, z);
		
		setupBehaviours();
	}
	
	public void setData(Elements element, String casterUID, float... data)
	{
		this.element = element;
		this.casterUID = casterUID;
		this.data = data;
		
	}

	ArrayList<SpellBehaviour> behaviourChain = new ArrayList<SpellBehaviour>();
	HashMap<SpellBehaviour, SpellBehaviourPackage> behaviours = new HashMap<SpellBehaviour, SpellBehaviourPackage>();
	
	public void update (float delta)
	{
		if (dead) return;
		
		for (SpellBehaviour behaviour : behaviourChain)
		{
			behaviours.get(behaviour).update(this, delta);
		}
	}
	
	public void addBehaviour(SpellBehaviour sb)
	{
		behaviourChain.add(sb);
	}
	
	public void setBehaviours(SpellBehaviour... sbs)
	{
		behaviourChain.clear();
		
		for (int i = 0; i < sbs.length; i++)
		{
			behaviourChain.add(sbs[i]);
		}
	}
	
	public void setupBehaviours()
	{
		behaviours.clear();
		
		behaviours.put(SpellBehaviour.PROJECTILE, new SpellProjectile());
	}

}
