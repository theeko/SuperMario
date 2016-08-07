package com.mygdx.game.supermario.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.supermario.Scenes.Hud;
import com.mygdx.game.supermario.Sprites.Mario;
import com.mygdx.game.supermario.SuperMario;
import com.mygdx.game.supermario.Tools.B2WorldCreator;
import com.mygdx.game.supermario.Tools.WorldContactListener;

public class PlayScreen implements Screen {
    private SuperMario game;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    private Texture buttonImg;
    private TextureRegion leftButton, rightButton;
    private TextureAtlas atlas;

    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer tmRenderer;

    //box2d stuff
    private World world;
    private Box2DDebugRenderer b2dr;

    private Mario player;

    public PlayScreen (SuperMario game){

        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
        gamecam = new OrthographicCamera();
        buttonImg = new Texture(Gdx.files.internal("left-button.png"));

        leftButton = new TextureRegion(buttonImg, buttonImg.getWidth(), buttonImg.getHeight());
        rightButton = new TextureRegion(buttonImg, buttonImg.getWidth(), buttonImg.getHeight());
        rightButton.flip(true, false);


        gamePort = new FitViewport(SuperMario.V_WIDTH /SuperMario.PPM, SuperMario.V_HEIGHT /SuperMario.PPM, gamecam);

        maploader = new TmxMapLoader();
        map = maploader.load("level1.tmx");
        tmRenderer = new OrthogonalTiledMapRenderer(map, 1 /SuperMario.PPM);

        gamecam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2,0);

        hud = new Hud(game.batch, player);

        world = new World(new Vector2(0,-10), true); //first gravity x,y //second sleep objs not calculated
        b2dr = new Box2DDebugRenderer();

        new B2WorldCreator(world, map);

        player = new Mario(world, this);

        world.setContactListener(new WorldContactListener());

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
        game.batch.end();

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

    }

    public void update(float dt) {

        handleInput(dt);

        world.step(1/60f, 6, 2);

        player.update(dt);

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
