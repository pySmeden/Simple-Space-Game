package com.henriksmeds.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.henriksmeds.game.Application;
import com.henriksmeds.game.elements.MenuUI;


public class MenuScreen extends ScreenAdapter {
    private final Application app;
    Stage stage;
    OrthographicCamera worldCam;
    FillViewport worldViewport;
    Sprite background;
    TextureAtlas atlas;
    MenuUI menuUI;
    Sound sound;

    public MenuScreen(final Application app) {
        this.app = app;
    }

    @Override
    public void show() {
        super.show();
        sound = Gdx.audio.newSound(Gdx.files.internal("sounds/button-click.ogg"));
        atlas = new TextureAtlas(Gdx.files.internal("game-elements/assets.pack"));
        background = new Sprite(atlas.findRegion("background"));
        background.setSize(app.VIRTUAL_WIDTH, app.VIRTUAL_HEIGHT);
        worldCam = new OrthographicCamera();
        worldCam.setToOrtho(false);
        worldViewport = new FillViewport(app.VIRTUAL_WIDTH, app.VIRTUAL_HEIGHT, worldCam);
        stage = new Stage(worldViewport, app.batch);
        stage.getViewport().apply();
        initMenu();

        stage.addActor(menuUI.table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        app.batch.begin();
        background.draw(app.batch);
        app.batch.end();

        stage.act(delta);
        stage.draw();


    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
    }


    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void pause() {
        super.pause();
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
        background.getTexture().dispose();
        atlas.dispose();
        sound.dispose();
    }

    public void initMenu() {
        // the panels dimensions, play around with these
        int panelWidth = 300;
        int panelHeight = 270;

        menuUI = new MenuUI(stage);
        menuUI.addBackgroundAndTitle("Simple Space Game", panelWidth, panelHeight, (int) app.VIRTUAL_WIDTH /2 , (int) app.VIRTUAL_HEIGHT /2);


        // the buttons size, play around with these
        int buttonWidth = 280;
        int buttonHeight = 20;

        // top padding between buttons, optional in the "addButton" method call
        int buttonPadding = 20;

        // add buttons in correct order, the first is placed at the top
        menuUI.addTextButton("play", "playBtn", buttonWidth, buttonHeight + 50, buttonPadding);
        menuUI.addTextButton("highscores", "highscoreBtn", buttonWidth, buttonHeight, buttonPadding);
        menuUI.addTextButton("exit", "exitBtn", buttonWidth, buttonHeight, buttonPadding);
        menuUI.getTextButton("playBtn").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                sound.play();
                stage.addAction(sequence(moveBy(0,800, 1.5f, Interpolation.swing), run(new Runnable() {
                    @Override
                    public void run() {
                        app.setScreen(app.gameScreen);
                    }
                })));

            }
        });

        menuUI.getTextButton("highscoreBtn").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                app.setScreen(app.highscoreScreen);
                dispose();
                sound.play();
            }
        });

        menuUI.getTextButton("exitBtn").addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                sound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });


}

}
