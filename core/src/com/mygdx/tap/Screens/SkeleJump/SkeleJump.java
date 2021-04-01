package com.mygdx.tap.Screens.SkeleJump;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.tap.Tap;

import java.util.Locale;
import java.util.Random;

public class SkeleJump implements Screen {
    private final Stage stage;
    private Tap parent;
    private final Tap tap;

    private Texture gameOver;
    private Texture backGround;
    private Texture ghost;
    private Texture upTube;
    private Texture downTube;

    private int ObstacleDistance = 350; //distance  in pixels of the distance between obstacles
    private int ObstacleCount = 6; //number of obstacles in array at a given time (the array size)
    private int gap = 90; // distance in pixels between the bottom of the up obstacle, and the top of the down obstacle

    private BitmapFont bf = new BitmapFont(); //debug font
    private Array<Vector2> obstacles; //Array of points x,y which mark the center of the gap between obstacles
    private String restart = "Tap to return to main menu";
    private boolean gameLoss = false; //marks whether we lost a life

    private static int FlySpeed = 100; //the velocity the camera moves, in pixels per second
    private Vector3 velo; //dynamic velocity
    private Vector3 pos; // dynamic position
    private static int GRAVITATION = -10; //constant gravity, how fast the bird falls
    private Rectangle ghostRect; //position of the bird texture

    ShapeRenderer sr = new ShapeRenderer();
    OrthographicCamera camera;

    public SkeleJump(Tap tap) {
        parent = tap;
        this.tap = tap;
        stage    = new Stage(new FitViewport(600, 700));
        ghost    = new Texture("bird64.png");
        upTube   = new Texture("uptube.png");
        downTube = new Texture("downtube.png");
        gameOver = new Texture("deadskeleton.jpg");
        ghost    = new Texture("bird64.png");
        backGround = new Texture("background.png");

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 600,700);
        stage.getViewport().apply();

        //bgPod starts with the leftmost edge of the texture, matching the leftmost edge of the camera view
        //camera/ minus 2 is the same as -(camera/2), negates the resulting value (viewportWidth/2 positive = right, negative = left)
        //camera starts at x,y = 0,0, extending to -(cameraWidth/2) up to +(cameraWidth/2)
        bgPos   = camera.viewportWidth/-2; //fixed position for the bg
        bgWidth = Math.round(camera.viewportWidth*4); //we stretch the texture width to four times the screen width so it scrolls a bunch

        velo = new Vector3(0,0,0);
        pos  = new Vector3(0,300,0);
        ghostRect = new Rectangle(0,300,ghost.getWidth(),ghost.getHeight());
        obstacles = new Array<Vector2>();

