package com.lyeeedar.Roguelike3D.Graphics;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Preferences;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;

public abstract class ApplicationChanger {
	
	public Preferences prefs;
	
	public ApplicationChanger(Preferences prefs)
	{
		this.prefs = prefs;
		if (!prefs.getBoolean("created"))
		{
			prefs.putBoolean("created", true);
			prefs.putString("window-name", "Roguelike3D");
			prefs.putInteger("resolutionX", 800);
			prefs.putInteger("resolutionY", 600);
			prefs.putBoolean("fullscreen", false);
			prefs.putBoolean("vSync", true);
			prefs.putInteger("MSAA-samples", 16);
			prefs.putString("Renderer", "Deferred");
			prefs.flush();
		}
	}
	
	public abstract Application createApplication(Roguelike3DGame game, Preferences pref);
	
	public abstract void updateApplication(Preferences pref);
	
	public abstract String[] getSupportedDisplayModes();
}
