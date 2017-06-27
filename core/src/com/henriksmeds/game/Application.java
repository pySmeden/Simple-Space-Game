package com.henriksmeds.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.henriksmeds.game.screens.GameScreen;
import com.henriksmeds.game.screens.HighscoreScreen;
import com.henriksmeds.game.screens.MenuScreen;
import com.henriksmeds.game.screens.PauseScreen;
import com.henriksmeds.game.utils.HighScore;

public class Application extends Game {

	public final float WORLD_HEIGHT = 8f;
	public final float WORLD_WIDTH = 4.8f;

	public final float VIRTUAL_HEIGHT = 800f;
	public final float VIRTUAL_WIDTH = 480f;

	public final float SCALE = 0.01f;

	public SpriteBatch batch;

	public GameScreen gameScreen;
	public MenuScreen menuScreen;
	public HighscoreScreen highscoreScreen;
	public PauseScreen pauseScreen;

	
	@Override
	public void create () {
		batch = new SpriteBatch();

		gameScreen = new GameScreen(this);
		menuScreen = new MenuScreen(this);
		highscoreScreen = new HighscoreScreen(this);
		pauseScreen = new PauseScreen(this);

		this.setScreen(gameScreen);
	}


	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		super.dispose();
	}
}
