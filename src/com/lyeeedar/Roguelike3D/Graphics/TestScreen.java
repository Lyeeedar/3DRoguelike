package com.lyeeedar.Roguelike3D.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lyeeedar.Roguelike3D.Roguelike3DGame;
import com.lyeeedar.Roguelike3D.Game.GameData;

public class TestScreen extends GameScreen {
	
	Mesh mesh;
	ShaderProgram shader;
	Texture texture;

	public TestScreen(Roguelike3DGame game) {
		super(game);
		// TODO Auto-generated constructor stub
	}

	float move = -1;
	float angle = 0;
	@Override
	void draw(float delta) {
		
//		 move += delta/10;
//		 if (move > 1) move = -1;// -1<->+1 every 5 seconds
		 angle += delta*10;
		 if (angle > 180) angle = 0;// 45Åã per second
		 Vector3 axis = new Vector3(0, 1, 0);
		 Matrix4 transform = new Matrix4();
//		 
		 transform.rotate(axis, angle);
//		 transform.translate(move, 0, 0);
//		
//		Vector3 positions = new Vector3(0, 0, 0);
//		Vector3 colour = new Vector3(1f, 0f, 0.5f);
		
		Matrix4 model = new Matrix4();
		model.translate(0.0f, 0.0f, -4.0f);
		
		Matrix4 view = new Matrix4();
		//view.setToLookAt(new Vector3(0.0f, 2.0f, 0.0f), new Vector3(0.0f, 0.0f, -4.0f), new Vector3(0.0f, 1.0f, 0.0f));
		view.setToLookAt(GameData.player.getPosition(), GameData.player.getRotation(), GameData.player.getUp());
		
		Matrix4 projection = new Matrix4();
		projection.setToProjection(0.1f, 100.0f, 60.0f, 800f/600f);
		
		Matrix4 mvp = projection.mul(view);
		mvp.mul(model);
		mvp.mul(transform);
		
		shader.begin();
		texture.bind();
		shader.setUniformMatrix("u_matrix", mvp);
	//	shader.setUniformf("u_colour", colour);
		mesh.render(shader, GL20.GL_TRIANGLES);
		shader.end();
	}

	@Override
	void update(float delta) {
		// TODO Auto-generated method stub
		GameData.player.update(delta);

	}

	@Override
	public void show() {
		mesh = new Mesh(true, 3, 0, VertexAttribute.Position());
		mesh.setVertices(new float[]{
				0.0f,  0.8f, 0.0f,
				-0.8f, -0.8f, 0.0f,
				0.8f, -0.8f, 0.0f
		});
		
		mesh = Shapes.genCuboid(1, 1, 1);

//		String vertex_shader = 
//				"attribute vec3 a_position;\n" +
//				"uniform mat4 u_matrix;\n" +
//				//"uniform vec3 u_colour;\n" +
//				//"varying vec4 v_colour;\n" +
//				"void main(void) {\n" +
//					"gl_Position = u_matrix * vec4(a_position, 1.0);\n" +
//				//	"v_colour = vec4(u_colour, 1.0);\n" +
//				"}\n";
//		
//		String fragment_shader = 
//		//		"varying vec3 v_colour; \n" +
//				"void main(void) {\n"+
//			 " gl_FragColor = vec4(1.0, 0.1, 0.5, 1.0);\n"+
//			"}";
		
		String vertex_shader = 
				"attribute vec3 a_position;\n" +
				"attribute vec2 a_texcoords;\n" +
				"uniform mat4 u_matrix;\n" +
				//"uniform vec3 u_colour;\n" +
				"varying vec2 v_texcoords;\n" +
				"void main(void) {\n" +
					"gl_Position = u_matrix * vec4(a_position, 1.0);\n" +
					"v_texcoords = a_texcoords; \n" +
				//	"v_colour = vec4(u_colour, 1.0);\n" +
				"}\n";
		
		String fragment_shader = 
		//		"varying vec3 v_colour; \n" +
				"varying vec2 v_texcoords; \n" +
				"uniform sampler2D texture; \n" +
				"void main(void) {\n"+
					"gl_FragColor =  vec4(1.0, 0.1, 0.1, 1.0) * texture2D(texture, v_texcoords);\n"+
			"}";
		
		shader = new ShaderProgram(vertex_shader, fragment_shader);
		
		texture = new Texture(Gdx.files.internal("Data/tex#.png"));
		texture.setWrap( TextureWrap.Repeat, TextureWrap.Repeat );
		texture.setFilter( TextureFilter.Nearest, TextureFilter.Nearest );
		
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
