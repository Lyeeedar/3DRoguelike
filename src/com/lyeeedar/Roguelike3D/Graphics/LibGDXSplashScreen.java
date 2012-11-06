package com.lyeeedar.Roguelike3D.Graphics;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
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

public class LibGDXSplashScreen extends GameScreen {
	
	ArrayList<VisibleObject> vobjects = new ArrayList<VisibleObject>();
	ArrayList<GameObject> gobjects = new ArrayList<GameObject>();
	ShaderProgram shader;
	Matrix4 mat = new Matrix4();
	GameObject go;
	
	public LibGDXSplashScreen(Roguelike3DGame game)
	{
		super(game);
	}

	@Override
	public void draw(float delta) {
//		Gdx.gl.glEnable(GL10.GL_LIGHTING);
//		Gdx.gl.glEnable(GL10.GL_LIGHT0);
		
		//Gdx.graphics.getGL10().glShadeModel(GL10.GL_SMOOTH);
		
		//Gdx.graphics.getGL10().glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, new float[]{0.05f, 0.05f, 0.05f, 1}, 0);
		
		// Create light components
//		float ambientLight[] = { 0.2f, 0.2f, 0.2f, 1.0f };
//		float diffuseLight[] = { 0.8f, 0.8f, 0.8f, 1.0f };
//		float specularLight[] = { 0.5f, 0.5f, 0.5f, 1.0f };
//		float position[] = { -1.5f, 1.0f, 4.0f, 1.0f };

		// Assign created components to GL_LIGHT0
//		Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLight, 0);
//		Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseLight, 0);
//		Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularLight, 0);
//		Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, position, 0);
//		
//		Gdx.graphics.getGL10().glLightf(GL10.GL_LIGHT0, GL10.GL_LINEAR_ATTENUATION, 0.05f);
//		
//		Gdx.graphics.getGL10().glEnable(GL10.GL_BLEND);

//		Matrix4 view = new Matrix4();
//		view.setToLookAt(GameData.player.getPosition(), GameData.player.getRotation(), GameData.player.getUp());
//		Matrix4 projection = new Matrix4();
//		projection.setToProjection(0.1f, 10f, 60f, 800/600);
//		
//		Matrix4 model = new Matrix4();
//		model.setToTranslation(go.getPosition());
//		
//		//Matrix4 mvp = projection.mul(view);
//		Matrix4 mvp = view.mul(projection);
//		mvp = mvp.mul(model);
				
		shader.begin();
		//shader.setUniformMatrix("u_worldView", mvp);
		//shader.setUniformf("u_worldView", go.getPosition());
//		go.vo.texture.bind();
		go.vo.mesh.render(shader, GL10.GL_TRIANGLES);
		shader.end();
		
		for (GameObject go : GameData.currentLevel.getLevelGraphics())
		{
			
//			mat.setToTranslation(go.getPosition());
//			//mat.setToRotation(axis, angle)
//			Matrix4 resMat = projection.mul(view);
//			resMat.mul(mat);
//			
//			go.vo.texture.bind();
//			shader.begin();
//			shader.setUniformMatrix("u_worldView", resMat);
//			shader.setUniformi("u_texture", 0);
//			go.vo.mesh.render(shader, GL10.GL_TRIANGLES);
//			shader.end();
//			gl.glPushMatrix();
//			gl.glTranslatef(go.getPosition().x, go.getPosition().y, go.getPosition().z);
//			gl.glRotatef(1, go.getRotation().x, go.getRotation().y, go.getRotation().z);
//			go.vo.texture.bind();
//			gl.glColor4f(go.vo.colour[0], go.vo.colour[01], go.vo.colour[2], go.vo.colour[3]);
//			go.vo.mesh.render(GL10.GL_TRIANGLES);
//			gl.glPopMatrix();
		}
//		
//		for (VisibleObject o : vobjects)
//		{
//			o.mesh.render(GL10.GL_TRIANGLES);
//		}
//		
//		for (GameObject o : gobjects)
//		{
//			Gdx.graphics.getGL10().glPushMatrix();
//			Gdx.graphics.getGL10().glTranslatef(o.getPosition().x, o.getPosition().y, o.getPosition().z);
//			o.vo.mesh.render(GL10.GL_TRIANGLES);
//			Gdx.graphics.getGL10().glPopMatrix();
//		}
		
		GameData.frame.paint(GameData.frame.getGraphics());
	}

	float xrotate = -800f/720f;
	float yrotate = -600f/720f;

