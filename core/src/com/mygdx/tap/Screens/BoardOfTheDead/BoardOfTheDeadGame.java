
package com.mygdx.tap.Screens.BoardOfTheDead;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.tap.Tap;
import com.mygdx.tap.Utility.AnimatedSprite;

public class BoardOfTheDeadGame  implements Screen {
    private final Stage stage;
    private final Tap tap;
    private Tap parent;
    OrthographicCamera camera;
    private Texture skeleton;
    private Texture spookyBg;
    private Texture heart;
    private Texture boardIcons;
    private Texture hitStar;
    private Texture gameover;
    private Texture winGame;
    private Texture badgood;
    private String restart = "Tap to return to main menu";

    //This is the array of textures, which holds the icon images from rows and columns
    private TextureRegion[][] icons;

    //See the class source
    private AnimatedSprite skeledude = new AnimatedSprite("anim_birb.png", 92);

    private Rectangle skeletonRect = new Rectangle();; //character position, size
    private Rectangle diceRect     = new Rectangle();; //dice position, size is not used
    private Rectangle hitRect      = new Rectangle();; //the star shows when the dice hits the screen edges

    //the board data, in 0, 1 or 2 to determine which icon from TextureRegion will be drawn and what will it do
    private int[] squares = new int[9*9];


    private float  score = 0; //not really used
    private int   hitpoints = 2; //health
    private int   direction = 1; //1 going right, -1 going left
    private int   box = 0; //which box on the board is the character sitting in
    private int   boxview = 1; //how many boxes have we "seen" with the character getting to them
    private float hitTime = 10; //time in seconds when the dice hit an edge

    Vector3 diceSpeed = new Vector3(); //when mouse dragged, this sets the "speed" on which the dice will fly
    Vector3 diceStart = new Vector3(); //sets the start of the speed the mouse will throw, on touchin the screen
    float maxLength; // the diagonal of the bottom,left up to top,right of the screen, determines what is the absolute maximum drag length

    private modes   mode = modes.DiceStart;
    private enum  modes{
        DiceStart, //starts throwing the dice, waits for player to touch and drag
        DiceRolling,  //started throwing, moves the dice all over the screen
        DiceStop,  //the dice has stopped so we process the next state
        GuyMoving,  //the character is walking the board
        BadEvent, // the character is sitting on a box marked as trap
        GoodEvent // the character is sitting on a power up
    }

    //constants
    public final static int TYPE_BOARD   = 0;
    public final static int TYPE_TRAP    = 1;
    public final static int TYPE_POWERUP = 2;

    public final static int TRAP_BEAR  = 0;
    public final static int TRAP_JAIL  = 1;
    public final static int TRAP_SNAKE = 2;

    public final static int PUP_HEART  = 0;
    public final static int PUP_SHIELD = 1;
    public final static int PUP_SWORD  = 2;


    private BitmapFont bf = new BitmapFont(); //debug
    ShapeRenderer sr = new ShapeRenderer(); //debug

