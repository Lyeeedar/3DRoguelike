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
