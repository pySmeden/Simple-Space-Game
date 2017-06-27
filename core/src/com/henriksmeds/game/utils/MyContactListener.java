package com.henriksmeds.game.utils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.henriksmeds.game.elements.Asteroids;
import com.henriksmeds.game.elements.PhysicsPlayer;
import com.henriksmeds.game.elements.Star;

/**
 * Created by Henrik on 2017-06-27.
 */

public class MyContactListener implements ContactListener{

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;

        if (hasPlayerContactStar(fa, fb)) {
            System.out.println("Touch");
        }

        hasPlayerContactAsteroid(fa, fb);

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
    private boolean hasPlayerContactStar(Fixture fa, Fixture fb) {
        boolean isContact = false;
        if(fa.getBody().getUserData() instanceof Star || fb.getBody().getUserData() instanceof Star){
            if(fa.getBody().getUserData() instanceof PhysicsPlayer || fb.getBody().getUserData() instanceof PhysicsPlayer) {
                isContact = true;
            }
        }
        return isContact;
    }

    private boolean hasPlayerContactAsteroid(Fixture fa, Fixture fb) {
        boolean isContact = false;
        if(fa.getBody().getUserData() instanceof Asteroids || fb.getBody().getUserData() instanceof Asteroids){
            if(fa.getBody().getUserData() instanceof PhysicsPlayer || fb.getBody().getUserData() instanceof PhysicsPlayer) {
                isContact = true;
            }
        }
        return isContact;
    }
}
