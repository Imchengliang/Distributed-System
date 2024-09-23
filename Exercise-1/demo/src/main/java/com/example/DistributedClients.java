package com.example;

import java.io.IOException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.FileWriter;
import java.io.IOException;
import javax.management.Query;

public class DistributedClients implements Remote {
    private int clientNumber;
    private Registry registry = null;
    private LoadBalancerInterface loadBalancer = null;
    private CityInterface server = null;
    private boolean toggle = true;
    private List<List<CityServiceResult>> results = new ArrayList<>();
    
    private List<Integer> getPopulationOfCountryTurnAround = new ArrayList<>();
    private List<Integer> getPopulationOfCountryExecution = new ArrayList<>();
    private List<Integer> getPopulationOfCountryWaiting = new ArrayList<>();

    private List<Integer> getNumberOfCitiesTurnAround = new ArrayList<>();
    private List<Integer> getNumberOfCitiesExecution = new ArrayList<>();
    private List<Integer> getNumberOfCitiesWaiting = new ArrayList<>();

    private List<Integer> getNumberOfCountries1TurnAround = new ArrayList<>();
    private List<Integer> getNumberOfCountries1Execution = new ArrayList<>();
    private List<Integer> getNumberOfCountries1Waiting = new ArrayList<>();

    private List<Integer> getNumberOfCountries2TurnAround = new ArrayList<>();
    private List<Integer> getNumberOfCountries2Execution = new ArrayList<>();
    private List<Integer> getNumberOfCountries2Waiting = new ArrayList<>();

    Lock lock = new ReentrantLock();

    public DistributedClients(int clientNumber, int port) {
        this.clientNumber = clientNumber;
        startClient(port);
    }

    private void startClient(int port) {
        try {
            registry = LocateRegistry.getRegistry("localhost", 1099);
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
            List<CityServiceResult> result = new ArrayList<>();;
            switch (methodName) {
                case "getPopulationOfCountry" -> {
                    result = server.getPopulationOfCountry(clientZone, args);  // args is the country name in this case
                    results.add(result);
                    for (CityServiceResult cityResult : result) {
                        getPopulationOfCountryTurnAround.add((int) cityResult.getTurnaroundTime());
                        getPopulationOfCountryExecution.add((int) cityResult.getExecutionTime());
                        getPopulationOfCountryWaiting.add((int) cityResult.getWaitingTime());
                    }
                }
                case "getNumberOfCities" -> {
                    String[] arg = args.split(" ");
                    result = server.getNumberOfCities(clientZone, arg[0], Integer.parseInt(arg[1]));  // arg[0] is country name, arg[1] is min population
                    results.add(result);
                    for (CityServiceResult cityResult : result) {
                        getNumberOfCitiesTurnAround.add((int) cityResult.getTurnaroundTime());
                        getNumberOfCitiesExecution.add((int) cityResult.getExecutionTime());
                        getNumberOfCitiesWaiting.add((int) cityResult.getWaitingTime());
                    }
                }
                case "getNumberOfCountries" -> {
                    String[] arg = args.split(" ");
                    if (arg.length == 2) {
                        result = server.getNumberOfCountries(clientZone, Integer.parseInt(arg[0]), Integer.parseInt(arg[1]));  // cityCount, minPopulation
                        results.add(result);
                        for (CityServiceResult cityResult : result) {
                            getNumberOfCountries1TurnAround.add((int) cityResult.getTurnaroundTime());
                            getNumberOfCountries1Execution.add((int) cityResult.getExecutionTime());
                            getNumberOfCountries1Waiting.add((int) cityResult.getWaitingTime());
                        }
                    } else {
                        result = server.getNumberOfCountries(clientZone, Integer.parseInt(arg[0]), Integer.parseInt(arg[1]), Integer.parseInt(arg[2]));  // cityCount, minPopulation, maxPopulation
                        results.add(result);
                        //CityServiceResult cityResult1 = result.get(4);
                        //getNumberOfCountries2TurnAround.add((int) cityResult1.getTurnaroundTime());
                        //CityServiceResult cityResult2 = result.get(5);
                        //getNumberOfCountries2Execution.add((int) cityResult2.getExecutionTime());
                        //CityServiceResult cityResult3 = result.get(6);
                        //getNumberOfCountries2Waiting.add((int) cityResult3.getWaitingTime());
                        for (CityServiceResult cityResult : result) {
                            getNumberOfCountries2TurnAround.add((int) cityResult.getTurnaroundTime());
                            getNumberOfCountries2Execution.add((int) cityResult.getExecutionTime());
                            getNumberOfCountries2Waiting.add((int) cityResult.getWaitingTime());
                        }
                    }
                }
            }
            System.out.println("Result: " + result);

            // trigger delay
            int delay = toggle ? 50 : 20;
            Thread.sleep(delay);
            toggle = !toggle;

            //writeResultToTxt(results);
            //query.timeStamps[0] = System.currentTimeMillis();
            //server.sendQuery(query);
            //sentQueries++;
            //System.out.println("Client sent query. Number of sent queries: " + sentQueries);


        } catch (Exception e) {
            System.out.println("Query Error:" + e.toString());
            System.exit(1);
        }
    }

