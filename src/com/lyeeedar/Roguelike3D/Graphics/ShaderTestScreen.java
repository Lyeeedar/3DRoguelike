package com.lyeeedar.Roguelike3D.Graphics;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;
import com.lyeeedar.Roguelike3D.Game.Player;

public class ShaderTestScreen extends GameScreen {
	
	ArrayList<GameObject> objects = new ArrayList<GameObject>();
	ArrayList<ShaderProgram> shaders = new ArrayList<ShaderProgram>();
	
	int shaderIndex = 0;

	public ShaderTestScreen(Roguelike3DGame game) {
		super(game);
	}

	float move = 0;
	float angle = 0;
	@Override
	void draw(float delta) {
		
		move += delta;
		
		ShaderProgram shader = shaders.get(shaderIndex);
		
		for (GameObject go : GameData.currentLevel.getLevelGraphics())
		{
			/** Calculate Matrix's for use in shaders **/
			
			// Model matrix - The position of the object in 3D space comparative to the origin
			Matrix4 model = new Matrix4();
			model.setToTranslation(go.getPosition());

			// Rotation matrix - The rotation of the object
			Matrix4 axis = new Matrix4();
			axis.setFromEulerAngles(go.getEuler_rotation().x, go.getEuler_rotation().y, go.getEuler_rotation().z);

			// View matrix - The position and direction of the 'camera'. In this case, the player.
			Matrix4 view = new Matrix4();
			view.setToLookAt(GameData.player.getPosition(), GameData.player.getPosition().cpy().add(GameData.player.getRotation()), GameData.player.getUp());

			// Projection matrix - The camera details, i.e. the fov, the view distance and the screen size
			Matrix4 projection = new Matrix4();
			projection.setToProjection(0.1f, 100.0f, 60.0f, (float)screen_width/(float)screen_height);	

			// Model-View-Projection matrix - The matrix used to transform the objects mesh coordinates to get them onto the screen
			Matrix4 mvp = projection.mul(view).mul(model).mul(axis);

			shader.begin();
			go.vo.texture.bind();
			
			// basic_movement
			if (shaderIndex == 0)
			{
				shader.setUniformMatrix("u_mvp", mvp);
				shader.setUniformf("u_colour", new Vector3(go.vo.colour));
			}
			// basic_vert_lighting
			else if (shaderIndex == 1)
			{
				shader.setUniformMatrix("u_mvp", mvp);
				shader.setUniformf("u_position", go.getPosition());
				shader.setUniformf("u_pposition", GameData.player.getPosition());
				shader.setUniformf("u_colour", new Vector3(go.vo.colour));
			}
			// basic_frag_lighting
			else if (shaderIndex == 2)
			{
				shader.setUniformMatrix("u_mvp", mvp);
				shader.setUniformf("u_position", go.getPosition());
				shader.setUniformf("u_pposition", GameData.player.getPosition());
				shader.setUniformf("u_colour", new Vector3(go.vo.colour));
			}

			go.vo.mesh.render(shader, GL20.GL_TRIANGLES);
			shader.end();
		}
		
		String shaderName = "";
		
		if (shaderIndex == 0) shaderName = "Basic Movement and Colour";
		else if (shaderIndex == 1) shaderName = "Basic Movement and Colour and Vertex Lighting";
		else if (shaderIndex == 2) shaderName = "Basic Movement and Colour and Fragment (pixel) Lighting";
		
		Gdx.gl.glDisable(GL20.GL_CULL_FACE);
		
		spritebatch.begin();
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		font.draw(spritebatch, "Shader: "+shaderName, 20, 490);
		font.draw(spritebatch, "Player Controlled: "+playerControl, 20, 460);
		spritebatch.end();
		
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		
		GameData.frame.paint(GameData.frame.getGraphics());

	}

	boolean playerControl = true;
	@Override
	void update(float delta) {
		
		float xrotate = 800f/720f;
		objects.get(0).euler_rotate((float)Gdx.input.getDeltaX()*xrotate, 1, 0, 0);
		objects.get(0).euler_rotate(0.5f, 0, 1, 1);
		
		if (Gdx.input.isKeyPressed(Keys.NUM_0)) shaderIndex = 0;
		if (Gdx.input.isKeyPressed(Keys.NUM_1)) shaderIndex = 1;
		if (Gdx.input.isKeyPressed(Keys.NUM_2)) shaderIndex = 2;
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) playerControl = false;
		if (Gdx.input.isKeyPressed(Keys.SPACE)) playerControl = true;
		
		if (playerControl) GameData.player.update(delta);

	}

	@Override
	public void show() {
		Mesh mesh = new Mesh(true, 3, 0, VertexAttribute.Position());
		mesh.setVertices(new float[]{
				0.0f,  0.8f, 0.0f,
				-0.8f, -0.8f, 0.0f,
				0.8f, -0.8f, 0.0f
		});
		
		mesh = Shapes.genCuboid(1, 1, 1);
		
		VisibleObject vo1 = new VisibleObject(mesh, new float[]{1.0f, 0.2f, 0.7f}, "icon");
		
		objects.add(new GameObject(vo1, 0, -2, -5));
		
		Mesh mesh1 = Shapes.genCuboid(50, 1, 50);
		
		VisibleObject vo2 = new VisibleObject(mesh1, new float[]{0.0f, 0.3f, 0.8f}, "tex#");
		
		objects.add(new GameObject(vo2, 0, 10, 0));
		
		for (int x = 0; x < 10; x++)
		{
			for (int y = 0; y < 10; y++)
			{
				Mesh mesh2 = Shapes.genCuboid(5, 1, 5);
				
				VisibleObject vo3 = new VisibleObject(mesh1, new float[]{0.3f, 0.8f, 0.3f}, "tex.");
				
				objects.add(new GameObject(vo3, x*10, -5, y*10));
			}
		}
		
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
	    
	    shader = new ShaderProgram(
	            Gdx.files.internal("data/shaders/basic_vert_lighting.vert").readString(),
	            Gdx.files.internal("data/shaders/basic_vert_lighting.frag").readString());
	    if(!shader.isCompiled()) {
	        Gdx.app.log("Problem loading shader:", shader.getLog());
	    }
	    else
	    {
	    	shaders.add(shader);
	    }
	    
	    shader = new ShaderProgram(
	            Gdx.files.internal("data/shaders/basic_frag_lighting.vert").readString(),
	            Gdx.files.internal("data/shaders/basic_frag_lighting.frag").readString());
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
