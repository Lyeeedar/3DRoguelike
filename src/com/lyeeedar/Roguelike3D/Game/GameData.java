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
package com.lyeeedar.Roguelike3D.Game;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Actor.Player;
import com.lyeeedar.Roguelike3D.Game.Level.Level;
import com.lyeeedar.Roguelike3D.Game.Level.LevelContainer;
import com.lyeeedar.Roguelike3D.Game.Level.LevelGraphics;
import com.lyeeedar.Roguelike3D.Game.Level.XML.BiomeReader;
import com.lyeeedar.Roguelike3D.Game.Level.XML.RoomReader;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.LevelObject;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.PlayerPlacer;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.Spawner;
import com.lyeeedar.Roguelike3D.Game.LevelObjects.Stair;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Roguelike3D.Graphics.Models.SkyBox;
import com.lyeeedar.Roguelike3D.Graphics.ParticleEffects.ParticleEmitter;


public class GameData {
	
	/**
	 * Cycle of elements:
	 * 
	 * Destruction - 
	 * 		FIRE melts METAL
	 * 		METAL cuts WOOD
	 * 		WOOD funnels AIR
	 * 		AIR evaporates WATER
	 * 		WATER douses FIRE
	 * 
	 * 		VOID consumes ALL (except AETHER)
	 * 
	 * Creation - 
	 * 		FIRE excites AIR
	 * 		AIR polishes METAL
	 * 		METAL carries WATER
	 * 		WATER nourishes WOOD
	 * 		WOOD feeds FIRE
	 * 
	 * 		AETHER creates ALL (except VOID)
	 * 
	 * @author Philip
	 */
	public enum Element {
		FIRE,
		METAL,
		WOOD,
		AIR,
		WATER,
		AETHER,
		VOID
	}
	
	public static HashMap<Element, Integer> getElementMap()
	{
		HashMap<Element, Integer> map = new HashMap<Element, Integer>();
		
		for (Element e : Element.values())
		{
			map.put(e, 0);
		}
		
		return map;
	}
	
	/**
	 * PIERCE = Armour piercing. (e.g. A Spear)
	 * IMPACT = Launching (e.g. A Hammer)
	 * TOUCH = Effect on touch (e.g. Elemental attacks)
	 * @author Philip
	 */
	public enum Damage_Type {
		PIERCE,
		IMPACT,
		TOUCH
	}
	
	public static HashMap<Damage_Type, Integer> getDamageMap()
	{
		HashMap<Damage_Type, Integer> map = new HashMap<Damage_Type, Integer>();
		
		for (Damage_Type d : Damage_Type.values())
		{
			map.put(d, 0);
		}
		
		return map;
	}
	
	public enum Rarity {
		COMMON(1, 139, 79, 2),
		UNCOMMON(2, 208, 14, 9),
		FABULOUS(3, 255, 138, 0),
		RARE(4, 250, 250, 0),
		UNIQUE(5, 88, 221, 23),
		MYSTICAL(16, 13, 247, 192),
		LEGENDARY(37, 50, 41, 209),
		GODLIKE(8, 127, 11, 209),
		DIVINE(9, 250, 0, 250),
		TRUE(10, 255, 255, 255)
		;
		
		private final int val;
		private final Color colour;
		Rarity(int val, int r, int g, int b) { this.val = val; this.colour = new Color(r/255f, g/255f, b/255f, 1.0f); }
		public int getVal() { return val; }
		public Color getColour() { return colour; }
	}
	
	public static Rarity getRarity(int i)
	{
		for (Rarity r : Rarity.values())
		{
			if (r.getVal() == i) return r;
		}
		
		return null;
	}
	
	public static Label getRarityLabel(int i, Skin skin)
	{
		Rarity r = getRarity(i);
		
		Label l = new Label(""+r, skin);
		
		LabelStyle ls = l.getStyle();
		LabelStyle nls = new LabelStyle();
		nls.fontColor = r.getColour();
		nls.background = ls.background;
		nls.font = ls.font;
		
		l.setStyle(nls);
		
		return l;
	}
	
	public static LightQuality lightQuality = LightQuality.NORMALMAP;
	
	public static LightManager lightManager;
	
	public static Level level;
	
