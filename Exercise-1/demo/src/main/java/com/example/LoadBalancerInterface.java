package com.example.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalancerInterface extends Remote {
    String assignServer(int clientZone, String clientMessage) throws RemoteException;

    String getAvailableServer(int clientZone) throws RemoteException;
}
