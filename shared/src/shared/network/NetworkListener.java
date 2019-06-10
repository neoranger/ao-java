package shared.network;

public interface NetworkListener {
    /** object received from connection with ID */
    void received(int connectionId, Object object);

    /** connection with ID connected. */
    void connected(int connectionId);

    /** connection with ID disconnected */
    void disconnected(int connectionId);
}
