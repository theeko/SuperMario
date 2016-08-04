package com.mygdx.game.supermario.Sprites;


import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.supermario.Screens.PlayScreen;
import com.mygdx.game.supermario.SuperMario;

public class Mario extends Sprite {
    public World world;
    public Body b2body;
    private TextureRegion marioStand;

    public Mario(World world, PlayScreen screen){
        super(screen.getAtlas().findRegion("little_mario"));
        this.world = world;
        defineMario();
        marioStand = new TextureRegion(getTexture(), 0, 0, 16, 16);
        setBounds(0,0, 16 / SuperMario.PPM, 16 / SuperMario.PPM);
        setRegion(marioStand);
    }

    public void update(float dt){
        setPosition(b2body.getPosition().x - getWidth() / 2,  b2body.getPosition().y - getWidth()/2);
    }

    public void defineMario(){

        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / SuperMario.PPM, 32 / SuperMario.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / SuperMario.PPM);

        fdef.shape = shape;
        b2body.createFixture(fdef);

    }
}
