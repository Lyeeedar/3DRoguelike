package com.lyeeedar.Roguelike3D.Graphics;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.*;
import com.lyeeedar.Roguelike3D.Game.Actor.GameActor;
import com.lyeeedar.Roguelike3D.Game.Item.VisibleItem;
import com.lyeeedar.Roguelike3D.Game.Level.Tile;

public class InGameScreen extends AbstractScreen {
	
	ArrayList<Light> currentLights = new ArrayList<Light>();


	public InGameScreen(Roguelike3DGame game) {
		super(game);
	}

	public static final Matrix4 pv = new Matrix4();
	@Override
	void draw(float delta) {
		
		// Projection matrix - The camera details, i.e. the fov, the view distance and the screen size
		pv.setToProjection(0.01f, 500.0f, 70.0f, (float)screen_width/(float)screen_height).mul(GameData.player.getView());

		for (GameObject go : GameData.level.getLevelGraphics())
		{
			drawGameObject(go);
		}
		
		for (int x = 0; x < GameData.level.getLevelArray().length; x++)
		{
			for (int z = 0; z < GameData.level.getLevelArray()[0].length; z++)
			{
				Tile t = GameData.level.getLevelArray()[x][z];
				for (GameActor go : t.actors)
				{
					drawGameObject(go);
					drawCollisionBox(go);
				}
				
				for (VisibleItem vi : t.items)
				{
					//System.out.println(x + "   " + z);
					drawGameObjectMovementOnly(vi);
					drawCollisionBox(vi);
				}
			}
		}
	}
	
	public static final int maxLights = 5;
	public static final float[] light_vectors = new float[maxLights*3];
	public static final float[] light_colours = new float[maxLights*3];
	public static final float[] light_atts = new float[maxLights];
	public static final Matrix3 normal = new Matrix3();
	
	public static final Matrix4 model = new Matrix4();
	public static final Matrix4 mvp = new Matrix4();
	
	public void drawGameObject(GameObject go)
	{
		if (go.vo == null) return;
		
		/** Calculate Matrix's for use in shaders **/
		
		// Model matrix - The position of the object in 3D space comparative to the origin
		model.setToTranslation(go.getPosition()).mul(go.getRotationMatrix());

		// Model-View-Projection matrix - The matrix used to transform the objects mesh coordinates to get them onto the screen
		mvp.set(pv).mul(model);
		
		/** Work out how many lights effect this Object **/
		currentLights.clear();
		
		for (Light l : GameData.level.getLevelLights())
		{
			//if (l.inDrawDistance(go.getPosition().cpy(), 2000)) currentLights.add(l);
			currentLights.add(l);
		}
		
		int i = 0;
		for (; i < maxLights && i < currentLights.size(); i++)
		{
			Vector3 vec = currentLights.get(i).position;
			light_vectors[i*3] = vec.x;
			light_vectors[(i*3)+1] = vec.y;
			light_vectors[(i*3)+2] = vec.z;
			
			vec = currentLights.get(i).colour;
			light_colours[i*3] = vec.x;
			light_colours[(i*3)+1] = vec.y;
			light_colours[(i*3)+2] = vec.z;
			
			light_atts[i] = currentLights.get(i).attenuation;
		}
		
		for (; i < maxLights; i++)
		{
			light_vectors[i*3] = 0;
			light_vectors[(i*3)+1] = 0;
			light_vectors[(i*3)+2] = 0;
			
			light_colours[i*3] = 0;
			light_colours[(i*3)+1] = 0;
			light_colours[(i*3)+2] = 0;
			
			light_atts[i] = 0;
		}

		ShaderProgram shader = shaders.get(0);
		
		shader.begin();
		shader.setUniformMatrix("u_mvp", mvp);
		shader.setUniformMatrix("u_model", model);
		shader.setUniformMatrix("u_normal", normal.set(model.toNormalMatrix()));
		shader.setUniformf("u_colour", go.vo.colour);
		shader.setUniformf("u_ambient", GameData.level.getAmbient());
		
		shader.setUniform3fv("u_light_vector", light_vectors, 0, maxLights*3);
		shader.setUniform3fv("u_light_colour", light_colours, 0, maxLights*3);
		shader.setUniform1fv("u_light_attenuation", light_atts, 0, maxLights);

		go.vo.texture.bind();
		go.vo.mesh.render(shader, GL20.GL_TRIANGLES);
		shader.end();
	}
	
	public void drawCollisionBox(GameObject go)
	{
		// Model matrix - The position of the object in 3D space comparative to the origin
		model.setToTranslation(go.collisionBox.position);
		// Model-View-Projection matrix - The matrix used to transform the objects mesh coordinates to get them onto the screen
		mvp.set(pv).mul(model);
		
		GameData.collisionShader.begin();
		GameData.collisionShader.setUniformMatrix("u_mvp", mvp);
		go.collisionMesh.render(GameData.collisionShader, GL20.GL_LINE_LOOP);
		GameData.collisionShader.end();
	}
	
