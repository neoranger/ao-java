package game.screens;

import com.badlogic.gdx.Screen;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.network.NetworkClient;

public enum ScreenEnum {

    LOGIN {
        public Screen getScreen(Object... params) {
            return new LoginScreen();
        }
    },
    LOBBY {
        public Screen getScreen(Object... params) {
            Player player = (Player) params[0];
            Room[] rooms = (Room[]) params[1];
            NetworkClient networkClient = (NetworkClient) params[2];
            return new LobbyScreen(networkClient, player, rooms);
        }
    },
    ROOM {
        @Override
        public Screen getScreen(Object... params) {
            return new RoomScreen((NetworkClient) params[0], (Room) params[1], (Player) params[2]);
        }
    },
    GAME {
        public Screen getScreen(Object... params) {
            return new GameScreen((String) params[0], (int) params[1], (Player) params[2]);
        }
    };

    public abstract Screen getScreen(Object... params);
}
