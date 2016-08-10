package com.mygdx.game.supermario.Sprites.TileObjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.supermario.Items.ItemDef;
import com.mygdx.game.supermario.Items.Mushroom;
import com.mygdx.game.supermario.Scenes.Hud;
import com.mygdx.game.supermario.Screens.PlayScreen;
import com.mygdx.game.supermario.SuperMario;

public class Coin extends InteractiveTileObject{

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28; //tilsets starts from 1, propeties value 27

    public Coin(PlayScreen screen, Rectangle bounds) {
        super(screen, bounds);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(SuperMario.COIN_BIT);
    }

    @Override
    public void onHeadHit() {
        Gdx.app.debug("coin", "onHeadHit()");
        if(getCell().getTile().getId() == BLANK_COIN){
            SuperMario.manager.get("audio/sounds/bump.wav", Sound.class).play();
        } else {
            Gdx.app.error("coin", "onhead else block");
            Gdx.app.error("coin", "onhead else block");
            Gdx.app.error("coin", "onhead else block");
            Gdx.app.error("coin", "onhead else block");
            SuperMario.manager.get("audio/sounds/coin.wav", Sound.class).play();
            screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / SuperMario.PPM),
                    Mushroom.class));
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(100);

    }
}
