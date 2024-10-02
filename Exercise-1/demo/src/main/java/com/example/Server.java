package com.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;

public class Server extends CityService{
    private final int serverZone; // Server zone for latency calculation

    private final boolean cacheEnabled;

    private final String logFilePath;
    private int lastQueueSize = 0;
    public Server(String csvFile, int serverZone, boolean cacheEnabled, String logFilePath) throws RemoteException, IOException {
    //public Server(String csvFile, int serverZone, boolean cacheEnabled) throws RemoteException, IOException {
        super("dataset/exercise_1_dataset.csv", serverZone, cacheEnabled);
        this.serverZone = serverZone;
        this.cacheEnabled = cacheEnabled;
        this.logFilePath = logFilePath;
        logQueueSize(getQueueSize());
    }

    @Override
    public void addTask(Runnable task, int clientZone) {
        super.addTask(task, clientZone); // Call the super method to add the task
        logQueueSizeIfChanged(); // Log if the queue size has changed
    }

    public void logQueueSizeIfChanged() {
        try {
            int currentQueueSize = getQueueSize();
            if (currentQueueSize != lastQueueSize) {
                lastQueueSize = currentQueueSize;
                logQueueSize(currentQueueSize); // Log the new queue size
            }
        } catch (RemoteException e) {
            System.err.println("Failed to get queue size: " + e.getMessage());
            // You may want to handle this more gracefully depending on your needs
        }
    }


    private void logQueueSize(int queueSize) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            String logEntry = "Timestamp: " + System.currentTimeMillis() + ", Queue size: " + queueSize;
            writer.write(logEntry);
            writer.newLine();
            System.out.println(logEntry); // Optional: print to console for real-time monitoring
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }


    public static void startServer(String name, int port, int serverZone, boolean enableCache, String logFilePath) {
    //public static void startServer(String name, int port, int serverZone, boolean enableCache) {
        try {
            Server obj = new Server("dataset/exercise_1_dataset.csv", serverZone, enableCache, logFilePath);
            //Server obj = new Server("dataset/exercise_1_dataset.csv", serverZone, enableCache);
            CityInterface stub = (CityInterface) UnicastRemoteObject.exportObject(obj, port);
            Registry registry;
            try {
                registry = LocateRegistry.getRegistry(1099);  // Try to get the registry at 1099
                registry.list();  // Test if the registry exists
            } catch (RemoteException e) {
                // If not found, create the registry
                registry = LocateRegistry.createRegistry(1099);
            }
            registry.bind(name, stub);
            System.out.println("Server " + name + " ready on port " + port + (enableCache ? " with cache enabled" : " with cache disabled"));


        } catch (Exception e) {
            System.err.println("Server Exception: " + e.toString());
            throw new RuntimeException(e);
        }
    }



}
