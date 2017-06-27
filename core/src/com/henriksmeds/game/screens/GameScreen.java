package com.henriksmeds.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.codeandweb.physicseditor.PhysicsShapeCache;
import com.henriksmeds.game.Application;
import com.henriksmeds.game.elements.PhysicsPlayer;
import com.henriksmeds.game.elements.ScrollingBackground;
import com.henriksmeds.game.elements.Star;
import com.henriksmeds.game.utils.MyContactListener;

import java.util.HashMap;
import java.util.Iterator;


public class GameScreen extends ScreenAdapter implements InputProcessor {
    static final int GAME_INTRO = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_OVER = 3;
    int gameState = 0;
    private int currentScore;

    Star star;

    Button returnBtn, pauseBtn;
    Label scoreLabel;
    Application app;
    Stage worldStage, guiStage;
    OrthographicCamera worldCam, guiCam;
    FillViewport worldViewport, guiViewport;
    ScrollingBackground scrollingBackground;
    World world;
    TextureAtlas atlas;
    Sprite playerSprite;
    Box2DDebugRenderer b2drender;
    PhysicsShapeCache physicsShapeCache;
    Body asteroids, playerAnchor;
    PrismaticJointDef pDef;
    PhysicsPlayer myPlayer;
    Skin skin;
    HashMap<Body, Sprite> hashMap = new HashMap<Body, Sprite>();
    float x = 0.0f;
    float randomX, randomSpin;

