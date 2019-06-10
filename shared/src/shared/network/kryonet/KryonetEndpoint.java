package shared.network.kryonet;

import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import shared.network.init.NetworkDictionary;

import java.util.Map;

public abstract class KryonetEndpoint {
    protected EndPoint endpoint;
    protected EndpointState endpointState = EndpointState.STOPPED;
    protected NetworkDictionary dictionary;
    protected String host;
    protected int port;


    /**
     * @todo revisar la parte de dictionary, pero afectaría también al server
     *
     */
    public void setDictionary(NetworkDictionary dictionary) {
        if (dictionary == null) throw new IllegalArgumentException("Dictionary cannot be null.");
        if (endpointState == EndpointState.CONNECTED) throw new IllegalStateException("Cannot assign dictionary once connected.");
        this.dictionary = dictionary;
    }

    /** Register all classes from dictionary with kryonet. */
    protected void registerDictionary( ) {
        endpoint.getKryo().reset();
        for (Map.Entry<Integer, Class> entry : dictionary.getItems().entrySet()) {
            endpoint.getKryo().register(entry.getValue(), entry.getKey());
        }
    }

    /** Establish connection / prepare to listen. */
    public void start() {
        endpointState = EndpointState.STARTING;
        endpoint.start();
        endpointState = EndpointState.STARTED;
    }

    /** Connection logic specific to the implementation (i.e. client or server) */
    protected abstract void connect();

    /** Disconnect our endpoint. */
    public void close() {
        endpointState = EndpointState.CLOSING;
        endpoint.close();
        endpointState = EndpointState.CLOSED;
    }

    /** */
    public void stop() {
        endpointState = EndpointState.STOPPING;
        endpoint.stop();
        endpointState = EndpointState.STOPPED;
    }

    public void addListener(Listener listener) {
        endpoint.addListener(listener);
    }

    public void removeListener(Listener listener) {
        endpoint.removeListener(listener);
    }

    public EndpointState getEndpointState() {
        return endpointState;
    }

    public void setHost(String host) {
        if (endpointState == EndpointState.CONNECTED) throw new IllegalStateException("Cannot assign host once connected.");
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPort(int port) {
        if (endpointState == EndpointState.CONNECTED) throw new IllegalStateException("Cannot assign port once started.");
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
