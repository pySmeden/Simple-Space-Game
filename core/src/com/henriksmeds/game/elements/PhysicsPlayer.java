package com.henriksmeds.game.elements;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.codeandweb.physicseditor.PhysicsShapeCache;

/**
 * Created by Henrik on 2017-06-11.
 */

public class PhysicsPlayer extends Actor {
    public PhysicsShapeCache shape;
    private float health;
    public Body playerBody;
    public Sprite sprite;
    private TextureAtlas atlas;

    public PhysicsPlayer(Sprite sprite,float sizeX, float sizeY) {
        atlas = new TextureAtlas("game-elements/assets.pack");
        this.health = 100;
        this.sprite = sprite;
        this.sprite.setSize(sizeX, sizeY);
    }

    public void setPhysicsShape(String internalPath, World physicsWorld, float scaleX, float scaleY) {
        shape = new PhysicsShapeCache(internalPath);
        this.playerBody = shape.createBody("ship1_02", physicsWorld,scaleX, scaleY);
        this.playerBody.setUserData(this);
    }

    public void playerDamage(){
        this.health -= 10;
    }


    public float Health() {
        return this.health;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        this.sprite.setPosition(playerBody.getPosition().x, playerBody.getPosition().y);
        this.sprite.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.playerBody.setTransform(playerBody.getPosition().x, -2 + getY(),0);
    }

}
