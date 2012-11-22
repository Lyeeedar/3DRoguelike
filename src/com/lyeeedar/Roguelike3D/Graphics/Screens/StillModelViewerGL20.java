/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.lyeeedar.Roguelike3D.Graphics.Screens;

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Graphics.Lights.DirectionalLight;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager;
import com.lyeeedar.Roguelike3D.Graphics.Lights.LightManager.LightQuality;
import com.lyeeedar.Roguelike3D.Graphics.Lights.PointLight;
import com.lyeeedar.Roguelike3D.Graphics.Materials.ColorAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.Material;
import com.lyeeedar.Roguelike3D.Graphics.Materials.MaterialAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Materials.TextureAttribute;
import com.lyeeedar.Roguelike3D.Graphics.Models.Shapes;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModel;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModelLoader;
import com.lyeeedar.Roguelike3D.Graphics.Models.StillModelAttributes;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;

public class StillModelViewerGL20 extends AbstractScreen {

	StillModel model;
	Texture[] textures = null;
	boolean hasNormals = false;
	BoundingBox bounds = new BoundingBox();
	String[] textureFileNames;
	FPSLogger fps = new FPSLogger();
	private LightManager lightManager;
	private StillModelAttributes instance;
	private StillModelAttributes instance2;
	ShaderProgram shader;
	
	public StillModelViewerGL20 (Roguelike3DGame game, String... textureFileNames) {
		super(game);
		this.textureFileNames = textureFileNames;
		System.out.println(textureFileNames.length);
	}

	public void create () {
		long start = System.nanoTime();
		model = StillModelLoader.createFromList(StillModelLoader.convertMeshtoSubMesh(Shapes.genCuboid(2, 2, 2), "Cube", GL20.GL_TRIANGLES));
		Gdx.app.log("StillModelViewer", "loading took: " + (System.nanoTime() - start) / 1000000000.0f);

		if (textureFileNames.length != 0) {
			textures = new Texture[textureFileNames.length];
			for (int i = 0; i < textureFileNames.length; i++) {
				textures[i] = new Texture(Gdx.files.internal(textureFileNames[i]));
			}
		}

		model.getBoundingBox(bounds);
		float len = bounds.getDimensions().len();
		System.out.println("bounds: " + bounds);

		cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(bounds.getCenter().cpy().add(len / 2, len / 2, len / 2));
		cam.lookAt(bounds.getCenter().x, bounds.getCenter().y, bounds.getCenter().z);
		cam.near = 0.1f;
		cam.far = 64;

		// shader1 = ShaderLoader.createShader("light", "light");
		// shader2 = ShaderLoader.createShader("vertexpath", "vertexpath");

		lightManager = new LightManager(4, LightQuality.VERTEX);
//		lightManager.dirLight = new DirectionalLight();
//		lightManager.dirLight.color.set(0.09f, 0.07f, 0.09f, 0);
//		lightManager.dirLight.direction.set(-.4f, -1, 0.03f).nor();

		for (int i = 0; i < 4; i++) {
			PointLight l = new PointLight();
			l.position.set(-MathUtils.random(8) + 4, MathUtils.random(3), -MathUtils.random(6) + 3);

			l.colour.r = MathUtils.random();
			l.colour.b = MathUtils.random();
			l.colour.g = MathUtils.random();
			l.intensity = 3;
			lightManager.addLight(l);
		}
		lightManager.ambientLight.set(0.3f, 0.5f, 0.6f, 1);

		protoRenderer = new PrototypeRendererGL20(lightManager);
		protoRenderer.cam = cam;

		MaterialAttribute c1 = new ColorAttribute(new Color(0.5f, 0.51f, 0.51f, 1.0f), ColorAttribute.specular);
		MaterialAttribute c2 = new ColorAttribute(new Color(0.95f, 0.95f, 0.95f, 1.0f), ColorAttribute.diffuse);
		MaterialAttribute t0 = new TextureAttribute(textures[0], 0, TextureAttribute.diffuseTexture);
		Material material = new Material("basic", c1, c2, t0);

		instance = new StillModelAttributes(material, 5);
		instance.getTransform().translate(-len / 2, -1, 2);
		instance.radius = bounds.getDimensions().len() / 2;

		instance2 = new StillModelAttributes(material, 5);
		instance2.getTransform().translate(len / 2, -1, -7);

		instance2.radius = instance.radius;

		
		shader = new ShaderProgram(
	    		Gdx.files.internal("data/shaders/model/basic_movement.vert").readString(),
	    		Gdx.files.internal("data/shaders/model/basic_movement.frag").readString());
	    if(!shader.isCompiled()) {
	    	Gdx.app.log("Problem loading shader:", shader.getLog());
	    }
	    
	    shader = new ShaderProgram(
	    		Gdx.files.internal("data/shaders/model/collision_box.vert").readString(),
	    		Gdx.files.internal("data/shaders/model/collision_box.frag").readString());
	    if(!shader.isCompiled()) {
	    	Gdx.app.log("Problem loading shader:", shader.getLog());
	    }
	}

	public final Matrix4 testmodel = new Matrix4();
	public final Matrix4 mvp = new Matrix4();
	public void draw (float delta) {

		instance.getTransform().rotate(0, 1, -0.1f, 35 * Gdx.graphics.getDeltaTime());
		instance2.getTransform().rotate(0, 1, 0.1f, -15 * Gdx.graphics.getDeltaTime());

		cam.update();

		protoRenderer.begin();
		protoRenderer.draw(model, instance);
		protoRenderer.draw(model, instance2);
		protoRenderer.end();
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);

		spriteBatch.begin();
		font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond(), 20, 30);
		font.draw(spriteBatch, "Rotation: " + cam.direction, 20, 60);
		spriteBatch.end();

		fps.log();
	}

	float xr = -800/720f;
	@Override
	public void update(float delta) {
		cam.update();
		
		float x = Gdx.input.getDeltaX()*xr;
		cam.rotate(x, 0, 1, 0);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
}
