package com.mygdx.game.supermario.Sprites.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.supermario.Scenes.Hud;
import com.mygdx.game.supermario.Screens.PlayScreen;
import com.mygdx.game.supermario.SuperMario;

public class Brick extends InteractiveTileObject {
    public Brick(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        fixture.setUserData(this);
        setCategoryFilter(SuperMario.BRICK_BIT);
    }

    @Override
    public void onHeadHit() {
        setCategoryFilter(SuperMario.DESTROYED_BIT);
        getCell().setTile(null);
        Gdx.app.debug("onHeathit", "called");
        Hud.addScore(200);
        SuperMario.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
    }
}
