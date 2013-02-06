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
package com.lyeeedar.Roguelike3D.Graphics.Materials;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.ShaderHandler;

public class Material {
	protected String name;
	public Array<MaterialAttribute> attributes;
	
	public boolean affectedByLighting = true;

	/** This flag is true if material contain blendingAttribute */
	protected boolean needBlending;

	protected ShaderProgram shader;

	public ShaderProgram getShader () {
		return shader;
	}

	public Material () {
		attributes = new Array<MaterialAttribute>(2);
	}

	public Material (String name, Array<MaterialAttribute> attributes) {
		this.name = name;
		this.attributes = attributes;

		// this way we foresee if blending is needed with this material and rendering can deferred more easily
		this.needBlending = false;
		for (int i = 0; i < this.attributes.size; i++) {
			if (this.attributes.get(i) instanceof BlendingAttribute) {
				this.needBlending = true;
			}
		}
	}

	public Material (String name, MaterialAttribute... attributes) {
		this.name = name;
		this.attributes = new Array<MaterialAttribute>(attributes);

		// this way we foresee if blending is needed with this material and rendering can deferred more easily
		this.needBlending = false;
		for (int i = 0; i < this.attributes.size; i++) {
			if (this.attributes.get(i) instanceof BlendingAttribute) {
				this.needBlending = true;
			}
		}
	}
	
	public void addAttributes(MaterialAttribute... attributes)
	{
		for (int i = 0; i < attributes.length; i++)
		{
			this.attributes.add(attributes[i]);
		}
	}

	public void bind (ShaderProgram program) {
		for (int i = 0; i < attributes.size; i++) {
			attributes.get(i).bind(program);
		}
	}

	public String getName () {
		return name;
	}

	public Material copy () {
		Array<MaterialAttribute> attributes = new Array<MaterialAttribute>(this.attributes.size);
		for (int i = 0; i < attributes.size; i++) {
			attributes.add(this.attributes.get(i).copy());
		}
		final Material copy = new Material(name, attributes);
		copy.shader = this.shader;
		return copy;
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + attributes.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Material other = (Material)obj;
		if (other.affectedByLighting != this.affectedByLighting) return false;
		if (other.attributes.size != attributes.size) return false;
		for (int i = 0; i < attributes.size; i++) {
			if (!attributes.get(i).equals(other.attributes.get(i))) return false;
		}
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		return true;
	}

	public boolean shaderEquals (Material other) {
		if (this == other) return true;

		int len = this.attributes.size;
		if (len != other.attributes.size) return false;

		for (int i = 0; i < len; i++) {
			final String str = this.attributes.get(i).name;
			if (str == null) return false;

			boolean matchFound = false;
			for (int j = 0; j < len; j++) {
				if (str.equals(other.attributes.get(j).name)) {
					matchFound = true;
					break;
				}
			}
			if (!matchFound) return false;
		}

		return true;
	}

	public void setPooled (Material material) {
		name = material.name;
		shader = material.shader;
		needBlending = material.needBlending;
		attributes.clear();
		for (int i = 0, len = material.attributes.size; i < len; i++) {
			attributes.add(material.attributes.get(i).pooledCopy());
		}
	}

	public boolean isNeedBlending () {
		return needBlending;
	}

	public void resetShader () {
		shader = null;
	}

	public void generateShader (ShaderHandler materialShaderHandler) {
		shader = materialShaderHandler.getShader(this);
	}
	
	public void dispose()
	{
		shader.dispose();
		for (MaterialAttribute ma : attributes)
		{
			ma.dispose();
		}
	}
}
