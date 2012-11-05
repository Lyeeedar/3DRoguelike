package com.lyeeedar.Roguelike3D;

import java.util.HashMap;

import com.badlogic.gdx.Game;
import com.lyeeedar.Roguelike3D.Graphics.GameScreen;
import com.lyeeedar.Roguelike3D.Graphics.LibGDXSplashScreen;
import com.lyeeedar.Roguelike3D.Graphics.MainMenuScreen;

public class Roguelike3DGame extends Game {

	public HashMap<String, GameScreen> screens = new HashMap<String, GameScreen>();
	
	@Override
	public void create() {
		loadScreens();
	}
	
	public void loadScreens()
	{
		screens.put("LibGDXSplash", new LibGDXSplashScreen(this));
		screens.put("MainMenu", new MainMenuScreen(this));
		setScreen(screens.get("LibGDXSplash"));
	}

}
