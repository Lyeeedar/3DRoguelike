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

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;

public class ShaderHandler {
	
	private LightManager lightManager;

	public ShaderHandler (LightManager lightManager) {
		this.lightManager = lightManager;
	}

	private final Array<Material> materialsWithShader = new Array<Material>(false, 64);

	public ShaderProgram getShader (Material material) {
		for (int i = 0; i < materialsWithShader.size; i++) {
			if (material.shaderEquals(materialsWithShader.get(i))) {
				return materialsWithShader.get(i).getShader();
			}
		}

		System.out.println("Adding shader");
		materialsWithShader.add(material);
		return ShaderFactory.createShader(material, lightManager);
	}
	
	public void updateShader(LightManager lightManager)
	{
		this.lightManager = lightManager;
		
		materialsWithShader.clear();
	}

	public void dispose () {
		for (int i = 0; i < materialsWithShader.size; i++) {
			if (materialsWithShader.get(i).getShader() != null) {
				materialsWithShader.get(i).getShader().dispose();
				materialsWithShader.get(i).resetShader();
			}
		}
	}
}
