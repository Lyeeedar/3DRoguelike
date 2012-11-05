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
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;

public class LibGDXSplashScreen extends GameScreen {
	
	ArrayList<VisibleObject> vobjects = new ArrayList<VisibleObject>();
	ArrayList<GameObject> gobjects = new ArrayList<GameObject>();
	
	public LibGDXSplashScreen(Roguelike3DGame game)
	{
		super(game);
	}

	@Override
	public void draw(float delta) {
		Gdx.gl.glEnable(GL10.GL_LIGHTING);
		Gdx.gl.glEnable(GL10.GL_LIGHT0);
		
		Gdx.graphics.getGL10().glShadeModel(GL10.GL_SMOOTH);
		
		Gdx.graphics.getGL10().glLightModelfv(GL10.GL_LIGHT_MODEL_AMBIENT, new float[]{0.05f, 0.05f, 0.05f, 1}, 0);
		
		// Create light components
		float ambientLight[] = { 0.2f, 0.2f, 0.2f, 1.0f };
		float diffuseLight[] = { 0.8f, 0.8f, 0.8f, 1.0f };
		float specularLight[] = { 0.5f, 0.5f, 0.5f, 1.0f };
		float position[] = { -1.5f, 1.0f, 4.0f, 1.0f };

		// Assign created components to GL_LIGHT0
		Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLight, 0);
		Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, diffuseLight, 0);
		Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, specularLight, 0);
		Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, position, 0);
		
		Gdx.graphics.getGL10().glLightf(GL10.GL_LIGHT0, GL10.GL_LINEAR_ATTENUATION, 0.05f);
		
		Gdx.graphics.getGL10().glEnable(GL10.GL_BLEND);
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

	float xrotate = -640f/720f;
	float yrotate = -480f/720f;

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

		camera.direction.set(GameData.player.getRotation());
		camera.position.set(GameData.player.getPosition());
		camera.up.set(GameData.player.getUp());

		//if (Gdx.input.isTouched()) game.setScreen(game.screens.get("MainMenu"));
		
		//if (Gdx.input.isKeyPressed(Keys.W)) Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION,
		//				new float[]{camera.position.x, camera.position.y, camera.position.z, 1}, 0);
		//Gdx.graphics.getGL10().glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION,
		//		new float[]{camera.position.x, camera.position.y, camera.position.z, 1}, 0);
	}

	@Override
	public void resize(int width, int height) {
		float aspectRatio = (float) width / (float) height;
		camera = new PerspectiveCamera(60, 2f * aspectRatio, 2f);

	}

	@Override
	public void show() {
		
		Gdx.input.setCursorCatched(true);

		GameData.createNewLevel();
		
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

}
