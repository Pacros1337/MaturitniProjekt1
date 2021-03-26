package com.mygdx.tap.Screens.MenuStuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.tap.Tap;

public class Menu implements Screen {
    private final Stage stage;
    private Tap parent;
    OrthographicCamera camera;
    boolean slider = false;

    @Override
    public void show() {

        stage.clear();

        Gdx.input.setInputProcessor(stage);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Skin skin = new Skin(Gdx.files.internal("retroUI/vhs-ui.json"));
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label label = new Label("TAP GAME TESxT ", skin, "play");
        root.add(label).expandX().left().padLeft(30.0f).padTop(30.0f);

        root.row();
        Table table = new Table();
        table.defaults().left();
        root.add(table);

        label = new Label("- T A P -", skin);
        table.add(label).padTop(50.0f).center();

        table.row();
        final TextButton playButton = new TextButton("PLAY", skin);
        table.add(playButton).padTop(35.0f);

        table.row();
        table.defaults().padTop(10.0f);
        TextButton optionsButton = new TextButton("GAME OPTIONS", skin);
        table.add(optionsButton);

        table.row();
        table.defaults().padTop(10.0f);
        TextButton highScoreButton = new TextButton("HALL OF FAME", skin);
        table.add(highScoreButton);

        table.row();
        table.defaults().padTop(10.0f);
        TextButton aboutButton = new TextButton("ABOUT THE GAME", skin);
        table.add(aboutButton);

        table.row();
        TextButton quitButton = new TextButton("QUIT", skin);
        table.add(quitButton);

        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Pls");

                parent.screenChanger(Tap.GAMESCREEN);
            }
        });


        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.screenChanger(Tap.OPTIONSCREEN);
            }
        });

        highScoreButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.screenChanger(Tap.HALLOFFAMESCREEN);
            }
        });

        aboutButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.screenChanger(Tap.ABOUTSCREEN);

            }
        });

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(20f, 0f, 10f, 300);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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

    public Menu(Tap tap) {
        parent = tap;
        stage = new Stage(new FitViewport(600, 700));


    }
}
