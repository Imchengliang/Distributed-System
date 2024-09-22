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
    private final int serverZone; // Server zone for latency calculation

    public Server(String csvFile, int serverZone) throws RemoteException, IOException {
        super("Exercise-1/demo/src/main/resources/dataset/exercise_1_dataset.csv", serverZone);
        this.serverZone = serverZone;
    }

    public static void startServer(String name, int port, int serverZone) {
        try {
            Server obj = new Server("Exercise-1/demo/src/main/resources/dataset/exercise_1_dataset.csv", serverZone);
            CityInterface stub = (CityInterface) UnicastRemoteObject.exportObject(obj, port);
            Registry registry = LocateRegistry.createRegistry(port); // Create registry on the specified port
            registry.rebind(name, stub);
            System.out.println("Server " + name + " ready on port " + port);


        } catch (Exception e) {
            System.err.println("Server Exception: " + e.toString());
            throw new RuntimeException(e);
        }
    }


}
