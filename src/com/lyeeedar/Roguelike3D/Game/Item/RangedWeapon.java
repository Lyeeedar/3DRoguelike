package com.lyeeedar.Roguelike3D.Game.Item;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.lyeeedar.Roguelike3D.Game.GameData.Damage_Type;
import com.lyeeedar.Roguelike3D.Game.GameData.Element;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Spell.Spell;
import com.lyeeedar.Roguelike3D.Game.Spell.SpellBehaviourBolt;
import com.lyeeedar.Roguelike3D.Game.Spell.SpellBehaviourSingleDamage;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedModel;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedModelBehaviourCastSpell;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedModelNode;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedOneHandedStab;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedOneHandedSwing;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;

public class RangedWeapon extends Equipment_HAND {

	private static final long serialVersionUID = -5163322429662568362L;
	
	public enum Ranged_Weapon_Style {
		ARROW,
		FIREBALL
	}
	
	public static Ranged_Weapon_Style convertWeaponStyle(String style)
	{
		for (Ranged_Weapon_Style rws : Ranged_Weapon_Style.values())
		{
			if (style.equalsIgnoreCase(""+rws)) return rws;
		}
		
		return null;
	}

	public RangedWeapon(Ranged_Weapon_Style style,
			float weight, int strength,	HashMap<Element, Integer> ele_dam,	HashMap<Damage_Type, Integer> dam_dam, float attack_speed,
			boolean two_handed, RiggedModel model) {
		super(weight, strength, ele_dam, dam_dam, attack_speed, two_handed,
				model);
		
		if (style == Ranged_Weapon_Style.FIREBALL)
		{
			ParticleEmitter pe = new ParticleEmitter(0, 0, 0, 0.3f, 0.3f, 0.3f, 0.005f, 1);
			pe.setTexture("texf", new Vector3(0.0f, 2.0f, 0.0f), new Colour(0.7f, 0.9f, 0.3f, 1.0f), new Colour(1.0f, 0.0f, 0.0f, 1.0f), true, 0.5f, 1f, true, false);
			
			Spell spell = new Spell("", pe, pe.getRadius());
			spell.setDamage(dam_dam, ele_dam, strength);
			spell.setBehaviour(new SpellBehaviourBolt(), new SpellBehaviourSingleDamage());
			
			RiggedModelNode tip = model.getNode("Tip");
			RiggedModelBehaviourCastSpell behaviour = new RiggedModelBehaviourCastSpell(tip, spell);
			tip.setBehaviour(behaviour);
			
			model.rootNode.setBehaviour(new RiggedOneHandedSwing(model.rootNode, weight, attack_speed));
		}
	}

	@Override
	protected void unequipped() {
	}

	@Override
	protected void equipped(GameActor actor, int side) {
	}

	@Override
	protected void drawed(Camera cam) {
	}

	@Override
	protected void updated(float delta) {
	}

	@Override
	protected void fixReferencesSuper() {
	}

	@Override
	public Table getDescriptionWidget(Skin skin) {
		return null;
	}

	@Override
	public Table getComparisonWidget(Equippable other, Skin skin) {
		return null;
	}

}
