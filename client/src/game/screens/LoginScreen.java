package game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import game.AOGame;
import game.handlers.MusicHandler;
import shared.interfaces.Hero;
import shared.network.NetworkClient;
import shared.network.lobby.JoinLobbyRequest;

public class LoginScreen extends AbstractScreen {
/*
    private static final String SERVER_IP = "ec2-18-231-116-111.sa-east-1.compute.amazonaws.com";
    private static final int SERVER_PORT = 7666;
 */
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 7666;

    private NetworkClient networkClient;

    public LoginScreen() {
        super();
        /**
         * @todo cada screen debería guardar una referencia a AOGame y tomar de ahi el cliente
         * @see ScreenManager
         */
        this.networkClient = ((AOGame)Gdx.app.getApplicationListener()).networkClient;
        init();
    }

    private void init() {
        MusicHandler.playMusic(101);
    }

    @Override
    void createContent() {
        Label userLabel = new Label("User", getSkin());
        TextField username = new TextField("", getSkin());
        username.setMessageText("username");

        Label heroLabel = new Label("Hero", getSkin());
        SelectBox<Hero> heroSelect = new SelectBox<>(getSkin());
        final Array<Hero> heroes = new Array<>();
        Hero.getHeroes().forEach(hero -> {
            heroes.add(hero);
        });
        heroSelect.setItems(heroes);

        Table connectionTable = new Table((getSkin()));

        Label ipLabel = new Label("IP: ", getSkin());
        TextField ipText = new TextField(SERVER_IP, getSkin());

        Label portLabel = new Label("Port: ", getSkin());
        TextField portText = new TextField("" + SERVER_PORT, getSkin());
        portText.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

        TextButton loginButton = new TextButton("Connect", getSkin());
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String user = username.getText();
                Hero hero = heroSelect.getSelected();
                String ip = ipText.getText();
                int port = Integer.valueOf(portText.getText());

                loginButton.setDisabled(true);
                connectThenLogin(ip, port, user, hero);
                loginButton.setDisabled(false);
            }

        });

        getMainTable().add(userLabel);
        getMainTable().row();
        getMainTable().add(username).width(200);
        getMainTable().row();
        getMainTable().add(heroLabel).padTop(20);
        getMainTable().row();
        getMainTable().add(heroSelect).width(200);
        getMainTable().row();
        getMainTable().add(loginButton).padTop(20).expandX().row();

        connectionTable.add(ipLabel);
        connectionTable.add(ipText).width(500);
        connectionTable.add(portLabel);
        connectionTable.add(portText);
        connectionTable.setPosition(420, 30); //Hardcoded

        getMainTable().add(connectionTable);
        getStage().setKeyboardFocus(username);
    }

    private void connectThenLogin(String host, int port, String user, Hero hero) {
//        try {
            networkClient.connect(host, port);
            networkClient.sendToAll(new JoinLobbyRequest(user, hero));
/*        } catch() {
            Dialog dialog = new Dialog("Network error", getSkin());
            dialog.text("Failed to connect! :(");
            dialog.button("OK");
            dialog.show(getStage());
        }*/ //@todo implementar algun manejo de errores
    }
}
