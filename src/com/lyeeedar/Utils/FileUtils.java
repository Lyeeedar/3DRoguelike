package com.lyeeedar.Utils;

import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g3d.loaders.wavefront.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;
import com.badlogic.gdx.utils.Array;

public class FileUtils {

	public static HashMap<String, Texture> loadedTextures = new HashMap<String, Texture>();
	/**
	 * Tries to load the given texture. If set to urgent, will throw a runtime exception if this texture does not exist.
	 * @param textureName
	 * @param urgent
	 * @return
	 */
	public static Texture loadTexture(String textureName, boolean urgent)
	{
		String textureLocation = "data/textures/"+textureName+".png";
		
		if (loadedTextures.containsKey(textureLocation)) return loadedTextures.get(textureLocation);
		
		if (!Gdx.files.internal(textureLocation).exists()) {
			if (urgent) throw new RuntimeException("Texture "+textureLocation+" does not exist!");
			else return null;
		}
		
		Texture texture = new Texture(Gdx.files.internal(textureLocation), true);
		
		loadedTextures.put(textureLocation, texture);
		
		return texture;
	}
	
	public static void unloadTextures()
	{
		for (Entry<String, Texture> entry : loadedTextures.entrySet())
		{
			entry.getValue().dispose();
		}
		loadedTextures.clear();
	}
	
	public static HashMap<String, Mesh> loadedMeshes = new HashMap<String, Mesh>();
	public static Mesh loadMesh(String meshName)
	{
		String meshLocation = "data/models/"+meshName+".obj";
		
		if (loadedMeshes.containsKey(meshLocation)) return loadedMeshes.get(meshLocation);
		
		if (!Gdx.files.internal(meshLocation).exists()) {
			throw new RuntimeException("Mesh "+meshName+" does not exist!");
		}
		ObjLoader loader = new ObjLoader();
		StillModel model = loader.loadObj(Gdx.files.internal(meshLocation));
		Mesh mesh = model.subMeshes[0].mesh;
		
		loadedMeshes.put(meshLocation, mesh);
		
		return mesh;
	}
	
	public static void unloadMeshes()
	{
		for (Entry<String, Mesh> entry : loadedMeshes.entrySet())
		{
			entry.getValue().dispose();
		}
		loadedMeshes.clear();
	}
	
	public static HashMap<String, TextureAtlas> loadedAtlases = new HashMap<String, TextureAtlas>();
	public static TextureAtlas loadAtlas(String atlasName)
	{
		String atlasLocation = "data/atlases/"+atlasName+".atlas";
		
		if (loadedMeshes.containsKey(atlasLocation)) return loadedAtlases.get(atlasLocation);
		
		if (!Gdx.files.internal(atlasLocation).exists()) {
			throw new RuntimeException("Atlas "+atlasName+" does not exist!");
		}
		
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasLocation));
		
		loadedAtlases.put(atlasLocation, atlas);
		
		return atlas;
	}
	
	public static void unloadAtlases()
	{
		for (Entry<String, TextureAtlas> entry : loadedAtlases.entrySet())
		{
			entry.getValue().dispose();
		}
		loadedAtlases.clear();
	}
	
	public static BufferedImage[] deconstructAtlas(TextureAtlas atlas)
	{
		Texture tex = atlas.getTextures().iterator().next();
		tex.getTextureData().prepare();
		Pixmap pixels = tex.getTextureData().consumePixmap();
		
		Array<AtlasRegion> regions = atlas.getRegions();
		regions.sort(new Comparator<AtlasRegion>(){
			@Override
			public int compare(AtlasRegion a1, AtlasRegion a2) {
				int val1 = Integer.parseInt(a1.name.replace("sprite", ""));
				int val2 = Integer.parseInt(a2.name.replace("sprite", ""));
				return val1 - val2;
			}});
		
		BufferedImage[] images = new BufferedImage[regions.size];
		
		for (int i = 0; i < regions.size; i++)
		{
			AtlasRegion region = regions.get(i);
			images[i] = new BufferedImage(region.getRegionWidth(), region.getRegionHeight(), BufferedImage.TYPE_INT_ARGB);
			
			for (int x = region.getRegionX(); x < region.getRegionX()+region.getRegionWidth(); x++)
			{
				for (int y = region.getRegionY(); y < region.getRegionY()+region.getRegionHeight(); y++)
				{
					Color c = new Color();
					Color.rgba8888ToColor(c, pixels.getPixel(x, y));
					
					java.awt.Color cc = new java.awt.Color(c.r, c.g, c.b, c.a);
					
					images[i].setRGB(x-region.getRegionX(), y-region.getRegionY(), cc.getRGB());
				}
			}
		}
		
		return images;
	}
}