	public void drawGameObjectMovementOnly(GameObject go)
	{
		if (go.vo == null) return;
		
		/** Calculate Matrix's for use in shaders **/
		
		// Model matrix - The position of the object in 3D space comparative to the origin
		model.setToTranslation(go.getPosition()).mul(go.getRotationMatrix());

		// Model-View-Projection matrix - The matrix used to transform the objects mesh coordinates to get them onto the screen
		mvp.set(pv).mul(model);
		
		ShaderProgram shader = shaders.get(1);
		
		shader.begin();
		shader.setUniformMatrix("u_mvp", mvp);
		shader.setUniformf("u_colour", go.vo.colour);

		go.vo.texture.bind();
		go.vo.mesh.render(shader, GL20.GL_TRIANGLES);
		shader.end();
	}
	
	ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	int count = 1;
	@Override
	void update(float delta) {
		
		gameObjects.clear();
		for (Tile[] ts : GameData.level.getLevelArray())
		{
			for (Tile t : ts)
			{
				for (GameActor ga : t.actors)
				{
					gameObjects.add(ga);
				}
				
				for (VisibleItem vi : t.items)
				{
					gameObjects.add(vi);
				}
			}
		}
		
		for (GameObject ga : gameObjects)
		{
			ga.update(delta);
		}
		
		if (Gdx.input.justTouched()) game.switchScreen("LibGDXSplash");
		
		count--;
		if (count <= 0) {
			count = 10;
			//GameData.frame.paint(GameData.frame.getGraphics());
			String map = "";
			for (Tile[] row : GameData.level.getLevelArray()) {
				String r = "";
				for (Tile t : row) {
					if (t.actors.size() != 0) {
						boolean player = false;
						for (GameActor ga : t.actors) {
							if (ga.UID.equals(GameData.player.UID)) {
								player = true;
								break;
							}
						}

						if (player) {
							r += '@';
						} else {
							r += '&';
						}
					} else
						r += t.character;
				}
				map += r + "\n";
			}
			label.setText(map);
		}
	}

	Label label;
	@Override
	public void create() {
		
		Skin skin = new Skin(Gdx.files.internal( "data/skins/uiskin.json" ));
		//skin.addResource("verdana", font);
		//skin.("default_font1", new BitmapFont());
		//font = skin.getFont("default-font");
		
	    
		label = new Label("", skin);
		
		LabelStyle defaultStyle = new LabelStyle();
	    defaultStyle.font = font;
	    defaultStyle.fontColor = Color.WHITE;
		label.setStyle(defaultStyle);
		
		stage.addActor(label);
	    
	    ShaderProgram shader = new ShaderProgram(

	    		Gdx.files.internal("data/shaders/basic_diffuse_lighting.vert").readString(),
	            Gdx.files.internal("data/shaders/basic_diffuse_lighting.frag").readString());
	    if(!shader.isCompiled()) {
	        Gdx.app.log("Problem loading shader:", shader.getLog());
	    }
	    else
	    {
	    	shaders.add(shader);
	    }
	    
	    shader = new ShaderProgram(
	    		Gdx.files.internal("data/shaders/basic_movement.vert").readString(),
	    		Gdx.files.internal("data/shaders/basic_movement.frag").readString());
	    if(!shader.isCompiled()) {
	    	Gdx.app.log("Problem loading shader:", shader.getLog());
	    }
	    else
	    {
	    	shaders.add(shader);
	    }
	    
//	    shader = new ShaderProgram(
//	    		Gdx.files.internal("data/shaders/2src_vert_lighting.vert").readString(),
//	            Gdx.files.internal("data/shaders/2src_vert_lighting.frag").readString());
//	    if(!shader.isCompiled()) {
//	        Gdx.app.log("Problem loading shader:", shader.getLog());
//	    }
//	    else
//	    {
//	    	shaders.add(shader);
//	    }
//	    
//	    shader = new ShaderProgram(
//	            Gdx.files.internal("data/shaders/3src_vert_lighting.vert").readString(),
//	            Gdx.files.internal("data/shaders/3src_vert_lighting.frag").readString());
//	    if(!shader.isCompiled()) {
//	        Gdx.app.log("Problem loading shader:", shader.getLog());
//	    }
//	    else
//	    {
//	    	shaders.add(shader);
//	    }
	}

	@Override
	public void hide() {
		Gdx.input.setCursorCatched(false);
	}
	
	@Override
	public void show()
	{
		Gdx.input.setCursorCatched(true);
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
