package com.example;

import java.rmi.RemoteException;

public interface ProxyInterface {
    ServerInfo getServer(int zone) throws RemoteException;
}
