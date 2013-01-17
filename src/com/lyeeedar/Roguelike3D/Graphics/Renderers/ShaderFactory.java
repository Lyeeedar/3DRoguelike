/*******************************************************************************
 * Copyright (c) 2012 Philip Collin.
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
	
	static final String VERTEX_FILE = "vertex_lighting";
	static final String NORMALMAP_FILE = "normal_mapping";

	static final String define = "#define ";
	static final String lightsNum = define + "LIGHTS_NUM ";

	static public ShaderProgram createShader (Material material, LightManager lights) {

		final StringBuilder flags = new StringBuilder(128);
		flags.append(lightsNum);
		
		if (material.affectedByLighting)
			flags.append(lights.maxLightsPerModel);
		else
			flags.append(0);
		
		flags.append("\n");

		if (material != null) {
			for (int i = 0; i < material.attributes.size; i++) {
				flags.append(define);
				flags.append(material.attributes.get(i).getShaderFlag());
				flags.append("\n");
			}
		}
		// TODO FIX light chose method
		String fileName = "";
		if (lights.quality == LightQuality.FRAGMENT)
			fileName = "pixel_lighting";
		else if (lights.quality == LightQuality.NORMALMAP)
			fileName = NORMALMAP_FILE;
		else if (lights.quality == LightQuality.VERTEX){
			fileName = VERTEX_FILE;
		}
		else
		{
			System.err.println("Error! Light Quality invalid!");
		}
		
		final String vertexShader = flags + Gdx.files.internal("data/shaders/model/" + fileName + ".vertex.glsl").readString();
		final String fragmentShader = flags + Gdx.files.internal("data/shaders/model/" + fileName + ".fragment.glsl").readString();
		
		ShaderProgram.pedantic = false;
		ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.isCompiled())
		{
			Gdx.app.error("Problem loading shader:", shader.getLog());
			System.out.println(fragmentShader);
			
			final String vertexShader2 = flags + Gdx.files.internal("data/shaders/model/" + VERTEX_FILE + ".vertex.glsl").readString();
			final String fragmentShader2 = flags + Gdx.files.internal("data/shaders/model/" + VERTEX_FILE + ".fragment.glsl").readString();
			
			shader = new ShaderProgram(vertexShader2, fragmentShader2);
		}
		
		return shader;
	}
}
