package com.mygdx.game.supermario.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.mygdx.game.supermario.Scenes.Hud;
import com.mygdx.game.supermario.Screens.PlayScreen;
import com.mygdx.game.supermario.Sprites.Mario;
import com.mygdx.game.supermario.SuperMario;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(SuperMario.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()){
            setCategoryFilter(SuperMario.DESTROYED_BIT);
            getCell().setTile(null);
            Gdx.app.debug("onHeathit", "called");
            Hud.addScore(200);
            SuperMario.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        SuperMario.manager.get("audio/sounds/bump.wav", Sound.class).play();

    }
}
