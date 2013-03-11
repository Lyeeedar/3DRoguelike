package com.lyeeedar.Utils;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.loaders.wavefront.ObjLoader;
import com.badlogic.gdx.graphics.g3d.model.still.StillModel;

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
}
