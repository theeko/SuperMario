package com.mygdx.game.supermario.Sprites;


import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.supermario.Screens.PlayScreen;
import com.mygdx.game.supermario.Sprites.Enemies.Enemy;
import com.mygdx.game.supermario.Sprites.Enemies.Turtle;
import com.mygdx.game.supermario.SuperMario;

public class Mario extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD};
    public State currentState;
    public State prevState;
    public World world;
    public Body b2body;

    private TextureRegion marioStand;
    private Animation marioRun;
    private TextureRegion marioJump;
    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private TextureRegion marioDead;
    private Animation bigMarioRun;
    private Animation growMario;
    private boolean timeToDefineBigMario;
    private boolean timeToReDefineBigMario;
    private boolean marioIsDead;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBig;
    private boolean runGrowAnimation;

    public Mario(PlayScreen screen){

        this.world = screen.getWorld();
        currentState = State.STANDING;
        prevState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        //get run animation frames and add them to marioRun Animation
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        marioRun = new Animation(0.1f, frames);

        frames.clear();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRun = new Animation(0.1f, frames);

        frames.clear();

        //get set animation frames from growing mario
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation(0.2f, frames);


        //get jump animation frames and add them to marioJump Animation
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        //create texture region for mario standing
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        //define mario in Box2d
        defineMario();

        //set initial values for marios location, width and height. And initial frame as marioStand.
        setBounds(0, 0, 16 / SuperMario.PPM, 16 / SuperMario.PPM);
        setRegion(marioStand);

    }

    public void update(float dt){
        if(marioIsBig)
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2 - 6 / SuperMario.PPM);
        else
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);setRegion(getFrame(dt));
        if(timeToDefineBigMario)
            defineBigMario();
        if(timeToReDefineBigMario)
            redefineMario();
    }

    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer)){
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                region = marioIsBig ?  bigMarioRun.getKeyFrame(stateTimer, true) : marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioIsBig ? bigMarioStand : marioStand;
                break;
        }

        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        } else if((b2body.getLinearVelocity().x > 0 || runningRight)&& region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == prevState ? stateTimer + dt : 0;
        prevState = currentState;
        return region;

    }

    public State getState(){
        if(runGrowAnimation){
            return State.GROWING;
        }
        else if(marioIsDead) {
            return State.DEAD;
        }
        else if(b2body.getLinearVelocity().y>0 || (b2body.getLinearVelocity().y<0) && prevState == State.JUMPING){
            return State.JUMPING;
        }
        else if(b2body.getLinearVelocity().y <0){
            return State.FALLING;
        }
        else if(b2body.getLinearVelocity().x != 0){
            return State.RUNNING;
        } else {
            return State.STANDING;
        }
    }

    public void grow(){
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight()*2);
        SuperMario.manager.get("audio/sounds/powerup.wav", Sound.class).play();
    }

    public void defineBigMario(){

        Vector2 currentPosition = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(currentPosition.add(0, 10 / SuperMario.PPM));
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / SuperMario.PPM);
        fdef.filter.categoryBits = SuperMario.MARIO_BIT;
        fdef.filter.maskBits = SuperMario.GROUND_BIT |
                SuperMario.COIN_BIT |
                SuperMario.BRICK_BIT |
                SuperMario.ENEMY_BIT |
                SuperMario.OBJECT_BIT |
                SuperMario.ENEMY_HEAD_BIT |
                SuperMario.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / SuperMario.PPM));
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / SuperMario.PPM, 6 / SuperMario.PPM), new Vector2(2 / SuperMario.PPM, 6 / SuperMario.PPM));
        fdef.filter.categoryBits = SuperMario.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void defineMario(){

        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / SuperMario.PPM, 32 / SuperMario.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / SuperMario.PPM);
        fdef.filter.categoryBits = SuperMario.MARIO_BIT;
        fdef.filter.maskBits = SuperMario.GROUND_BIT |
                SuperMario.COIN_BIT |
                SuperMario.BRICK_BIT |
                SuperMario.ENEMY_BIT |
                SuperMario.OBJECT_BIT |
                SuperMario.ENEMY_HEAD_BIT |
                SuperMario.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / SuperMario.PPM, 6 / SuperMario.PPM), new Vector2(2 / SuperMario.PPM, 6 / SuperMario.PPM));
        fdef.filter.categoryBits = SuperMario.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }

    public boolean isBig(){
        return marioIsBig;
    }

    public void hit(Enemy enemy){
        if(enemy instanceof Turtle && ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle) enemy).kick(this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else {
            if(marioIsBig) {
                marioIsBig = false;
                timeToReDefineBigMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                SuperMario.manager.get("audio/sounds/powerup.wav", Sound.class).play();
            } else {
                SuperMario.manager.get("audio/music/mario_music.ogg", Music.class).stop();
                SuperMario.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = SuperMario.NOTHING_BIT;
                for(Fixture fixture : b2body.getFixtureList()){
                    fixture.setFilterData(filter);
                }
                b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true); //last boolean wakes body up if sleeping

            }
        }
    }



    public void redefineMario(){
        Vector2 pos = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(pos);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / SuperMario.PPM);
        fdef.filter.categoryBits = SuperMario.MARIO_BIT;
        fdef.filter.maskBits = SuperMario.GROUND_BIT |
                SuperMario.COIN_BIT |
                SuperMario.BRICK_BIT |
                SuperMario.ENEMY_BIT |
                SuperMario.OBJECT_BIT |
                SuperMario.ENEMY_HEAD_BIT |
                SuperMario.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / SuperMario.PPM, 6 / SuperMario.PPM), new Vector2(2 / SuperMario.PPM, 6 / SuperMario.PPM));
        fdef.filter.categoryBits = SuperMario.MARIO_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToReDefineBigMario = false;
    }

    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

}
