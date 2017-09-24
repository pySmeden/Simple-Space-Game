package com.henriksmeds.game.utils;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.henriksmeds.game.elements.Asteroids;
import com.henriksmeds.game.elements.Star;

import java.util.Stack;

/**
 * Created by Henrik on 2017-05-30.
 */

public class Assets {

    public AssetManager manager;
    public String gameAssets = "game-elements/assets.pack";
    public String clickSound = "sounds/button-click.ogg";
    public String scoreSound = "sounds/score.wav";
    public String crashSound = "sounds/MetalClang.wav";
    public String thrustersSound = "sounds/RocketThrusters.wav";
    public String explosionSounds = "sounds/Explosion.wav";


    public Assets() {
        manager = new AssetManager();
    }

    public void load() {
        manager.load(gameAssets, TextureAtlas.class);
        manager.load(clickSound, Sound.class);
        manager.load(scoreSound, Sound.class);
        manager.load(crashSound, Sound.class);
        manager.load(explosionSounds, Sound.class);
        manager.load(thrustersSound, Music.class);

    }

}
