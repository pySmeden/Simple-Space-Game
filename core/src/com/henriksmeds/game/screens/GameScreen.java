package com.henriksmeds.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
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
import com.henriksmeds.game.elements.ScrollingBackground;
import com.henriksmeds.game.elements.ScrollingBackgroundCopy;
import com.henriksmeds.game.elements.Star;
import com.henriksmeds.game.utils.HighScore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;


public class GameScreen extends ScreenAdapter implements InputProcessor {
    static final int GAME_INTRO = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_OVER = 3;
    int gameState = 0;
    private int currentScore;


    Explosion explosion;
    Button returnBtn, pauseBtn;
    Label scoreLabel;
    Application app;
    Stage worldStage, guiStage;
    OrthographicCamera worldCam, guiCam;
    FillViewport worldViewport, guiViewport;

    World world;
    TextureAtlas atlas;
    Sprite playerSprite;
    AsteroidPool2 pool;
    PhysicsShapeCache physicsShapeCache;
    Body playerAnchor;
    PrismaticJointDef pDef;
    PhysicsPlayer myPlayer;
    Skin skin;
    ShapeRenderer shapeRenderer;
    Box2DDebugRenderer b2drender;
    Array asteroidArray;
    float animTime;

    ScrollingBackgroundCopy scrollingBackground;

    float x = 0.0f;

    public GameScreen(final Application app) {
        this.app = app;
        skin = new Skin(Gdx.files.internal("gui/uiskin.json"), new TextureAtlas(Gdx.files.internal("gui/uiAtlas.atlas")));
        atlas = new TextureAtlas("game-elements/assets.pack");
        world = new World(new Vector2(0, -2f), true);
        initGuiStage();
        asteroidArray = new Array();

        createPlayer();
        createPlayerAnchor();
        createPrismaticJoint();
        createWalls();
        setContactListener();
        shapeRenderer = new ShapeRenderer();
        explosion = new Explosion(0.2f);
        pool = new AsteroidPool2(20, world);

        b2drender = new Box2DDebugRenderer();
        float xLerp = 0;
        worldCam = new OrthographicCamera();
        worldViewport = new FillViewport(app.WORLD_WIDTH, app.WORLD_HEIGHT, worldCam);
        worldStage = new Stage(worldViewport, app.batch);
        worldStage.getViewport().apply();

        scrollingBackground = new ScrollingBackgroundCopy(15f);

        worldStage.addActor(scrollingBackground);
        worldStage.addActor(myPlayer);
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
        x = Gdx.input.getAccelerometerX();
        myPlayer.playerBody.setLinearVelocity(-x, 0);

        worldStage.draw();

        app.batch.begin();
        drawSprites();
        if(!myPlayer.alive) {
            explosion.render(app.batch, Gdx.graphics.getDeltaTime(), myPlayer.playerBody.getPosition().x - 0.3f, myPlayer.playerBody.getPosition().y);
            if(explosion.isFinished){
                gameState = GAME_OVER;
                guiStage.clear();
                explosion.reset();
                initGameOverGui();
            }
        }
        app.batch.end();

        guiStage.draw();

        removeAsteroids();

    }

    private void updatePaused(float delta) {

        returnBtn.setVisible(true);
        pauseBtn.setVisible(false);

        worldStage.draw();

        app.batch.begin();
        drawSprites();
        app.batch.end();

        guiStage.draw();
    }

