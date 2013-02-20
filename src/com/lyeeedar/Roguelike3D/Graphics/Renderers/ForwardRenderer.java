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

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Materials.ColorAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Materials.MaterialAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.TextureAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer.DrawableManager.Drawable;

public class ForwardRenderer extends Renderer {

	static final int SIZE = 256;

	public static ShaderProgram shader;

	public ForwardRenderer () {
		
	}
	
	@Override
	public void createShader(LightManager lights)
	{
		shader = ShaderFactory.createShader("forward_vertex", "LIGHTS_NUM "+lights.maxLightsPerModel, ColorAttribute.colour+"Flag", TextureAttribute.diffuseTexture+"Flag");
	}

	private TextureAttribute lastTexture;

	final Matrix3 normalMatrix = new Matrix3();
	@Override
	protected void flush (LightManager lightManager) {
		
		if (shader == null) return;
		
		if (lightManager == null) {}
		else if (GameData.player != null)
			lightManager.calculateDynamicLights(GameData.player.getPosition().x, GameData.player.getPosition().y, GameData.player.getPosition().z);
		else
			lightManager.calculateDynamicLights(0, 0, 0);
		
		shader.begin();
		shader.setUniformMatrix("u_pv", cam.combined);
		lightManager.applyDynamicLights(shader);

		drawableManager.drawables.sort(sorter);
		for (int i = drawableManager.drawables.size; --i >= 0;) {

			final Drawable drawable = drawableManager.drawables.get(i);

			final Matrix4 modelMatrix = drawable.model_matrix;
			normalMatrix.set(modelMatrix);//.toNormalMatrix();

			final Mesh mesh = drawable.mesh;
			final Material material = drawable.material;

			shader.setUniformMatrix("u_model_matrix", modelMatrix);
			if (lightManager.maxLightsPerModel > 0) shader.setUniformMatrix("u_normal_matrix", normalMatrix);

			lastTexture = material.bind(shader, lightManager, lastTexture);

			mesh.render(shader, drawable.primitiveType);
		}

		shader.end();
		lastTexture = null;

	}

	@Override
	protected void disposeSuper () {
		shader.dispose();
		shader = null;
	}

	@Override
	public void updateResolution() {
	}

}
