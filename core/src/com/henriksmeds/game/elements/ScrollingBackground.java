package com.henriksmeds.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.henriksmeds.game.Application;

/**
 * Created by Henrik on 2017-05-30.
 */

public class ScrollingBackground extends Actor {
    private TextureRegion textureRegion;
    private Texture texture;
    private TextureAtlas atlas;
    private final Application app;

    public float scrollspeed;

    public ScrollingBackground(float scrollspeed) {
        app = new Application();
        this.scrollspeed = scrollspeed;
        atlas = new TextureAtlas(Gdx.files.internal("game-elements/assets.pack"));
        texture = new Texture("background.png");
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        textureRegion = new TextureRegion(texture, texture.getWidth(), texture.getHeight() * 2);
        setWidth(app.WORLD_WIDTH);
        setHeight(app.WORLD_HEIGHT);
        setPosition(0, app.WORLD_HEIGHT);

        addAction(forever(sequence(moveTo(0, 0, this.scrollspeed), moveTo(0, app.WORLD_HEIGHT))));


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(textureRegion, getX(), getY() - getHeight(), getWidth(), getHeight() * 2);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
