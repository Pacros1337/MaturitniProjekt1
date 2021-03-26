package com.mygdx.tap.Screens.SkeleJump;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Ghost {
    private static int FlySpeed = 60;
    private Vector3 velo;
    private Vector3 pos;
    private Texture ghost;
    private static int GRAVITATION = -10;
    private Rectangle ghostRect;

    public Ghost(int x, int y){

        velo = new Vector3(0,0,0);
        pos = new Vector3(x,y,0);
        ghost = new Texture("bird64.png");
        ghostRect = new Rectangle(x,y,ghost.getWidth(),ghost.getHeight());
    }

    public Vector3 getPos() {
        return pos;
    }

    public Texture getGhost() {
        return ghost;
    }

    public Vector3 getVelo() {
        return velo;
    }

    public void fly(float delta){
        velo.add(0,GRAVITATION,0);
        velo.scl(delta);
        pos.add(FlySpeed * delta,velo.y,0);
        velo.scl(1/delta);
        ghostRect.setPosition(pos.x,pos.y);
        if (pos.y<0)pos.y=0;
        if(pos.y> 500)pos.y=500;


    }

    public void setVelo(Vector3 velo) {
        this.velo = velo;
    }

    public static void setFlySpeed(int flySpeed) {
        FlySpeed = flySpeed;
    }

    public static int getGRAVITATION() {
        return GRAVITATION;
    }

    public static void setGRAVITATION(int GRAVITATION) {
        Ghost.GRAVITATION = GRAVITATION;
    }

    public static int getFlySpeed() {
        return FlySpeed;
    }

    public Rectangle ghostBounds(){
        return ghostRect;
    }
    public void dispose(){
        ghost.dispose();
    }
}
