package game.screens;

import com.artemis.Entity;
import com.artemis.SuperMapper;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import game.AOGame;
import game.handlers.MusicHandler;
import game.systems.Sound.SoundSytem;
import game.systems.anim.IdleAnimationSystem;
import game.systems.anim.MovementAnimationSystem;
import game.systems.camera.CameraFocusSystem;
import game.systems.camera.CameraMovementSystem;
import game.systems.camera.CameraSystem;
import game.systems.map.TiledMapSystem;
import game.systems.network.TimeSync;
import game.systems.physics.MovementProcessorSystem;
import game.systems.physics.MovementSystem;
import game.systems.physics.PhysicsAttackSystem;
import game.systems.physics.PlayerInputSystem;
import game.systems.render.ui.CoordinatesRenderingSystem;
import game.systems.render.world.*;
import game.ui.GUI;
import shared.model.lobby.Player;
import shared.model.map.Tile;
import shared.network.NetworkClient;
import shared.network.lobby.player.PlayerLoginRequest;

import static com.artemis.E.E;
import static com.artemis.WorldConfigurationBuilder.Priority.HIGH;

public class GameScreen extends ScreenAdapter {

    public static final int RENDER_PRE_ENTITIES = WorldConfigurationBuilder.Priority.NORMAL + 3;
    public static final int RENDER_ENTITIES = WorldConfigurationBuilder.Priority.NORMAL + 2;
    public static final int RENDER_POST_ENTITIES = WorldConfigurationBuilder.Priority.NORMAL + 1;
    private static final int DECORATIONS = WorldConfigurationBuilder.Priority.NORMAL - 1;
    public static World world;
    public static int player = -1;
    private static GUI gui = new GUI();
    private static NetworkClient networkClient;
    protected FPSLogger logger;
    protected GameState state;
    private SpriteBatch spriteBatch;
    private CameraSystem cameraSystem;

    public GameScreen(String host, int port, Player player) {
        this.spriteBatch = new SpriteBatch();
        this.logger = new FPSLogger();
        long start = System.currentTimeMillis();
        initWorld();
        networkClient = ((AOGame) Gdx.app.getApplicationListener()).networkClient; //@todo hotfix
        networkClient.sendToAll(new PlayerLoginRequest(player));
        Gdx.app.log("Game screen initialization", "Elapsed time: " + (System.currentTimeMillis() - start)) ;
    }

    public static int getPlayer() {
        return player;
    }

    public static void setPlayer(int player) {
        GameScreen.player = player;
        GUI.getInventory().updateUserInventory();
        GUI.getSpellView().updateSpells();
        GUI.getUserTable().refresh();
    }

    public static NetworkClient getClient() {
        return networkClient;
    }

    public static GUI getGui() {
        return gui;
    }

    public static World getWorld() {
        return world;
    }

