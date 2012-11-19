/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.lyeeedar.Roguelike3D.Graphics.Renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;

public class ShaderFactory {

	static final String define = "#define ";
	static final String lightsNum = define + "LIGHTS_NUM ";
	static final String dir_light = define + "DIR_LIGHTS ";

	static public ShaderProgram createShader (Material material, LightManager lights) {

		final StringBuilder flags = new StringBuilder(128);
		flags.append(lightsNum);
		flags.append(lights.maxLightsPerModel);
		flags.append("\n");
		
		flags.append(dir_light);
		int d_l = (lights.dirLight == null) ? 0 : 1;
		flags.append(d_l);
		flags.append("\n");

		if (material != null) {
			for (int i = 0; i < material.attributes.size; i++) {
				flags.append(define);
				flags.append(material.attributes.get(i).getShaderFlag());
				flags.append("\n");
			}
		}
		// TODO FIX light chose method
		String fileName;
		if (lights.quality == LightQuality.FRAGMENT)
			fileName = "pixel_lighting";
		else {
			fileName = "vertex_lighting";
		}
		final String vertexShader = flags + Gdx.files.internal("data/shaders/model/" + fileName + ".vertex.glsl").readString();
		final String fragmentShader = flags + Gdx.files.internal("data/shaders/model/" + fileName + ".fragment.glsl").readString();
		
		ShaderProgram.pedantic = false;
		final ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
		if (!shader.isCompiled())
		{
			Gdx.app.log("Problem loading shader:", shader.getLog());
		}
		
		return shader;
	}
}
