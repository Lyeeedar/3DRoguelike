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
package com.lyeeedar.Roguelike3D.Graphics.Lights;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Utils.Bag;

public class LightManager implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1441486882161209270L;

	public static final int maxLights = 10;

	public enum LightQuality {
		FORWARD_VERTEX, DEFERRED
	};
	
	public static LightQuality getLightQuality(String quality)
	{
		for (LightQuality lq : LightQuality.values())
		{
			if (quality.equalsIgnoreCase(""+lq)) return lq;
		}
		
		return null;
	}

	public LightQuality quality;

	public final Bag<PointLight> dynamicPointLights = new Bag<PointLight>(maxLights);
	public final Bag<PointLight> staticPointLights = new Bag<PointLight>(maxLights);
	private transient float[] positions;
	private transient float[] colors;
	private transient float[] attenuations;
	private transient float[] powers;
	private transient int i;

	public int maxLightsPerModel;
	public final Color ambientLight = new Color();
	public final Vector3 ambientDir = new Vector3();

	public LightManager (int maxLightsPerModel, LightQuality lightQuality) {
		quality = lightQuality;
		this.maxLightsPerModel = maxLightsPerModel;
		
		fixReferences();
	}
	
	public void setAmbient(float r, float g, float b, float x, float y, float z)
	{
		ambientLight.set(r, g, b, 1.0f);
		ambientDir.set(x, y, z);
	}
	
	public void setAmbient(Color colour, Vector3 dir)
	{
		ambientLight.set(colour);
		ambientDir.set(dir);
	}
	
	public void fixReferences()
	{
		if (maxLightsPerModel == 0) return;
		
		colors = new float[3 * maxLightsPerModel];
		positions = new float[3 * maxLightsPerModel];
		attenuations = new float[maxLightsPerModel];
		powers = new float[maxLightsPerModel];
		
		for (PointLight p : staticPointLights) p.fixReferences();
		for (PointLight p : dynamicPointLights) p.fixReferences();
	}
	
	public PointLight getDynamicLight(String UID)
	{
		for (PointLight p : dynamicPointLights)
		{
			if (p.UID.equals(UID)) return p;
		}
		
		System.err.println("Light not found!");
		return null;
	}
	
	public PointLight getStaticLight(String UID)
	{
		for (PointLight p : staticPointLights)
		{
			if (p.UID.equals(UID)) return p;
		}
		
		System.err.println("Light not found!");
		return null;
	}

	public void addDynamicLight (PointLight light) {
		dynamicPointLights.add(light);
	}
	
	public void addStaticLight (PointLight light) {
		staticPointLights.add(light);
	}
	
	public void removeDynamicLight(String UID)
	{
		for (int i = 0; i < dynamicPointLights.size; i++)
		{
			if (dynamicPointLights.get(i).UID.equals(UID))
			{
				dynamicPointLights.remove(i);
				return;
			}
		}
	}
	
	public void removeStaticLight(String UID) {
		for (int i = 0; i < staticPointLights.size; i++)
		{
			if (staticPointLights.get(i).UID.equals(UID))
			{
				staticPointLights.remove(i);
				return;
			}
		}
	}

	public void clearAllLights () {
		dynamicPointLights.clear();
		staticPointLights.clear();
	}
	
	public void updateLightNum(int val)
	{
		if (val < 1) val = 1;
		
		maxLightsPerModel = val;
		
		colors = new float[3 * maxLightsPerModel];
		positions = new float[3 * maxLightsPerModel];
		attenuations = new float[maxLightsPerModel];
		powers = new float[maxLightsPerModel];
	}
	
	public void calculateDynamicLights (float x, float y, float z) {
		if (maxLightsPerModel == 0) return;
		
		final int maxSize = dynamicPointLights.size;
		
		if (maxSize > maxLightsPerModel) {

			for (i = 0; i < maxSize; i++) {
				final PointLight light = dynamicPointLights.get(i);
				light.priority = (int)(PointLight.PRIORITY_DISCRETE_STEPS * light.position.dst(x, y, z));
			}
			dynamicPointLights.sort();
		}

		// fill the light arrays
		final int size = maxLightsPerModel > maxSize ? maxSize : maxLightsPerModel;
		for (i = 0; i < size; i++) {
			final PointLight light = dynamicPointLights.get(i);
			final Vector3 pos = light.position;

			positions[3 * i + 0] = pos.x;
			positions[3 * i + 1] = pos.y;
			positions[3 * i + 2] = pos.z;

			final Color col = light.getColour();
			colors[3 * i + 0] = col.r;
			colors[3 * i + 1] = col.g;
			colors[3 * i + 2] = col.b;

			attenuations[i] = light.attenuation;
			powers[i] = light.power;
		}
	}
	
	public void applyDynamicLights (ShaderProgram shader) {
		if (maxLightsPerModel == 0) return;
		shader.setUniform3fv("u_light_positions", positions, 0, maxLightsPerModel * 3);
		shader.setUniform3fv("u_light_colours", colors, 0, maxLightsPerModel * 3);
		shader.setUniform1fv("u_light_attenuations", attenuations, 0, maxLightsPerModel);
		shader.setUniform1fv("u_light_powers", powers, 0, maxLightsPerModel);
	}
	
	public void applyAmbient(ShaderProgram shader)
	{
		shader.setUniformf("u_colour", ambientLight.r, ambientLight.g, ambientLight.b);
	}
	
	public Color calculateLightAtPoint(Vector3 position, Vector3 normal, boolean bakeStatics)
	{
		Color h_ambient = ambientLight.cpy().mul(0.5f);
		Color light_agg_col = h_ambient.add(calculateLight(ambientDir, h_ambient.cpy(), 0, 1, normal));
		
		if (!bakeStatics) return light_agg_col;
		
		for (PointLight pl : staticPointLights)
		{
			Vector3 l_vector = pl.position.cpy().sub(position);

			light_agg_col.add(calculateLight(l_vector, pl.getColour().cpy(), pl.attenuation, pl.power, normal));

		}
		return light_agg_col;
	}
	
	private Color calculateLight(Vector3 l_vector, Color l_colour, float l_attenuation, float l_power, Vector3 n_dir)
	{
		float distance = l_vector.len();
	    Vector3 l_dir = l_vector.cpy().div(distance);

	    float NdotL = n_dir.dot(l_dir);
	    float intensity = MathUtils.clamp( NdotL, 0.0f, 1.0f );
	    
	    float attenuation = 1.0f;
	    if (l_attenuation != 0) attenuation /= (l_attenuation*distance + l_attenuation/10*distance*distance);
	    
	   	return l_colour.mul(intensity).mul(l_power).mul(attenuation);
	}

	public Color getAmbient() {
		return ambientLight;
	}
}