        init();
    }

    public void init(){
        //sets the starting  values for the elements in the game, its called when a new life starts

        //bgPod starts with the leftmost edge of the texture, matching the leftmost edge of the camera view
        //camera/ minus 2 is the same as -(camera/2), negates the resulting value (viewportWidth/2 positive = right, negative = left)
        //camera starts at x,y = 0,0, extending to -(cameraWidth/2) up to +(cameraWidth/2)
        bgPos = camera.viewportWidth/ -2;

        //reset the camera position  to the beginning of the level, we use viewportHeight/2 to center vertically
        camera.position.set(0, camera.viewportHeight/2, 0);

        //restart the obstacles array to random values
        obstacles.clear();
        for (int i = 1; i <= ObstacleCount; i++){
            //add the obstacle
            //300 is more or less the camera.viewportWidth/2, just hardcoded
            //  this makes the starting obstacle start at 300, which is just beyond the edge of the screen at the start
            // i * obstacleDistance gives a position increasing on each element added, in multiples of obstacleDistance
            //MathUtils.random takes two values for a minimum and maximum, here we want the space to be more or less half
            //  of the screen height, so we divide by 4 giving us 4 even spaces,
            //  this means the top most is 1/4 from the top of the screen and the bottom most is 1/4 from the bottom
            //  and finally we center the "space" in between by adding half viewportHeight
            obstacles.add(
                new Vector2(
                    300 + (i*ObstacleDistance),
                    MathUtils.random(camera.viewportHeight/4, -camera.viewportHeight/4) + camera.viewportHeight/2
                )
            );
        }

    }

    //declarations
    Circle ghostHit = new Circle();
    Rectangle up = new Rectangle(), down = new Rectangle();
    public void updateGhost(float delta){
        //bgPos increases half as fast as the rest of the obstacles, camera and ghost, which is used to make
        //  the effect of "going slower on the distance" to give a sense of depth. This is called a "parallax" effect
        bgPos += (delta*FlySpeed)/2;
        camera.position.x += delta*FlySpeed; //camera increases the full FlySpeed
        camera.update(); //update matrices

        //if we are still playing, and the player touched screen
        if (!gameLoss && Gdx.input.justTouched()){
            velo.y=200; //velocity goes up by 200 pixels
        }

        velo.add(0,GRAVITATION,0);
        velo.scl(delta);
        pos.add(FlySpeed*delta, velo.y, 0);
        velo.scl(1/delta);

        //if we havent lost yet, we want the ghost to stay on the bottom edge
        //  if we lost already, we allow the ghost to go beyond the bottom edge
        if(!gameLoss && pos.y < -20)   pos.y=-20;
        float maxy = camera.viewportHeight - (92); //92 is the approximate hand calculated distance between the center of the ghost and the upper edge
        if(pos.y > maxy) pos.y=maxy; //we clamp the position to not go beyond the upper edge

        // update the ghost position
        ghostRect.setPosition(camera.position.x-(camera.viewportWidth/3), pos.y);
        //32 and 57 is the approximate center of the image inside the texture
        //  36 is approximate the radius which contains the body of the ghost, ignoring the tool
        ghostHit.set(ghostRect.x+32, ghostRect.y+57, 36);


        for(Vector2 obstacle : obstacles){
            //if the position of the obstacle is beyond the left edge
            //  obstacle.x is the left side of the obstacle, downTube.getWidth() is the width of the texture
            //  camera.poxition.x - cameraViewport/2 is the leftmost edge
            if(obstacle.x+downTube.getWidth() < camera.position.x-(camera.viewportWidth/2)){
                obstacle.set(
                    obstacle.x + ObstacleDistance*(obstacles.size), // move the tubes to the position following the last existing tube
                    MathUtils.random(camera.viewportHeight/4, -camera.viewportHeight/4) + camera.viewportHeight/2 //same as init, calculate a random position in Y centered in half the height
                );
            }

            //we dont really care about the obstacle until its closer to the ghost
            //  which is the center of the screen
            if(obstacle.x <= camera.position.x){
                //down is a Rectangle used to check collision
                //  obstacle.x is the left side of the tube collision
                //  gap is the center of the space between obstacles in the same X position
                //  obstacle.y is the top side of the  tube collision
                //  we set each up, down collsion rectangle to the position of the obstacle and fill the texture size
                down.set(
                    obstacle.x, obstacle.y-gap-upTube.getHeight(),
                    upTube.getWidth(), upTube.getHeight());
                up  .set(
                    obstacle.x, obstacle.y+gap,
                    upTube.getWidth(), upTube.getHeight());
            }

            //if we are still playing then check if we just lost
            if(!gameLoss){
                //gameLoss is a boolean so we take advantage of logical OR to check both tubes on the same line
                // if ghostHit overlaps with down or with up then we set gameLoss to true
               gameLoss = Intersector.overlaps(ghostHit, down) || Intersector.overlaps(ghostHit, up);
            }
        }

    }


    @Override
    public void show() {
        stage.clear();
        Gdx.input.setInputProcessor(stage);
    }



    float restartTime=0;
    @Override
    public void render(float delta) {
        if(gameLoss==true){ //have we lost
            FlySpeed = 0; //stop moving everything
            restartTime += delta; //count the time to restart the level

            //we can restart by touch or by waiting 2.5 seconds
            if(Gdx.input.justTouched() || restartTime >= 2.5f){
                restartTime = 0; //reset the time between game over and new game
                init(); //reset all the pieces
                gameLoss=false; // mark as playing again
                FlySpeed = 100; // the same speed we started with
            }

            parent.batch.begin();
            bf.draw(parent.batch, restart, 50,250); // no idea
            parent.batch.draw(gameOver,0,0);
            parent.batch.end();
        }

        play(delta);
    }

    float bgPos   = 0;
    float bgWidth = 0;
    public void play(float delta) {
        updateGhost(delta); //update all the pieces in the game

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        parent.batch.setProjectionMatrix(camera.combined);

        parent.batch.begin();
            //draw two copies of the background, to try get a smooth scrolling background
            //if the camera rightmost edge goes beyond the size of the stretched texture's rightmost edge
            //  we draw the second copy starting this new one's left side at the first copy's right side
            //  and adjust the height to the camera height
            if(camera.position.x+camera.viewportWidth >= bgWidth) {
                parent.batch.draw(backGround,bgPos+bgWidth,0, bgWidth, camera.viewportHeight);
            }
            //if first copy of the background went beyond  the camera view, we keep drawing the second copy
            //  and move the first copy to the end of the second copy, so that we can keep infinite scrolling
            if(camera.position.x-(camera.viewportWidth/2) > bgPos+bgWidth) bgPos += bgWidth;
            parent.batch.draw(backGround,bgPos,0, bgWidth, camera.viewportHeight); //draw the first copy

            parent.batch.draw(ghost, ghostRect.x, ghostRect.y); //draw the ghost

            //draw all the obstacles, adding the gap to both upTube and downTube position in Y
            for(Vector2 obstacle : obstacles){
                parent.batch.draw(upTube,   obstacle.x, obstacle.y+gap);
                parent.batch.draw(downTube, obstacle.x, obstacle.y-gap-downTube.getHeight());
            }

        parent.batch.end();
/**
        //debug info
        sr.setProjectionMatrix(camera.combined);
            if(gameLoss)  sr.setColor(Color.RED); //if we lost, we show the collision shapes in red
            if(!gameLoss) sr.setColor(Color.WHITE); // if we still playing, show collision shapes in white
            sr.begin(ShapeRenderer.ShapeType.Line);
                sr.circle(ghostHit.x, ghostHit.y, ghostHit.radius); //ghost circle hit area
                for(Vector2 b : obstacles){
                    sr.rect(up.x,   up.y,   up.width,   up.height); //for each of the obstacles collision rectangle
                    sr.rect(down.x, down.y, down.width, down.height);
                }
        sr.end();
    }
 **/
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
        ghost.dispose();


    }
}
