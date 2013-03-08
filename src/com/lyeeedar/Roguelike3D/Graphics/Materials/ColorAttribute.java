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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Pool;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;

public class ColorAttribute extends MaterialAttribute {

	private static final long serialVersionUID = 8802901657668185338L;
	static final public String colour = "u_colour";

	public final Color color;

	protected ColorAttribute () {
		color = new Color();
	}

	/** Creates a {@link MaterialAttribute} that is a pure {@link Color}.
	 * 
	 * @param color The {@link Colour} that you wish the attribute to represent.
	 * @param name The name of the uniform in the {@link ShaderProgram} that will have its value set to this color. (A 'name' does
	 *           not matter for a game that uses {@link GL10}). */
	public ColorAttribute (Color color, String name) {
		super(name);
		this.color = color.cpy();
	}

	@Override
	public void bind (ShaderProgram program, LightManager lights) {
		program.setUniformf(name, color.r, color.g, color.b);
	}

	@Override
	public MaterialAttribute copy () {
		return new ColorAttribute(color, name);
	}

	@Override
	public void set (MaterialAttribute attr) {
		ColorAttribute colAttr = (ColorAttribute)attr;
		name = colAttr.name;
		final Color c = colAttr.color;
		color.r = c.r;
		color.g = c.g;
		color.b = c.b;
		color.a = c.a;
	}

	private final static Pool<ColorAttribute> pool = new Pool<ColorAttribute>() {
		@Override
		protected ColorAttribute newObject () {
			return new ColorAttribute();
		}
	};

	@Override
	public MaterialAttribute pooledCopy () {
		ColorAttribute attr = pool.obtain();
		attr.set(this);
		return attr;
	}

	@Override
	public void free () {
		if (isPooled) pool.free(this);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void create() {
	}
}
