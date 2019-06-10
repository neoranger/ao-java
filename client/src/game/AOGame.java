package game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import game.handlers.AssetHandler;
import game.handlers.StateHandler;
import game.network.ClientListener;
import game.network.ClientResponseProcessor;
import game.network.GameNotificationProcessor;
import game.network.KryonetClient;
import game.screens.ScreenEnum;
import game.screens.ScreenManager;
import game.utils.Cursors;
import shared.model.lobby.Player;
import shared.network.NetworkClient;
import shared.network.init.NetworkDictionary;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

/** <p>
 * Implements libGDX {@link ApplicationListener}.
 * "An ApplicationListener is called when the Application is created, resumed, rendering, paused or destroyed.
 * All methods are called in a thread that has the OpenGL context current.
 * You can thus safely create and manipulate graphics resources."
 *
 * This is the starting point of the entire game logic.
 * </p> */
public class AOGame extends Game {

    public static final float GAME_SCREEN_ZOOM = 1f;
    public static final float GAME_SCREEN_MAX_ZOOM = 1.3f;

    public final NetworkClient networkClient = new KryonetClient();

    @Override
    public void create() {
        Gdx.app.debug("AOGame", "Creating AOGame...");

        long start = System.currentTimeMillis();
        /** Load game assets */
        AssetHandler.load();
        if (AssetHandler.getState() == StateHandler.LOADED)
            Gdx.app.debug("AOGame", "Handler loaded!");
        /** Initialize network client */
        networkClient.create();
        networkClient.setListener(new ClientListener());
        Gdx.app.log("Client initialization", "Elapsed time: " + (System.currentTimeMillis() - start));
        Cursors.setCursor("hand");
        ScreenManager.getInstance().initialize(this);
        toLogin();
    }

    @Override
    public void render() {
        GL20 gl = Gdx.gl;
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }

    public void toGame(String host, int port, Player player) {
        ScreenManager.getInstance().showScreen(ScreenEnum.GAME, host, port, player);
    }

    public void toLogin() {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOGIN);
    }

    public void toLobby(Object... params) {
        ScreenManager.getInstance().showScreen(ScreenEnum.LOBBY, params);
    }

    public void toRoom(Object... params) {
        ScreenManager.getInstance().showScreen(ScreenEnum.ROOM, params);
    }

    public void dispose() {
        Log.info("Closing client...");
        AssetHandler.unload();
        networkClient.dispose();
        Gdx.app.exit();
        Log.info("Thank you for playing! See you soon...");
        System.exit(0);
    }
}
