package shared.network;

/** <p>
 * <code>NetworkClient</code> provides a layer of abstraction to the application,
 * independent from the implemented protocol (Kryonet, etc.).
 * </p> */
public interface NetworkClient {
    /** */
    void create();

    /** */
    void connect(String host, int port);

    /** */
    void setListener(NetworkListener listener);

    /** */
    void sendToAll(Object object);

    /** */
    void sendTo(int connectionId, Object object);

    /** */
    void dispose();
}
