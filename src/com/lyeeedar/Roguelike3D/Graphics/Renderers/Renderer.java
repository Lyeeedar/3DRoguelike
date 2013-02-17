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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModel;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModelAttributes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModelInstance;
import com.lyeeedar.Roguelike3D.Graphics.Models.RiggedModels.RiggedSubMesh;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.Renderer.DrawableManager.Drawable;

public abstract class Renderer {
	
	public Camera cam;
	public boolean drawing;
	protected DrawableManager drawableManager = new DrawableManager();

	
	public void begin () {
		drawing = true;
	}


	public void draw (StillModel model, StillModelAttributes attributes) {
		if (cam != null) if (!cam.frustum.sphereInFrustum(attributes.getSortCenter(), attributes.getBoundingSphereRadius()*2)) return;
		drawableManager.add(model, attributes);
	}
	
	public void draw (RiggedSubMesh mesh, Matrix4 model_matrix, Material mat, float radius) {
		if (cam != null) if (!cam.frustum.sphereInFrustum(Vector3.tmp3.set(0, 0, 0).mul(model_matrix), radius*2)) return;
		drawableManager.add(mesh, model_matrix, mat);
	}
	
	public void end (LightManager lightManager) {
		flush(lightManager);
		drawing = false;
	}
	
	protected abstract void flush(LightManager lightManager);
	
	public void dispose()
	{
		disposeSuper();
	}
	protected abstract void disposeSuper();
	

	class DrawableManager {
		Pool<Drawable> drawablePool = new Pool<Drawable>() {
			@Override
			protected Drawable newObject () {
				return new Drawable();
			}
		};

		Array<Drawable> drawables = new Array<Drawable>();

		public void add (StillModel model, StillModelAttributes attributes) {
			Drawable drawable = drawablePool.obtain();
			drawable.set(model, attributes);

			drawables.add(drawable);
		}

		public void add(RiggedSubMesh mesh, Matrix4 model_matrix, Material mat) {
			Drawable drawable = drawablePool.obtain();
			drawable.set(mesh, model_matrix, mat);

			drawables.add(drawable);
		}

		public void clear () {
			clear(drawables);
		}

		private void clear (Array<Drawable> drawables) {
			while (drawables.size > 0) {
				final Drawable drawable = drawables.pop();
				drawablePool.free(drawable);
			}
		}

		class Drawable implements Comparable<Drawable> {
			private static final int PRIORITY_DISCRETE_STEPS = 256;
			Mesh mesh;
			final Matrix4 model_matrix = new Matrix4();
			final Vector3 sortCenter = new Vector3();
			Material material;

			int distance;
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
				} else {
					System.err.println("Error! Attributes has no Material!");
				}
				
				materialHash = material.hashCode();
			}

			@Override
			public int compareTo (Drawable other) {
				return other.distance - this.distance;
			}
		}
	}

	public static final Comparator<Drawable> sorter = new Comparator<Drawable>() {

		public int compare (Drawable a, Drawable b) {
			if (a.materialHash != b.materialHash) return b.materialHash - a.materialHash;
			return b.distance - a.distance;
		}

	};

}
