package com.example.rmi;

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

    private ServerInterface[] servers;
    private int[] serverQueuesSizes;
    private int[] serverAssignmentCounts;

    public LoadBalancer() throws RemoteException {
        super();
        this.numServers = numServers;
        this.port = port;
        this.servers = new ServerInterface[numServers];
        this.serverQueuesSizes = new int[numServers];
        this.serverAssignmentCounts = new int[numServers];
        //startLoadBalancer();

        try {
            // Export LoadBalancer
            UnicastRemoteObject.exportObject(this, port);
            registry = LocateRegistry.getRegistry("localhost", port - 1);
            for (int i = 0; i < numServers; i++) {
                servers[i] = (ServerInterface) registry.lookup("server_" + i);
            }

            // Bind the LoadBalancer to the registry
            registry.bind("LoadBalancer", this);

        } catch (Exception e) {
            System.out.println("\nError:\n" + e);
            System.exit(1);
        }
        System.out.println("LoadBalancer is started");
    }

    private void updateAssignmentCount(int clientZone) {
        serverAssignmentCounts[clientZone]++;

        if (serverAssignmentCounts[clientZone] >= 18) {
            new Thread(new ProxyServerQueueUpdater(this, clientZone)).start();
            serverAssignmentCounts[clientZone] = 0;
        }
    }

    // Get queue size in each server
    public void updateQueueData(int clientZone) throws RemoteException {
        int queueSize = servers[clientZone].getQueueSize();
        serverQueuesSizes[clientZone] = queueSize;
    }

    @Override
    public ServerAddress assignServer(int clientZone, String clientMessage) throws RemoteException {
        int selectedServer;

        if (clientZone < 0 || clientZone > 4) {
            System.out.println("Invalid client zone.");
            System.exit(1);
        }

        if (serverQueuesSizes[clientZone] < 18) {
            selectedServer = clientZone;
            //return new ServerAddress("server_" + selectedServer);
        }

        else {
            int neighborServer1 = (clientZone % 5) + 1; 
            int neighborServer2 = ((clientZone + 1) % 5) + 1; 

            if (serverQueuesSizes[neighborServer1] >= 8 && serverQueuesSizes[neighborServer2] >= 8) {
                selectedServer = clientZone;
            }

            else if (serverQueuesSizes[neighborServer1] == serverQueuesSizes[neighborServer2]) {
                selectedServer = (random.nextBoolean()) ? neighborServer1 : neighborServer2;
            }

            else {
                selectedServer = (serverQueuesSizes[neighborServer1] < serverQueuesSizes[neighborServer2]) ? neighborServer1 : neighborServer2;
            }
        }

        updateAssignmentCount(clientZone);

        System.out.println("server_" + selectedServer + " is assigned to client_" + clientZone);
        return new ServerAddress("server_" + selectedServer);
    }
}
