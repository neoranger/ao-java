package game.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import shared.network.NetworkClient;
import shared.network.NetworkListener;
import shared.network.init.NetworkDictionary;
import shared.network.kryonet.EndpointState;
import shared.network.kryonet.KryonetEndpoint;

import java.io.IOException;

/** <p>
 * <code>KryonetClient</code> is a client specification for {@link KryonetEndpoint}.
 * </p> */
public class KryonetClient extends KryonetEndpoint implements NetworkClient {

    protected static final int CONNECTION_TIMEOUT = 3000;

    /** Create client connection handler. */
    @Override
    public void create() {
        endpoint = new Client(8192, 8192);
    }

    @Override
    public void setListener(NetworkListener listener) {
        assert(listener instanceof Listener); //@todo si no es asi no podemos seguir, hay que flexibilizar
        endpoint.addListener((Listener)listener);
    }

    @Override
    public void connect(String host, int port) {
        setHost(host);
        setPort(port);

        // @todo revisar, esta metido acá para que ande
        setDictionary(new NetworkDictionary());
        registerDictionary();
        start();

        connect();
    }

    @Override
    public void connect() {
        if (endpointState != EndpointState.STARTED) throw new IllegalStateException("Endpoint must be started before connecting.");
        try {
            endpointState = EndpointState.CONNECTING;
            ((Client) endpoint).connect(CONNECTION_TIMEOUT, host, port, port + 1);
            Log.info("Connected to " + host + ":" + port);
            endpointState = EndpointState.CONNECTED;
        } catch (IOException e) {
            Log.info("Failed to connect.");
            endpointState = EndpointState.FAILED_TO_CONNECT;
        }
    }

    @Override
    public void dispose() {
        try {
            ((Client) endpoint).dispose();
        } catch(IOException e) {
            // ignored
        }
    }

    /**
     * Send object to everyone.
     * Since the client is only connected to the server, this only targets the server.
     * @todo might want to consider if we want to send from a different perspective. (client > client)
     */
    public void sendToAll(Object o) {
        ((Client) endpoint).sendTCP(o);
    }

    public void sendTo(int connectionId, Object o) {((Client)endpoint).sendTCP(o);}
}
