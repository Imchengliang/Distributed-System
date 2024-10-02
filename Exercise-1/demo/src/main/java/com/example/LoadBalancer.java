package com.example;


import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class LoadBalancer extends UnicastRemoteObject implements LoadBalancerInterface {
    private Random random = new Random();

    private Registry registry = null;
    private int port;
    private int numServers;

    private CityInterface[] servers;
    private int[] serverQueuesSizes;
    private int[] serverAssignmentCounts;

    public LoadBalancer(int numServers, int port) throws RemoteException {
        super();
        this.numServers = numServers;
        this.port = port;
        this.servers = new CityInterface[numServers];
        this.serverQueuesSizes = new int[numServers];
        this.serverAssignmentCounts = new int[numServers];
        //startLoadBalancer();

        try {
            // Export LoadBalancer
            //TODO:LOOK FOR REGISTRY ON RIGHT PORT

            int serverPort = 1099;
            registry = LocateRegistry.getRegistry("localhost", serverPort);
            for (int i = 1; i <= numServers; i++) {
                try {
                    servers[i-1] = (CityInterface) registry.lookup("server_" + i);
                    System.out.println("Server " + i + " bound successfully.");
                } catch (NotBoundException e) {
                    System.err.println("Server " + i + " not bound: " + e.getMessage());
                    servers[i - 1] = null; // Explicitly set to null for clarity
                }
            }

            // Bind the LoadBalancer to the registry
            registry.bind("load-balancer", this);

        } catch (Exception e) {
            System.out.println("\nError between LoadBalancer and Server: \n" + e);
            System.exit(1);
        }
        System.out.println("LoadBalancer is started");
    }

    private void updateAssignmentCount(int clientZone) {
        serverAssignmentCounts[clientZone - 1]++;

        /* for checking on the queue
        if (serverAssignmentCounts[clientZone] >= 18) {
            new Thread(new ProxyServerQueueUpdater(this, clientZone)).start();
            serverAssignmentCounts[clientZone] = 0;
        }

         */
    }

    // Get queue size in each server

    public void updateQueueData(int clientZone) throws RemoteException {
        int queueSize = servers[clientZone - 1].getQueueSize();
        serverQueuesSizes[clientZone - 1] = queueSize;
    }



    @Override
    public ServerAddress assignServer(int clientZone) throws RemoteException {
        int selectedServer;

        // was clientZone 0 to 4 previously
        //if (clientZone < 1 || clientZone > numServers) {
            //System.out.println("Invalid client zone.");
            //System.exit(1);
        //}

        updateQueueData(clientZone);
        System.out.println("Queue size in Server " + clientZone + " is " + serverQueuesSizes[clientZone - 1]);

        if (serverQueuesSizes[clientZone - 1] < 18) {
            selectedServer = clientZone;
            //return new ServerAddress("server_" + selectedServer);
        }

        else {
            int neighborServer1 = (clientZone % 5) + 1; 
            int neighborServer2 = ((clientZone + 1) % 5) + 1; 

            if (serverQueuesSizes[neighborServer1 - 1] >= 8 && serverQueuesSizes[neighborServer2 - 1] >= 8) {
                selectedServer = clientZone;
            }

            else if (serverQueuesSizes[neighborServer1 - 1] == serverQueuesSizes[neighborServer2 - 1]) {
                selectedServer = (random.nextBoolean()) ? neighborServer1 : neighborServer2;
            }

            else {
                selectedServer = (serverQueuesSizes[neighborServer1 - 1] < serverQueuesSizes[neighborServer2 - 1]) ? neighborServer1 : neighborServer2;
            }
        }

        updateAssignmentCount(clientZone);

        System.out.println("Server " + selectedServer + " is assigned to Client " + clientZone);
        return new ServerAddress("server_" + selectedServer);
    }
}
