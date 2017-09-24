package com.henriksmeds.game.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;


import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Henrik on 2017-05-31.
 */

public class MenuUI extends Actor{
    // object variables
    private Stack<Actor> stack;
    public Table table;
    private Skin skin;
    public Image image;
    private Label titleLabel;


    public MenuUI(Stage stage) {
        this.stack = new Stack<Actor>();
        skin = new Skin(Gdx.files.internal("gui/uiskin.json"), new TextureAtlas(Gdx.files.internal("gui/uiAtlas.atlas")));
        table = new Table(skin);
        setTouchable(Touchable.enabled);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        image.draw(batch, parentAlpha);
        table.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        table.act(delta);
        image.act(delta);
    }

    public void addBackgroundAndTitle(String headerTitle, int width, int height, int positionX, int positionY) {
        float offsetX = positionX - width / 2f;
        float offsetY = positionY - height / 2f;
        float imageHeight = 50f;

        // optional: use "blue_panel" or "green_panel" as background
        table.setBackground(new NinePatchDrawable(skin.getPatch("grey_panel")));
        table.getBackground().setMinWidth(width);
        table.getBackground().setMinHeight(height);
        table.setPosition(offsetX, offsetY);
        table.pack();

        image = new Image(new NinePatchDrawable(skin.getPatch("blue_panel")));
        image.setSize(width, imageHeight);
        image.setPosition(offsetX, table.getY() + table.getBackground().getMinHeight() - 10f);

        titleLabel = new Label(headerTitle, skin);
        titleLabel.setColor(Color.DARK_GRAY);
        titleLabel.setPosition(positionX - titleLabel.getMinWidth()/2f, image.getY() + titleLabel.getMinHeight());


    }

    public Button getButton(String id){
        Button button = null;
        for(Actor btn : this.stack) {
            if(btn.getName().equals(id)) {
                button = (Button) btn;
            }
        }
        return button;
    }

    public TextButton getTextButton(String id) {
        TextButton textButton = null;
        for(Actor btn: this.stack) {
            if(btn.getName().equals(id)) {
                textButton = (TextButton) btn;
            }
        }
        return textButton;
    }



    public void addTextButton(String text, String id, int width, int height, int padTop) {
        TextButton textBtn = new TextButton(text, skin);
        textBtn.setName(id);
        table.add(textBtn).minWidth(width).minHeight(height);
        table.row().colspan(1).padTop(padTop);

        this.stack.push(textBtn);
    }

    public void addButton(int x, int y, String style, String id) {
        Button returnBtn = new Button(skin, style);
        returnBtn.setPosition(x, y);
        returnBtn.setSize(50, 50);
        returnBtn.setName(id);
        this.stack.push(returnBtn);
    }

    public void addButton(int x, int y, String style) {
        Button returnBtn = new Button(skin, style);
        returnBtn.setPosition(x, y);
        returnBtn.setSize(50, 50);
        //stage.addActor(returnBtn);
    }

    public void addLabel(String text) {
        Label scoreLabel = new Label(text, skin);
        scoreLabel.setColor(Color.DARK_GRAY);
        table.add(scoreLabel);
        table.row().colspan(1).padTop(20f);

    }

    public void addLabel(String text, int col) {
        Label scoreLabel = new Label(text, skin);
        scoreLabel.setColor(Color.DARK_GRAY);
        table.add(scoreLabel);
    }


}
