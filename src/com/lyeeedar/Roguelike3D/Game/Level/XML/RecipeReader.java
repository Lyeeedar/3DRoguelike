package com.lyeeedar.Roguelike3D.Game.Level.XML;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Node;

import com.lyeeedar.Roguelike3D.Game.Item.Item;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon;
import com.lyeeedar.Roguelike3D.Game.Item.Item.Item_Type;
import com.lyeeedar.Roguelike3D.Game.Item.MeleeWeapon.Weapon_Style;

public class RecipeReader extends XMLReader
{

	public static final String TYPE = "type";
	public static final String COMPONENTS = "components";
	public static final String NAME = "name";
	public static final String AMOUNT = "amount";
	public static final String VISUAL = "visual";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String GRID = "grid";
	public static final String ROW = "row";
	public static final String ITEM = "item";
	public static final String ATTACK_STYLE = "attack_style";
	public static final String ARC_MIN = "arc_min";
	public static final String ARC_MAX = "arc_max";
	public static final String SCALE = "scale";
	public static final String EQN = "eqn";
	public static final String MULTIPLY = "multiply";
	public static final String ELEMENT = "element";
	public static final String STRENGTH = "strength";
	public static final String PIERCE = "pierce";
	public static final String IMPACT = "impact";
	public static final String TOUCH = "touch";
	public static final String ELEMENTAL = "elemental";
	public static final String ATTACK_SPEED = "attack_speed";
	public static final String META = "meta";


	Node recipe;
	public RecipeReader(String recipe)
	{		
		super("data/xml/recipes.data");
		
		this.recipe = getNode(recipe, root_node.getChildNodes());
	}

	public Item_Type getItemType()
	{
		return Item.convertItemType(getNodeValue(TYPE, recipe.getChildNodes()));
	}

	public Weapon_Style getWeaponStyle()
	{
		Node item = getNode(ITEM, recipe.getChildNodes());

		Node wep_style = getNode(ATTACK_STYLE, item.getChildNodes());

		return MeleeWeapon.convertWeaponStyle(getNodeValue(TYPE, wep_style.getChildNodes()));
	}

	public HashMap<String, String> getWeaponStyleMeta()
	{
		HashMap<String, String> styleMeta = new HashMap<String, String>();

		Node item = getNode(ITEM, recipe.getChildNodes());

		Node wep_style = getNode(ATTACK_STYLE, item.getChildNodes());

		Node meta = getNode(META, wep_style.getChildNodes());

		for (int i = 0; i < meta.getChildNodes().getLength(); i++)
		{
			Node n = meta.getChildNodes().item(i);

			styleMeta.put(n.getNodeName(), n.getNodeValue());
		}

		return styleMeta;
	}

	public float getScale(String att)
	{
		float scale = 1.0f;

		Node item = getNode(ITEM, recipe.getChildNodes());
		Node attribute = getNode(att, item.getChildNodes());

		scale = Float.parseFloat(getNodeValue(SCALE, attribute.getChildNodes()));

		return scale;
	}

	public ArrayList<ArrayList<String>> getEqn(String att)
	{
		ArrayList<ArrayList<String>> eqn = new ArrayList<ArrayList<String>>();

		Node item = getNode(ITEM, recipe.getChildNodes());
		Node attribute = getNode(att, item.getChildNodes());

		for (int i = 0; i < attribute.getChildNodes().getLength(); i++)
		{
			Node block = attribute.getChildNodes().item(i);
			if (!block.getNodeName().equalsIgnoreCase(MULTIPLY)) continue;

			// Valid block!

			ArrayList<String> eqnBlock = new ArrayList<String>();

			for (int j = 0; j < block.getChildNodes().getLength(); j++)
			{
				Node n = block.getChildNodes().item(j);

				if (!n.getNodeName().equalsIgnoreCase(ELEMENT)) continue;

				eqnBlock.add(n.getNodeValue());
			}

			eqn.add(eqnBlock);
		}

		return eqn;
	}

}
