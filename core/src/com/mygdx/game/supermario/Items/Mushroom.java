package com.mygdx.game.supermario.Items;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.game.supermario.Screens.PlayScreen;
import com.mygdx.game.supermario.SuperMario;

public class Mushroom extends Item{

    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"), 0, 0, 16, 16);
        velocity = new Vector2(0.7f,0);
    }

    @Override
    public void defineItem() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(16 / 2 / SuperMario.PPM, 16/2/ SuperMario.PPM);
        fdef.filter.categoryBits = SuperMario.ENEMY_BIT;
        fdef.filter.maskBits = SuperMario.GROUND_BIT | SuperMario.COIN_BIT |
                SuperMario.BRICK_BIT | SuperMario.ENEMY_BIT
                | SuperMario.OBJECT_BIT | SuperMario.MARIO_BIT;

        fdef.shape = shape;
        body.createFixture(fdef).setUserData(this);
    }

    @Override
    public void use() {
        destroy();
    }

    public void update(float dt) {
        super.update(dt);
        setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y / getHeight()/2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }
}