    private static Object[] calculateAvgMinMax(List<Integer> numbers) {
        int sum = 0;
        int max = Collections.max(numbers);
        int min = Collections.min(numbers); 

        for (int number : numbers) {
            sum += number;
        }

        float average = (float) sum / numbers.size();

        return new Object[] {average, min, max};
    }

    private void writeResultToTxt(List<List<Object>> results){
        try {
            FileWriter writer = new FileWriter("dataset/naive_server.txt");

            for (List<Object> result : results) {
                writer.write(result.toString() + "\n");
            }

            Object[] stats11 = calculateAvgMinMax(getPopulationOfCountryTurnAround);
            Object[] stats12 = calculateAvgMinMax(getPopulationOfCountryExecution);
            Object[] stats13 = calculateAvgMinMax(getPopulationOfCountryWaiting);
            writer.write("getPopulationOfCountry avg turn-around time: " + stats11[0] + "ms, avg execution time: " + stats12[0] + "ms, avg waiting time: " + stats13[0] + "ms, min turn-around time: " + stats11[1] + "ms, max turn-around time: " + stats11[1] + "ms\n");

            Object[] stats21 = calculateAvgMinMax(getNumberOfCitiesTurnAround);
            Object[] stats22 = calculateAvgMinMax(getNumberOfCitiesExecution);
            Object[] stats23 = calculateAvgMinMax(getNumberOfCitiesWaiting);
            writer.write("getNumberOfCities avg turn-around time: " + stats21[0] + "ms, avg execution time: " + stats22[0] + "ms, avg waiting time: " + stats23[0] + "ms, min turn-around time: " + stats21[1] + "ms, max turn-around time: " + stats21[1] + "ms\n");

            Object[] stats31 = calculateAvgMinMax(getNumberOfCountries1TurnAround);
            Object[] stats32 = calculateAvgMinMax(getNumberOfCountries1Execution);
            Object[] stats33 = calculateAvgMinMax(getNumberOfCountries1Waiting);
            writer.write("getNumberOfCountries avg turn-around time: " + stats31[0] + "ms, avg execution time: " + stats32[0] + "ms, avg waiting time: " + stats33[0] + "ms, min turn-around time: " + stats31[1] + "ms, max turn-around time: " + stats31[1] + "ms\n");

            Object[] stats41 = calculateAvgMinMax(getNumberOfCountries2TurnAround);
            Object[] stats42 = calculateAvgMinMax(getNumberOfCountries2Execution);
            Object[] stats43 = calculateAvgMinMax(getNumberOfCountries2Waiting);
            writer.write("getNumberOfCountries avg turn-around time: " + stats41[0] + "ms, avg execution time: " + stats42[0] + "ms, avg waiting time: " + stats43[0] + "ms, min turn-around time: " + stats41[1] + "ms, max turn-around time: " + stats41[1] + "ms\n");

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
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


