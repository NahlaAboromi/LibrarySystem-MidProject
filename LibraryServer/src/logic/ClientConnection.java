package logic;

import ocsf.server.ConnectionToClient;


/**
 * Represents a connection to a client in a server-client system.
 * This class holds information about the client's IP address, host name, and connection status.
 */
public class ClientConnection {
    private String ip;
    private String hostName;
    private String status;

    /**
     * Default constructor to initialize a ClientConnection instance.
     */
    public ClientConnection() {
    }

    // Getters
    /**
     * Gets the IP address of the client.
     * @return The client's IP address.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Gets the host name of the client.
     * @return The client's host name.
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Gets the connection status of the client.
     * @return The connection status (e.g., "connected", "disconnected").
     */
    public String getStatus() {
        return status;
    }

    // Setters
    /**
     * Sets the IP address of the client.
     * @param ip The IP address of the client.
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Sets the host name of the client.
     * @param hostName The host name of the client.
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Sets the connection status of the client.
     * @param status The connection status (e.g., "connected", "disconnected").
     */
    public void setStatus(String status) {
        this.status = status;
    }
}

