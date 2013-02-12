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

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Lights.*;
import com.lyeeedar.Roguelike3D.Graphics.Materials.*;
import com.lyeeedar.Roguelike3D.Graphics.Models.*;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedSubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20.DrawableManager.Drawable;

//stuff that happens
//0. render begin
//1. frustum culling
//1.1 if animated, animation is solved and..
//1.2. all models and attributess are put to one queue
//3. render ends
//for all models
//5. batching involving shaders, materials and texture should happen.(impossible to do perfect.)
//4. closest lights are calculated per model
//6  models are rendered
//7. tranparency

public class PrototypeRendererGL20 implements ModelRenderer {

	static final int SIZE = 256;

	final ShaderHandler shaderHandler;
	public LightManager lightManager;
	private boolean drawing;
	public Camera cam;
	private boolean remakeShaders = false;
	
	public boolean glowRequired = false;

	DrawableManager drawableManager = new DrawableManager();

	// TODO maybe there is better way
	public PrototypeRendererGL20 (LightManager lightManager) {
		this.lightManager = lightManager;
		shaderHandler = new ShaderHandler(lightManager);
	}
	
	public void updateShader(LightManager lightManager)
	{
		this.lightManager = lightManager;
		shaderHandler.updateShader(lightManager);
		
		remakeShaders = true;
	}

	@Override
	public void begin () {
		glowRequired = false;
		drawing = true;
		// all setting has to be done before this
		// example: camera updating or updating lights positions
	}

	@Override
	public void draw (StillModel model, StillModelAttributes attributes) {
		if (cam != null) if (!cam.frustum.sphereInFrustum(attributes.getSortCenter(), attributes.getBoundingSphereRadius()*2)) return;
		
		if (remakeShaders) attributes.material.generateShader(shaderHandler);	
		drawableManager.add(model, attributes);
	}
	
	public void draw (RiggedSubMesh mesh, Matrix4 model_matrix, Material mat, float radius) {
		if (cam != null) if (!cam.frustum.sphereInFrustum(Vector3.tmp3.set(0, 0, 0).mul(model_matrix), radius*2)) return;
		
		if (remakeShaders) mat.generateShader(shaderHandler);	
		drawableManager.add(mesh, model_matrix, mat);
	}

	@Override
	public void end () {
		
		if (remakeShaders) remakeShaders = false;
		
		flush();
	}

	private ShaderProgram currentShader;
	final private TextureAttribute lastTexture[] = new TextureAttribute[TextureAttribute.MAX_TEXTURE_UNITS];

	private void flush () {

		// opaque is sorted front to back
		// transparent is sorted back to front
		drawableManager.drawables.sort(opaqueSorter);
		for (int i = drawableManager.drawables.size; --i >= 0;) {

			final Drawable drawable = drawableManager.drawables.get(i);

			final Vector3 center = drawable.sortCenter;
			long light_hash = lightManager.getDynamicLightsHash();
			//lightManager.calculateDynamicLights(center.x, center.y, center.z);
			if (GameData.player != null)
				lightManager.calculateDynamicLights(GameData.player.getPosition().x, GameData.player.getPosition().y, GameData.player.getPosition().z);
			else
				lightManager.calculateDynamicLights(center.x, center.y, center.z);
			boolean check = (light_hash == lightManager.getDynamicLightsHash());

			final Matrix3 normalMatrix = new Matrix3().set(drawable.model_matrix);
			final Matrix4 modelMatrix = drawable.model_matrix;

			final Mesh mesh = drawable.mesh;

			boolean matrixChanged = true;

			final Material material = drawable.material;

			// bind new shader if material can't use old one
			final boolean shaderChanged = bindShader(material, check);

			if (shaderChanged || matrixChanged) {
				currentShader.setUniformMatrix("u_normal_matrix", normalMatrix);
				currentShader.setUniformMatrix("u_model_matrix", modelMatrix);
				matrixChanged = false;
			}

			for (int k = 0, len = material.attributes.size(); k < len; k++) {
				final MaterialAttribute atrib = material.attributes.get(k);

				// special case for textures. really important to batch these
				if (atrib instanceof TextureAttribute) {
					final TextureAttribute texAtrib = (TextureAttribute)atrib;
					if (lastTexture[0] == null || !texAtrib.textureName.equals(lastTexture[0].textureName)) {
						lastTexture[0] = texAtrib;
						texAtrib.bind(currentShader);
					} else {
						// need to be done, shader textureAtribute name could be changed.
						texAtrib.bind(currentShader);
					}
				} else {
					atrib.bind(currentShader);
				}
			}

			// finally render current submesh
			mesh.render(currentShader, drawable.primitiveType);
		}

		// if transparent queue is not empty enable blending(this force gpu to
		// flush and there is some time to sort)
		if (drawableManager.drawablesBlended.size > 0) renderBlended();

		// cleaning

		if (currentShader != null) {
			currentShader.end();
			currentShader = null;
		}
		for (int i = 0, len = TextureAttribute.MAX_TEXTURE_UNITS; i < len; i++)
			lastTexture[i] = null;
		// clear all queues

		drawing = false;

		drawableManager.clear();
	}