    private void updateGameOver(float delta) {
        worldStage.act(delta);
        guiStage.act(delta);
        world.step(1f/60f, 6, 2);

        worldStage.draw();
        app.batch.begin();
        drawSprites();
        app.batch.end();
        guiStage.draw();
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
                initPauseGui();
            }
        });

        returnBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                guiStage.clear();
                initGuiStage();
                gameState = GAME_RUNNING;

            }
        });

        guiStage.addActor(pauseBtn);
        guiStage.addActor(returnBtn);
        guiStage.addActor(scoreLabel);

        InputMultiplexer mux = new InputMultiplexer(guiStage, this);
        Gdx.input.setInputProcessor(mux);

    }

    private void initGameOverGui() {

        HighScore.load();
        HighScore.addScore(currentScore);
        HighScore.save();
        currentScore = 0;

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
        TextButton exitBtn = new TextButton("Exit", skin);

        table.add(retryBtn).minWidth(280).minHeight(20);
        table.row().colspan(1).padTop(20);

        table.add(exitBtn).minWidth(280).minHeight(20);
        table.row().colspan(1).padTop(20);


        Image image = new Image(new NinePatchDrawable(skin.getPatch("blue_panel")));
        image.setSize(width, imageHeight);
        image.setPosition(offsetX, table.getY() + table.getBackground().getMinHeight() - 10f);

        Label titleLabel = new Label("Game Over", skin);
        titleLabel.setColor(Color.DARK_GRAY);
        titleLabel.setPosition(app.VIRTUAL_WIDTH /2 - titleLabel.getMinWidth()/2f, image.getY() + titleLabel.getMinHeight());
        guiStage.addActor(table);

        retryBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                guiStage.clear();
                initGuiStage();
                gameState = GAME_INTRO;
                myPlayer.reset();
                animTime = 0f;
            }
        });

        exitBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.exit();
            }
        });

    }

    private void initPauseGui() {
        Table table = new Table(skin);
        int width = 300;
        int height = 180;
        float offsetX = app.VIRTUAL_WIDTH /2 - width / 2f;
        float offsetY = app.VIRTUAL_HEIGHT /2 - height / 2f;
        float imageHeight = 50f;

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
        guiStage.addActor(table);

        returnBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                guiStage.clear();
                initGuiStage();
                gameState = GAME_RUNNING;
            }
        });

        exitBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                HighScore.addScore(currentScore);
                HighScore.save();
                Gdx.app.exit();
            }
        });

    }
/*
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
*/

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

    private void drawSprites() {
        Iterator<Asteroids> iterator = asteroidArray.iterator();
        while(iterator.hasNext()) {
            iterator.next().drawAsteroid(app.batch);
        }
    }

private void removeAsteroids(){
    Iterator<Asteroids> iterator = asteroidArray.iterator();
    while(iterator.hasNext()) {
        Asteroids asteroid = iterator.next();
        if(asteroid.getBody().getPosition().y < 2) {
            pool.free(asteroid);
            iterator.remove();
        }
    }
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
                            ((Star) bodyA.getUserData()).alive = false;

                        }
                        if (bodyB.getUserData() instanceof Star) {
                            ((Star) bodyB.getUserData()).alive = false;
                        }
                    }
                }

                if(bodyA.getUserData() instanceof Asteroids || bodyB.getUserData() instanceof Asteroids) {
                    if(bodyA.getUserData() instanceof PhysicsPlayer || bodyB.getUserData() instanceof PhysicsPlayer) {
                        myPlayer.playerDamage();
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

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        worldStage.getViewport().update(width, height, true);
        guiStage.getViewport().update(width, height, true);
        app.batch.setProjectionMatrix(guiCam.combined);
    }

    @Override
    public void show() {
        super.show();
        myPlayer.playerBody.setTransform(1.8f, 0, 0);
        myPlayer.addAction(Actions.moveBy(0,4f,5f, Interpolation.smooth));

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(worldCam.combined);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if(gameState == GAME_RUNNING ) {
                    Asteroids asteroid = pool.obtain();
                    asteroidArray.add(asteroid);

                    Sprite sprite = new Sprite(atlas.findRegion("asteroid"));

                    asteroid.setSprite(sprite);

                    float randX = MathUtils.random(0f + asteroid.getSprite().getWidth() * app.SCALE * 0.25f, 4.0f - 10 * asteroid.getSprite().getWidth() * app.SCALE * 0.25f);
                    asteroid.getBody().setTransform(randX,6,0);
                    asteroid.getBody().setLinearVelocity(0,0);
                    asteroid.getBody().setAngularVelocity(0);

                }
            }
        }, 2, MathUtils.random(0.5f,1f));
        Gdx.input.setInputProcessor(guiStage);
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
            System.out.println(position.x);
            myPlayer.playerBody.setTransform(position.x, 2, 0);
        }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}

class AsteroidPool2 extends Pool<Asteroids> {

    World world;
    public AsteroidPool2(World world) {
        this.world = world;
    }

    public AsteroidPool2(int initialCapacity, World world) {
        super(initialCapacity);
        this.world = world;
    }

    @Override
    protected Asteroids newObject() {
        System.out.println("great success");
        return new Asteroids(this.world, 0.01f / 4, 0.01f / 4);
    }
}

