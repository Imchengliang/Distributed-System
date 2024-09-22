package com.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface LoadBalancerInterface extends Remote {
    ServerAddress assignServer(int clientZone) throws RemoteException;


}
