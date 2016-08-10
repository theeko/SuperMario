package com.mygdx.game.supermario.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.supermario.Items.Item;
import com.mygdx.game.supermario.Items.ItemDef;
import com.mygdx.game.supermario.Items.Mushroom;
import com.mygdx.game.supermario.Scenes.Hud;
import com.mygdx.game.supermario.Sprites.Enemies.Enemy;
import com.mygdx.game.supermario.Sprites.Mario;
import com.mygdx.game.supermario.SuperMario;
import com.mygdx.game.supermario.Tools.B2WorldCreator;
import com.mygdx.game.supermario.Tools.WorldContactListener;

import java.util.PriorityQueue;

public class PlayScreen implements Screen {

    private SuperMario game;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    private TextureAtlas atlas;

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer tmRenderer;

    //box2d stuff
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    private Mario player;

    private Music music;

    private Array<Item> items;
    private PriorityQueue<ItemDef> itemsToSpawn;

    public PlayScreen (SuperMario game){

        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        gamecam = new OrthographicCamera();


        gamePort = new FitViewport(SuperMario.V_WIDTH /SuperMario.PPM, SuperMario.V_HEIGHT /SuperMario.PPM, gamecam);

        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        tmRenderer = new OrthogonalTiledMapRenderer(map, 1 /SuperMario.PPM);

        gamecam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2,0);

        hud = new Hud(game.batch, player);

        world = new World(new Vector2(0,-10), true); //first gravity x,y //second sleep objs not calculated
        b2dr = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = SuperMario.manager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new PriorityQueue<ItemDef>();


    }

    public void spawnItem(ItemDef idef){
        itemsToSpawn.add(idef);
    }


    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef idef = itemsToSpawn.poll();
            if(idef.type == Mushroom.class){
                Gdx.app.error("screen", "handle spawn");
                Gdx.app.error("screen", "handle spawn");
                Gdx.app.error("screen", "handle spawn");
                items.add(new Mushroom(this, idef.position.x, idef.position.x));
            }
        }
    }

    public TextureAtlas getAtlas(){
        return atlas;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tmRenderer.render();
        b2dr.render(world, gamecam.combined);

        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy: creator.getGoombas()){
            enemy.draw(game.batch);
        }
        for(Item item: items){
            item.draw(game.batch);
        }
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

    }

    public void update(float dt) {

        handleInput(dt);
        handleSpawningItems();

        world.step(1/60f, 6, 2);

        player.update(dt);
        for(Enemy enemy: creator.getGoombas()){
            enemy.update(dt);
            if(enemy.getX() < player.getX() + 224/ SuperMario.PPM){
                enemy.b2body.setActive(true);
            }
        }

        for(Item item: items){
            item.update(dt);
        }

        hud.update(dt);

        gamecam.position.x = player.b2body.getPosition().x;

        gamecam.update();
        tmRenderer.setView(gamecam);


    }

    public void handleInput(float dt){
        if(Gdx.input.isTouched()){
            if(hud.touchedRight){
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            }
            else if(hud.touchedLeft){
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);
            }
            else if(player.getState() != Mario.State.JUMPING ){
                Gdx.app.error("PlayScreen", "jump");
                player.b2body.applyLinearImpulse(new Vector2(0, 4f), player.b2body.getWorldCenter(), true); //impulse immediate, force gradual

            }
        }

    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width,height);
    }


    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
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
        map.dispose();
        tmRenderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
