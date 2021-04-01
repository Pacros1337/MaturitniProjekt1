package com.mygdx.tap.Utility;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimatedSprite {
    //texture has the sprite sheet
    Texture texture;
    TextureRegion region[][]; //region will get each of the sprite images

    private Set animation_set=Set.Idle; //animation set is the row
    private Set queue =Set.Idle; //queue is the animation which will change once this row is over
    private int animation_frame; // which column of the row, i.e. a specific frame
    private float animation_time; // how much time has elapsed where 1.0 = one second

    public AnimatedSprite(String file, int size) {
        texture = new Texture(file);
        region = new TextureRegion(texture).split(size, size); //split into different tiny images
    }

    //getters
    public int frame(){return animation_frame;}
    public Set set(){return animation_set;}

    //setter overloaded
    // if repeat, then queue becomes the same Set we assign, so it loops, otherwise return to idle
    public void set(Set animation, boolean repeat){
        if( repeat) queue=animation;
        if(!repeat) queue= Set.Idle;
        set(animation);
    }

    //change animation immediatly without changing queue
    public void set(Set animation){
        if(animation == animation_set) return; //prevent restart on call

        animation_set=animation;
        animation_time=0;
        animation_frame=0;
    }


    public void update(float delta){
        animation_time += delta; //add time to the frame
        // time is in seconds, so we multiply 20 to make 1/20 seconds per frame
        // we know that there is a maximum of 12 frames per Set, so we prevent unbounded indexing from the region array
        animation_frame = (int)(animation_time*20) % 12;
        //Hit is the only row which has a different count of frames
        if(animation_set == Set.Hit && animation_frame >= 5){
            set(queue);
        }
        //once the row is played completely set the next animation
        if(animation_frame >= 11){
            set(queue);
        }
    }
    // gets the current texture for the frame that is going to be shown
    public TextureRegion getFrame(){
        return region[animation_set.ordinal()][animation_frame];
    }

    //ordered sets represent the row for each kind of animation
    public static enum Set{
        Running, Attacking, Hit, Idle;
    }
}
