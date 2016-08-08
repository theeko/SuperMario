package com.mygdx.game.supermario;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.supermario.Screens.PlayScreen;

public class SuperMario extends Game {
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public static final float PPM = 100; //for box2 scaling pixel/meters

	public static  final short MARIO_BIT = 2;
	public static  final short BRICK_BIT = 4;
	public static  final short COIN_BIT = 8;
	public static  final short DESTROYED_BIT = 16;
	public static  final short DEFAULT_BIT = 1;
	public static AssetManager manager; //normall should have sended to AudioManager insead of static usage

	public SpriteBatch batch;
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("audio/music/mario_music.ogg", Music.class);
		manager.load("audio/sounds/coin.wav", Sound.class);
		manager.load("audio/sounds/bump.wav", Sound.class);
		manager.load("audio/sounds/breakblock.wav", Sound.class);
		manager.finishLoading();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
		manager.update();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		manager.dispose();

	}
}
