package com.example.rmi;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class LoadBalancer extends UnicastRemoteObject implements LoadBalancerInterface {
    private Map<Integer, List<ServerInterface>> zoneServers;
    private Map<Integer, Integer> zoneServerIndex;

    public LoadBalancer() throws RemoteException {
        super();
        zoneServers = new HashMap<>();
        zoneServerIndex = new HashMap<>();

        for (int zone = 1; zone <= 5; zone++) {
            zoneServers.put(zone, new ArrayList<>());
            zoneServerIndex.put(zone, 0);

            try {
                ServerInterface server = (ServerInterface) Naming.lookup("rmi://localhost/Server" + zone);
                zoneServers.get(zone).add(server);
            } catch (Exception e) {
                System.err.println("Error locating server " + zone + ": " + e.getMessage());
            }
        }
    }

    @Override
    public String assignServer(int clientZone, String clientMessage) throws RemoteException {
        List<ServerInterface> servers = zoneServers.get(clientZone);
        int currentIndex = zoneServerIndex.get(clientZone);

        for (int i = 0; i < servers.size(); i++) {
            ServerInterface server = servers.get(currentIndex);
            if (!server.isBusy()) {
                zoneServerIndex.put(clientZone, (currentIndex + 1) % servers.size());
                return server.processRequest(clientMessage);
            }
            currentIndex = (currentIndex + 1) % servers.size();
        }

        return "No available server in zone " + clientZone;
    }

    public static void main(String[] args) {
        try {
            LoadBalancer lb = new LoadBalancer();
            Naming.rebind("rmi://localhost/LoadBalancer", lb);
            System.out.println("LoadBalancer started.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