    public BoardOfTheDeadGame(Tap tap) {
        parent   = tap;
        this.tap = tap;

        //load images
        stage      = new Stage(new FitViewport(600, 700));
        skeleton   = new Texture("anim.png");
        spookyBg   = new Texture("boardofthedead.jpg");
        boardIcons = new Texture("boardicons.png");
        hitStar    = new Texture("hit.png");
        badgood    = new Texture("badgood.png");
        heart      = new Texture("heart_pixel_art_32x32.png");
        gameover   = new Texture("deadskeleton.jpg");
        winGame    = new Texture("wingame.png");

        //splits the large image on a grid of 64x64 pixels, setting a 2D array of TextureRegion
        icons = new TextureRegion(boardIcons).split(64,64);

        //we want more traps than powerups
        int bad_size = 25; // the total boxes that will be a trap
        int bad[] = new int[bad_size];
        for(int i = 0; i != bad_size; i++) {
            bad[i] = MathUtils.random(0, 9*9); //calculate which box is going to be a trap
        }

        int good_size = 15; //total powerups in the board
        int good[] = new int[good_size];
        for(int i = 0; i != good_size; i++) {
            good[i] = MathUtils.random(0, 9*9); //calcs which boxes have powerup
        }

        //the board is a 9x9 tile
        for(int x = 0; x!=9*9; x++){
            squares[x] = TYPE_BOARD; //assume we will have a regular walkable board without event

            //check all the trap boxes if they match the box we're initializing
            for(int b = 0; b != bad_size; b++) {
                if(bad[b] == x){ //if the trap box is the box we're initing, set it as trap
                    squares[x] = TYPE_TRAP;
                    break;
                }
            }

            //the trap boxes and the powerups might overlap, in this case we want the powerup to show up instead
            //so we process same as above
            for(int g = 0; g != good_size; g++) {
                if(good[g] == x){
                    squares[x] = TYPE_POWERUP;
                    break;
                }
            }

//            squares[x] = TYPE_POWERUP; //debug
        }
        camera = new OrthographicCamera();
        camera.setToOrtho(false,600,700);

        //x 15 and y 10 are to position the character centered on the box position, calculated by hand
        skeletonRect.set(15, 10, 64, 64);
        hitRect.set(0, 0, 64, 64);

        //start with the dice centered in the screen
        diceRect.set(
            (camera.viewportWidth /2.0f) - 32,
            (camera.viewportHeight/2.0f) - 32,
            64,64);

    }
    @Override
    public void render(float delta) {
        //if hitpoints is negative
        if(hitpoints <= 0) {
            endgame(delta); //process end game render
            parent.batch.begin();
            bf.draw(parent.batch, restart, 100,50);
            if(Gdx.input.justTouched()){
                parent.screenChanger(Tap.MENUSCREEN);
            }
            parent.batch.end();
            return;
        }
        if(box>(9*9)-1){
            wingame(delta); //process win game render
            parent.batch.begin();
            bf.draw(parent.batch, restart, 100,50);
            if(Gdx.input.justTouched()){
                parent.screenChanger(Tap.MENUSCREEN);
            }
            parent.batch.end();
            return;
        }

        play(delta); //process game loop
    }

    public void endgame(float delta) {
        parent.batch.begin();
            parent.batch.draw(gameover,0,0);
        parent.batch.end();

    }

    public void wingame(float delta){
        parent.batch.begin();
        parent.batch.draw(winGame,0,0);
        parent.batch.end();
    }

    public void play(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0.5f, 300);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getViewport().apply();
        camera.update();

        updateThings(delta); //process all the stuff in the board

        parent.batch.setProjectionMatrix(camera.combined);

        parent.batch.begin();
            parent.batch.draw(spookyBg,0,0);

            int n = boxview; //what is the longest we've seen of the board
            for(int y= 0; y!= 9; y++){ // line of board boxes
                for(int x = 0; x!=9; x++){ // each box in a line
                    // n>0 is outside of how many boxes we've seen so far
                    // y*9+x - the line we're drawing increases in multiples of 9, the width of our board
                    //   to this we add the number of boxes in the line up to 9
                    //   then we center its Y position adding 10 from the edge
                    if(n > 0 ) parent.batch.draw(getIcon(TYPE_BOARD,squares[y*9+x]),10+(x*64),y*64);
                    n--; //we know how many boxes we can see, so we decrement as we draw each
                }
            }

            parent.batch.draw(skeledude.getFrame(),
                (direction==-1
                    ? skeletonRect.x + 62 // if its facing left, we add 62 to try center the image in the box
                    : skeletonRect.x - 30 // if its facing right, direction == +1
                ),
                skeletonRect.y,
                92*direction,92 //direction is 1 or -1, we make the width -1 to mirror horizontally -> looking lft
            );


            //draw the hit star image
            if(hitTime < 0.2f) parent.batch.draw(hitStar,hitRect.x, hitRect.y, hitRect.width, hitRect.height);
            //if the character is trapped in the jail trap, draw the jail on top of the character
            if(hasJail){
                parent.batch.draw(
                getIcon(TYPE_TRAP, TRAP_JAIL),
                    (direction==-1
                    ? skeletonRect.x + 62
                    : skeletonRect.x - 30
                ),
                skeletonRect.y,
                92*direction,92);
            }
//            parent.batch.draw(badgood,skeletonRect.x,skeletonRect.y,0,badgood.getHeight()/2,badgood.getWidth(), badgood.getHeight()/2);
        parent.batch.end();

        sr.setProjectionMatrix(camera.combined);

        sr.begin(ShapeRenderer.ShapeType.Filled);
            drawDice(sr);

            //draws a circle which grows depending how far you drag
            if(mode == modes.DiceStart){
                sr.setColor(Color.ROYAL);
                sr.circle(diceStart.x, diceStart.y, diceSpeed.dst(0,0,0)/10);
            }
        sr.end();


        parent.batch.begin();
            //draw the hit star image
            if(hitTime < 0.2f) parent.batch.draw(hitStar,hitRect.x, hitRect.y, hitRect.width, hitRect.height);
            for(int i=0; i!= hitpoints; i++) parent.batch.draw(heart, 10 + (10*i) + (32*i), 640); //draw a line of hearts

