package com.lyeeedar.Roguelike3D.Graphics;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.loaders.obj.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Player;

public class LibGDXSplashScreen extends AbstractScreen {
	
	public LibGDXSplashScreen(Roguelike3DGame game)
	{
		super(game);
	}

	@Override
	void draw(float delta) {
		
		/** Calculate Matrix's for use in shaders **/
		
		for (int i = 0; i < objects.size(); i++) {
			
			GameObject go = objects.get(i);
			ShaderProgram shader = shaders.get(0);
			
			// Model matrix - The position of the object in 3D space comparative to the origin
			Matrix4 model = new Matrix4();
			model.setToTranslation(go.getPosition());
			
			// Rotation matrix - The rotation of the object
			Matrix4 axis = new Matrix4();
			axis.setToRotation(go.getRotation().x, go.getRotation().y, go.getRotation().z, 180);
			
			// View matrix - The position and direction of the 'camera'. In this case, the player.
			Matrix4 view = new Matrix4();
			view.setToLookAt(new Vector3(0, 0, 0), new Vector3(0, 0, 0).add(new Vector3(0, 0, -1)), new Vector3(0, 1, 0));
			// Projection matrix - The camera details, i.e. the fov, the view distance and the screen size
			Matrix4 projection = new Matrix4();
			projection.setToProjection(0.1f, 500.0f, 60.0f,
					(float) screen_width / (float) screen_height);
			// Model-View-Projection matrix - The matrix used to transform the objects mesh coordinates to get them onto the screen
			Matrix4 mvp = projection.mul(view).mul(model).mul(axis);
			
			shader.begin();
			go.vo.texture.bind();
			shader.setUniformMatrix("u_mvp", mvp);
			shader.setUniformf("u_colour", new Vector3(go.vo.colour));
			shader.setUniformf("u_ambient", new Vector3(1, 1, 1));
			go.vo.mesh.render(shader, GL20.GL_TRIANGLES);
			shader.end();
		}

	}


	public void update(float delta)
	{
		float xrotate = 800f/720f;
		objects.get(0).rotate(delta*xrotate*5, 0, 1, 0);
		objects.get(0).rotate(delta*xrotate*7, -1, 0, 1);
		
		if (Gdx.input.justTouched()) game.switchScreen("InGame");
	}

	@Override
	public void create() {
		Mesh mesh = Shapes.genCuboid(1, 1, 1);
		
		VisibleObject vo1 = new VisibleObject(mesh, new Vector3(1.0f, 1.0f, 1.0f), "icon");
		
		objects.add(new GameObject(vo1, 0, 0, -10));
		
		ShaderProgram shader = new ShaderProgram(
	            Gdx.files.internal("data/shaders/basic_movement.vert").readString(),
	            Gdx.files.internal("data/shaders/basic_movement.frag").readString());
	    if(!shader.isCompiled()) {
	        Gdx.app.log("Problem loading shader:", shader.getLog());
	    }
	    else
	    {
	    	shaders.add(shader);
	    }
	    
	    GameData.createNewLevel();
	}

	@Override
	public void hide() {
		
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
