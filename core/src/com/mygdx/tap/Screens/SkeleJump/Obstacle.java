package com.mygdx.tap.Screens.SkeleJump;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Obstacle {
    private Random random;
    private int obstaclePosition = 180;
    private int gap = 115;
    private int lowestBound = 150;
    private Vector2 posUp;
    private Vector2 posDown;
    private Texture upTube;
    private Texture downTube;
    public static int ObstacleWidth = 50;

    public int getObstaclePosition() {
        return obstaclePosition;
    }

    public int getGap() {
        return gap;
    }

    public int getLowestBound() {
        return lowestBound;
    }

    public Vector2 getPosUp() {
        return posUp;
    }

    public Vector2 getPosDown() {
        return posDown;
    }

    public Texture getUpTube() {
        return upTube;
    }

    public Texture getDownTube() {
        return downTube;
    }

    public Obstacle(float x){

        upTube = new Texture("uptube.png");
        downTube = new Texture("downtube.png");
        random = new Random();
        posUp = new Vector2(x,random.nextInt(obstaclePosition)+gap+lowestBound);
        posDown = new Vector2(x, posUp.y-gap-downTube.getHeight());

    }

    public void moveObstacle(float x){
        posUp.set(x,random.nextInt(obstaclePosition)+gap+lowestBound);
        posDown.set(x, posUp.y-gap-downTube.getHeight());

    }


}
