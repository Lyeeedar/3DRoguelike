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

public class TextureAttribute extends MaterialAttribute {
	
	public final static int MAX_TEXTURE_UNITS = 16;
	static final public String diffuseTexture = "u_diffuse_texture";
	static final public String lightmapTexture = "u_lightmap_texture";
	static final public String specularTexture = "u_specular_texture";
	static final public String normalmapTexture = "u_normalmap_texture";

	public Texture dTexture;
	public Texture nmTexture;
	public Texture lTexture;
	
	public int startUnit;
	public int minFilter;
	public int magFilter;
	public int uWrap;
	public int vWrap;
	
	protected TextureAttribute()
	{
		
	}
	
	public TextureAttribute (Texture dTexture, Texture nmTexture, Texture lTexture, int unit, TextureFilter minFilter, TextureFilter magFilter,
			TextureWrap uWrap, TextureWrap vWrap) {
		this(dTexture, nmTexture, lTexture, unit,  minFilter.getGLEnum(), magFilter.getGLEnum(), uWrap.getGLEnum(), vWrap.getGLEnum());
	}

	public TextureAttribute (Texture dTexture, Texture nmTexture, Texture lTexture, int unit, int minFilter, int magFilter, int uWrap, int vWrap) {
		super("");
		name = diffuseTexture;
		if (nmTexture != null)
		{
			name += FLAG+"\n#define "+normalmapTexture;
		}
		if (lTexture != null)
		{
			name += FLAG+"\n#define "+lightmapTexture;
		}

		this.dTexture = dTexture;
		this.nmTexture = nmTexture;
		this.lTexture = lTexture;
		if (unit+3 > MAX_TEXTURE_UNITS) throw new RuntimeException(MAX_TEXTURE_UNITS + " is max texture units supported");
		this.startUnit = unit;
		this.uWrap = uWrap;
		this.vWrap = vWrap;
		this.minFilter = minFilter;
		this.magFilter = magFilter;
	}

	public TextureAttribute (Texture dTexture, Texture nmTexture, Texture lTexture, int unit) {
		this(dTexture, nmTexture, lTexture, unit, dTexture.getMinFilter(), dTexture.getMagFilter(), dTexture.getUWrap(), dTexture.getVWrap());
	}

	@Override
	public void bind (ShaderProgram program) {
		

		if (nmTexture != null)
		{
			nmTexture.bind(3);
		}
		
		dTexture.bind(0);
		
//		if (lTexture != null)
//		{
//			lTexture.bind(texIndex);
//			Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, minFilter);
//			Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, magFilter);
//			Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, uWrap);
//			Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, vWrap);
//			program.setUniformi(lightmapTexture, texIndex);
//			texIndex++;
//		}
		
		program.setUniformi(diffuseTexture, 0);
		program.setUniformi(normalmapTexture, 3);
		program.setUniformi(lightmapTexture, 2);
	}

	@Override
	public MaterialAttribute copy () {
		return new TextureAttribute(dTexture, nmTexture, lTexture, startUnit, minFilter, magFilter, uWrap, vWrap);
	}

	@Override
	public void set (MaterialAttribute attr) {
		TextureAttribute texAttr = (TextureAttribute)attr;
		name = texAttr.name;
		dTexture = texAttr.dTexture;
		nmTexture = texAttr.nmTexture;
		lTexture = texAttr.lTexture;
		startUnit = texAttr.startUnit;
		magFilter = texAttr.magFilter;
		minFilter = texAttr.minFilter;
		uWrap = texAttr.uWrap;
		vWrap = texAttr.vWrap;
	}

	/** this method check if the texture portion of texture attribute is equal, name isn't used */
	public boolean texturePortionEquals (TextureAttribute other) {
		if (other == null) return false;
		if (this == other) return true;
		if ((nmTexture == null) != (other.nmTexture == null)) return false;
		if ((lTexture == null) != (other.lTexture == null)) return false;

		return (dTexture == other.dTexture) && (startUnit == other.startUnit) && (minFilter == other.minFilter)
			&& (magFilter == other.magFilter) && (uWrap == other.uWrap) && (vWrap == other.vWrap);

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
//		if (dTexture != null) dTexture.dispose();
//		if (nmTexture != null) nmTexture.dispose();
//		if (lTexture != null) lTexture.dispose();
	}
}
