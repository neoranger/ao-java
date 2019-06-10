package game.network;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import shared.network.NetworkListener;
import shared.network.interfaces.INotification;
import shared.network.interfaces.INotificationProcessor;
import shared.network.interfaces.IResponse;
import shared.network.interfaces.IResponseProcessor;

/**
 * @todo Revisar esto. Listener es específico de Kryonet y NetworkListener es la interfaz abstracta de networking.
 * Como esta ahora facilita las cosas, pero capaz se puede hacer más prolijo
 * @see KryonetClient#setListener(NetworkListener)
 */
public class ClientListener extends Listener implements NetworkListener {

    public final IResponseProcessor responseProcessor;
    public final INotificationProcessor notificationProcessor;

    public ClientListener() {
        responseProcessor = new ClientResponseProcessor();
        notificationProcessor = new GameNotificationProcessor();
    }

    @Override
    public void received(Connection connection, Object object) {
        received(connection.getID(), object);
    }

    @Override
    public void received(int connectionId, Object object) {
        Gdx.app.postRunnable(() -> {
            Log.trace("ClientListener.received()", object.toString());
            if (object instanceof IResponse)
                ((IResponse)object).accept(responseProcessor);
            else if (object instanceof INotification)
                ((INotification)object).accept(notificationProcessor);
        });
    }

    @Override
    public void connected(int connectionId) {

    }

    @Override
    public void disconnected(int connectionId) {

    }
}
