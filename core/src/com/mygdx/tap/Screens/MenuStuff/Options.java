package com.mygdx.tap.Screens.MenuStuff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.tap.Tap;
import com.mygdx.tap.Utility.HighScore;
import com.mygdx.tap.Utility.MusicPlayer;
import com.mygdx.tap.Utility.TimePlayed;

public class Options implements Screen {
    private final Stage stage;
    private Tap parent;
    OrthographicCamera camera;

    private MusicPlayer arcade = MusicPlayer.vratInstanci();


    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Skin skin = new Skin(Gdx.files.internal("retroUI/vhs-ui.json"));
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Label label = new Label("TAP GAME", skin, "play");
        root.add(label).expandX().left().padLeft(30.0f).padTop(30.0f);

        root.row();
        Table table = new Table();
        table.defaults().left();
        root.add(table);

        label = new Label("- O P T I O N S -", skin);
        table.add(label).padTop(50.0f).center();

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


        final CheckBox musicCheckBox = new CheckBox("MUSIC", skin);
        root.row();
        musicCheckBox.setChecked(parent.getPreferences().musicOn());
        musicCheckBox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                boolean on = musicCheckBox.isChecked();
                parent.getPreferences().setMusicOn(on);
                return false;
            }
        });
        musicCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (musicCheckBox.isChecked()) {
                    arcade.muzika.setVolume(parent.optionsCfg.getMusicVolume());
                    arcade.muzika.setLooping(true);
                    arcade.muzika.play();
                } else {
                    arcade.muzika.stop();

                }
            }
        });
        root.add(musicCheckBox).padTop(35f);

        root.row();
        table = new Table();
        root.add(table).expandY().top().padTop(50.0f);

        label = new Label("VOLUME", skin);
        table.add(label);

        final Slider musicSlider = new Slider(0.0f, 1.0f, 0.1f, false, skin);
        musicSlider.setValue(parent.getPreferences().getMusicVolume());
        musicSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                parent.getPreferences().setMusicVolume(musicSlider.getValue());
                arcade.muzika.setVolume(musicSlider.getValue());

                return false;
            }
        });

        table.add(musicSlider).width(300.0f).padLeft(10.0f);

    }

    public Options(Tap tap) {
        parent = tap;
        stage = new Stage(new FitViewport(600, 700));

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 1f, 0f, 300);
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

}
