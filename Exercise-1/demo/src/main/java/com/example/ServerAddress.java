package com.example;

import java.io.Serializable;

public class ServerAddress implements Serializable {
    public String address;

    /**
     * Stores an address for a server.
     * This is used to send the necessary data from the proxy-server to clients describing which server -
     * the client should connect to.
     * @param address: the server address name.
     */
    public ServerAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return address;
    }
}
