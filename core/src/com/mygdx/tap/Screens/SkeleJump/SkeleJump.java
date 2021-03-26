package com.mygdx.tap.Screens.SkeleJump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.tap.Tap;

public class SkeleJump implements Screen {
    private final Stage stage;
    private Tap parent;
    private final Tap tap;
    //private Texture bird;
    private Ghost ghost;
    private Obstacle tube;
    private Texture backGround;
    private int ObstacleDistance = 130;
    private int ObstacleCount = 3;
    private Array<Obstacle> obstacles;
    OrthographicCamera camera;


    public SkeleJump(Tap tap) {
        parent = tap;
        this.tap = tap;

        stage = new Stage(new FitViewport(600, 600));
        //bird = new Texture("bird64.png");
        ghost = new Ghost(10,250);
        tube = new Obstacle(250);
        obstacles = new Array<Obstacle>();

        backGround = new Texture("background.png");
        for (int i = 1; i <= ObstacleCount; i++){
            obstacles.add(new Obstacle(i*(ObstacleDistance + Obstacle.ObstacleWidth)));


        }
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600,700);
        stage.getViewport().apply();
    }

    public void jump(){
        ghost.getVelo().y=200;
    }


    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        parent.batch.setProjectionMatrix(camera.combined);

        ghost.fly(delta);
        camera.position.x =ghost.getPos().x+70;
        for(Obstacle obstacle : obstacles){
            if(camera.position.x - (camera.viewportWidth/2 ) > tube.getPosUp().x + tube.getUpTube().getWidth()){
                System.out.println("hhh");
                obstacle.moveObstacle(tube.getPosUp().x + (Obstacle.ObstacleWidth + ObstacleDistance * ObstacleCount));
            }
        }
        camera.update();
        parent.batch.begin();


        parent.batch.draw(backGround,camera.position.x -(camera.viewportWidth/2),0);

        for(Obstacle obstacle : obstacles){
            parent.batch.draw(tube.getUpTube(), tube.getPosUp().x, tube.getPosUp().y);
            parent.batch.draw(tube.getDownTube(), tube.getPosDown().x, tube.getPosDown().y);
        }

        parent.batch.draw(ghost.getGhost(), ghost.getPos().x, ghost.getPos().y);
        parent.batch.end();

        if (Gdx.input.justTouched()){
            jump();
        }
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
