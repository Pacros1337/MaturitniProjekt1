package com.mygdx.tap.Screens.SkeleJump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private Texture gameOver;
    private Obstacle tube;
    private Texture backGround;
    private int ObstacleDistance = 175;
    private int ObstacleCount = 3;
    private BitmapFont bf = new BitmapFont();
    private Array<Obstacle> obstacles;
    private String restart = "Tap to return to main menu";
    OrthographicCamera camera;
    private boolean gameLoss = false;


    public SkeleJump(Tap tap) {
        parent = tap;
        this.tap = tap;
        stage = new Stage(new FitViewport(600, 600));
        //bird = new Texture("bird64.png");
        ghost = new Ghost(10,250);
        gameOver   = new Texture("deadskeleton.jpg");
        bf = new BitmapFont();
        tube = new Obstacle(250);
        obstacles = new Array<Obstacle>();

        for (int i = 1; i <= ObstacleCount; i++){
            obstacles.add(new Obstacle(i*(ObstacleDistance + Obstacle.ObstacleWidth)));
        }

        backGround = new Texture("background.png");
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600,600);
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
        if(gameLoss==true){
            ghost.setFlySpeed(0);
            parent.batch.begin();
            bf.draw(parent.batch, restart, 50,250);
            parent.batch.draw(gameOver,0,0);
            parent.batch.end();
            if (Gdx.input.justTouched()){
                parent.screenChanger(Tap.MENUSCREEN);
                dispose();
                gameLoss=false;
            }
        }


        play(delta);

    }

    public void play(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        parent.batch.setProjectionMatrix(camera.combined);
        parent.batch.begin();
        parent.batch.draw(backGround,camera.position.x -(camera.viewportWidth/2),0);
        parent.batch.draw(ghost.getGhost(), ghost.getPos().x, ghost.getPos().y);
        camera.position.x =ghost.getPos().x+70;
        ghost.fly(delta);

        for(Obstacle obstacle : obstacles){
            if(camera.position.x - (camera.viewportWidth/2 ) > obstacle.getPosUp().x + obstacle.getUpTube().getWidth()){

                obstacle.moveObstacle(obstacle.getPosUp().x + ((Obstacle.ObstacleWidth+ObstacleDistance) * ObstacleCount));
            }
            if(obstacle.collision(ghost.ghostBounds())) {

                gameLoss=true;

            }
        }



        for(Obstacle obstacle : obstacles){

            parent.batch.draw(obstacle.getUpTube(), obstacle.getPosUp().x, obstacle.getPosUp().y);
            parent.batch.draw(obstacle.getDownTube(), obstacle.getPosDown().x, obstacle.getPosDown().y);
        }

        camera.update();


        parent.batch.end();

        if (Gdx.input.justTouched() && gameLoss==false){
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
        backGround.dispose();
        gameOver.dispose();
        //for(Obstacle obstacle : obstacles){
          //  obstacle.dispose();
        //}
        ghost.dispose();


    }
}
