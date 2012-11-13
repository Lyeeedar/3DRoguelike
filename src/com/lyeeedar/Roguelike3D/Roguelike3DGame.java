package com.lyeeedar.Roguelike3D;

import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.lyeeedar.Roguelike3D.Graphics.*;


public class Roguelike3DGame extends Game {

	public HashMap<String, AbstractScreen> screens = new HashMap<String, AbstractScreen>();
	
	@Override
	public void create() {
		loadScreens();
	}
	
	private void loadScreens()
	{
		screens.put("LibGDXSplash", new LibGDXSplashScreen(this));
		screens.put("MainMenu", new MainMenuScreen(this));
		screens.put("ShaderTest", new ShaderTestScreen(this));
		screens.put("InGame", new InGameScreen(this));
		setScreen(screens.get("LibGDXSplash"));
	}

	public void switchScreen(String screen)
	{
		setScreen(screens.get(screen));
	}
}
