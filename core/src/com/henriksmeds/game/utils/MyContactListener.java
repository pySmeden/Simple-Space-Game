package com.henriksmeds.game.utils;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.henriksmeds.game.elements.Asteroids;
import com.henriksmeds.game.elements.PhysicsPlayer;
import com.henriksmeds.game.elements.Star;

/**
 * Created by Henrik on 2017-06-27.
 */

public class MyContactListener implements ContactListener{
    World world;
    public MyContactListener(World world) {
        this.world = world;
    }

    @Override
    public void beginContact(Contact contact) {
    }

    @Override
    public void endContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();

        if(bodyA  == null || bodyB == null) return;

        if(hasPlayerContactStar(bodyA, bodyB)) {
            if(bodyA.getUserData() instanceof Star) System.out.print("blah");
        }
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
    private boolean hasPlayerContactStar(Body bodyA, Body bodyB) {
        boolean isContact = false;
        if(bodyA.getUserData() instanceof Star ||bodyA.getUserData() instanceof Star){
            if(bodyB.getUserData() instanceof PhysicsPlayer || bodyB.getUserData() instanceof PhysicsPlayer) {
                isContact = true;
            }
        }
        return isContact;
    }

}