	public static LevelGraphics levelGraphics;
	public static ArrayList<ParticleEmitter> particleEmitters = null;
	public static SkyBox skyBox = new SkyBox("sky");
	
	public static Player player;
	
	public static float gravity = 0.1f;

	public static HashMap<String, LevelContainer> dungeon;
	public static String currentLevel;
	
	public static Roguelike3DGame game;
	
	public static int[] resolution = {800, 600};
	
	public static HashMap<String, Texture> loadedTextures = new HashMap<String, Texture>();
	
	public static Texture loadTexture(String textureName)
	{
		String textureLocation = "data/textures/"+textureName+".png";
		
		if (loadedTextures.containsKey(textureLocation)) return loadedTextures.get(textureLocation);
		
		if (!Gdx.files.internal(textureLocation).exists()) return null;
		
		Texture texture = new Texture(Gdx.files.internal(textureLocation), true);
		
		loadedTextures.put(textureLocation, texture);
		
		return texture;
	}
	
	public static void load()
	{
		SaveGame save = SaveGame.load();
		dungeon = save.dungeon;
		currentLevel = save.currentLevel;
		
		changeLevel(currentLevel);
	}
	
	public static void save(SaveGame save)
	{
		save.setDungeon(dungeon, currentLevel);
	}
	
	public static void init(final Roguelike3DGame game)
	{
		GameData.game = game;
		GameStats.init();
		
		dungeon = new HashMap<String, LevelContainer>();
		LevelContainer lc = new LevelContainer("start_town", "start_town", 1, new String[]{}, new String[]{"generic1"});
		dungeon.put(lc.name, lc);
		
		LevelContainer lc1 = new LevelContainer("generic1", "generic", 1, new String[]{"start_town"}, new String[]{});
		dungeon.put(lc1.name, lc1);
		
		currentLevel = "start_town";

		changeLevel("start_town");
	}
	
	static String prevLevel;
	public static void changeLevel(String level)
	{
		player = null;
		
		prevLevel = currentLevel;
		currentLevel = level;
		
		LevelContainer lc = dungeon.get(level);
		
		BiomeReader biome = new BiomeReader(lc.biome);
		RoomReader rReader = new RoomReader(lc.biome, lc.depth);
		
		lightManager = lc.getLightManager();
		lightManager.ambientLight.set(biome.getAmbientLight());
		
		particleEmitters = lc.particleEmitters;
		
		game.loadLevel(biome, rReader, Roguelike3DGame.INGAME);
	}
	
	public static void finishLoading(Level level, LevelGraphics graphics, String nextScreen)
	{
		System.out.println("Finishing loading.");
		
		GameData.level = level;
		levelGraphics = graphics;
		
		for (GameActor ga : level.actors)
		{
			if (ga instanceof Player) {
				player = (Player) ga;
				break;
			}
		}
		
		if (player == null)
		{
			player = new Player(new Colour(0, 0.6f, 0, 1.0f), "blank", 0, 0, 0, 1.0f, GL20.GL_TRIANGLES, "file", "model@");
			player.create();
			player.visible = false;
			
			level.addActor(player);
			
			PointLight l = new PointLight(player.position.cpy(), new Colour(1.0f, 1.0f, 1.0f, 1.0f), 0.01f, 0.3f);
			player.boundLight = l;
			lightManager.addDynamicLight(l);		
			
		}
		
		GameStats.setPlayerStats(player);
		
		for (GameActor ga : level.actors) {
			ga.fixReferences();
		}
		
		for (LevelObject lo : level.levelObjects) {
			lo.fixReferences();
		}
		
		for (ParticleEmitter pe : particleEmitters)
		{
			pe.fixReferences();
		}
		
		lightManager.fixReferences();
		
		for (LevelObject lo : level.levelObjects)
		{
			lo.positionYAbsolutely(lo.getRadius());
			if (lo instanceof Spawner)
			{
				Spawner s = (Spawner) lo;
				
				s.spawn(level);
			}
		}
		
		for (LevelObject lo : level.levelObjects)
		{

			if (lo instanceof PlayerPlacer)
			{
				if (prevLevel.equals(currentLevel))
				{
					player.positionAbsolutely(lo.position.tmp());
					break;
				}
			}
			else if (lo instanceof Stair)
			{
				Stair s = (Stair) lo;
				
				if (s.level_UID.equals(prevLevel))
				{
					player.positionAbsolutely(s.position.tmp().add(0, s.getPosition().y+s.getRadius()+player.getRadius()+1, 0));
				}
			}
		}

		SaveGame save = new SaveGame();
		GameData.save(save);
		GameStats.save(save);
		SaveGame.save(save);
		
		game.switchScreen(nextScreen);
		
	}
	
