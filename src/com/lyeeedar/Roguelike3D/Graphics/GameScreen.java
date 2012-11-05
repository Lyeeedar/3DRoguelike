package com.lyeeedar.Roguelike3D.Graphics;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.utils.ArrayMap;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;
import com.lyeeedar.Roguelike3D.Game.GameObject;

public abstract class GameScreen implements Screen{

	Roguelike3DGame game;

	Camera camera;
	DecalBatch batch = new DecalBatch();

	public GameScreen(Roguelike3DGame game)
	{
		this.game = game;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.3f, 0.4f, 1.0f);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		Gdx.gl.glEnable(GL10.GL_TEXTURE_2D);
		Gdx.gl.glEnable(GL10.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(true);
		Gdx.gl.glEnable(GL10.GL_COLOR_MATERIAL);
		Gdx.graphics.getGL10().glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT_AND_DIFFUSE, 1f);

		camera.update();
		camera.apply(Gdx.gl10);

		GL10 gl = Gdx.graphics.getGL10();
		for (GameObject go : GameData.currentLevel.getLevelGraphics())
		{
			gl.glPushMatrix();
			gl.glTranslatef(go.getPosition().x, go.getPosition().y, go.getPosition().z);
			gl.glRotatef(1, go.getRotation().x, go.getRotation().y, go.getRotation().z);
			go.vo.texture.bind();
			gl.glColor4f(go.vo.colour[0], go.vo.colour[01], go.vo.colour[2], go.vo.colour[3]);
			go.vo.mesh.render(GL10.GL_TRIANGLES);
			gl.glPopMatrix();
		}

//		if (decals != null && decals.size() != 0)
//		{
//			for (DecalSprite sprite : decals)
//			{
//				batch.add(sprite.sprite);
//			}
//
//			batch.flush();
//		}

		draw(delta);

		update(delta);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		batch.dispose();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	abstract void draw(float delta);
	abstract void update(float delta);

}
