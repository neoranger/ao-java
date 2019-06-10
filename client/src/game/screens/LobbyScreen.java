package game.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import shared.model.lobby.Player;
import shared.model.lobby.Room;
import shared.network.NetworkClient;
import shared.network.lobby.CreateRoomRequest;
import shared.network.lobby.JoinRoomRequest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LobbyScreen extends AbstractScreen {

    private final Player player;
    private NetworkClient networkClient;
    private Set<Room> rooms;
    private List<Room> roomList;

    public LobbyScreen(NetworkClient networkClient, Player player, Room[] rooms) {
        super();
        this.networkClient = networkClient;
        this.player = player;
        this.rooms = new HashSet<>(Arrays.stream(rooms).collect(Collectors.toSet()));
        updateRooms();
    }

    public void roomCreated(Room room) {
        rooms.add(room);
        updateRooms();
    }

    public void roomClosed(Room room) {
        rooms.remove(room);
        updateRooms();
    }

    @Override
    void createContent() {
        roomList = new List<>(getSkin());

        TextButton createRoomButton = new TextButton("CREATE ROOM", getSkin());
        createRoomButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                networkClient.sendToAll(new CreateRoomRequest(10));
            }
        });

        TextButton joinRoomButton = new TextButton("JOIN ROOM", getSkin());
        joinRoomButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Room selected = roomList.getSelected();
                if (selected != null && !selected.isFull()) {
                    networkClient.sendToAll(new JoinRoomRequest(selected.getId()));
                }
            }
        });

        getMainTable().add(roomList).width(400).height(400);
        getMainTable().row();
        getMainTable().add(joinRoomButton);
        getMainTable().row();
        getMainTable().add(createRoomButton);

    }

    private void updateRooms() {
        roomList.setItems(new Array(rooms.toArray()));
    }
}