	public static Element getElement(String eleName)
	{
		for (Element e : Element.values())
		{
			if (eleName.equalsIgnoreCase(""+e)) return e;
		}
		
		return null;
	}
	
	public static Damage_Type getDamageType(String type)
	{
		for (Damage_Type dt : Damage_Type.values())
		{
			if (type.equalsIgnoreCase(""+dt)) return dt;
		}
		
		return null;
	}
	
	public static int calculateDamage(int strength, 
			HashMap<Element, Integer> ele_dam, HashMap<Damage_Type, Integer> dam_dam, 
			HashMap<Element, Integer> ele_def, HashMap<Damage_Type, Integer> dam_def)
	{
		float damage = 
				(((strength/100f)*dam_dam.get(Damage_Type.PIERCE)) * ((100f-dam_def.get(Damage_Type.PIERCE))/100f)) +
				(((strength/100f)*dam_dam.get(Damage_Type.IMPACT)) * ((100f-dam_def.get(Damage_Type.IMPACT))/100f)) +
				(((strength/100f)*dam_dam.get(Damage_Type.TOUCH)) * ((100f-dam_def.get(Damage_Type.TOUCH))/100f));
		
		float ele_damage = 
				(((damage/100f)*ele_dam.get(Element.FIRE)) * ((100f-ele_def.get(Element.FIRE))/100f)) +
				(((damage/100f)*ele_dam.get(Element.WATER)) * ((100f-ele_def.get(Element.WATER))/100f)) +
				(((damage/100f)*ele_dam.get(Element.AIR)) * ((100f-ele_def.get(Element.AIR))/100f)) +
				(((damage/100f)*ele_dam.get(Element.WOOD)) * ((100f-ele_def.get(Element.WOOD))/100f)) +
				(((damage/100f)*ele_dam.get(Element.METAL)) * ((100f-ele_def.get(Element.METAL))/100f)) +
				(((damage/100f)*ele_dam.get(Element.AETHER)) * ((100f-ele_def.get(Element.AETHER))/100f)) +
				(((damage/100f)*ele_dam.get(Element.VOID)) * ((100f-ele_def.get(Element.VOID))/100f));

		return (int)ele_damage;
	}
	
	public static int calculateSpeed(int weight, int strength)
	{
		return strength / weight;
	}

	public static LevelContainer getCurrentLevelContainer()
	{
		return dungeon.get(currentLevel);
	}
	
	public static LevelContainer getLevelContainer(String UID)
	{
		return dungeon.get(UID);
	}
	
	/**
	 * sx,sy,sz = sphere x,y,z centre co-ordinates
	 * sr = sphere radius
	 * bx,by,bz = box x,y,z corner co-ordinates
	 * bw,bh,bd = box width,height,depth
	 * @param sx
	 * @param sy
	 * @param sz
	 * @param sr
	 * @param bx
	 * @param by
	 * @param bz
	 * @param bw
	 * @param bh
	 * @param bd
	 * @return
	 */
	public static boolean SphereBoxIntersection(float sx, float sy, float sz, float sr, float bx, float by, float bz, float bw, float bh, float bd)
	{
		float dmin = 0;
		float sr2 = sr*sr;

		// x axis
		if (sx < bx)
			dmin=dmin+((sx-bx)*(sx-bx));

		else if (sx>(bx+bw))
			dmin=dmin+(((sx-(bx+bw)))*((sx-(bx+bw))));

		// y axis
		if (sy < by)
			dmin=dmin+((sy-by)*(sy-by));
		else if (sy>(by+bh))
			dmin=dmin+(((sy-(by+bh)))*((sy-(by+bh))));

		// z axis
		if (sz < bz)
			dmin=dmin+((sz-bz)*(sz-bz));
		else if (sz>(bz+bd))
			dmin=dmin+(((sz-(bz+bd)))*((sz-(bz+bd))));

		if (dmin<=sr2) return true; 
		else return false;
	}
}