    private void initWorld() {
        WorldConfigurationBuilder worldConfigBuilder = new WorldConfigurationBuilder();
        cameraSystem = new CameraSystem(AOGame.GAME_SCREEN_ZOOM);
        worldConfigBuilder.with(new SuperMapper())
                .with(HIGH, new TimeSync())
                // Player movement
                .with(HIGH, new PlayerInputSystem())
                .with(HIGH, new MovementProcessorSystem())
                .with(HIGH, new MovementAnimationSystem())
                .with(HIGH, new IdleAnimationSystem())
                .with(HIGH, new MovementSystem())
                // Camera
                .with(HIGH, cameraSystem)
                .with(HIGH, new CameraFocusSystem())
                .with(HIGH, new CameraMovementSystem())
                // Logic systems
                .with(HIGH, new PhysicsAttackSystem())
                // Sound systems
                .with(HIGH, new SoundSytem())
                .with(HIGH, new TiledMapSystem())
                // Rendering
                .with(RENDER_PRE_ENTITIES, new MapGroundRenderingSystem(spriteBatch))
                .with(RENDER_PRE_ENTITIES, new ObjectRenderingSystem(spriteBatch))
                .with(RENDER_PRE_ENTITIES, new GroundFXsRenderingSystem(spriteBatch))
                .with(RENDER_PRE_ENTITIES, new TargetRenderingSystem(spriteBatch))
                .with(RENDER_PRE_ENTITIES, new NameRenderingSystem(spriteBatch))
                .with(RENDER_ENTITIES, new EffectRenderingSystem(spriteBatch))
                .with(RENDER_ENTITIES, new CharacterRenderingSystem(spriteBatch))
                .with(RENDER_ENTITIES, new WorldRenderingSystem(spriteBatch))
                .with(RENDER_POST_ENTITIES, new MapUpperLayerRenderingSystem(spriteBatch))
                .with(RENDER_POST_ENTITIES, new LightRenderingSystem(spriteBatch))
                .with(DECORATIONS, new StateRenderingSystem(spriteBatch))
                .with(DECORATIONS, new CombatRenderingSystem(spriteBatch))
                .with(DECORATIONS, new DialogRenderingSystem(spriteBatch))
                .with(DECORATIONS, new CharacterStatesRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL, new CoordinatesRenderingSystem(spriteBatch))
                .with(WorldConfigurationBuilder.Priority.NORMAL, new BuffRenderingSystem(spriteBatch))
                // Other
                .with(new TagManager())
                .with(new UuidEntityManager()); // why?

        world = new World(worldConfigBuilder.build()); // preload Artemis world
    }

    protected void postWorldInit() {
        Entity cameraEntity = world.createEntity();
        E(cameraEntity)
                .aOCamera(true)
                .pos2D();
        world.getSystem(TagManager.class).register("camera", cameraEntity);

        // for testing
        world.getSystem(SoundSytem.class).setVolume(0);
        MusicHandler.setVolume(0);

        MusicHandler.FadeOutMusic(101, 0.02f);
//        MusicHandler.playMIDI(1);
    }

    protected void update(float deltaTime) {
        this.logger.log();

        world.setDelta(MathUtils.clamp(deltaTime, 0, 1 / 16f));
        world.process();

        switch (state) {
            case RUNNING: {
                updateRunning(deltaTime);
                break;
            }
            case PAUSED: {
                updatePaused();
                break;
            }
        }
    }

    public OrthographicCamera getGUICamera() {
        return cameraSystem.guiCamera;
    }

    @Override
    public void show() {
        this.postWorldInit();
        gui.initialize(); // TODO: gui.init() perhaps should on constructor but it has methods that shall execute on screen.show()
        this.state = GameState.RUNNING;
    }

    @Override
    public void render(float delta) {
        this.update(delta);
        if (player >= 0) {
            this.drawUI();
        }
    }

    @Override
    public void pause() {
        if (this.state == GameState.RUNNING) {
            this.state = GameState.PAUSED;
            this.pauseSystems();
        }
    }

    @Override
    public void resume() {
        if (this.state == GameState.PAUSED) {
            this.state = GameState.RUNNING;
            this.resumeSystems();
        }
    }

    @Override
    public void resize(int width, int height) {
        cameraSystem.camera.viewportWidth = Tile.TILE_PIXEL_WIDTH * 24f;  //We will see width/32f units!
        cameraSystem.camera.viewportHeight = cameraSystem.camera.viewportWidth * height / width;
        cameraSystem.camera.update();

        GUI.getStage().getViewport().update(width, height);

        getWorld().getSystem(LightRenderingSystem.class).resize(width, height);
    }

    private void drawUI() {
        gui.draw();
    }

    @Override
    public void dispose() {
        networkClient.dispose();
        gui.dispose();
        world.dispose();
    }

    private void updateRunning(float deltaTime) {
        //
    }

    private void updatePaused() {
        //
    }

    private void pauseSystems() {
        //
    }

    private void resumeSystems() {
        //
    }
}
