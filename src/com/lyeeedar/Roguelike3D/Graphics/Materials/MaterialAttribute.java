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
package com.lyeeedar.Roguelike3D.Graphics.Materials;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public abstract class MaterialAttribute {
	protected static final String FLAG = "Flag";
	public String name;
	protected final boolean isPooled;

	protected MaterialAttribute () {
		isPooled = true;
	}

	public MaterialAttribute (String name) {
		this.name = name;
		isPooled = false;
	}

	public abstract void bind (ShaderProgram program);

	public abstract MaterialAttribute copy ();

	public abstract MaterialAttribute pooledCopy ();

	public abstract void free ();

	public abstract void set (MaterialAttribute attr);

	public String getShaderFlag () {
		return name + FLAG;
	}
	
	public abstract void dispose();
}
