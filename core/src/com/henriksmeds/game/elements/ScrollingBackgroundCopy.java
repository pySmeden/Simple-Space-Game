package com.henriksmeds.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

import com.henriksmeds.game.Application;

/**
 * Created by Henrik on 2017-05-30.
 */

public class ScrollingBackgroundCopy extends Actor {
    private TextureRegion textureRegion;
    private Texture texture;
    private TextureAtlas atlas;
    private final Application app;

    public float scrollspeed;

    public ScrollingBackgroundCopy(float scrollspeed) {
        app = new Application();
        this.scrollspeed = scrollspeed;
        atlas = new TextureAtlas(Gdx.files.internal("game-elements/assets.pack"));
        texture = new Texture("background.png");
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        textureRegion = new TextureRegion(texture, texture.getWidth(), texture.getHeight() * 2);
        setWidth(app.WORLD_WIDTH);
        setHeight(app.WORLD_HEIGHT);
        setPosition(0f, app.WORLD_HEIGHT);

        addAction(sequence(moveTo(0,0, 17, Interpolation.pow2In),moveTo(0f, app.WORLD_HEIGHT), run(new Runnable() {
            @Override
            public void run() {
                addAction(forever(sequence(moveTo(0f, 0f, 7), moveTo(0f, app.WORLD_HEIGHT))));
            }
        })));

        //addAction(forever(sequence(moveTo(0f, 0f, this.scrollspeed), moveTo(0f, app.WORLD_HEIGHT))));


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(textureRegion, getX(), getY() - getHeight(), getWidth(), getHeight() * 2f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}