    public GameScreen(final Application app) {
        this.app = app;
        world = new World(new Vector2(0, -2f), true);
        b2drender = new Box2DDebugRenderer();
        star = new Star(world, 2, 2);
        initGuiStage();
        createPlayer();
        initWorldStage();
        createPlayerAnchor();
        createPrismaticJoint();
        createWalls();

        world.setContactListener(new MyContactListener());

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if(gameState == GAME_RUNNING) {
                    Sprite sprite = new Sprite(atlas.findRegion("asteroid"));
                    // generate random x-position for the spawning asteroids
                    randomX = MathUtils.random(0f + sprite.getWidth() * app.SCALE * 0.25f, 4.8f - sprite.getWidth() * app.SCALE * 0.25f);
                    float asteroidVelocity = -0.7f;
                    randomSpin = MathUtils.random(-1f, 1f);
                    asteroids = spawnAsteroid(randomX, asteroidVelocity, randomSpin);
                    asteroids.setUserData(sprite);

                    sprite.setOrigin(asteroids.getPosition().x * app.SCALE * 0.25f, asteroids.getPosition().y * app.SCALE * 0.25f);
                    sprite.setSize(sprite.getWidth() * app.SCALE * 0.25f, sprite.getHeight() * app.SCALE * 0.25f);

                    hashMap.put(asteroids, sprite);
                }
            }
        }, 0, MathUtils.random(1f,3f));

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if(gameState == GAME_RUNNING) {
                    star = new Star(world, 0.01f / 4, 0.01f / 4);
                    float randX = MathUtils.random(0f + star.getSprite().getWidth() * app.SCALE * 0.25f, 4.8f - star.getSprite().getWidth() * app.SCALE * 0.25f);
                    star.getBody().setTransform(randX,9,0);
                }
            }
        }, 0, MathUtils.random(5,10));

        InputMultiplexer mux = new InputMultiplexer(guiStage, this);
        Gdx.input.setInputProcessor(mux);

    }

    @Override
    public void render(float delta) {
        super.render(delta);

        Gdx.gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
        b2drender.render(world, worldCam.combined);
    }

    private void updateIntro(float delta) {
        worldStage.act(delta);
        guiStage.act(delta);
        returnBtn.setVisible(false);
        pauseBtn.setVisible(true);

        if(myPlayer.getActions().size == 0) {
            gameState = GAME_RUNNING;
        }

        worldStage.draw();
        guiStage.draw();
    }

    private void updateRunning(float delta) {

        returnBtn.setVisible(false);
        pauseBtn.setVisible(true);

        worldStage.act(delta);
        guiStage.act(delta);
        world.step(1f/60f, 6, 2);
        // removes asteroids that have passed the screen
        destroyAsteroids();
        //x = Gdx.input.getAccelerometerX();
        //myPlayer.playerBody.setLinearVelocity(-x, 0);

        worldStage.draw();

        app.batch.begin();
        drawAsteroidSprite();
        star.drawStar(app.batch);
        app.batch.end();

        guiStage.draw();


    }

    private void updatePaused(float delta) {

        returnBtn.setVisible(true);
        pauseBtn.setVisible(false);

        worldStage.draw();

        app.batch.begin();
        drawAsteroidSprite();
        app.batch.end();

        guiStage.draw();
    }

    private void updateGameOver(float delta) {

    }


    private void createPlayer() {
        physicsShapeCache = new PhysicsShapeCache("physics.xml");
        playerSprite = new Sprite(atlas.findRegion("ship1", 1));
        myPlayer = new PhysicsPlayer(playerSprite, playerSprite.getWidth() * 0.01f / 2, playerSprite.getHeight() * 0.01f / 2);
        myPlayer.setPhysicsShape("physics.xml", world, 0.01f/2f, 0.01f/2f);
        myPlayer.playerBody.setLinearDamping(20f);
    }

    private void initGuiStage() {
        skin = new Skin(Gdx.files.internal("gui/uiskin.json"), new TextureAtlas(Gdx.files.internal("gui/uiAtlas.atlas")));
        atlas = new TextureAtlas("game-elements/assets.pack");
        guiCam = new OrthographicCamera();
        guiViewport = new FillViewport(app.VIRTUAL_WIDTH, app.VIRTUAL_HEIGHT, guiCam);
        guiStage = new Stage(guiViewport, app.batch);

        scoreLabel = new Label("Score:"+" "+Integer.toString(currentScore), skin);
        returnBtn = new Button(skin, "return_white");
        pauseBtn = new Button(skin, "pause");

        scoreLabel.setPosition(20, 760);
        returnBtn.setPosition(420, 740);
        pauseBtn.setPosition(420, 740);


        pauseBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameState = GAME_PAUSED;
            }
        });

        returnBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                gameState = GAME_RUNNING;
            }
        });

        guiStage.addActor(pauseBtn);
        guiStage.addActor(returnBtn);
        guiStage.addActor(scoreLabel);

    }

    private void initWorldStage() {
        worldCam = new OrthographicCamera();
        worldViewport = new FillViewport(app.WORLD_WIDTH, app.WORLD_HEIGHT, worldCam);
        worldStage = new Stage(worldViewport, app.batch);
        worldStage.getViewport().apply();
        scrollingBackground = new ScrollingBackground(15f);
        worldStage.addActor(scrollingBackground);
        worldStage.addActor(myPlayer);
        myPlayer.playerBody.setTransform(1.8f, 0, 0);
        myPlayer.addAction(Actions.moveBy(0,4f,1.7f, Interpolation.smooth));
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
    private Body spawnAsteroid(float x, float linearVelocity, float angularVelocity) {
        asteroids = physicsShapeCache.createBody("asteroid_1", world, 0.01f / 4, 0.01f / 4);
        asteroids.setTransform(x, 8, 0);
        asteroids.setGravityScale(2f);
        asteroids.setLinearVelocity(0, linearVelocity);
        asteroids.setAngularVelocity(angularVelocity);
        return asteroids;
    }

    private void drawAsteroidSprite() {
        for(Body body: hashMap.keySet()) {

            Vector2 position = body.getPosition();
            float degrees = (float) Math.toDegrees(body.getAngle());

            hashMap.get(body).setPosition(position.x, position.y);
            hashMap.get(body).setRotation(degrees);
            hashMap.get(body).draw(app.batch);

        }
    }

    private void destroyAsteroids() {
        Iterator<Body> it = hashMap.keySet().iterator();
        while(it.hasNext()) {
            Body body = it.next();
            if(body != null && body.getPosition().y < -1f) {
                world.destroyBody(body);
                it.remove();
            }
        }
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

    private void createPrismaticJoint() {
        pDef = new PrismaticJointDef();
        pDef.bodyA = myPlayer.playerBody;
        pDef.bodyB = playerAnchor;

        pDef.collideConnected = false;
        world.createJoint(pDef);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        worldStage.getViewport().update(width, height, true);
        guiStage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
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

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 position = worldCam.unproject(new Vector3(screenX, screenY, 0f));
        if(gameState == GAME_RUNNING) {
            x = x + (x + position.x)*0.01f;
            myPlayer.playerBody.setTransform(position.x, 2, 0);
        }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
