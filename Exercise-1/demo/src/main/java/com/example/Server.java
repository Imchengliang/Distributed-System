package com.example;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server extends CityService{


    public Server(String csvFile, int serverZone, boolean cacheEnabled) throws RemoteException, IOException {
        super("dataset/exercise_1_dataset.csv", serverZone, cacheEnabled);
    }

    public static void startServer(String name, int port, int serverZone, boolean enableCache) {
        try {
            Server obj = new Server("dataset/exercise_1_dataset.csv", serverZone, enableCache);
            CityInterface stub = (CityInterface) UnicastRemoteObject.exportObject(obj, port);
            Registry registry;
            try {
                registry = LocateRegistry.getRegistry(1099);  // Try to get the registry at 1099
                registry.list();  // Test if the registry exists
            } catch (RemoteException e) {
                // If not found, create the registry
                registry = LocateRegistry.createRegistry(1099);
            }
            registry.rebind(name, stub);
            System.out.println("Server " + name + " ready on port " + port + (enableCache ? " with cache enabled" : " with cache disabled"));


        } catch (Exception e) {
            System.err.println("Server Exception: " + e.toString());
            throw new RuntimeException(e);
        }
    }



}
