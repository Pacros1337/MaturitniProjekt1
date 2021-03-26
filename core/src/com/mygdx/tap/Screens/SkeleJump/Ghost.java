package com.mygdx.tap.Screens.SkeleJump;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;

public class Ghost {
    private static int FlySpeed = 60;
    private Vector3 velo;
    private Vector3 pos;
    private Texture ghost;
    private static int GRAVITATION = -10;

    public Ghost(int x, int y){

        velo = new Vector3(0,0,0);
        pos = new Vector3(x,y,0);
        ghost = new Texture("bird64.png");
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
        if (pos.y<0)pos.y=0;
        if(pos.y> 600)pos.y=600;

    }
}
