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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Pool;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Utils.FileUtils;

public class TextureAttribute extends MaterialAttribute {
	
	private static final long serialVersionUID = 3095829882861332616L;
	public final static int MAX_TEXTURE_UNITS = 16;
	static final public String diffuseTexture = "u_diffuse_texture";
	static final public String normalmapTexture = "u_normalmap_texture";

	public transient Texture texture;
	
	public int unit;
	public TextureFilter minFilter;
	public TextureFilter magFilter;
	public TextureWrap uWrap;
	public TextureWrap vWrap;
	
	public String textureName;

	@Override
	public int hashCode()
	{
		return texture.hashCode();
	}
	
	public TextureAttribute (String textureName, int unit, TextureFilter minFilter, TextureFilter magFilter, TextureWrap uWrap, TextureWrap vWrap, String textureType) {
		this.textureName = textureName;

		if (unit+3 > MAX_TEXTURE_UNITS) throw new RuntimeException(MAX_TEXTURE_UNITS + " is max texture units supported");
		this.unit = unit;
		this.uWrap = uWrap;
		this.vWrap = vWrap;
		this.minFilter = minFilter;
		this.magFilter = magFilter;
		
		name = textureType;
		
	}

	private void loadTextures() {
		
		texture = FileUtils.loadTexture(textureName, false);
		
		if (texture != null) {
			texture.setWrap( uWrap, vWrap );
			texture.setFilter( minFilter, magFilter );
		}
	}

	public TextureAttribute (String textureName, int unit, String textureType) {
		this(textureName, unit, TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear, TextureWrap.Repeat, TextureWrap.Repeat, textureType);
	}

	public TextureAttribute() {
	}

	@Override
	public void bind (ShaderProgram program, LightManager lights) {
		if (texture == null) return;
		texture.bind(unit);
		program.setUniformi(diffuseTexture, unit);
	}

	@Override
	public MaterialAttribute copy () {
		return new TextureAttribute(textureName, unit, minFilter, magFilter, uWrap, vWrap, name);
	}

	@Override
	public void set (MaterialAttribute attr) {
		TextureAttribute texAttr = (TextureAttribute)attr;
		name = texAttr.name;
		texture = texAttr.texture;
		unit = texAttr.unit;
		magFilter = texAttr.magFilter;
		minFilter = texAttr.minFilter;
		uWrap = texAttr.uWrap;
		vWrap = texAttr.vWrap;
	}

	private final static Pool<TextureAttribute> pool = new Pool<TextureAttribute>() {
		@Override
		protected TextureAttribute newObject () {
			return new TextureAttribute();
		}
	};

	@Override
	public MaterialAttribute pooledCopy () {
		TextureAttribute attr = pool.obtain();
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
		loadTextures();
	}
}
