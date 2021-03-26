package com.mygdx.tap.Screens.SkeleDodge;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.tap.Tap;
import com.mygdx.tap.Utility.AnimatedSprite;
import com.mygdx.tap.Utility.HighScore;

public class SkeledodgeGame implements Screen {
    private final Stage stage;
    private final Tap tap;
    private Tap parent;
    OrthographicCamera camera;
    HighScore scores = HighScore.returnInstance();
    private Texture skeleton;
    private Texture meteorDrop;
    private Texture hitStar;
    private Texture heart;
    private Texture gameover;
    private AnimatedSprite skeledude = new AnimatedSprite("anim.png", 92);

    private Rectangle skeletonRect;
    private Rectangle hitRectangle;
    private Array<Rectangle> meteors = new Array<Rectangle>();;
    private Array<Float> rotations = new Array<Float>();
    private Array<Float> spins = new Array<Float>();
    private Array<Float> fallspeeds = new Array<Float>();

    private String scoreText;
    private String restart = "Tap to return to main menu";
    public float score = 0;
    private int hitpoints = 10;
    private float renderX;
    private float renderY;
    private float lastMeteor;
    private float lastHit = 0.0f;
    private int direction = 1;


    private BitmapFont bf = new BitmapFont();
    ShapeRenderer sr = new ShapeRenderer();


    public SkeledodgeGame(Tap tap) {
        parent = tap;
        this.tap = tap;

        stage = new Stage(new FitViewport(600, 700));


        skeleton = new Texture("anim.png");
        meteorDrop = new Texture("meteor.png");
        hitStar = new Texture("hit.png");
        heart = new Texture("heart_pixel_art_32x32.png");
        gameover = new Texture("deadskeleton.jpg");

        camera = new OrthographicCamera();
        camera.setToOrtho(false,600,700);
        dropMeteor();

        skeletonRect = new Rectangle();
        hitRectangle = new Rectangle();

        skeletonRect.x = 300 - 64;
        skeletonRect.y = 10;
        skeletonRect.height=70;
        skeletonRect.width=40;
        renderX=100;
        renderY=100;

        score(0);
    }

    @Override
    public void render(float delta) {
        if(hitpoints <= 0) {
            endgame(delta);
            return;
        }

        play(delta);
    }

    public void endgame(float delta) {
        parent.batch.begin();
            parent.batch.draw(gameover,0,0);
            if(score>scores.getTotal()){
                scores.addScore(score);
                scores.setScore(score);

                //parent.screenChanger(Tap.MENUSCREEN);
            }

            bf.draw(parent.batch, scoreText, 10,420);
            bf.draw(parent.batch, restart, 100,50);
            if(Gdx.input.justTouched()){
                parent.screenChanger(Tap.MENUSCREEN);
            }

        parent.batch.end();
    }

    public void play(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0.5f, 300);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getViewport().apply();
        camera.update();

        updateThings(delta);

        parent.batch.setProjectionMatrix(camera.combined);

        sr.setProjectionMatrix(camera.combined);
        sr.setColor(Color.BROWN);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        /* debug rects */
//            for (Rectangle meteor : meteors) {
//                sr.rect(meteor.x, meteor.y,meteor.width, meteor.height);
//            }

            sr.setColor(Color.BROWN);
            //skeledude hit rect
//            sr.rect(skeletonRect.x, skeletonRect.y,skeletonRect.width, skeletonRect.height);

            //draw ground
            sr.rect(0,0,600,35);
        sr.end();

        parent.batch.begin();
            parent.batch.draw(skeledude.getFrame(),
                //these numbers are picked by hand from the sprite image position
                (direction==-1
                    ? skeletonRect.x + 70 //facing left
                    : skeletonRect.x - 30 //facing right
                ),
                skeletonRect.y,
                92*direction,92
            );

            //draw the hit star image
            if(lastHit > 0.0f){
                parent.batch.draw(hitStar, hitRectangle.x, hitRectangle.y, hitRectangle.width, hitRectangle.height);
            }

            //we need the index to get() the image rotation
            for(int i = 0; i != meteors.size; i++) {
                Rectangle meteor = meteors.get(i);
                parent.batch.draw(
                    meteorDrop,
                    meteor.x-6,
                    meteor.y-6,
                    32,32,
                    64,
                    64,
                    1,1,
                    rotations.get(i),
                    0,0,
                    meteorDrop.getWidth(),meteorDrop.getHeight(),
                    false,false
                );
            }

