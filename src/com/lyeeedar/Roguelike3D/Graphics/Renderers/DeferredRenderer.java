package com.lyeeedar.Roguelike3D.Graphics.Renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Materials.TextureAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer.DrawableManager.Drawable;

public class DeferredRenderer extends Renderer {
	
	public static int BUFFER = 0;
	
	static ShaderProgram normalShader;
	static ShaderProgram normalmapShader;
	static ShaderProgram lightShader;
	static ShaderProgram finalShader;
	static ShaderProgram depthonlyShader;
	static ShaderProgram normalonlyShader;
	
	ShaderProgram currentShader;
	
	static FrameBuffer normalBuffer;
	static FrameBuffer lightBuffer;
	
	int[] resolution;
	
	SpriteBatch sB = new SpriteBatch();

	public DeferredRenderer() {
		super();
	}

	final Matrix3 normalMatrix = new Matrix3();
	final Matrix4 view = new Matrix4();
	@Override
	protected void flush(LightManager lightManager) {

		normalBuffer.begin();
		
		Gdx.graphics.getGL20().glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		drawableManager.drawables.sort(sorter);
		for (int i = drawableManager.drawables.size; --i >= 0;) {

			final Drawable drawable = drawableManager.drawables.get(i);

			final Matrix4 modelMatrix = drawable.model_matrix;
			normalMatrix.set(modelMatrix);

			final Mesh mesh = drawable.mesh;
			final Material material = drawable.material;
			
			if (material.normalmapAttribute.texture != null)
			{
				changeShader(normalmapShader);
			}
			else
			{
				changeShader(normalShader);
			}
			
			currentShader.setUniformMatrix("u_model_matrix", modelMatrix);
			currentShader.setUniformMatrix("u_normal_matrix", normalMatrix);

			material.normalmapAttribute.bind(currentShader, lightManager);
			
			mesh.render(currentShader, drawable.primitiveType);
			
		}
		currentShader.end();
		normalBuffer.end();
		currentShader = null;
		
		lightBuffer.begin();
		
		if (BUFFER == 1)
		{
			Gdx.graphics.getGL20().glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
			Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
		}
		else
		{
			Gdx.graphics.getGL20().glClearColor(lightManager.getAmbient().r/5f, lightManager.getAmbient().g/5f, lightManager.getAmbient().b/5f, 0.0f);
			Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
			Gdx.graphics.getGL20().glEnable(GL20.GL_BLEND);
			Gdx.graphics.getGL20().glBlendFunc(GL20.GL_ONE, GL20.GL_ONE);
			//Gdx.graphics.getGL20().glDisable(GL20.GL_CULL_FACE);
			Gdx.graphics.getGL20().glDisable(GL20.GL_DEPTH_TEST);
			
			changeShader(lightShader);
			normalBuffer.getColorBufferTexture().bind(0);
			currentShader.setUniformi("u_normals", 0);
			//currentShader.setUniformMatrix("u_inv_pv", cam.invProjectionView);
			currentShader.setUniformf("u_screen", resolution[0], resolution[1]);
			currentShader.setUniformf("u_cam", cam.position);
			
			view.set(cam.view).inv();
			currentShader.setUniformMatrix("u_inv_v", view);
			
			for (PointLight p : lightManager.staticPointLights)
			{
				if (!cam.frustum.sphereInFrustum(p.position, p.radius)) continue;
				
				p.bind(currentShader);
				p.area.render(currentShader, GL20.GL_TRIANGLES);
			}
			for (PointLight p : lightManager.dynamicPointLights)
			{
				if (!cam.frustum.sphereInFrustum(p.position, p.radius)) continue;
				
				p.bind(currentShader);
				p.area.render(currentShader, GL20.GL_TRIANGLES);
			}
			
			currentShader.end();
		}
		lightBuffer.end();
		currentShader = null;
		
		Gdx.graphics.getGL20().glDisable(GL20.GL_BLEND);
		Gdx.graphics.getGL20().glEnable(GL20.GL_CULL_FACE);
		Gdx.graphics.getGL20().glCullFace(GL20.GL_BACK);
		
		Gdx.graphics.getGL20().glEnable(GL20.GL_DEPTH_TEST);
		Gdx.graphics.getGL20().glDepthMask(true);
		
		changeShader(finalShader);
		
		lightBuffer.getColorBufferTexture().bind(1);
		currentShader.setUniformi("u_light_texture", 1);
		
		currentShader.setUniformf("u_screen", resolution[0], resolution[1]);
		
		for (int i = drawableManager.drawables.size; --i >= 0;) {

			final Drawable drawable = drawableManager.drawables.get(i);

			final Matrix4 modelMatrix = drawable.model_matrix;

			final Mesh mesh = drawable.mesh;
			final Material material = drawable.material;

			currentShader.setUniformMatrix("u_model_matrix", modelMatrix);
			
			material.colourAttribute.bind(currentShader, lightManager);
			material.textureAttribute.bind(currentShader, lightManager);
			
			mesh.render(currentShader, drawable.primitiveType);
		}
		
		currentShader.end();
		currentShader = null;
		
		Gdx.graphics.getGL20().glDisable(GL20.GL_CULL_FACE);
		Gdx.graphics.getGL20().glDisable(GL20.GL_DEPTH_TEST);
		
		//sB.enableBlending();
		//sB.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_COLOR);
		if (BUFFER == 2) {
			sB.disableBlending();
			sB.setShader(normalonlyShader);
			sB.begin();
			normalonlyShader.setUniformMatrix("u_inv_v", view);
			sB.draw(normalBuffer.getColorBufferTexture(), 0, 0, resolution[0], resolution[1], 0, 0, resolution[0], resolution[1], false, true);
			sB.end();
		}
		else if (BUFFER == 3) {
			sB.disableBlending();
			sB.setShader(depthonlyShader);
			sB.begin();
			sB.draw(normalBuffer.getColorBufferTexture(), 0, 0, resolution[0], resolution[1], 0, 0, resolution[0], resolution[1], false, true);
			sB.end();
			sB.setShader(null);
		}
		else if (BUFFER == 4) {
			sB.begin();
			sB.draw(lightBuffer.getColorBufferTexture(), 0, 0, resolution[0], resolution[1], 0, 0, resolution[0], resolution[1], false, true);
			sB.end();
		}
	}
	
