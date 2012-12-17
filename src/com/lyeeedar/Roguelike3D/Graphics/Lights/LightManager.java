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
package com.lyeeedar.Roguelike3D.Graphics.Lights;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;

public class LightManager {
	
	public static final int maxLights = 16;

	public enum LightQuality {
		VERTEX, FRAGMENT
	};

	public LightQuality quality;

	final public Array<PointLight> dynamicPointLights = new Array<PointLight>(false, maxLights);
	final public Array<PointLight> staticPointLights = new Array<PointLight>(false, maxLights);
	final private float[] positions;
	final private float[] colors;
	final private float[] attenuations;
	final private float[] intensities;

	public final int maxLightsPerModel;

	final public Color ambientLight = new Color();

	public LightManager () {
		this(4, LightQuality.VERTEX);
	}

	public LightManager (int maxLightsPerModel, LightQuality lightQuality) {
		quality = lightQuality;
		this.maxLightsPerModel = maxLightsPerModel;

		colors = new float[3 * maxLightsPerModel];
		positions = new float[3 * maxLightsPerModel];
		attenuations = new float[maxLightsPerModel];
		intensities = new float[maxLightsPerModel];
	}

	public void addDynamicLight (PointLight light) {
		dynamicPointLights.add(light);
	}
	
	/**
	 * Add a static light to the scene. WARNING!!! You need to rebake the lights ({@link com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject#bakeLights(LightManager lights, boolean bakeStatics) VisibleObject.bakeLights}) for this to take any effect
	 * @param light
	 */
	public void addStaticLight (PointLight light) {
		staticPointLights.add(light);
	}
	
	public void removeDynamicLight(String UID)
	{
		for (int i = 0; i < dynamicPointLights.size; i++)
		{
			if (dynamicPointLights.get(i).UID.equals(UID))
			{
				dynamicPointLights.removeIndex(i);
				return;
			}
		}
	}

	public void clearAllLights () {
		dynamicPointLights.clear();
		staticPointLights.clear();
	}

	// TODO make it better if it slow
	// NAIVE but simple implementation of light choosing algorithm
	// currently calculate lights based on transformed center position of model
	// TODO one idea would be first cull lights that can't affect the scene with
	// frustum check.
	// TODO another idea would be first cut lights that are further from model
	// than x that would make sorted faster
	public void calculateDynamicLights (float x, float y, float z) {
		final int maxSize = dynamicPointLights.size;
		// solve what are lights that influence most
		if (maxSize > maxLightsPerModel) {

			for (int i = 0; i < maxSize; i++) {
				final PointLight light = dynamicPointLights.get(i);
				light.priority = (int)(PointLight.PRIORITY_DISCRETE_STEPS * ((light.intensity) * light.position.dst(x, y, z)));
				// if just linear falloff
			}
			dynamicPointLights.sort();
		}

		// fill the light arrays
		final int size = maxLightsPerModel > maxSize ? maxSize : maxLightsPerModel;
		for (int i = 0; i < size; i++) {
			final PointLight light = dynamicPointLights.get(i);
			final Vector3 pos = light.position;
			positions[3 * i + 0] = pos.x;
			positions[3 * i + 1] = pos.y;
			positions[3 * i + 2] = pos.z;

			final Color col = light.colour;
			colors[3 * i + 0] = col.r;
			colors[3 * i + 1] = col.g;
			colors[3 * i + 2] = col.b;

			attenuations[i] = light.attenuation;
			
			intensities[i] = light.intensity;
		}
	}

	public long getDynamicLightsHash()
	{
		final int maxSize = dynamicPointLights.size;
		final int size = maxLightsPerModel > maxSize ? maxSize : maxLightsPerModel;
		long hash = 1;
		for (int i = 0; i < size; i++) {
			final PointLight light = dynamicPointLights.get(i);
			long temp = 1;
			for (int ii = 0; ii < light.UID.length(); ii++)
			{
				temp += (int)light.UID.charAt(ii);
			}
			hash *= temp;
		}
		return hash;
	}
	
	/** Apply lights GLES2.0, call calculateLights before applying */
	public void applyDynamicLights (ShaderProgram shader, Material material) {
		
		if (!material.affectedByLighting) return;
		
		shader.setUniform3fv("u_light_positions", positions, 0, maxLightsPerModel * 3);
		shader.setUniform3fv("u_light_colours", colors, 0, maxLightsPerModel * 3);
		shader.setUniform1fv("u_light_intensities", intensities, 0, maxLightsPerModel);
		shader.setUniform1fv("u_light_attenuations", attenuations, 0, maxLightsPerModel);
	}

	public static final Color WHITE = Color.WHITE;
	public void applyGlobalLights (ShaderProgram shader, Material material) {
		
		if (!material.affectedByLighting) return;
		
		shader.setUniformf("u_ambient", ambientLight);
	}
	
	public Vector3 calculateLightAtPoint(Vector3 position, Vector3 normal, boolean bakeStatics)
	{
		Vector3 light_agg_col = new Vector3(ambientLight.r, ambientLight.g, ambientLight.b);
		
		if (!bakeStatics) return light_agg_col;
		
		//System.out.println("Start light = "+light_agg_col);
		
		for (PointLight pl : staticPointLights)
		{
			Vector3 l_vector = pl.position.cpy();
			l_vector.sub(position);
			
			light_agg_col.add(calculateLight(l_vector, pl.getColourRGB(), pl.attenuation, pl.intensity, normal.cpy()));
		}
		
		//System.out.println("Final light = "+light_agg_col);
		return light_agg_col;
	}
	
	private Vector3 calculateLight(Vector3 l_vector, Vector3 l_colour, float l_attenuation, float l_intensity, Vector3 n_dir)
	{
		if (l_colour.len2() == 0) return new Vector3(0, 0, 0);

		float distance = l_vector.len();
		Vector3 l_dir = l_vector.nor();
		
		float attenuation = 1.0f / (l_attenuation * distance * distance);
		float intensity = attenuation * Math.max(0.0f, l_dir.dot(n_dir));
		
		return l_colour.mul(intensity * l_intensity);
	}
}
