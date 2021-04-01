package com.mygdx.tap.Screens.MenuStuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.tap.Screens.BoardOfTheDead.BoardOfTheDeadGame;
import com.mygdx.tap.Tap;
import com.mygdx.tap.Utility.HighScore;
import com.mygdx.tap.Utility.TimePlayed;

public class ChooseGame implements Screen {

    private final Stage stage;
    private Tap parent;
    OrthographicCamera camera;
    TimePlayed single = TimePlayed.returnInstance();

    Skin skin = new Skin(Gdx.files.internal("retroUI/vhs-ui.json"));

    TextButton skeleDodge = new TextButton("|> SKELEDODGE <|", skin);
    TextButton flappyBird = new TextButton("|> FLAPPY JUMP <|", skin);
    TextButton boardotd = new TextButton("|> BOARD OF THE DEAD <|", skin);

    ClickListener changeColor = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.getTarget().setColor(Color.GOLD);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                event.getTarget().setColor(Color.WHITE);
                super.touchUp(event, x, y, pointer, button);
            }
        };

    public ChooseGame(Tap tap) {
        parent = tap;
        stage = new Stage(new FitViewport(600, 700));
    }
    @Override
    public void show() {


        stage.clear();
        Gdx.input.setInputProcessor(stage);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label label = new Label("TAP GAME", skin, "play");

        root.add(label).expandX().left().padLeft(30.0f).padTop(30.0f);

        root.row();
        Table table = new Table();
        table.defaults().left();
        root.add(table);

        label = new Label("- CHOOSE GAME -", skin);
        table.add(label).padTop(50.0f).center();

        root.row();
        root.add(skeleDodge).padTop(35f);

        root.row();
        root.add(flappyBird).padTop(35f);

        root.row();
        root.add(boardotd).padTop(35f);




        root.row();
        TextButton backBtn = new TextButton("BACK", skin);
        root.add(backBtn).padTop(35f);




        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.screenChanger(Tap.MENUSCREEN);
                stage.clear();
            }
        });

        skeleDodge.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.screenChanger(Tap.SKELEDODGE);
                stage.clear();
            }
        });

        boardotd.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                parent.screenChanger(Tap.BOARDOTD);
                stage.clear();
            }
        });

        flappyBird.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.screenChanger(Tap.FLAPPYBIRD);
                stage.clear();
            }
        });

        skeleDodge.addListener(changeColor);
        boardotd.addListener(changeColor);
        flappyBird.addListener(changeColor);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1f, 0f, 0f, 300);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
