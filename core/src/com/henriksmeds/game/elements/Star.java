package com.henriksmeds.game.elements;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.codeandweb.physicseditor.PhysicsShapeCache;

/**
 * Created by Henrik on 2017-05-30.
 */

public class Star implements Pool.Poolable{
    public boolean alive;
    PhysicsShapeCache shapeCache = new PhysicsShapeCache("physics.xml");
    Body body;
    Sprite sprite;

    public Star(World world, float width, float height) {
        this.alive = true;
        this.body = shapeCache.createBody("star", world, width, height);
        this.body.setUserData(this);
        this.body.setActive(true);
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.sprite.setSize(this.sprite.getWidth() * 0.01f * 0.25f, this.sprite.getHeight() * 0.01f * 0.25f);
        this.sprite.setOrigin(this.body.getPosition().x * 0.01f * 0.25f, this.body.getPosition().y * 0.01f * 0.25f);
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
        this.sprite.setPosition(position.x, position.y);
        this.sprite.setRotation(degrees);
        this.sprite.draw(batch);
    }

    @Override
    public void reset() {
        this.getBody().setActive(true);
        this.alive = true;
        this.body.setTransform(0,0,0);
        this.body.setLinearVelocity(0,0);
    }
}
