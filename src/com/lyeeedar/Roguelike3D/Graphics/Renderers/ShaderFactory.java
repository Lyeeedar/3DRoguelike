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
package com.lyeeedar.Roguelike3D.Graphics.Renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;

public class ShaderFactory {

	static final String DEFINE = "#define ";

	static public ShaderProgram createShader (String fileName, String... flags) {

		final StringBuilder flagList = new StringBuilder(128);
		
		for (int i = 0; i < flags.length; i++) {
			flagList.append(DEFINE);
			flagList.append(flags[i]);
			flagList.append("\n");
		}

		fileName = fileName.toLowerCase();
		
		final String vertexShader = flagList + Gdx.files.internal("data/shaders/model/" + fileName + ".vertex.glsl").readString();
		final String fragmentShader = flagList + Gdx.files.internal("data/shaders/model/" + fileName + ".fragment.glsl").readString();
		
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.isCompiled())
		{
			Gdx.app.error("Problem loading shader:", shader.getLog());
			
			System.out.println(vertexShader);
			System.out.println(fragmentShader);
		}
		
		return shader;
	}
}
