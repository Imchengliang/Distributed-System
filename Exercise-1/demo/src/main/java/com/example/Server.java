package com.example;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends CityService{
    public Server () throws RemoteException {
        super();
    }

    public static void startServer(String name, int port) {
        try {
            CityService obj = new CityService();
            CityInterface stub = (CityInterface) UnicastRemoteObject.exportObject(obj,port);
            // LocateRegistry.createRegistry(port) if you want 5 instances of port
            Registry registry = LocateRegistry.getRegistry();

            //using rebind instead of bind, can also use bind and add method for unbind of registry name
            registry.rebind(name , stub);
            System.out.println("Server " + name + " ready on port " + port);

        } catch (Exception e) {
            System.err.println("Server Exception" + e.toString());
            throw new RuntimeException(e);
        }
    }
}
