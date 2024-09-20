package com.example;

import java.io.Serializable;
import java.util.List;

public class ServerInfo implements Serializable {

    private String address;
    private int port;
    private int zone;
    public ServerInfo(String address, int port, int zone) {
        this.address = address;
        this.port = port;
        this.zone = zone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getZone() {
        return zone;
    }

    public void setZone(int zone) {
        this.zone = zone;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "address='" + address + '\'' +
                ", port=" + port +
                ", zone=" + zone +
                '}';
    }
}
