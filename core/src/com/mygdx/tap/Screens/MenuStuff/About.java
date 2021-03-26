package com.mygdx.tap.Screens.MenuStuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.tap.Tap;
import com.mygdx.tap.Utility.HighScore;
import com.mygdx.tap.Utility.TimePlayed;

public class About implements Screen {

    private final Stage stage;
    private Tap parent;
    OrthographicCamera camera;
    TimePlayed single = TimePlayed.returnInstance();

    public About(Tap tap) {
        parent = tap;
        stage = new Stage(new FitViewport(600, 700));
    }
    @Override
    public void show() {


        stage.clear();
        Gdx.input.setInputProcessor(stage);

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Skin skin = new Skin(Gdx.files.internal("retroUI/vhs-ui.json"));
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label label = new Label("TAP GAME ALPHA", skin, "play");
        root.add(label).expandX().left().padLeft(30.0f).padTop(30.0f);

        root.row();
        Table table = new Table();
        table.defaults().left();
        root.add(table);

        label = new Label("- A B O U T -", skin);
        table.add(label).padTop(50.0f).center();

        root.row();
        Label labelXD = new Label("COMPLETE SIMPLE MINI GAMES", skin);

        root.row();
        Label labelXD2 = new Label("CHALLENGE YOURSELF", skin);
        root.add(labelXD2).padTop(50.0f).center();
        root.row();


        root.add(labelXD).padTop(50.0f).center();

        root.row();

        Label highScoreLabel = new Label("GAME MADE BY PACROS", skin);

        root.add(highScoreLabel).padTop(50.0f).center();


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
