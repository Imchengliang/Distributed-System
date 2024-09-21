package com.example.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalancerInterface extends Remote {
    ServerAddress assignServer(int clientZone, String clientMessage) throws RemoteException;


}