	public void update(float delta)
	{
//		Vector3 nPos = new Vector3();
//		
//		Vector3 movement = new Vector3();
//		Vector3 angle = camera.direction.cpy();
//
//		if (Gdx.input.isKeyPressed(Keys.LEFT)) movement.x = 0.3f;
//		if (Gdx.input.isKeyPressed(Keys.RIGHT)) movement.x = -0.3f;
//		
//		if (movement.x != 0){
//			
//			movement.z = -(float)Math.sin(angle.x) * movement.x;
//			movement.x = (float)Math.sin(angle.z) * movement.x;
//
//			nPos.add(movement.x, 0f, movement.z);
//			
//			movement.x = 0;
//			movement.z = 0;
//		}
//
//		if (Gdx.input.isKeyPressed(Keys.UP)) movement.z = 0.3f;
//		if (Gdx.input.isKeyPressed(Keys.DOWN)) movement.z = -0.3f;
//
//		if (movement.z != 0){
//
//			movement.x = (float) Math.sin(angle.x) * movement.z;
//			movement.z = (float) Math.sin(angle.z) * movement.z;
//
//			nPos.add(movement.x, 0f, movement.z);
//		}
//		
//		camera.translate(nPos);
//
//		float pitch = (float)Gdx.input.getDeltaY()*yrotate;
//
//		Vector3 camdir = camera.direction.cpy();
//		if( (camdir.nor().y>-0.9) && (pitch<0) || (camdir.nor().y<+0.9) && (pitch>0) )
//		{
//			Vector3 localCamAxisX = camera.direction.cpy();
//			localCamAxisX.crs(camera.up.tmp()).nor();
//			camera.rotate(pitch, localCamAxisX.x, localCamAxisX.y, localCamAxisX.z);
//			camera.up.nor();
//		}
//
//		camera.rotate((float)Gdx.input.getDeltaX()*xrotate, 0, 1, 0);
		
		GameData.player.update(delta);

		//if (Gdx.input.isTouched()) game.setScreen(game.screens.get("MainMenu"));
		
		//if (Gdx.input.isKeyPressed(Keys.W)) Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION,
		//				new float[]{camera.position.x, camera.position.y, camera.position.z, 1}, 0);
		//Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION,
		//		new float[]{camera.position.x, camera.position.y, camera.position.z, 1}, 0);
	}

	@Override
	public void resize(int width, int height) {
		float aspectRatio = (float) width / (float) height;

	}

	@Override
	public void show() {
		
		Gdx.input.setCursorCatched(true);

		GameData.createNewLevel();
		
		VisibleObject vo = new VisibleObject(Shapes.genCuboid(15, 15, 15), new float[]{1, 1, 1, 1}, "Data/tex..png");
		go = new GameObject(vo, 0, 0, -10);
		
		String vertexShader = 
				"attribute vec4 a_position;\n" + 
                "attribute vec4 a_normal;\n" +
                "attribute vec2 a_texCoord0;\n" + 
                "void main()                  \n" + 
                "{                            \n" + 
                "   gl_Position =  a_position - 10;  \n"      + 
                "}                            \n" ;
		String fragmentShader = 
                  "void main()                                  \n" + 
                  "{                                            \n" + 
                  "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
                  "} \n";
		
		shader = new ShaderProgram(vertexShader, fragmentShader);
		
//		int in = 0;
//		for (float i = 0.5f; in < 10; i += 0.5f, in++)
//		{
//			VisibleObject vo = VisibleObject.createCuboid(i, i, i, new float[]{i/10f, 0.4f, 0.4f, 1.0f}, "Data/tex#.png");
//			vo.move(in*15, 0, in);
//			objects.add(vo); 
//		}
//		
//		for (int i = 0; i < 10; i++)
//		{
//			for (int ii = 0; ii < 100; ii++)
//			{
//				VisibleObject floor = VisibleObject.createCuboid(10, 0, 10, new float[]{0.8f, 0.4f, 0.3f, 1.0f}, "Data/tex#.png");
//				//floor.move(i*10, -1, ii*10);
//				GameObject go = new GameObject(floor, i*10, -1, ii*10);
//				gobjects.add(go);
//			}
//		}
////		
//		VisibleObject roof = VisibleObject.createCuboid(100, 0, 100, new float[]{0.8f, 0.4f, 0.3f, 1.0f}, "Data/tex#.png");
//		roof.move(0, 10, 0);
//		vobjects.add(roof);
//		
//		VisibleObject wall1 = VisibleObject.createCuboid(60, 18, 2, new float[]{0.8f, 0.4f, 0.3f, 1.0f}, "Data/tex#.png");
//		wall1.move(0, 0, 30);
//		vobjects.add(wall1);
//		
//		
//		DecalSprite sprite = new DecalSprite();
//		sprite.build("data/libgdx.png");
//		sprite.sprite.setDimensions(3, 3);
//		sprite.sprite.setPosition(-1.5f, -1f, -2);
//		
//		decals.add(sprite);
//		
//		
		 
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
