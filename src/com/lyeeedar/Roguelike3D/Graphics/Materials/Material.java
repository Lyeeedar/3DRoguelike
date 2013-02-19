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
package com.lyeeedar.Roguelike3D.Graphics.Materials;

import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.lyeeedar.Roguelike3D.Graphics.Colour;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;

public class Material implements Serializable {

	private static final long serialVersionUID = 7913278056780119939L;
	protected String name;

	public ColorAttribute colourAttribute;
	public TextureAttribute textureAttribute;
	public TextureAttribute normalmapAttribute;

	public Material (String name) {
		this.name = name;
	}
	
	public void setColour(Colour colour)
	{
		colourAttribute = new ColorAttribute(colour, ColorAttribute.colour);
	}
	
	public void setTexture(String textureName)
	{
		textureAttribute = new TextureAttribute(textureName, 0, TextureAttribute.diffuseTexture);
		normalmapAttribute = new TextureAttribute(textureName+".map", 0, TextureAttribute.normalmapTexture);
	}

	public TextureAttribute bind (ShaderProgram program, LightManager lights, TextureAttribute lastTexture) {
		if (colourAttribute != null)
		{
			colourAttribute.bind(program, lights);
		}
		
		if (lastTexture != null && textureAttribute != null && lastTexture.textureName.equals(textureAttribute.textureName)) return lastTexture;

		if (lights.quality != LightQuality.FORWARD_VERTEX && normalmapAttribute != null)
		{
			normalmapAttribute.bind(program, lights);
		}
		if (textureAttribute != null)
		{
			textureAttribute.bind(program, lights);
		}
		
		return textureAttribute;
	}

	public String getName () {
		return name;
	}

	@Override
	public int hashCode () {
		if (textureAttribute != null) return textureAttribute.hashCode();
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	public void create()
	{
		if (colourAttribute != null)
		{
			colourAttribute.create();
		}
		if (normalmapAttribute != null)
		{
			normalmapAttribute.create();
		}
		if (textureAttribute != null)
		{
			textureAttribute.create();
		}
	}
	
	public void dispose()
	{
		if (colourAttribute != null)
		{
			colourAttribute.dispose();
		}
		if (normalmapAttribute != null)
		{
			normalmapAttribute.dispose();
		}
		if (textureAttribute != null)
		{
			textureAttribute.dispose();
		}
	}
}