            //draw the power ups in the corner, all the numbers are calculated by hand
            if(hasSword)  parent.batch.draw(getIcon(TYPE_POWERUP, PUP_SWORD),  camera.viewportWidth-40, camera.viewportHeight-40, 32, 32);
            if(hasShield) parent.batch.draw(getIcon(TYPE_POWERUP, PUP_SHIELD), camera.viewportWidth-80, camera.viewportHeight-40, 32, 32);
            if(hasSnake)  parent.batch.draw(getIcon(TYPE_TRAP,    TRAP_SNAKE), camera.viewportWidth-40, camera.viewportHeight-80, 32, 32);
            if(hasJail)   parent.batch.draw(getIcon(TYPE_TRAP,    TRAP_JAIL),  camera.viewportWidth-80, camera.viewportHeight-80, 32, 32);

            if(mode==modes.BadEvent){
                // draw the character's dialog bubble
                parent.batch.draw(
                    badgood,
                    skeletonRect.x+32,skeletonRect.y+64,128,64,
                    0,0,badgood.getWidth(), badgood.getHeight()/2,
                    false,false);

                if(trap>=0) {
                    //make a kind of animation, moving the icon upwards and vanishing
                    // set color rgba = 1,1,1 becomes the real color, alpha = 0.0 to 1.0 becomes the transparency
                    // we want to fade out so, as eventTime increases, we substract it from 1.1f which will go from 1.1 to 0.0
                    parent.batch.setColor(1,1,1,1.1f-(eventTime) ); //1.5 is the event time duration
                    parent.batch.draw(
                        getIcon(TYPE_TRAP,trap),
                        skeletonRect.x-20,
                        skeletonRect.y+10 + (eventTime*64)); //we start the animation above the character and move it up as much as 1.5 * 64 pixels above
                    parent.batch.setColor(1,1,1,1); //reset the transparencies
                }

            }

