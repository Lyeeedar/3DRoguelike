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

package com.lyeeedar.Roguelike3D.Graphics.Lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class LightManager {
	
	public static final int maxLights = 16;

	public enum LightQuality {
		VERTEX, FRAGMENT
	};

	public LightQuality quality;

	final public Array<PointLight> pointLights = new Array<PointLight>(false, maxLights);
	final private float[] positions;
	final private float[] colors;
	final private float[] intensities;

	public final int maxLightsPerModel;

	final public Color ambientLight = new Color();

	/** Only one for optimizing - at least at now */
	public DirectionalLight dirLight;

	public LightManager () {
		this(4, LightQuality.VERTEX);
	}

	public LightManager (int maxLightsPerModel, LightQuality lightQuality) {
		quality = lightQuality;
		this.maxLightsPerModel = maxLightsPerModel;

		colors = new float[3 * maxLightsPerModel];
		positions = new float[3 * maxLightsPerModel];
		intensities = new float[maxLightsPerModel];
	}

	public void addLight (PointLight light) {
		pointLights.add(light);
	}
	
	public void removeLight(String UID)
	{
		for (int i = 0; i < maxLights; i++)
		{
			if (pointLights.get(i).UID.equals(UID))
			{
				pointLights.removeIndex(i);
				return;
			}
		}
	}

	public void clear () {
		pointLights.clear();
	}

	public void calculateAndApplyLightsToModel (Vector3 center, ShaderProgram shader) {
		this.calculateLights(center.x, center.y, center.z);
		this.applyLights(shader);
	}

	// TODO make it better if it slow
	// NAIVE but simple implementation of light choosing algorithm
	// currently calculate lights based on transformed center position of model
	// TODO one idea would be first cull lights that can't affect the scene with
	// frustum check.
	// TODO another idea would be first cut lights that are further from model
	// than x that would make sorted faster
	public void calculateLights (float x, float y, float z) {
		final int maxSize = pointLights.size;
		// solve what are lights that influence most
		if (maxSize > maxLightsPerModel) {

			for (int i = 0; i < maxSize; i++) {
				final PointLight light = pointLights.get(i);
				light.priority = (int)(PointLight.PRIORITY_DISCRETE_STEPS * (light.intensity * light.position.dst(x, y, z)));
				// if just linear fallof
			}
			pointLights.sort();
		}

		// fill the light arrays
		final int size = maxLightsPerModel > maxSize ? maxSize : maxLightsPerModel;
		for (int i = 0; i < size; i++) {
			final PointLight light = pointLights.get(i);
			final Vector3 pos = light.position;
			positions[3 * i + 0] = pos.x;
			positions[3 * i + 1] = pos.y;
			positions[3 * i + 2] = pos.z;

			final Color col = light.colour;
			colors[3 * i + 0] = col.r;
			colors[3 * i + 1] = col.g;
			colors[3 * i + 2] = col.b;

			intensities[i] = light.intensity;
		}
	}

	/** Apply lights GLES2.0, call calculateLights before applying */
	public void applyLights (ShaderProgram shader) {
		shader.setUniform3fv("u_light_positions", positions, 0, maxLightsPerModel * 3);
		shader.setUniform3fv("u_light_colours", colors, 0, maxLightsPerModel * 3);
		shader.setUniform1fv("u_light_intensities", intensities, 0, maxLightsPerModel);
	}

	public void applyGlobalLights (ShaderProgram shader) {
		shader.setUniformf("u_ambient", ambientLight);
		if (dirLight != null) {
			final Vector3 v = dirLight.direction;
			final Color c = dirLight.color;
			shader.setUniformf("u_directional_light_direction", v.x, v.y, v.z);
			shader.setUniformf("u_directional_light_colour", c.r, c.g, c.b);
		}
	}
}
