package shared.network.kryonet;

/**
 * Endpoint state.
 * @see KryonetEndpoint
 */
public enum EndpointState {
    STARTING,

    /** #update() thread is running */
    STARTED,

    CONNECTING,

    /** connection established or socket bind */
    CONNECTED,

    CLOSING,

    CLOSED,

    STOPPING,

    STOPPED,

    FAILED_TO_CONNECT
}