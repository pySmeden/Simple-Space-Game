package com.henriksmeds.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.henriksmeds.game.Application;
import com.henriksmeds.game.elements.MenuUI;
import com.henriksmeds.game.utils.HighScore;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by Henrik on 2017-08-20.
 */

public class MenuScreenCopy extends ScreenAdapter {
    static final int MAIN_MENU= 0;
    static final int HIGHSCORE_MENU = 1;
    int gameState = 0;

    private final Application app;
    Stage stage;
    Stage highScoreStage;
    OrthographicCamera worldCam;
    FillViewport worldViewport;
    Sprite background;
    TextureAtlas atlas;
    Sound sound;
    Table table;
    Table highScoreTable;
    Skin skin;
    Image image;
    Label titleLabel;

    public MenuScreenCopy(final Application app) {
        this.app = app;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        app.batch.begin();
        background.draw(app.batch);
        app.batch.end();

        switch (gameState) {
            case MAIN_MENU:
                stage.act(delta);
                stage.draw();
                break;
            case HIGHSCORE_MENU:
                highScoreStage.act(delta);
                highScoreStage.draw();
                break;
        }

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
        highScoreStage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {

        super.show();
        sound = app.assets.manager.get(app.assets.clickSound, Sound.class);
        atlas = app.assets.manager.get(app.assets.gameAssets, TextureAtlas.class);

        skin = new Skin(Gdx.files.internal("gui/uiskin.json"), new TextureAtlas(Gdx.files.internal("gui/uiAtlas.atlas")));
        table = new Table(skin);
        highScoreTable = new Table(skin);

        background = new Sprite(atlas.findRegion("background"));
        background.setSize(app.VIRTUAL_WIDTH, app.VIRTUAL_HEIGHT);
        worldCam = new OrthographicCamera();
        worldCam.setToOrtho(false);
        worldViewport = new FillViewport(app.VIRTUAL_WIDTH, app.VIRTUAL_HEIGHT, worldCam);

        stage = new Stage(worldViewport, app.batch);
        stage.getViewport().apply();

        highScoreStage = new Stage(worldViewport, app.batch);
        highScoreStage.getViewport().apply();

        initMain();
        initHighScoreMenu();

        Gdx.input.setInputProcessor(stage);

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
    }

    void initMain() {
        int panelWidth = 300;
        int panelHeight = 270;
        float positionX = app.VIRTUAL_WIDTH / 2;
        float positionY = app.VIRTUAL_HEIGHT / 2;

        float offsetX = positionX - panelWidth / 2f;
        float offsetY = positionY - panelHeight / 2f;
        float imageHeight = 50f;

        // optional: use "blue_panel" or "green_panel" as background
        table.setBackground(new NinePatchDrawable(skin.getPatch("grey_panel")));
        table.getBackground().setMinWidth(panelWidth);
        table.getBackground().setMinHeight(panelHeight);
        table.setPosition(offsetX, offsetY);
        table.pack();

        image = new Image(new NinePatchDrawable(skin.getPatch("blue_panel")));
        image.setSize(panelWidth, imageHeight);
        image.setPosition(offsetX, table.getY() + table.getBackground().getMinHeight() - 10f);

        titleLabel = new Label("Space Game", skin);
        titleLabel.setColor(Color.DARK_GRAY);
        titleLabel.setPosition(positionX - titleLabel.getMinWidth()/2f, image.getY() + titleLabel.getMinHeight());

        // the buttons size, play around with these
        int buttonWidth = 280;
        int buttonHeight = 20;

        // top padding between buttons, optional in the "addButton" method call
        int buttonPadding = 20;

        TextButton playBtn = new TextButton("Play", skin);
        table.add(playBtn).minWidth(buttonWidth).minHeight(buttonHeight);
        table.row().colspan(1).padTop(20);

        playBtn.addListener(new InputListener() {
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
                        app.setScreen(app.gameScreenCopy);
                    }
                })));
            }
        });

        TextButton highScoreBtn = new TextButton("Highscores", skin);
        table.add(highScoreBtn).minWidth(buttonWidth).minHeight(buttonHeight);
        table.row().colspan(1).padTop(20);

        highScoreBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                sound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(highScoreStage);
                gameState = HIGHSCORE_MENU;
            }
        });

        TextButton exitBtn = new TextButton("Exit", skin);
        table.add(exitBtn).minWidth(buttonWidth).minHeight(buttonHeight);
        table.row().colspan(1).padTop(20);

        exitBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                sound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                app.assets.manager.dispose();
                Gdx.app.exit();
            }
        });


        stage.addActor(image);
        stage.addActor(table);
        stage.addActor(titleLabel);
    }

    void initHighScoreMenu() {
        // the panels dimensions, play around with these

        int panelWidth = 300;
        int panelHeight = 270;

        float positionX = app.VIRTUAL_WIDTH / 2;
        float positionY = app.VIRTUAL_HEIGHT / 2;

        float offsetX = positionX - panelWidth / 2f;
        float offsetY = positionY - panelHeight / 2f;

        float imageHeight = 50f;

        highScoreTable.setBackground(new NinePatchDrawable(skin.getPatch("grey_panel")));
        highScoreTable.getBackground().setMinWidth(panelWidth);
        highScoreTable.getBackground().setMinHeight(panelHeight);
        highScoreTable.setPosition(offsetX, offsetY);
        highScoreTable.pack();

        Image image1 = new Image(new NinePatchDrawable(skin.getPatch("blue_panel")));
        image1.setSize(panelWidth, imageHeight);
        image1.setPosition(offsetX, table.getY() + table.getBackground().getMinHeight() - 10f);

        titleLabel = new Label("Space Game", skin);
        titleLabel.setColor(Color.DARK_GRAY);
        titleLabel.setPosition(positionX - titleLabel.getMinWidth()/2f, image.getY() + titleLabel.getMinHeight());

        HighScore.load();

        for(int i = 0; i < 5; i++) {
            Label scoreLabel = new Label(Integer.toString(i + 1) + ".    " + Integer.toString(HighScore.scores[i]), skin);
            scoreLabel.setColor(Color.DARK_GRAY);
            highScoreTable.add(scoreLabel);
            highScoreTable.row().colspan(1).padTop(20f);
        }

        // add a button of "return" style and id "returnButton"
        Button button = new Button(skin, "return_grey");
        button.setSize(50f,50f);

        highScoreTable.addActor(button);
        highScoreTable.addActor(image1);

        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                sound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(stage);
                gameState = MAIN_MENU;
            }
        });

        highScoreStage.addActor(image1);
        highScoreStage.addActor(highScoreTable);
        highScoreStage.addActor(titleLabel);

    }

}
