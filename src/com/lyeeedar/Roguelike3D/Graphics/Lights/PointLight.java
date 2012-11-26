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
import com.badlogic.gdx.math.Vector3;

public class PointLight implements Comparable {
	
	final public String UID;

	final public Vector3 position;
	final public Color colour;

	public float attenuation;
	public float intensity;

	protected int priority;

	static final int PRIORITY_DISCRETE_STEPS = 256;
	
	private final Vector3 tmpVec = new Vector3();
	
	public PointLight()
	{
		this(new Vector3(), new Color(), 1.0f, 1.0f);
	}
	
	public PointLight(Vector3 position, Color colour, float attentuation, float intensity)
	{
		UID = this.toString()+this.hashCode()+System.currentTimeMillis()+System.nanoTime();
		this.position = position;
		this.colour = colour;
		this.intensity = intensity;
		this.attenuation = attentuation;
	}
	
	public void positionAbsolutely(float x, float y, float z)
	{
		positionAbsolutely(tmpVec.set(x, y, z));
	}
	
	public void positionAbsolutely(Vector3 pos)
	{
		position.set(pos);
	}
	
	public void transform(float x, float y, float z)
	{
		transform(tmpVec.set(x, y, z));
	}
	
	public void transform(Vector3 amount)
	{
		position.add(amount);
	}

	@Override
	public int compareTo (Object other) {
		return this.priority - ((PointLight)other).priority;
	}

}
