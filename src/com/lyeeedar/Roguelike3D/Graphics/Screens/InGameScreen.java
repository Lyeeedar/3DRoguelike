package com.lyeeedar.Roguelike3D.Graphics.Screens;

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
import com.lyeeedar.Roguelike3D.Graphics.Models.VisibleObject;
import com.lyeeedar.Roguelike3D.Graphics.Renderers.PrototypeRendererGL20;
import com.lyeeedar.Roguelike3D.Graphics.Screens.AbstractScreen;

public class InGameScreen extends AbstractScreen {

	public InGameScreen(Roguelike3DGame game) {
		super(game);
	}

	@Override
	public void draw(float delta) {
		
		protoRenderer.begin();

		for (VisibleObject vo : GameData.levelGraphics.graphics)
		{
			vo.render(protoRenderer);
		}
		
		for (int x = 0; x < GameData.level.getLevelArray().length; x++)
		{
			for (int z = 0; z < GameData.level.getLevelArray()[0].length; z++)
			{
				Tile t = GameData.level.getLevelArray()[x][z];
				for (GameActor go : t.actors)
				{
					go.vo.render(protoRenderer);
				}
				
				for (VisibleItem vi : t.items)
				{
					vi.vo.render(protoRenderer);
				}
			}
		}
		
		protoRenderer.end();
	}
	
	ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	int count = 1;
	@Override
	public void update(float delta) {
		
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
					}
					else if (t.items.size() != 0)
					{
						r += 'i';
					}
					else r += t.character;
				}
				map += r + "\n";
			}
			label.setText(map);
		}
		
		cam.position.set(GameData.player.getPosition());
		cam.direction.set(GameData.player.getRotation());
		cam.update();
	}

	Label label;
	@Override
	public void create() {
		
		GameData.createNewLevel();
		
		protoRenderer = new PrototypeRendererGL20(GameData.lightManager);
		protoRenderer.cam = cam;
		
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
	}

	@Override
	public void hide() {
		Gdx.input.setCursorCatched(false);
	}
	
	@Override
	public void show()
	{
		//Gdx.input.setCursorCatched(true);
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