            //draw score text
            bf.draw(parent.batch, scoreText, 10,690);

            //draw the hearts, we need the index to calculate the X offset
            for(int i=0; i!= hitpoints; i++) parent.batch.draw(heart, 10 + (10*i) + (32*i), 640);
        parent.batch.end();

    }

    private float timeToIdle =0;
    private void updateThings(float delta){
        skeledude.update(delta);

        //hit star image
        if(lastHit > 0.0f) {
            lastHit -= delta;
        }

        //two "modes" one for android using the inclination
        // one for pc testing using the mouse to "run towards" the x mouse position
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            renderX -= Math.round(Gdx.input.getAccelerometerX());
        } else {
            renderX += Math.round(
                MathUtils.clamp(
                    (Gdx.input.getX() - renderX), -30, 30
                )/8 //magic numbers to limit the speed skeledude runs
            );
        }

        //stay within the screen
        if(renderX < 0) renderX = 0;
        if(renderX > stage.getViewport().getWorldWidth()-64) renderX = stage.getViewport().getWorldWidth()- 64;

        if(Math.abs(skeletonRect.x - renderX) > 0.8f){ //threshold to change direction
            direction = (skeletonRect.x > renderX) ? -1: 1; // running to left or right
            if(skeledude.set() != AnimatedSprite.Set.Hit) { //if hit animation we wait until its done playing
                skeledude.set(AnimatedSprite.Set.Running);
                timeToIdle =0.2f; // attempt to allow "stopping" time to go back to idle
            }
        }
        //decrease time to wait and if there is still time, do nothing
        if((timeToIdle -= delta) < 0) skeledude.set(AnimatedSprite.Set.Idle);

        //apply the skeledude image position
        //since the renderX is a float, we round it to discard the 0.xx values to avoid sliding
        skeletonRect.x = renderX;

        //time between meteors going down
        lastMeteor+=delta;
        if(lastMeteor >= getDifficulty() ) dropMeteor();

        //we need the index to add and remove the speed, rotation etc
        for(int i = meteors.size-1; i >= 0; --i){
            Rectangle meteor = meteors.get(i);
            meteor.y -= (200+fallspeeds.get(i) ) * delta;
            rotations.set(i, rotations.get(i)+ spins.get(i) );

            if(meteor.y < 0) {
                removeMeteor(i, false);
            }
            if(meteor.overlaps(skeletonRect)){
                removeMeteor(i, true);
                skeledude.set(AnimatedSprite.Set.Hit);
                timeToIdle =0.2f;
            }
        }


        score(delta);
    }

    private void removeMeteor(int i, boolean hit){

        if(hit) {
            hitRectangle.set(skeletonRect.x-20, 60, 64,64);
            lastHit = 0.4f; // one second
            hitpoints--;
            parent.playSfx(Tap.oofSound);
        } else {
            hitRectangle.set( meteors.get(i) );
            lastHit = 0.20f; // one second
            parent.playSfx(Tap.crashSound);
        }

        meteors.removeIndex(i);
        rotations.removeIndex(i);
        spins.removeIndex(i);
        fallspeeds.removeIndex(i);
    }

    private void dropMeteor() {
        Rectangle meteor = new Rectangle();
        meteor.x = MathUtils.random(0, 600-64) ;
        meteor.y = 700;
        meteor.width = 50;
        meteor.height = 50;
        System.out.println(scores);

        rotations.add( 0.0f );
        meteors.add(meteor);
        spins.add(MathUtils.random(5.0f) - 2.5f );
        fallspeeds.add(MathUtils.random(0,400.0f) );

        lastMeteor = 0; //reset time to wait for new meteor
    }

    private void score(float delta){
        score += delta;
        scoreText =  String.format("Score: %4.2f ", score);
    }

    private float getDifficulty(){
        float x = 1.0f - (score/100);
        return x < 0.15 ? 0.15f : x;
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
        skeleton.dispose();
        gameover.dispose();
        hitStar.dispose();
        heart.dispose();
        meteorDrop.dispose();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }


}
