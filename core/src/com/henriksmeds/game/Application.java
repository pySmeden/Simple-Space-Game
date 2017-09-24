package com.henriksmeds.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.henriksmeds.game.screens.GameScreenCopy;
import com.henriksmeds.game.screens.MenuScreenCopy;
import com.henriksmeds.game.screens.SplashScreen;
import com.henriksmeds.game.utils.Assets;

public class Application extends Game {


	public final float WORLD_HEIGHT = 8f;
	public final float WORLD_WIDTH = 4.8f;

	public final float VIRTUAL_HEIGHT = 800f;
	public final float VIRTUAL_WIDTH = 480f;

	public final float SCALE = 0.01f;

	public SpriteBatch batch;
	public Assets assets;

	public GameScreenCopy gameScreenCopy;
	public MenuScreenCopy menuScreenCopy;
	public SplashScreen splashScreen;

	
	@Override
	public void create () {
		assets = new Assets();
		batch = new SpriteBatch();

		gameScreenCopy = new GameScreenCopy(this);
		menuScreenCopy = new MenuScreenCopy(this);
		splashScreen = new SplashScreen(this);

		this.setScreen(splashScreen);
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