	/** @param material
	 * @return true if new shader was binded */
	boolean bindShader (Material material, boolean check) {
		ShaderProgram shader = material.getShader();
		if (check && shader == currentShader) return false;

		currentShader = shader;
		currentShader.begin();

		lightManager.applyGlobalLights(currentShader, material);
		lightManager.applyDynamicLights(currentShader, material);
		
		currentShader.setUniformMatrix("u_pv", cam.combined);
		return true;
	}

	public void dispose () {
		shaderHandler.dispose();
	}

	private void renderBlended () {

		Gdx.gl.glEnable(GL20.GL_BLEND);
		final Array<Drawable> transparentDrawables = drawableManager.drawablesBlended;
		transparentDrawables.sort();

		// find N nearest lights per model
		// draw all models from opaque queue

		int lastSrcBlend = -1;
		int lastDstBlend = -1;

		for (int i = 0, size = transparentDrawables.size; i < size; i++) {

			final Drawable drawable = transparentDrawables.get(i);

			final Vector3 center = drawable.sortCenter;
			long light_hash = lightManager.getDynamicLightsHash();
			lightManager.calculateDynamicLights(center.x, center.y, center.z);
			boolean check = (light_hash == lightManager.getDynamicLightsHash());

			final Matrix4 modelMatrix = drawable.model_matrix;
			final Matrix3 normalMatrix = new Matrix3().set(drawable.model_matrix);

			final Mesh mesh = drawable.mesh;

			boolean matrixChanged = true;

			final Material material = drawable.material;

			// bind new shader if material can't use old one
			final boolean shaderChanged = bindShader(material, check);

			if (shaderChanged || matrixChanged) {
				currentShader.setUniformMatrix("u_normal_matrix", normalMatrix, false);
				currentShader.setUniformMatrix("u_model_matrix", modelMatrix, false);
				matrixChanged = false;
			}

			for (int k = 0, len = material.attributes.size(); k < len; k++) {
				final MaterialAttribute atrib = material.attributes.get(k);

				// yet another attributesof. TODO is there any better way to do this? maybe stuffing this to material
				if (atrib instanceof BlendingAttribute) {
					final BlendingAttribute blending = (BlendingAttribute)atrib;
					if (blending.blendSrcFunc != lastSrcBlend || blending.blendDstFunc != lastDstBlend) {
						atrib.bind(currentShader);
						lastSrcBlend = blending.blendSrcFunc;
						lastDstBlend = blending.blendDstFunc;
					}
				} 
				else if (atrib instanceof TextureAttribute) {
					// special case for textures. really important to batch these
					final TextureAttribute texAtrib = (TextureAttribute)atrib;
					if (!texAtrib.textureName.equals(lastTexture[0].textureName)) {
						lastTexture[0] = texAtrib;
						texAtrib.bind(currentShader);
					} else {
						// need to be done, shader textureAtribute name could be changed.
						currentShader.setUniformi(texAtrib.name, 0);
					}
					texAtrib.bind(currentShader);
				} 
				else {
					atrib.bind(currentShader);
				}
			}
			// finally render current submesh
			mesh.render(currentShader, drawable.primitiveType);
		}
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	class DrawableManager {
		Pool<Drawable> drawablePool = new Pool<Drawable>() {
			@Override
			protected Drawable newObject () {
				return new Drawable();
			}
		};
		Pool<Material> materialPool = new Pool<Material>() {
			@Override
			protected Material newObject () {
				return new Material();
			}
		};
		Array<Drawable> drawables = new Array<Drawable>();
		Array<Drawable> drawablesBlended = new Array<Drawable>();

		public void add (StillModel model, StillModelAttributes attributes) {
			Drawable drawable = drawablePool.obtain();
			drawable.set(model, attributes);

			if (drawable.blending)
				drawablesBlended.add(drawable);
			else
				drawables.add(drawable);
		}

		public void add(RiggedSubMesh mesh, Matrix4 model_matrix, Material mat) {
			Drawable drawable = drawablePool.obtain();
			drawable.set(mesh, model_matrix, mat);

			if (drawable.blending)
				drawablesBlended.add(drawable);
			else
				drawables.add(drawable);
		}

		public void clear () {
			clear(drawables);
			clear(drawablesBlended);
		}

		private void clear (Array<Drawable> drawables) {
			while (drawables.size > 0) {
				final Drawable drawable = drawables.pop();

				//drawable.material.resetShader();
				
				// reset the drawable and return it to the drawable pool
				drawablePool.free(drawable);
			}
		}

		/** A drawable is a copy of the state of the model and attributes passed to either
		 * {@link PrototypeRendererGL20#draw(AnimatedModel, AnimatedModelattributes)} or
		 * {@link PrototypeRendererGL20#draw(StillModel, StillModelattributes)}. It is used in {@link PrototypeRendererGL20#flush()}
		 * to do material and depth sorting for blending without having to deal with the API client changing any attributes of a
		 * model or attributes in between draw calls.
		 * @author mzechner */
		class Drawable implements Comparable<Drawable> {
			private static final int PRIORITY_DISCRETE_STEPS = 256;
			Mesh mesh;
			final Matrix4 model_matrix = new Matrix4();
			final Vector3 sortCenter = new Vector3();
			Material material;

			boolean blending;
			int distance;
			int shaderHash;
			int materialHash;
			
			int primitiveType;

			public void set (RiggedSubMesh mesh, Matrix4 model_matrix, Material mat)
			{
				setCommon(mesh.getMesh(), mesh.primitiveType, model_matrix, mat);
			}
			
			public void set (StillModel model, StillModelAttributes attributes) {
				model_matrix.set(attributes.getTransform()).scale(attributes.scale, attributes.scale, attributes.scale).mul(attributes.rotation);
				setCommon(model.subMeshes[0].mesh, model.subMeshes[0].primitiveType, model_matrix, attributes.material);

			}

			private void setCommon (Mesh mesh, int primitiveType, Matrix4 model_matrix, Material mat) {
				
				this.mesh = mesh;
				this.model_matrix.set(model_matrix);
				this.material = mat;
				this.primitiveType = primitiveType;
				
				sortCenter.set(0, 0, 0).mul(model_matrix);
				
				distance = (int)(PRIORITY_DISCRETE_STEPS * sortCenter.dst(cam.position));
				if (material != null) {
					if (material.getShader() == null) material.generateShader(shaderHandler);
				} else {
					System.err.println("Error! Attributes has no Material!");
				}
				
				blending = material.isNeedBlending();

				shaderHash = material.getShader().hashCode();
				materialHash = material.hashCode();
			}

			@Override
			public int compareTo (Drawable other) {
				return other.distance - this.distance;
			}
		}
	}

	public static final Comparator<Drawable> opaqueSorter = new Comparator<Drawable>() {

		public int compare (Drawable a, Drawable b) {
			if (a.shaderHash != b.shaderHash) return b.shaderHash - a.shaderHash;
			if (a.materialHash != b.materialHash) return b.materialHash - a.materialHash;

			return b.distance - a.distance;
		}

	};

}
