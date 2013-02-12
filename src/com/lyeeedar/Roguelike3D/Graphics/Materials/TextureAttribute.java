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

public class TextureAttribute extends MaterialAttribute {
	
	private static final long serialVersionUID = 3095829882861332616L;
	public final static int MAX_TEXTURE_UNITS = 16;
	static final public String diffuseTexture = "u_diffuse_texture";
	static final public String lightmapTexture = "u_lightmap_texture";
	static final public String specularTexture = "u_specular_texture";
	static final public String normalmapTexture = "u_normalmap_texture";

	public transient Texture dTexture;
	public transient Texture nmTexture;
	public transient Texture lTexture;
	
	public int startUnit;
	public TextureFilter minFilter;
	public TextureFilter magFilter;
	public TextureWrap uWrap;
	public TextureWrap vWrap;
	
	public String textureName;

	@Override
	public int hashCode()
	{
		return dTexture.hashCode();
	}
	
	public TextureAttribute (String textureName, int unit, TextureFilter minFilter, TextureFilter magFilter, TextureWrap uWrap, TextureWrap vWrap) {
		this.textureName = textureName;

		if (unit+3 > MAX_TEXTURE_UNITS) throw new RuntimeException(MAX_TEXTURE_UNITS + " is max texture units supported");
		this.startUnit = unit;
		this.uWrap = uWrap;
		this.vWrap = vWrap;
		this.minFilter = minFilter;
		this.magFilter = magFilter;
	}

	private void loadTextures() {
		
		dTexture = GameData.loadTexture(textureName);
		dTexture.setWrap( uWrap, vWrap );
		dTexture.setFilter( minFilter, magFilter );
		
		nmTexture = GameData.loadTexture(textureName+".map");
		if (nmTexture != null)
		{
			nmTexture.setWrap( uWrap, vWrap );
			nmTexture.setFilter( minFilter, magFilter );
			System.out.println("Normal map found for "+textureName);
		}
		
		name = diffuseTexture;
		if (nmTexture != null)
		{
			name += FLAG+"\n#define "+normalmapTexture;
		}
		if (lTexture != null)
		{
			name += FLAG+"\n#define "+lightmapTexture;
		}
	}

	public TextureAttribute (String textureName, int unit) {
		this(textureName, unit, TextureFilter.MipMapLinearLinear, TextureFilter.MipMapLinearLinear, TextureWrap.Repeat, TextureWrap.Repeat);
	}

	public TextureAttribute() {
	}

	@Override
	public void bind (ShaderProgram program) {
		

		if (nmTexture != null)
		{
			nmTexture.bind(3);
		}
		
		dTexture.bind(0);
		
		program.setUniformi(diffuseTexture, 0);
		program.setUniformi(normalmapTexture, 3);
		program.setUniformi(lightmapTexture, 2);
	}

	@Override
	public MaterialAttribute copy () {
		return new TextureAttribute(textureName, startUnit, minFilter, magFilter, uWrap, vWrap);
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
	}

	@Override
	public void create() {
		loadTextures();
	}
}
