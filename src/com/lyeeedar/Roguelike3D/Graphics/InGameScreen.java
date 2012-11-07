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

public class InGameScreen extends GameScreen {
	
	ArrayList<Light> currentLights = new ArrayList<Light>();


	public InGameScreen(Roguelike3DGame game) {
		super(game);
	}

	@Override
	void draw(float delta) {

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
			projection.setToProjection(0.1f, 500.0f, 60.0f, (float)screen_width/(float)screen_height);	

			// Model-View-Projection matrix - The matrix used to transform the objects mesh coordinates to get them onto the screen
			Matrix4 mvp = projection.mul(view).mul(model).mul(axis);
			
			
			/** Work out how many lights effect this Object **/
			currentLights.clear();
			
			for (Light l : GameData.currentLevel.getLevelLights())
			{
				if (l.inDrawDistance(go.getPosition().cpy(), 20)) currentLights.add(l);
			}
			
			shaderIndex = currentLights.size();

			ShaderProgram shader = shaders.get(shaderIndex);
			
			// basic_movement
			if (shaderIndex == 0)
			{
				shader.begin();
				shader.setUniformMatrix("u_mvp", mvp);
				shader.setUniformf("u_colour", new Vector3(go.vo.colour));
				shader.setUniformf("u_ambient", GameData.currentLevel.getAmbient());
			}
			// 1src_vert_lighting
			else if (shaderIndex == 1)
			{
				shader.begin();
				shader.setUniformMatrix("u_mvp", mvp);
				shader.setUniformf("u_position", go.getPosition());
				shader.setUniformf("u_colour", new Vector3(go.vo.colour));
				shader.setUniformf("u_ambient", GameData.currentLevel.getAmbient());
				
				shader.setUniformf("u_light1_position", currentLights.get(0).position);
				shader.setUniformf("u_light1_colour", currentLights.get(0).colour);
				shader.setUniformf("u_light1_attenuation", currentLights.get(0).attenuation);
			}
			// 2src_vert_lighting
			else if (shaderIndex == 2)
			{
				shader.begin();
				shader.setUniformMatrix("u_mvp", mvp);
				shader.setUniformf("u_position", go.getPosition());
				shader.setUniformf("u_colour", new Vector3(go.vo.colour));
				shader.setUniformf("u_ambient", GameData.currentLevel.getAmbient());
				
				shader.setUniformf("u_light1_position", currentLights.get(0).position);
				shader.setUniformf("u_light1_colour", currentLights.get(0).colour);
				shader.setUniformf("u_light1_attenuation", currentLights.get(0).attenuation);
				
				shader.setUniformf("u_light2_position", currentLights.get(1).position);
				shader.setUniformf("u_light2_colour", currentLights.get(1).colour);
				shader.setUniformf("u_light2_attenuation", currentLights.get(1).attenuation);
			}
			// 3src_vert_lighting
			else if (shaderIndex == 3)
			{
				shader.begin();
				shader.setUniformMatrix("u_mvp", mvp);
				shader.setUniformf("u_position", go.getPosition());
				shader.setUniformf("u_colour", new Vector3(go.vo.colour));
				shader.setUniformf("u_ambient", GameData.currentLevel.getAmbient());
				
				shader.setUniformf("u_light1_position", currentLights.get(0).position);
				shader.setUniformf("u_light1_colour", currentLights.get(0).colour);
				shader.setUniformf("u_light1_attenuation", currentLights.get(0).attenuation);
				
				shader.setUniformf("u_light2_position", currentLights.get(1).position);
				shader.setUniformf("u_light2_colour", currentLights.get(1).colour);
				shader.setUniformf("u_light2_attenuation", currentLights.get(1).attenuation);
				
				shader.setUniformf("u_light3_position", currentLights.get(2).position);
				shader.setUniformf("u_light3_colour", currentLights.get(2).colour);
				shader.setUniformf("u_light3_attenuation", currentLights.get(2).attenuation);
			}

			go.vo.texture.bind();
			go.vo.mesh.render(shader, GL20.GL_TRIANGLES);
			shader.end();
		}
	}
	
	@Override
	void update(float delta) {		
		GameData.player.update(delta);
		
		if (Gdx.input.justTouched()) game.switchScreen("LibGDXSplash");
	}

	@Override
	public void show() {
		
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
	    		Gdx.files.internal("data/shaders/1src_vert_lighting.vert").readString(),
	            Gdx.files.internal("data/shaders/1src_vert_lighting.frag").readString());
	    if(!shader.isCompiled()) {
	        Gdx.app.log("Problem loading shader:", shader.getLog());
	    }
	    else
	    {
	    	shaders.add(shader);
	    }
	    
	    shader = new ShaderProgram(
	    		Gdx.files.internal("data/shaders/2src_vert_lighting.vert").readString(),
	            Gdx.files.internal("data/shaders/2src_vert_lighting.frag").readString());
	    if(!shader.isCompiled()) {
	        Gdx.app.log("Problem loading shader:", shader.getLog());
	    }
	    else
	    {
	    	shaders.add(shader);
	    }
	    
	    shader = new ShaderProgram(
	            Gdx.files.internal("data/shaders/3src_vert_lighting.vert").readString(),
	            Gdx.files.internal("data/shaders/3src_vert_lighting.frag").readString());
	    if(!shader.isCompiled()) {
	        Gdx.app.log("Problem loading shader:", shader.getLog());
	    }
	    else
	    {
	    	shaders.add(shader);
	    }
	    
	    Gdx.input.setCursorCatched(true);
	}

	@Override
	public void hide() {
		Gdx.input.setCursorCatched(false);
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
