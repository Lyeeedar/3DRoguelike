package com.lyeeedar.Roguelike3D.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;

public class MainMenuScreen extends GameScreen {
	
	BitmapFont font;
	Mesh mesh;

	public MainMenuScreen(Roguelike3DGame game)
	{
		super(game);
	}

	@Override
	public void draw(float delta) {

		mesh.render(GL10.GL_TRIANGLES, 0, 3);

//		batch.begin();
//		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
//		font.draw(batch, "Main Menu!", 200, 200);
//		batch.end();

	}

	public void update(float delta)
	{

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void show() {

		font = new BitmapFont();

		mesh = new Mesh(true, 3, 3, 
		        new VertexAttribute(Usage.Position, 3, "a_position"),
		        new VertexAttribute(Usage.ColorPacked, 4, "a_color"));          

		mesh.setVertices(new float[] { -0.5f, -0.5f, 0, Color.toFloatBits(255, 0, 0, 255),
		                               0.5f, -0.5f, 0, Color.toFloatBits(0, 255, 0, 255),
		                               0, 0.5f, 0, Color.toFloatBits(0, 0, 255, 255) });        
		mesh.setIndices(new short[] { 0, 1, 2 });


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

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
