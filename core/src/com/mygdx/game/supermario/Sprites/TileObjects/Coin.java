package com.mygdx.game.supermario.Sprites.TileObjects;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.supermario.Items.ItemDef;
import com.mygdx.game.supermario.Items.Mushroom;
import com.mygdx.game.supermario.Scenes.Hud;
import com.mygdx.game.supermario.Screens.PlayScreen;
import com.mygdx.game.supermario.Sprites.Mario;
import com.mygdx.game.supermario.SuperMario;

public class Coin extends InteractiveTileObject{

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28; //tilsets starts from 1, propeties value 27

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(SuperMario.COIN_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {

        if(getCell().getTile().getId() == BLANK_COIN){
            SuperMario.manager.get("audio/sounds/bump.wav", Sound.class).play();
        } else {
            if(object.getProperties().containsKey("mushroom")){
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / SuperMario.PPM),
                        Mushroom.class));
                SuperMario.manager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else{
                SuperMario.manager.get("audio/sounds/coin.wav", Sound.class).play();
            }
            SuperMario.manager.get("audio/sounds/coin.wav", Sound.class).play();
        }

        getCell().setTile(tileSet.getTile(BLANK_COIN));
        Hud.addScore(100);

    }
}
