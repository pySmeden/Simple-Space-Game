package com.henriksmeds.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.henriksmeds.game.Application;
import com.henriksmeds.game.utils.Assets;

import javax.sound.midi.Sequence;

/**
 * Created by Henrik on 2017-08-18.
 */

public class SplashScreen extends ScreenAdapter {
    private final Application app;
    Stage stage;
    Actor logo;
    OrthographicCamera camera;
    FillViewport fillViewport;
    Sprite logoSprite;
    public SplashScreen(final Application app) {this.app = app;}

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.97f, 0.97f, 0.97f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        camera.update();
        stage.act(delta);

        app.batch.begin();
        logoSprite.setPosition(logo.getX(), logo.getY());
        logoSprite.draw(app.batch);
        app.batch.end();

        stage.draw();
        if(app.assets.manager.update() && !logo.hasActions()) app.setScreen(app.menuScreenCopy);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        stage.getViewport().update(width, height, true);
        camera.update();

    }

    @Override
    public void show() {
        super.show();
        logoSprite = new Sprite(new Texture(Gdx.files.internal("game-elements/flowRoot5734.png")));
        logoSprite.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        float spriteRatio = logoSprite.getHeight() / logoSprite.getWidth();
        float scaleX = app.VIRTUAL_WIDTH / spriteRatio;
        float scaleY = app.VIRTUAL_HEIGHT * spriteRatio;
        logoSprite.setSize(scaleX / 2.5f, scaleY/ 2.5f);

        logo = new Actor();
        logo.setPosition(- logoSprite.getWidth(), app.VIRTUAL_HEIGHT/2f - logoSprite.getHeight() / 2f);
        camera = new OrthographicCamera();
        camera.setToOrtho(false);

        fillViewport = new FillViewport(app.VIRTUAL_WIDTH, app.VIRTUAL_HEIGHT, camera);

        stage = new Stage(fillViewport, app.batch);
        stage.getViewport().apply();
        stage.addActor(logo);

        app.assets.load();
        logo.addAction(Actions.sequence(Actions.moveBy(0,0,0.3f), Actions.moveTo(app.VIRTUAL_WIDTH / 2f - logoSprite.getWidth()/2f ,app.VIRTUAL_HEIGHT/2f - logoSprite.getHeight() / 2f,1.5f, Interpolation.swing),
                Actions.moveBy(0,0,0.5f),
                Actions.moveTo(app.VIRTUAL_WIDTH + logoSprite.getWidth()/2f, app.VIRTUAL_HEIGHT/2f - logoSprite.getHeight() / 2f ,1.5f, Interpolation.swing)));

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
    }
}