            if(mode==modes.GoodEvent){
                //draw characters dialog bubble
                parent.batch.draw(
                        badgood,skeletonRect.x+32,skeletonRect.y+64,128,64,
                        0,badgood.getHeight()/2,badgood.getWidth(), badgood.getHeight()/2,
                        false,false);

                if(powerup>=0){
                    //same as above
                    parent.batch.setColor(1,1,1,1.1f-(eventTime) ); //1.5 is the event time duration
                    parent.batch.draw(
                        getIcon(TYPE_POWERUP,powerup),
                        skeletonRect.x-20,
                        skeletonRect.y+10 + (eventTime*64) );
                    parent.batch.setColor(1,1,1,1);
                }
            }
        parent.batch.end();
    }

    private TextureRegion getIcon(int type, int index){
        return icons[type][index];
    }

    private int   old_box = box; // remembers the last box we step on
    private float old_box_x; // this is meant to be a value between -1 .. 0 .. +1
    private int   steps; // how many steps have we walked ?

    float eventTime;
    int   snakeSteps; // how many steps will we walk under the effect of the snake
    private void updateThings(float delta){
        //calculate the position of the guy depending on what was the last box it step on
        // 64 is the width of the box, 9.0f is the count of boxes per row
        // y+10 is to center approximately the guy vertically in the box
        // x + 30 tries to center the guy horizontaly
        // 64*old_box_x gives one number between 0 to 64 pixels which is the distance the guy walks
        skeletonRect.y = (64*(int)(old_box / 9.0f)) + 10; //int to discard the decimals
        skeletonRect.x = (64*     (old_box % 9.0f)) + 30 + (64*old_box_x);
        skeledude.update(delta);

        //state machine
        switch(mode){
            case BadEvent:
                badEvent(delta);
                break;
            case GoodEvent:
                goodEvent(delta);
                break;
            case GuyMoving:
                updateGuyPosition(delta);
                break;
            case DiceStart:
                count=0;
                break;
            case DiceRolling:
                roll(delta);
                break;
            case DiceStop:
                if(hasJail){
                    //trap mode of the jail, we will roll over and over until we get a number higher than 3
                    // if our dice rolls lower than 3 we take a hit
                    mode = modes.DiceStart;
                    if(number < 3){
                        skeledude.set(AnimatedSprite.Set.Hit, false);
                        hitpoints--;
                    } else {
                        hasJail = false; //afte we roll above 3 we "defeat" the jail
                    }
                } else {
                    //regular movement
                    boxview = Math.max(boxview, box+number+2); //box is the current position, number is the dice, 2 is one box for each side between character and number
                    mode    = modes.GuyMoving; // set state as moving

                    //if we are under the effect of the snake, we go backwards instead
                    if(hasSnake) {
                        moveLeft();
                    } else {
                        moveRight(); //regular movement
                    }
                }
                break;
        }
    }

    int     powerup   = -1;
    int     hitAction = 0;
    boolean hasSword  = false;
    boolean hasShield = false;
    void goodEvent(float delta){
        eventTime+=delta; //add time to our event
        if(eventTime>1.5f){
            // once the time since start is above 1.5 seconds, we return to the dice throw
            mode      = modes.DiceStart;
            eventTime = 0; //clear time
            powerup   = -1; // clear powerup event animation etc
        }

        //flags whether we are running the event or not
        if(powerup<0){
            powerup   = MathUtils.random(0,2); //assign a random powerup
            hitAction = 0; // clear flag
        }

        //this might fail in cases where frame rate is too uneven
        if(eventTime>.7f && hitAction == 0){ //wait .7 second and if we havent set the flag
            hitAction = 1; //we set the flag to prevent running this block twice
            skeledude.set(AnimatedSprite.Set.Attacking, false); //play animation

            switch(powerup){
                case PUP_HEART : hitpoints++;    break; //heart increases health
                case PUP_SHIELD: hasShield=true; break; //shield defends against bear trap
                case PUP_SWORD : hasSword =true; break; //sword defends against snake
            }
        }

        if(eventTime>1.5f){ //run the event for one and half second
            mode = modes.DiceStart;
            eventTime = 0; //clear flags
            powerup = -1;
        }
    }

    int trap=-1;
    boolean hasSnake=false;
    boolean hasJail =false;
    void badEvent(float delta){
        eventTime+=delta;

        if(trap < 0){
            trap = MathUtils.random(0,2);
            hitAction = 0;
        }

        //this might fail in cases where frame rate is too uneven
        if(eventTime>.7f && hitAction == 0){
            hitAction=1; //prevent entering this block

            AnimatedSprite.Set n = AnimatedSprite.Set.Hit; //assume we're drawing the damage animation
            if(hasSword || hasShield) n = AnimatedSprite.Set.Attacking; //if we have powerups do the attack instead
            skeledude.set(n, false); //start animation

            switch(trap){
                case TRAP_BEAR :
                    if(!hasShield) hitpoints--; // reduce health points after the event so that the game over screen doesnt interrupt
                    hasShield = false;
                    break;
                case TRAP_JAIL :
                    //roll above a 3 to move
                    hasJail=true;
                    break;
                case TRAP_SNAKE:
                    if(hasSword) {
                        hasSword  = false;
                        direction = 1;
                        hasSnake  = false;
                    } else {
                        //go back, if already going back go forward
                        direction  = -direction;
                        hasSnake   = !hasSnake;
                        snakeSteps = MathUtils.random(1,3);
                    }
                    break;
            }
        }

        //run this event for 1 and half seconds
        if(eventTime>1.5f){
            mode = modes.DiceStart;
            eventTime = 0;
            trap = -1;
        }
    }

    void moveLeft(){
        if(box==0){
            //we arrived at box 0, so we wont go further left. Heal snake and go right
            hasSnake =false;
            moveRight();
            return;
        }
        box--;
        direction = -1;
        skeledude.set(AnimatedSprite.Set.Running, true);
    }

    void moveRight(){
        box++;
        direction = 1;
        skeledude.set(AnimatedSprite.Set.Running, true);
    }

    int number = 5;
    float lastRoll = 0.0f;
    float what = 0;
    void roll(float delta){
        lastRoll += delta;
        hitTime  += delta;
        if(mode==modes.DiceRolling){
            score =  0.25f - ( (diceSpeed.dst2(0, 0, 0) / maxLength) );
            if( lastRoll >  score  ) {
                what = lastRoll;
                number = MathUtils.random(0,5);
                lastRoll = 0;
            }

            float damp = 0.95f;
            diceSpeed.x *= damp;
            diceSpeed.y *= damp;
        }

        if(diceSpeed.dst2(0,0,0) < 1) {
            mode = modes.DiceStop;
            hitTime=10;
        }

        float newX, newY;
        newX = diceRect.x + (diceSpeed.x*0.5f);
        newY = diceRect.y + (diceSpeed.y*0.5f);

        if(newX - 32 < 0 || newX + 32 > camera.viewportWidth) {
            hitRect.x = (newX < 300
                ? -32
                : camera.viewportWidth-32);
            hitTime=0;
            hitRect.y = newY-32;

            newX = diceRect.x - (diceSpeed.x * 0.35f);
            diceSpeed.x *= -1;
        }
        if(newY - 32 < 0 || newY + 32 > camera.viewportHeight) {
            hitRect.y = (newY < 300
                ? -32
                : camera.viewportHeight-32);
            hitTime=0;
            hitRect.x = newX-32;

            newY = diceRect.y - (diceSpeed.y * 0.35f);
            diceSpeed.y *= -1;
        }

        diceRect.x = newX;
        diceRect.y = newY;

        count += delta;
        if(count > 3) {
            mode = modes.DiceStop;
        }
    }
    float count;

    int dots[][][] = {
        {{32,32}},
        {{53,53},{12,12}},
        {{53,53},{12,12},{32,32}},
        {{53,53},{12,12},{53,12},{12,53}},
        {{53,53},{12,12},{53,12},{12,53},{32,32}},
        {{53,53},{12,12},{53,12},{12,53},{12,32},{53,32}}
    };

    private void drawDice(ShapeRenderer sr){
        float x = diceRect.x-32, y = diceRect.y-32;
        sr.setColor(Color.BLACK);
        sr.rect(x-5, y-5, diceRect.width+10, diceRect.height+10);
        sr.setColor(Color.WHITE);
        sr.rect(x, y, diceRect.width, diceRect.height);
        sr.setColor(Color.BLACK);
        for(int dotlist[] : dots[number]) {
            sr.circle(x+dotlist[0],y+dotlist[1],6);
        }
    }

    private void updateGuyPosition(float delta){
        AnimatedSprite.Set n = skeledude.set();

        //we want to be able to change the next value for old_box_x depending on several conditions
        float newVal = (old_box_x + (delta*direction) );

        //when old_box is different from box, we are moving on the board
        if( (old_box==box) &&
            (box%9 == 0 || box%9 == 8) && // box%9 is 0 or 8 when its on the edge of a row
            (Math.signum(direction) == Math.signum(old_box_x)) // when old_box_x is different sign from direction, we've past the center
          ){
            //stop walking and reset position to the center of the box
            n = AnimatedSprite.Set.Idle;
            newVal=0;
        }

        //when newVal is smaller than 1 we're walking towards the next box
        if(Math.abs(newVal)>1){
            //assume we finished walking
            n = AnimatedSprite.Set.Idle;

            //we have past the center of the next box
            newVal  = 0; //reset to the center

            //walking from one edge towards outside of the screen
            if( (box%9==0 && old_box%9==8) ||
                (box%9==8 && old_box%9==0)
            ){
                //we make the guy start showing from outside of the screen, on the opposite direction its walking to
                newVal = -direction;
                n = AnimatedSprite.Set.Running; //keep walking
            }

            old_box = box; //mark we arrived to the new box
        }

        if(n == AnimatedSprite.Set.Idle){
            snakeSteps--;
            if(snakeSteps<=0){
                hasSnake = false;
                direction = 1;
            }

            if(steps < number) {
                steps++;
                box = (hasSnake ? box-1 : box+1);
                n   = AnimatedSprite.Set.Running;
            } else {
                mode  = modes.DiceStart;
                steps = 0;
                n     = AnimatedSprite.Set.Idle;

                if(squares[old_box] == TYPE_BOARD)   mode = modes.DiceStart;
                if(squares[old_box] == TYPE_TRAP)    mode = modes.BadEvent;
                if(squares[old_box] == TYPE_POWERUP) mode = modes.GoodEvent;
            }
        }

        //set() disallows reseting animation only when the new animation is the same
        //since we are going to do two conditional changes, we use an intermediate variable
        skeledude.set(n, true);

        old_box_x=newVal; //set the current position of the guy

    };


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
        winGame.dispose();
        gameover.dispose();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputAdapter() {



            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if(mode == modes.DiceStart ){
//                    mode=modes.DiceStart;
                    x.set(screenX, screenY, 0);
                    camera.unproject(x);

                    diceStart.set(x.x,x.y,0);
                }
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if(mode==modes.DiceStart) {
                    mode = modes.DiceRolling;
                    maxLength = diceSpeed.dst2(0,0,0);
                }
                return false;
            }

            Vector3 x = new Vector3();
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if(mode==modes.DiceStart){
                    x.set(screenX, screenY, 0);
                    camera.unproject(x);

                    diceSpeed.set(x.x-diceStart.x, x.y-diceStart.y,0);
                }
                return true;
            }

        });

    }


}
