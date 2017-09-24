package com.henriksmeds.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.henriksmeds.game.Application;
import com.henriksmeds.game.elements.Asteroids;
import com.henriksmeds.game.elements.Explosion;
import com.henriksmeds.game.elements.PhysicsPlayer;
import com.henriksmeds.game.elements.ScrollingBackgroundCopy;
import com.henriksmeds.game.elements.Star;
import com.henriksmeds.game.utils.HighScore;

import java.util.Iterator;


/**
 * Created by Henrik on 2017-08-20.
 */
public class GameScreenCopy extends ScreenAdapter  {
    private final Application app;

    static final int GAME_INTRO = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_OVER = 3;
    int gameState = 0;

    int currentScore = 0;

    Sound btnSound, scoreSound, crashSound, explosionSound;
    ParticleEffect particleEffect, explosionEffect, rocketEffect;
    Label scoreLabel;
    Stage worldStage, backgroundStage, guiStage, guiPausedStage, gameOverGui;
    Button  pauseBtn;
    OrthographicCamera worldCam, guiCam;
    FillViewport worldViewport, guiViewport;
    Skin skin;
    Body playerAnchor;
    PrismaticJointDef pDef;
    ShapeRenderer shapeRenderer;
    Explosion explosion;
    World world;
    Box2DDebugRenderer b2drender;
    AsteroidPool pool;
    StarPool starPool;
    Array asteroidArray, starArray;
    PhysicsPlayer myPlayer;
    Sprite playerSprite;
    PhysicsShapeCache physicsShapeCache;
    Music music;
    TextureAtlas atlas;
    float xLerp;

    ScrollingBackgroundCopy scrollingBackground;

