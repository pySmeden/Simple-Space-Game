package com.henriksmeds.game.elements;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.codeandweb.physicseditor.PhysicsShapeCache;

import static com.badlogic.gdx.Gdx.app;

/**
 * Created by Henrik on 2017-06-26.
 */

public class Star {
    PhysicsShapeCache shapeCache = new PhysicsShapeCache("physics.xml");
    Body body;
    TextureAtlas atlas = new TextureAtlas("game-elements/assets.pack");
    Sprite sprite = new Sprite(atlas.findRegion("star"));

    public Star(World world, float width, float height) {
        this.body = shapeCache.createBody("star", world, width, height);
        this.body.setUserData(this);
        sprite.setSize(sprite.getWidth() * 0.01f * 0.25f, sprite.getHeight() * 0.01f * 0.25f);
        sprite.setOrigin(this.body.getPosition().x * 0.01f * 0.25f, this.body.getPosition().y * 0.01f * 0.25f);
    }
    public Body getBody() {
        return this.body;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void drawStar(Batch batch) {
        Vector2 position = this.body.getPosition();
        float degrees = (float) Math.toDegrees(this.body.getAngle());
        sprite.setPosition(position.x, position.y);
        sprite.setRotation(degrees);
        sprite.draw(batch);

    }


}
