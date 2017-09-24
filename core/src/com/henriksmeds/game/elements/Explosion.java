package com.henriksmeds.game.elements;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.henriksmeds.game.Application;

/**
 * Created by Henrik on 2017-06-07.
 */

public class Explosion {
    public float frameDuration; //display each frame 1/3 of a second
    public float stateTime;
    public Sprite[] explosionFrames;
    public TextureAtlas atlas;
    public Animation<Sprite> explotionAnimation;
    public boolean isFinished = false;

    public Explosion(float frameDuration) {
        stateTime = 0f;
        this.frameDuration = frameDuration;
        this.explosionFrames = new Sprite[3];
        atlas = new TextureAtlas("game-elements/assets.pack");
        for(int i = 0; i <= 2; i++) {
            explosionFrames[i] = new Sprite(atlas.findRegion("explosion0" + Integer.toString(i + 1)));
            explosionFrames[i].setSize(explosionFrames[i].getWidth() * 0.01f / 4, explosionFrames[i].getHeight() * 0.01f / 4);
            System.out.println(explosionFrames[i].getWidth());
            System.out.println(explosionFrames[i].getHeight());
        }

        explotionAnimation = new Animation<Sprite>( this.frameDuration, explosionFrames);
    }

    public void render(SpriteBatch batch, float delta, float x, float y) {
        stateTime += delta;
        TextureRegion currentFrame = explotionAnimation.getKeyFrame(stateTime, false);
        Sprite sprite = new Sprite(currentFrame);
        sprite.setSize(2,2);
        sprite.setPosition(x, y);
        if(!explotionAnimation.isAnimationFinished(stateTime)) {
            sprite.draw(batch);
        } else {
            isFinished = true;
        }
    }

    public void reset() {
        this.stateTime = 0;
        isFinished = false;
    }
}