    public GameScreenCopy(final Application app) {

        this.app = app;
        music = Gdx.audio.newMusic(Gdx.files.internal("sounds/RocketThrusters.wav"));
        music.setLooping(true);
        music.setVolume(0.08f);

        particleEffect = new ParticleEffect();
        explosionEffect = new ParticleEffect();
        rocketEffect = new ParticleEffect();
        explosionEffect.load(Gdx.files.internal("explosion"),Gdx.files.internal(""));
        particleEffect.load(Gdx.files.internal("Stars2"), Gdx.files.internal(""));
        rocketEffect.load(Gdx.files.internal("rocket"), Gdx.files.internal(""));

        skin = new Skin(Gdx.files.internal("gui/uiskin.json"), new TextureAtlas(Gdx.files.internal("gui/uiAtlas.atlas")));
        atlas = new TextureAtlas("game-elements/assets.pack");
        asteroidArray = new Array();
        starArray = new Array();

        explosion = new Explosion(0.2f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if(gameState == GAME_RUNNING ) {
                    Asteroids asteroid = pool.obtain();
                    asteroidArray.add(asteroid);

                    Sprite sprite = new Sprite(atlas.findRegion("asteroid"));

                    asteroid.setSprite(sprite);

                    float randX = MathUtils.random(0f + asteroid.getSprite().getWidth() * app.SCALE * 0.25f, 4.0f - 10 * asteroid.getSprite().getWidth() * app.SCALE * 0.25f);
                    asteroid.getBody().setTransform(randX,9,0);
                    asteroid.getBody().setLinearVelocity(0,0);
                    float randAngularVel = MathUtils.random(-1,1);
                    asteroid.getBody().setAngularVelocity(randAngularVel);

                }
            }
        }, 5, MathUtils.random(1f,1f));

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if(gameState == GAME_RUNNING ) {
                    Star star = starPool.obtain();
                    starArray.add(star);
                    Sprite sprite = new Sprite(atlas.findRegion("star"));

                    star.setSprite(sprite);

                    float randX = MathUtils.random(0f + star.getSprite().getWidth() * app.SCALE * 0.25f, 4.0f - 10 * star.getSprite().getWidth() * app.SCALE * 0.25f);
                    star.getBody().setTransform(randX,9,0);
                    star.getBody().setLinearVelocity(0,0);
                    star.getBody().setAngularVelocity(0);

                }
            }
        }, 5, MathUtils.random(5f,10f));

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);


        switch (gameState) {
            case GAME_INTRO:
                updateIntro(delta);
                break;
            case GAME_RUNNING:
                updateRunning(delta);
                break;
            case GAME_PAUSED:
                updatePaused(delta);
                break;
            case GAME_OVER:
                updateGameOver(delta);
                break;
        }



        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0,1,0,0.5f);
        if(xLerp < 1) {
            xLerp = xLerp + (xLerp + delta) * 0.03f;
        } else xLerp = 1;
        shapeRenderer.rect(0.2f,0.1f,4.4f * xLerp * myPlayer.getHealth(),0.2f);
        shapeRenderer.end();

        //b2drender.render(world, worldCam.combined);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        worldStage.getViewport().update(width, height, true);
        backgroundStage.getViewport().update(width, height, true);
        guiStage.getViewport().update(width, height, true);
        guiPausedStage.getViewport().update(width, height, true);
        gameOverGui.getViewport().update(width, height, true);
        app.batch.setProjectionMatrix(guiCam.combined);
    }

    private void updateIntro(float delta) {
        backgroundStage.act(delta);
        worldStage.act(delta);
        guiStage.act(delta);

        rocketEffect.setPosition(myPlayer.playerBody.getPosition().x + playerSprite.getWidth()/2 + 0.05f, myPlayer.playerBody.getPosition().y  + 0.05f);
        rocketEffect.update(delta);

        if(myPlayer.getActions().size == 0) {
            gameState = GAME_RUNNING;
            Gdx.input.setInputProcessor(guiStage);
        }
        scoreLabel.setText("Score:"+" "+Integer.toString(currentScore));
        backgroundStage.draw();
        app.batch.begin();
        rocketEffect.draw(app.batch);
        app.batch.end();

        worldStage.draw();

        guiStage.draw();

    }
    private void updateRunning(float delta) {
        world.step(1f/60f, 6, 2);
        backgroundStage.act(delta);
        worldStage.act(delta);
        guiStage.act(delta);
        rocketEffect.update(delta);
        particleEffect.update(delta);
        backgroundStage.draw();

        app.batch.begin();
        if(myPlayer.alive) {
            rocketEffect.setPosition(myPlayer.playerBody.getPosition().x + playerSprite.getWidth() / 2 + 0.05f, myPlayer.playerBody.getPosition().y + 0.05f);
            rocketEffect.draw(app.batch);
        }

        removeAsteroids();

        float x = Gdx.input.getAccelerometerX();
        myPlayer.playerBody.setLinearVelocity(-x, 0);

        if(!myPlayer.alive) {

            myPlayer.playerBody.setActive(false);
            explosionEffect.setPosition(myPlayer.playerBody.getPosition().x  + playerSprite.getWidth() / 2f, myPlayer.playerBody.getPosition().y + playerSprite.getHeight() / 2f);
            explosionEffect.update(delta);
            explosionEffect.draw(app.batch);

            if(explosionEffect.isComplete()) {
                gameState = GAME_OVER;
                Gdx.input.setInputProcessor(gameOverGui);
            }
        }
        scoreLabel.setText("Score:"+" "+Integer.toString(currentScore));

        drawSprites();
        particleEffect.draw(app.batch);


        app.batch.end();
        worldStage.draw();
        guiStage.draw();
    }
    private void updatePaused(float delta) {

        guiPausedStage.act(delta);
        backgroundStage.draw();
        app.batch.begin();
        rocketEffect.setPosition(myPlayer.playerBody.getPosition().x + playerSprite.getWidth() / 2 + 0.05f, myPlayer.playerBody.getPosition().y + 0.05f);
        rocketEffect.draw(app.batch);
        drawSprites();

        app.batch.end();

        worldStage.draw();

        guiPausedStage.draw();
    }
    private void updateGameOver(float delta) {
        world.step(1f/60f, 6, 2);
        gameOverGui.act(delta);
        backgroundStage.act(delta);
        backgroundStage.draw();
        worldStage.act(delta);
        worldStage.draw();
        scoreLabel.setText("Score:"+" "+Integer.toString(currentScore));
        app.batch.begin();
        drawSprites();
        app.batch.end();

        gameOverGui.draw();
    }


    @Override
    public void show() {
        super.show();
        btnSound = app.assets.manager.get(app.assets.clickSound, Sound.class);
        scoreSound = app.assets.manager.get(app.assets.scoreSound, Sound.class);
        crashSound = app.assets.manager.get(app.assets.crashSound, Sound.class);
        explosionSound = app.assets.manager.get(app.assets.explosionSounds, Sound.class);
        world = new World(new Vector2(0, -2f), true);
        createPlayer();
        rocketEffect.setPosition(myPlayer.playerBody.getPosition().x + playerSprite.getWidth()/2, myPlayer.playerBody.getPosition().y + 10f);
        rocketEffect.start();
        createPlayerAnchor();
        createPrismaticJoint();
        createWalls();
        gameState = GAME_INTRO;
        setContactListener();
        initGuiStage();
        initPauseGui();
        initGameOverGui();
        xLerp = 0;
        myPlayer.playerBody.setTransform(1.8f, 0, 0);
        myPlayer.addAction(Actions.moveBy(0,4f,5f, Interpolation.smooth));

        shapeRenderer = new ShapeRenderer();
        explosion = new Explosion(0.2f);

        pool = new AsteroidPool(20, world);
        starPool = new StarPool(20, world);

        b2drender = new Box2DDebugRenderer();
        worldCam = new OrthographicCamera();
        worldViewport = new FillViewport(app.WORLD_WIDTH, app.WORLD_HEIGHT, worldCam);
        backgroundStage = new Stage(worldViewport, app.batch);
        backgroundStage.getViewport().apply();
        worldStage = new Stage(worldViewport, app.batch);
        worldStage.getViewport().apply();

        scrollingBackground = new ScrollingBackgroundCopy(15f);

        backgroundStage.addActor(scrollingBackground);
        worldStage.addActor(myPlayer);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(worldCam.combined);


        Gdx.input.setInputProcessor(guiStage);
        music.play();

    }

    @Override
    public void hide() {
        super.hide();
        removeAsteroids();
        asteroidArray.clear();
        guiPausedStage.dispose();
        gameOverGui.dispose();
        guiStage.dispose();
        world.dispose();
        pool.clear();
        myPlayer.clear();
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

    private void removeAsteroids(){

        Iterator<Asteroids> iterator = asteroidArray.iterator();
        while(iterator.hasNext()) {
            Asteroids asteroid = iterator.next();
            if(asteroid.getBody().getPosition().y < -1 || gameState == GAME_OVER) {
                pool.free(asteroid);
                iterator.remove();
            }
        }

        Iterator<Star> starIterator = starArray.iterator();
        while(starIterator.hasNext()) {
            Star star = starIterator.next();
            if(star.getBody().getPosition().y < -1 || gameState == GAME_OVER || !star.alive) {
                if(!star.alive) {
                    star.getBody().setActive(false);
                    starPool.free(star);
                    starIterator.remove();
                    currentScore += 100;
                    scoreSound.play(0.1f);
                } else {
                    starPool.free(star);
                    starIterator.remove();
                }

            }
        }
    }

    private void drawSprites() {

        Iterator<Asteroids> iterator = asteroidArray.iterator();
        while(iterator.hasNext()) {
            iterator.next().drawAsteroid(app.batch);
        }

        Iterator<Star> starIterator = starArray.iterator();
        while(starIterator.hasNext()) {
            starIterator.next().drawStar(app.batch);
        }


    }

    private void createPlayer() {
        physicsShapeCache = new PhysicsShapeCache("physics.xml");
        playerSprite = new Sprite(new Texture(Gdx.files.internal("game-elements/shipCut.png")));
        myPlayer = new PhysicsPlayer(playerSprite, playerSprite.getWidth() * 0.01f / 2, playerSprite.getHeight() * 0.01f / 2);
        myPlayer.setPhysicsShape("physics.xml", world, 0.01f/2f, 0.01f/2f);
        myPlayer.playerBody.setLinearDamping(20f);
    }

    private void createPlayerAnchor() {

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.maskBits = 0;

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(2f, 0.1f);

        fixtureDef.shape = shape;

        playerAnchor = world.createBody(def);
        playerAnchor.createFixture(fixtureDef);
        playerAnchor.setTransform(2,2,0);

        shape.dispose();

    }

    private void createPrismaticJoint() {
        pDef = new PrismaticJointDef();
        pDef.bodyA = myPlayer.playerBody;
        pDef.bodyB = playerAnchor;

        pDef.collideConnected = false;
        world.createJoint(pDef);
    }

    private void createWalls() {
        Body wall;
        BodyDef wallDef = new BodyDef();
        wallDef.type = BodyDef.BodyType.StaticBody;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = 4;
        fixtureDef.filter.maskBits = 5;

        ChainShape shape = new ChainShape();
        Vector2[] vertices = {new Vector2(0,8f), new Vector2(0,0), new Vector2(4.8f, 0), new Vector2(4.8f, 8f)};
        shape.createChain(vertices);

        fixtureDef.shape = shape;


        wall = world.createBody(wallDef);
        wall.createFixture(fixtureDef);

        shape.dispose();
    }

    private void initGuiStage() {

        atlas = new TextureAtlas("game-elements/assets.pack");
        guiCam = new OrthographicCamera();
        guiViewport = new FillViewport(app.VIRTUAL_WIDTH, app.VIRTUAL_HEIGHT, guiCam);
        guiStage = new Stage(guiViewport, app.batch);

        scoreLabel = new Label("Score:"+" "+Integer.toString(currentScore), skin);
        pauseBtn = new Button(skin, "pause");

        scoreLabel.setPosition(40, 760);
        pauseBtn.setPosition(400, 740);


        pauseBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnSound.play();
                music.pause();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(gameState != GAME_INTRO) gameState = GAME_PAUSED;
                Gdx.input.setInputProcessor(guiPausedStage);
            }
        });

        guiStage.addActor(pauseBtn);
        guiStage.addActor(scoreLabel);

    }

    private void initPauseGui() {
        Table table = new Table(skin);
        int width = 300;
        int height = 180;
        float offsetX = app.VIRTUAL_WIDTH /2 - width / 2f;
        float offsetY = app.VIRTUAL_HEIGHT /2 - height / 2f;
        float imageHeight = 50f;

        guiPausedStage = new Stage(guiViewport, app.batch);


        // optional: use "blue_panel" or "green_panel" as background
        table.setBackground(new NinePatchDrawable(skin.getPatch("grey_panel")));
        table.getBackground().setMinWidth(width);
        table.getBackground().setMinHeight(height);
        table.setPosition(offsetX, offsetY);
        table.pack();

        TextButton returnBtn = new TextButton("Return", skin);
        TextButton exitBtn = new TextButton("Exit", skin);

        table.add(returnBtn).minWidth(280).minHeight(20);
        table.row().colspan(1).padTop(20);

        table.add(exitBtn).minWidth(280).minHeight(20);
        table.row().colspan(1).padTop(20);


        Image image = new Image(new NinePatchDrawable(skin.getPatch("blue_panel")));
        image.setSize(width, imageHeight);
        image.setPosition(offsetX, table.getY() + table.getBackground().getMinHeight() - 10f);

        Label titleLabel = new Label("Game Over", skin);
        titleLabel.setColor(Color.DARK_GRAY);
        titleLabel.setPosition(app.VIRTUAL_WIDTH /2 - titleLabel.getMinWidth()/2f, image.getY() + titleLabel.getMinHeight());
        guiPausedStage.addActor(table);

        returnBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnSound.play();
                music.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameState = GAME_RUNNING;
                Gdx.input.setInputProcessor(guiStage);
            }
        });

        exitBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                HighScore.load();
                HighScore.addScore(currentScore);
                HighScore.save();
                Gdx.app.exit();
            }
        });

    }

    private void initGameOverGui() {

        HighScore.load();
        HighScore.addScore(currentScore);
        HighScore.save();
        currentScore = 0;

        gameOverGui = new Stage(guiViewport, app.batch);

        Table table = new Table(skin);
        int width = 300;
        int height = 270;
        float offsetX = app.VIRTUAL_WIDTH /2 - width / 2f;
        float offsetY = app.VIRTUAL_HEIGHT /2 - height / 2f;
        float imageHeight = 50f;

        // optional: use "blue_panel" or "green_panel" as background
        table.setBackground(new NinePatchDrawable(skin.getPatch("grey_panel")));
        table.getBackground().setMinWidth(width);
        table.getBackground().setMinHeight(height);
        table.setPosition(offsetX, offsetY);
        table.pack();

        TextButton retryBtn = new TextButton("Retry", skin);
        TextButton mainBtn = new TextButton("Main Menu", skin);
        TextButton exitBtn = new TextButton("Exit", skin);

        table.add(retryBtn).minWidth(280).minHeight(20);
        table.row().colspan(1).padTop(20);

        table.add(mainBtn).minWidth(280).minHeight(20);
        table.row().colspan(1).padTop(20);

        table.add(exitBtn).minWidth(280).minHeight(20);
        table.row().colspan(1).padTop(20);


        Image image = new Image(new NinePatchDrawable(skin.getPatch("blue_panel")));
        image.setSize(width, imageHeight);
        image.setPosition(offsetX, table.getY() + table.getBackground().getMinHeight() - 10f);

        Label titleLabel = new Label("Game Over", skin);
        titleLabel.setColor(Color.DARK_GRAY);
        titleLabel.setPosition(app.VIRTUAL_WIDTH /2 - titleLabel.getMinWidth()/2f, image.getY() + titleLabel.getMinHeight());
        gameOverGui.addActor(table);

        retryBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnSound.play();
                music.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                removeAsteroids();
                HighScore.load();
                HighScore.addScore(currentScore);
                HighScore.save();
                rocketEffect.reset();
                myPlayer.playerBody.setActive(true);
                currentScore = 0;
                xLerp = 0f;
                gameState = GAME_INTRO;
                myPlayer.addAction(Actions.sequence(Actions.moveTo(1.8f,0), Actions.moveBy(0,4f,5f, Interpolation.smooth)));
                asteroidArray.clear();
                myPlayer.reset();

                Gdx.input.setInputProcessor(guiStage);
            }
        });

        mainBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnSound.play();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameState = GAME_INTRO;
                rocketEffect.reset();
                HighScore.load();
                HighScore.addScore(currentScore);
                HighScore.save();
                app.setScreen(app.menuScreenCopy);

            }
        });

        exitBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btnSound.play();
                HighScore.load();
                HighScore.addScore(currentScore);
                HighScore.save();
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });
    }

    private void setContactListener() {
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

            }

            @Override
            public void endContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                if(bodyA.getUserData() instanceof Star || bodyB.getUserData() instanceof Star){
                    if(bodyA.getUserData() instanceof PhysicsPlayer || bodyB.getUserData() instanceof PhysicsPlayer) {



                        if (bodyA.getUserData() instanceof Star) {
                        }
                        if (bodyB.getUserData() instanceof Star) {
                            scoreSound.play(0.2f);
                            particleEffect.getEmitters().first().setPosition(bodyB.getPosition().x, bodyB.getPosition().y + 0.2f);
                            ((Star) bodyB.getUserData()).alive = false;
                        }
                        particleEffect.start();
                    }
                }

                if(bodyA.getUserData() instanceof Asteroids || bodyB.getUserData() instanceof Asteroids) {
                    if(bodyA.getUserData() instanceof PhysicsPlayer || bodyB.getUserData() instanceof PhysicsPlayer) {
                        crashSound.play(0.03f);
                        myPlayer.playerDamage();
                        if(!myPlayer.alive) {
                            explosionSound.play(0.2f);
                            music.pause();
                            explosionEffect.getEmitters().first().setPosition(myPlayer.playerBody.getPosition().x + playerSprite.getWidth()/2f, myPlayer.playerBody.getPosition().y + playerSprite.getHeight()/2f);
                            explosionEffect.start();
                        }
                    }
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });
    }



}

class AsteroidPool extends Pool<Asteroids> {

    World world;
    public AsteroidPool(World world) {
        this.world = world;
    }

    public AsteroidPool(int initialCapacity, World world) {
        super(initialCapacity);
        this.world = world;
    }

    @Override
    protected Asteroids newObject() {
        return new Asteroids(this.world, 0.01f / 4, 0.01f / 4);
    }
}

class StarPool extends Pool<Star> {

    World world;
    public StarPool(World world) {
        this.world = world;
    }

    public StarPool(int initialCapacity, World world) {
        super(initialCapacity);
        this.world = world;
    }

    @Override
    protected Star newObject() {
        return new Star(this.world, 0.01f / 4, 0.01f / 4);
    }
}