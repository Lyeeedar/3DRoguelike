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
package com.lyeeedar.Roguelike3D.Graphics.PostProcessing.Effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class GlowEffect extends PostProcessingEffect {
	
	public GlowEffect() {
		create();
	}

	@Override
	public void create() {
		shader = new ShaderProgram(
				Gdx.files.internal("data/shaders/postprocessing/glow.vertex.glsl"),
				Gdx.files.internal("data/shaders/postprocessing/glow.fragment.glsl")
				);
		if (!shader.isCompiled()) Gdx.app.log("Problem loading shader:", shader.getLog());
	}

	@Override
	public void bindUniforms() {
		// TODO Auto-generated method stub
		
	}

}
