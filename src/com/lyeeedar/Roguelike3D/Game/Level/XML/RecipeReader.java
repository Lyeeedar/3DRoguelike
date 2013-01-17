package com.lyeeedar.Roguelike3D.Game.Level.XML;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.Node;

import com.lyeeedar.Roguelike3D.Game.Item.Component;
import com.lyeeedar.Roguelike3D.Game.Item.Component.Component_Type;
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
	public static final String RARITY = "rarity";
	public static final String RECIPES = "recipes";
	public static final String CONSTRAINTS = "constraints";


	Node recipe;
	public RecipeReader(String recipeName)
	{		
		super("data/xml/recipes.data");
		
		this.recipe = getNode(recipeName, getNode(RECIPES, root_node.getChildNodes()).getChildNodes());
	}
	
	public String getComponentName(char tag)
	{
		Node components = getNode(COMPONENTS, recipe.getChildNodes());
		
		Node c = getNode(""+tag, components.getChildNodes());
		
		return getNodeValue(NAME, c.getChildNodes());
	}
	
	public int getComponentAmount(char tag)
	{
		Node components = getNode(COMPONENTS, recipe.getChildNodes());
		
		Node c = getNode(""+tag, components.getChildNodes());
		
		return Integer.parseInt(getNodeValue(AMOUNT, c.getChildNodes()));
	}
	
	public ArrayList<Component_Type> getComponentConstraints(char tag)
	{
		Node components = getNode(COMPONENTS, recipe.getChildNodes());
		
		Node c = getNode(""+tag, components.getChildNodes());
		
		Node constraints = getNode(CONSTRAINTS, c.getChildNodes());
		
		ArrayList<Component_Type> list = new ArrayList<Component_Type>();
		
		if (constraints == null) return list;
		
		for (int i = 0; i < constraints.getChildNodes().getLength(); i++)
		{
			Node n = constraints.getChildNodes().item(i);
			
			if (n.getNodeName().equalsIgnoreCase(TYPE))
			{
				Component_Type ct = Component.convertComponentType(getNodeValue(n));
				list.add(ct);
			}
		}
		
		return list;
	}
	
	public char[][] getVisual()
	{
		Node visual = getNode(VISUAL, recipe.getChildNodes());
		int width = Integer.parseInt(getNodeValue(WIDTH, visual.getChildNodes()));
		int height = Integer.parseInt(getNodeValue(HEIGHT, visual.getChildNodes()));
		
		char[][] visualGrid = new char[height][width];
		
		int h = 0;
		Node grid = getNode(GRID, visual.getChildNodes());
		for (int i = 0; i < grid.getChildNodes().getLength(); i++)
		{
			Node n = grid.getChildNodes().item(i);
			if (n.getNodeName().equalsIgnoreCase(ROW))
			{
				String row = getNodeValue(n);
				for (int j = 0; j < row.length(); j++)
				{
					visualGrid[h][j] = row.charAt(j);
				}
				h++;
			}
		}
		
		return visualGrid;
	}
	
	public int getRecipeRarity()
	{
		String r = getNodeValue(RARITY, recipe.getChildNodes());
		
		return Integer.parseInt(r);
	}
	
	public String getRecipeName()
	{
		return recipe.getNodeName();
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
