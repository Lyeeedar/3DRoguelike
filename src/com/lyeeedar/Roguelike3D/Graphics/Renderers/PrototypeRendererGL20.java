
package com.lyeeedar.Roguelike3D.Graphics.Renderers;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.lyeeedar.Roguelike3D.Graphics.Lights.*;
import com.lyeeedar.Roguelike3D.Graphics.Materials.*;
import com.lyeeedar.Roguelike3D.Graphics.Models.*;
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
	private LightManager lightManager;
	private boolean drawing;
	public Camera cam;

	DrawableManager drawableManager = new DrawableManager();

	// TODO maybe there is better way
	public PrototypeRendererGL20 (LightManager lightManager) {
		this.lightManager = lightManager;
		shaderHandler = new ShaderHandler(lightManager);
	}

	@Override
	public void begin () {
		drawing = true;
		// all setting has to be done before this
		// example: camera updating or updating lights positions
	}

	@Override
	public void draw (StillModel model, StillModelAttributes attributes) {
		if (cam != null) if (!cam.frustum.sphereInFrustum(attributes.getSortCenter(), attributes.getBoundingSphereRadius())) return;
		//if (cam != null) if (cam.position.dst(attributes.getSortCenter()) > 150) return;
		drawableManager.add(model, attributes);
	}

	@Override
	public void end () {
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
			long light_hash = lightManager.getLightsHash();
			lightManager.calculateLights(center.x, center.y, center.z);
			boolean check = (light_hash == lightManager.getLightsHash());

			final Matrix3 normalMatrix = new Matrix3().set(drawable.rotation);
			final Matrix4 modelMatrix = drawable.transform.mul(drawable.rotation);

			final SubMesh subMeshes[] = drawable.model.getSubMeshes();

			boolean matrixChanged = true;
			for (int j = 0; j < subMeshes.length; j++) {

				final SubMesh subMesh = subMeshes[j];
				final Material material = drawable.materials.get(j);

				// bind new shader if material can't use old one
				final boolean shaderChanged = bindShader(material, check);

				if (shaderChanged || matrixChanged) {
					currentShader.setUniformMatrix("u_normal_matrix", normalMatrix);
					currentShader.setUniformMatrix("u_model_matrix", modelMatrix);
					matrixChanged = false;
				}

				for (int k = 0, len = material.attributes.size; k < len; k++) {
					final MaterialAttribute atrib = material.attributes.get(k);

					// special case for textures. really important to batch these
					if (atrib instanceof TextureAttribute) {
						final TextureAttribute texAtrib = (TextureAttribute)atrib;
						if (!texAtrib.texturePortionEquals(lastTexture[texAtrib.unit])) {
							lastTexture[texAtrib.unit] = texAtrib;
							texAtrib.bind(currentShader);
						} else {
							// need to be done, shader textureAtribute name could be changed.
							currentShader.setUniformi(texAtrib.name, texAtrib.unit);
						}
					} else {
						atrib.bind(currentShader);
					}
				}

				// finally render current submesh
				subMesh.getMesh().render(currentShader, subMesh.primitiveType);
			}
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

		lightManager.applyGlobalLights(currentShader);
		lightManager.applyLights(currentShader);
		currentShader.setUniformMatrix("u_pv", cam.combined);
		currentShader.setUniformf("u_cam_position", cam.position.x, cam.position.y, cam.position.z, 1.2f / cam.far);
		currentShader.setUniformf("u_cam_direction", cam.direction.x, cam.direction.y, cam.direction.z);
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
			long light_hash = lightManager.getLightsHash();
			lightManager.calculateLights(center.x, center.y, center.z);
			boolean check = (light_hash == lightManager.getLightsHash());

			final Matrix4 modelMatrix = drawable.transform.mul(drawable.rotation);
			final Matrix3 normalMatrix = new Matrix3().set(drawable.rotation);

			final SubMesh subMeshes[] = drawable.model.getSubMeshes();

			boolean matrixChanged = true;
			for (int j = 0; j < subMeshes.length; j++) {

				final SubMesh subMesh = subMeshes[j];
				final Material material = drawable.materials.get(j);

				// bind new shader if material can't use old one
				final boolean shaderChanged = bindShader(material, check);

				if (shaderChanged || matrixChanged) {
					currentShader.setUniformMatrix("u_normal_matrix", normalMatrix, false);
					currentShader.setUniformMatrix("u_model_matrix", modelMatrix, false);
					matrixChanged = false;
				}

				for (int k = 0, len = material.attributes.size; k < len; k++) {
					final MaterialAttribute atrib = material.attributes.get(k);

					// yet another attributesof. TODO is there any better way to do this? maybe stuffing this to material
					if (atrib instanceof BlendingAttribute) {
						final BlendingAttribute blending = (BlendingAttribute)atrib;
						if (blending.blendSrcFunc != lastSrcBlend || blending.blendDstFunc != lastDstBlend) {
							atrib.bind(currentShader);
							lastSrcBlend = blending.blendSrcFunc;
							lastDstBlend = blending.blendDstFunc;
						}
					} else if (atrib instanceof TextureAttribute) {
						// special case for textures. really important to batch these
						final TextureAttribute texAtrib = (TextureAttribute)atrib;
						if (!texAtrib.texturePortionEquals(lastTexture[texAtrib.unit])) {
							lastTexture[texAtrib.unit] = texAtrib;
							texAtrib.bind(currentShader);
						} else {
							// need to be done, shader textureAtribute name could be changed.
							currentShader.setUniformi(texAtrib.name, texAtrib.unit);
						}
					} else {
						atrib.bind(currentShader);
					}
				}
				// finally render current submesh
				subMesh.getMesh().render(currentShader, subMesh.primitiveType);
			}
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

		public void clear () {
			clear(drawables);
			clear(drawablesBlended);
		}

		private void clear (Array<Drawable> drawables) {
			while (drawables.size > 0) {
				final Drawable drawable = drawables.pop();

				// return all materials and attribuets to the pools
				while (drawable.materials.size > 0) {
					final Material material = drawable.materials.pop();

					while (material.attributes.size > 0) {
						material.attributes.pop().free();
					}
					material.resetShader();
					materialPool.free(material);
				}
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
			Model model;
			final Matrix4 transform = new Matrix4();
			final Matrix4 rotation = new Matrix4();
			final Vector3 sortCenter = new Vector3();
			final Array<Material> materials = new Array<Material>(2);
			boolean isAnimated;
			String animation;
			float animationTime;
			boolean isLooping;
			boolean blending;
			int distance;
			int firstShaderHash;
			int modelHash;

			public void set (StillModel model, StillModelAttributes attributes) {
				setCommon(model, attributes);
				isAnimated = false;
			}

			private void setCommon (Model model, StillModelAttributes attributes) {
				this.model = model;
				modelHash = model.hashCode();
				System.arraycopy(attributes.getTransform().val, 0, transform.val, 0, 16);
				System.arraycopy(attributes.getRotation().val, 0, rotation.val, 0, 16);

				sortCenter.set(attributes.getSortCenter());
				distance = (int)(PRIORITY_DISCRETE_STEPS * sortCenter.dst(cam.position));
				if (attributes.getMaterial() != null) {
					if (attributes.material.getShader() == null) attributes.material.generateShader(shaderHandler);

					final Material copy = materialPool.obtain();
					copy.setPooled(attributes.material);
					materials.add(copy);
				} else {
					System.err.println("Error! Attributes has no Material!");
				}
				blending = false;
				for (Material mat : materials) {
					if (mat.isNeedBlending()) {
						blending = true;
						break;
					}
				}
				if (materials.size > 0) firstShaderHash = materials.get(0).getShader().hashCode();

			}

			@Override
			public int compareTo (Drawable other) {
				return other.distance - this.distance;
			}
		}
	}

	public static final Comparator<Drawable> opaqueSorter = new Comparator<Drawable>() {

		public int compare (Drawable a, Drawable b) {
			if (a.firstShaderHash != b.firstShaderHash) return b.firstShaderHash - a.firstShaderHash;

			if (a.modelHash != b.modelHash) return b.modelHash - a.modelHash;

			return b.distance - a.distance;
		}

	};

}
