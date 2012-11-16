package com.lyeeedar.Roguelike3D;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.lyeeedar.Roguelike3D.Graphics.Screens.*;


public class Roguelike3DGame extends Game {

	public HashMap<String, AbstractScreen> screens = new HashMap<String, AbstractScreen>();
	
	@Override
	public void create() {
		loadScreens();
		setScreen(screens.get("InGame"));
	}
	
	private void loadScreens()
	{
		//screens.put("LibGDXSplash", new LibGDXSplashScreen(this));
		//screens.put("InGame", new InGameScreen(this));
		screens.put("InGame", new StillModelViewerGL20(this, "data/textures/icon.png"));
		
		for (Map.Entry<String, AbstractScreen> entry : screens.entrySet())
		{
			entry.getValue().create();
		}
	}

	public void switchScreen(String screen)
	{
		setScreen(screens.get(screen));
	}
}
