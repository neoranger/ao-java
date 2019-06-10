package game.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.minlog.Log;
import game.AOGame;
import game.managers.WorldManager;
import game.screens.LobbyScreen;
import game.screens.LoginScreen;
import game.screens.RoomScreen;
import game.systems.network.TimeSync;
import game.systems.physics.MovementProcessorSystem;
import shared.network.interfaces.IResponseProcessor;
import shared.network.lobby.CreateRoomResponse;
import shared.network.lobby.JoinLobbyResponse;
import shared.network.lobby.JoinRoomResponse;
import shared.network.lobby.StartGameResponse;
import shared.network.movement.MovementResponse;
import shared.network.time.TimeSyncResponse;

public class ClientResponseProcessor implements IResponseProcessor {

    @Override
    public void processResponse(MovementResponse movementResponse) {
        MovementProcessorSystem.validateRequest(movementResponse.requestNumber, movementResponse.destination);
    }

    @Override
    public void processResponse(CreateRoomResponse createRoomResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        game.toRoom(game.networkClient, createRoomResponse.getRoom(), createRoomResponse.getPlayer());
    }

    /**
     * @todo Pasar el networkClient como parámetro acá pincha capas, las screens deberían guardar referencia de AOGame
     * Y tomar de ahi el cliente, o algo asi...
     * la referencia se puede pasar a través del ScreenManager
     */

    @Override
    public void processResponse(JoinLobbyResponse joinLobbyResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        game.toLobby(joinLobbyResponse.getPlayer(), joinLobbyResponse.getRooms(), game.networkClient);
    }

    @Override
    public void processResponse(JoinRoomResponse joinRoomResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        game.toRoom(game.networkClient, joinRoomResponse.getRoom(), joinRoomResponse.getPlayer());
    }

    @Override
    public void processResponse(StartGameResponse startGameResponse) {
        AOGame game = (AOGame) Gdx.app.getApplicationListener();
        if (game.getScreen() instanceof RoomScreen) {
            RoomScreen roomScreen = (RoomScreen) game.getScreen();
            game.toGame(startGameResponse.getHost(), startGameResponse.getTcpPort(), roomScreen.getPlayer());
        }
    }

    @Override
    public void processResponse(TimeSyncResponse timeSyncResponse) {
        TimeSync system = WorldManager.getWorld().getSystem(TimeSync.class);
        system.receive(timeSyncResponse);
        Log.info("Local timestamp: " + TimeUtils.millis() / 1000);
        Log.info("RTT: " + system.getRtt() / 1000);
        Log.info("Time offset: " + system.getTimeOffset() / 1000);
    }

}
