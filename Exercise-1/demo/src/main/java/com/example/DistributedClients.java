package com.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
//import java.rmi.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.Query;

public class DistributedClients implements Remote {
    private int clientNumber;
    private Registry registry = null;
    private LoadBalancerInterface loadBalancer = null;
    private CityInterface server = null;

    long getPopulationOfCountryTurnaround = 0;
    long getNumberOfCitiesTurnaround = 0;
    long getNumberOfCountriesTurnaround = 0;

    long getPopulationOfCountryExecution = 0;
    long getNumberOfCitiesExecution = 0;
    long getNumberOfCountriesExecution = 0;

    long getPopulationOfCountryWaiting = 0;
    long getNumberOfCitiesWaiting = 0;
    long getNumberOfCountriesWaiting = 0;

    Lock lock = new ReentrantLock();

    public DistributedClients(int clientNumber, int port) {
        this.clientNumber = clientNumber;
        startClient(port);
    }

    private void startClient(int port) {
        try {
            registry = LocateRegistry.getRegistry("localhost", port - 7);
            loadBalancer = (LoadBalancerInterface) registry.lookup("load-balancer");
            UnicastRemoteObject.exportObject(this, port);
            registry.bind("client:" + clientNumber, this);

        } catch (Exception e) {
            System.out.println("Client Error:" + e.toString());
            System.exit(1);
        }
        System.out.println("Client:" + clientNumber + "is running");
    }

    public void sendQueryToServer(String fullQuery, int clientZone){
        getServer(clientZone);

        Pattern pattern = Pattern.compile("^(\\w+)\\s+(.*)\\s+Zone:(\\d+)$");
        Matcher matcher = pattern.matcher(fullQuery.trim());
        String methodName = null;
        String args = null;
        String zone = null;

        if (matcher.matches()) {
            methodName = matcher.group(1); // Get the method name
            args = matcher.group(2);       // Get the arguments
            zone = matcher.group(3);
        }

        try {
            int result = 0;
            switch (methodName) {
                case "getPopulationOfCountry" -> {
                    result = server.getPopulationOfCountry(clientZone, args);  // args is the country name in this case
                }
                case "getNumberOfCities" -> {
                    String[] arg = args.split(" ");
                    result = server.getNumberOfCities(clientZone, arg[0], Integer.parseInt(arg[1]));  // arg[0] is country name, arg[1] is min population
                }
                case "getNumberOfCountries" -> {
                    String[] arg = args.split(" ");
                    if (arg.length == 2) {
                        result = server.getNumberOfCountries(clientZone, Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));  // cityCount, minPopulation
                    } else {
                        result = server.getNumberOfCountries(clientZone, Integer.parseInt(arg[0]), Integer.parseInt(arg[1]), Integer.parseInt(arg[2]));  // cityCount, minPopulation, maxPopulation
                    }
                }
            }
            System.out.println("Result: " + result);
            //query.timeStamps[0] = System.currentTimeMillis();
            //server.sendQuery(query);
            //sentQueries++;
            //System.out.println("Client sent query. Number of sent queries: " + sentQueries);


        } catch (Exception e) {
            System.out.println("Query Error:" + e.toString());
            System.exit(1);
        }
    }

    private void getServer(int clientZone) {
        try {
            // Ask the proxy-server for a server address
            ServerAddress response = loadBalancer.assignServer(clientZone);

            // Lookup the returned server address
            server = (CityInterface) registry.lookup(response.address);
        } catch (Exception e) {
            System.out.println("Error occurs between LoadBalancer and Client " + clientNumber);
            System.exit(1);
        }
    }
}    