	private void changeShader(ShaderProgram newShader)
	{
		if (currentShader != null && currentShader.equals(newShader)) return;
		if (currentShader != null) currentShader.end();
		
		currentShader = newShader;
		
		currentShader.begin();
		currentShader.setUniformMatrix("u_pv", cam.combined);
		currentShader.setUniformMatrix("u_v", cam.view);
		currentShader.setUniformf("u_cam", cam.position);
		currentShader.setUniformf("u_linearDepth", cam.far-cam.near);
	}

	@Override
	protected void disposeSuper() {
	}

	@Override
	public void createShader(LightManager lights) {
		if (normalShader == null) normalShader = ShaderFactory.createShader("deferred_normals");
		if (normalmapShader == null) normalmapShader = ShaderFactory.createShader("deferred_normals", TextureAttribute.normalmapTexture+"Flag");
		if (lightShader == null) lightShader = ShaderFactory.createShader("deferred_lighting");
		if (finalShader == null) finalShader = ShaderFactory.createShader("deferred_finalise");
		if (depthonlyShader == null) depthonlyShader = ShaderFactory.createShader("depth_only");
		if (normalonlyShader == null) normalonlyShader = ShaderFactory.createShader("normal_only");
	}

	@Override
	public void updateResolution() {
		if (resolution == null) resolution = new int[]{0, 0};
		if (GameData.resolution[0] != resolution[0] && GameData.resolution[1] != resolution[1])
		{
			resolution[0] = GameData.resolution[0];
			resolution[1] = GameData.resolution[1];
			
			if (normalBuffer != null) normalBuffer.dispose();
			normalBuffer = new FrameBuffer(Format.RGBA8888, resolution[0], resolution[1], true);
			
			if (lightBuffer != null) lightBuffer.dispose();
			lightBuffer = new FrameBuffer(Format.RGB888, resolution[0], resolution[1], false);
		}
	}

}
