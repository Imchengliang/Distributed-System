package com.example;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadBalancer2 extends UnicastRemoteObject implements ProxyInterface {

    private static final int SAME_ZONE_LATENCY_MS = 80;
    private static final int NEIGHBOR_ZONE_LATENCY_MS = 170;

    private Map<Integer, ServerInfo> serverInfoMap;
    private int numZones;

    LoadBalancer2(int numZones) throws RemoteException {
        super();
        this.numZones = numZones;
        serverInfoMap = new HashMap<>();

    }

    public void registerServer(int zone, ServerInfo serverInfo){
        serverInfoMap.put(zone, serverInfo);
        System.out.println("Loadbalancer registered server " + serverInfo + " serverzone " + zone);
    }

    public List<Integer> getNeighborZones(int zone) {
        List<Integer> neighbors = new ArrayList<>();
        int numZones = 5; // Total number of zones

        // Calculate next two neighbor zones
        for (int i = 1; i <= 2; i++) {
            int neighborZone = zone + i;

            // wrap-around
            if (neighborZone > numZones) {
                neighborZone -= numZones;
            }

            neighbors.add(neighborZone);
        }

        return neighbors;
    }

    private void simulateNetworkLatency(boolean isNeighbor) {
        int latency = isNeighbor ? NEIGHBOR_ZONE_LATENCY_MS : SAME_ZONE_LATENCY_MS;
        try {
            Thread.sleep(latency);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void applyNetworkLatency(int requestedZone) {
        // Determine if the requested zone is a neighbor zone
        List<Integer> neighbors = getNeighborZones(requestedZone);
        boolean isNeighbor = neighbors.contains(requestedZone);

        // Simulate network latency based on whether it's a neighbor zone or same zone
        simulateNetworkLatency(isNeighbor);
    }

    @Override
    public ServerInfo getServer(int zone) throws RemoteException {
        return serverInfoMap.get(zone);
    }
}
