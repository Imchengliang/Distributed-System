package com.example;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class CityService implements CityInterface {

    private List<City> cities;
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
    private final int serverZone;
    private volatile boolean executing = false;
    private final FixedSizeCache<String, CityServiceResult> cache;
    private final boolean cacheEnabled;

    public CityService(String csvFilePath, int serverZone, boolean cacheEnabled) throws IOException {
        CSVDatabase csvDatabase = new CSVDatabase();
        this.cities = csvDatabase.readCitiesFromCSV(csvFilePath);
        this.serverZone = serverZone;
        this.cacheEnabled = cacheEnabled;
        this.cache = new FixedSizeCache<>(150);
        new Thread(this::processTasks).start();
    }

    private long checkZoneLatency(int clientZone) throws InterruptedException {
        long delayTime;
        if (clientZone == this.serverZone) {
            delayTime = 80;
            Thread.sleep(delayTime); // Same zone, lower latency
            System.out.println("\nSame zone, adding 80ms latency.");
        } else {
            delayTime = 170;
            Thread.sleep(delayTime); // Different zones, higher latency
            System.out.println("\nDifferent zone, adding 170ms latency.");
        }
        return delayTime;
    }

    public void addTask(Runnable task, int clientZone) {
        taskQueue.add(new TimedTask(task, clientZone));
        synchronized (taskQueue) {
            taskQueue.notify();
        }
    }

    private void processTasks() {
        System.out.println("Starting processTasks...");
        while (true) {
            try {
                // This will block until a task is available
                TimedTask timedTask = (TimedTask) taskQueue.take();
                //logQueueSizeIfChanged();

                // Now we have a valid task to execute
                executing = true;
                try {
                    // Check delay time
                    //long delayTime = checkZoneLatency(timedTask.getClientZone());

                    // Set the delay time in the timedTask
                    //timedTask.setDelayTime(delayTime);

                    Thread.sleep(80);
                    timedTask.run(); // Execute the task
                    //new Thread(timedTask.getTask()).start();

                    // You can log or process execution and waiting times as needed
                } catch (Exception e) {
                    System.out.println("Error during task execution: " + e.getMessage());
                } finally {
                    executing = false;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return; // Exit if interrupted
            }
        }
    }


    @Override
    public List<CityServiceResult> getPopulationOfCountry(int clientZone, String countryName) throws RemoteException {
        String cacheKey = "population:" + countryName;
        if (cacheEnabled && cache.containsKey(cacheKey)) {
            CityServiceResult cachedResult = cache.get(cacheKey);
            return List.of(cachedResult);
        }

        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            int totalPopulation = 0;
            for (City city : cities) {
                if (city.getCountryNameEN().equalsIgnoreCase(countryName)) {
                    totalPopulation += city.getPopulation();
                }
            }
            return totalPopulation;
        });

        
        long startTime = System.currentTimeMillis(); // Time for entering queue
        TimedTask timedTask = new TimedTask(futureTask, clientZone);

        long delayTime;
        try {
            delayTime = checkZoneLatency(timedTask.getClientZone());
        } catch (InterruptedException e) {
            // Handle the exception, for example:
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.err.println("Thread was interrupted: " + e.getMessage());
            // You can also choose to return a default value or rethrow the exception
            delayTime = -1; // or any appropriate default value
        }

        addTask(timedTask, clientZone); // Add to queue

        try {
            // Wait for the result of the future task
            int population = futureTask.get();
            long endTime = System.currentTimeMillis(); // Time for getting result
            long turnaroundTime = endTime - startTime;

            /* 
            long delayTime;
            if (clientZone == this.serverZone) {
                delayTime = 80;
            } else {
                delayTime = 170;
            }*/

            long executionTime = timedTask.getExecutionTime();
            long waitingTime = timedTask.getWaitingTime(delayTime);

            CityServiceResult result = new CityServiceResult(population, "getPopulationOfCountry", countryName, clientZone, turnaroundTime, executionTime, waitingTime, serverZone);
            cache.put(cacheKey, result);
            System.out.println(result);
            return List.of(result);
        } catch (Exception e) {
            throw new RemoteException("Error retrieving population", e);
        }
    }

    @Override
    public List<CityServiceResult> getNumberOfCities(int clientZone, String countryName, int min) throws RemoteException {
        String cacheKey = "cities:" + countryName + ":" + min;
        if (cacheEnabled && cache.containsKey(cacheKey)) {
            CityServiceResult cachedResult = cache.get(cacheKey);
            return List.of(cachedResult);
        }

        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            int numberOfCities = 0;
            for (City city : cities) {
                if (city.getCountryNameEN().equalsIgnoreCase(countryName) && city.getPopulation() >= min) {
                    numberOfCities++;
                }
            }
            return numberOfCities;
        });

        long startTime = System.currentTimeMillis();
        TimedTask timedTask = new TimedTask(futureTask, clientZone);

        long delayTime;
        try {
            delayTime = checkZoneLatency(timedTask.getClientZone());
        } catch (InterruptedException e) {
            // Handle the exception, for example:
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.err.println("Thread was interrupted: " + e.getMessage());
            // You can also choose to return a default value or rethrow the exception
            delayTime = -1; // or any appropriate default value
        }
        addTask(futureTask, clientZone); // Updated to call addTask with FutureTask

        try {
            int numberOfCities = futureTask.get(); // Wait for the result
            long endTime = System.currentTimeMillis();
            long turnaroundTime = endTime - startTime;

            /* 
            long delayTime;
            if (clientZone == this.serverZone) {
                delayTime = 80;
            } else {
                delayTime = 170;
            }*/

            long executionTime = timedTask.getExecutionTime();
            long waitingTime = timedTask.getWaitingTime(delayTime);

            CityServiceResult result = new CityServiceResult(numberOfCities, "getNumberOfCities", countryName + " " + min, clientZone, turnaroundTime, executionTime, waitingTime, serverZone);
            cache.put(cacheKey, result);
            return List.of(result);
        } catch (Exception e) {
            throw new RemoteException("Error retrieving Cities", e);
        }
    }


    @Override
    public List<CityServiceResult> getNumberOfCountries(int clientZone, int cityCount, int minPopulation) throws RemoteException {
        String cacheKey = "countries:" + cityCount + ":" + minPopulation;
        if (cacheEnabled && cache.containsKey(cacheKey)) {
            CityServiceResult cachedResult = cache.get(cacheKey);
            return List.of(cachedResult);
        }

        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            Map<String, Integer> countryCityCount = new HashMap<>();
            for (City city : cities) {
                if (city.getPopulation() >= minPopulation) {
                    String countryName = city.getCountryNameEN();
                    countryCityCount.put(countryName, countryCityCount.getOrDefault(countryName, 0) + 1);
                }
            }

            int numberOfCountries = 0;
            for (int count : countryCityCount.values()) {
                if (count >= cityCount) {
                    numberOfCountries++;
                }
            }
            return numberOfCountries;
        });

        long startTime = System.currentTimeMillis();
        TimedTask timedTask = new TimedTask(futureTask, clientZone);
        long delayTime;
        try {
            delayTime = checkZoneLatency(timedTask.getClientZone());
        } catch (InterruptedException e) {
            // Handle the exception, for example:
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.err.println("Thread was interrupted: " + e.getMessage());
            // You can also choose to return a default value or rethrow the exception
            delayTime = -1; // or any appropriate default value
        }
        addTask(futureTask, clientZone); // Directly add FutureTask to the queue

        try {
            int numberOfCountries = futureTask.get(); // Wait for the result
            long endTime = System.currentTimeMillis();
            long turnaroundTime = endTime - startTime;

            /* 
            long delayTime;
            if (clientZone == this.serverZone) {
                delayTime = 80;
            } else {
                delayTime = 170;
            }*/

            long executionTime = timedTask.getExecutionTime();
            long waitingTime = timedTask.getWaitingTime(delayTime);

            CityServiceResult result = new CityServiceResult(numberOfCountries, "getNumberOfCountries", cityCount + " " + minPopulation, clientZone, turnaroundTime, executionTime, waitingTime, serverZone);
            cache.put(cacheKey, result);
            return List.of(result);
        } catch (Exception e) {
            throw new RemoteException("Error retrieving countries", e);
        }
    }


    @Override
    public List<CityServiceResult> getNumberOfCountries(int clientZone, int cityCount, int minPopulation, int maxPopulation) throws RemoteException {
        String cacheKey = "countries:" + cityCount + ":" + minPopulation + ":" + maxPopulation;
        if (cacheEnabled && cache.containsKey(cacheKey)) {
            CityServiceResult cachedResult = cache.get(cacheKey);
            return List.of(cachedResult);
        }

        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            Map<String, Integer> countryCityCount = new HashMap<>();
            for (City city : cities) {
                if (city.getPopulation() >= minPopulation && city.getPopulation() <= maxPopulation) {
                    String countryName = city.getCountryNameEN();
                    countryCityCount.put(countryName, countryCityCount.getOrDefault(countryName, 0) + 1);
                }
            }

            int numberOfCountries = 0;
            for (int count : countryCityCount.values()) {
                if (count >= cityCount) {
                    numberOfCountries++;
                }
            }
            return numberOfCountries;
        });

        long startTime = System.currentTimeMillis();
        TimedTask timedTask = new TimedTask(futureTask, clientZone);
        long delayTime;
        try {
            delayTime = checkZoneLatency(timedTask.getClientZone());
        } catch (InterruptedException e) {
            // Handle the exception, for example:
            Thread.currentThread().interrupt(); // Restore the interrupted status
            System.err.println("Thread was interrupted: " + e.getMessage());
            // You can also choose to return a default value or rethrow the exception
            delayTime = -1; // or any appropriate default value
        }
        addTask(futureTask, clientZone); // Directly add FutureTask to the queue

        try {
            int numberOfCountries = futureTask.get(); // Wait for the result
            long endTime = System.currentTimeMillis();
            long turnaroundTime = endTime - startTime;

            /* 
            long delayTime;
            if (clientZone == this.serverZone) {
                delayTime = 80;
            } else {
                delayTime = 170;
            }*/

            long executionTime = timedTask.getExecutionTime();
            long waitingTime = timedTask.getWaitingTime(delayTime);

            CityServiceResult result = new CityServiceResult(numberOfCountries, "getNumberOfCountries", cityCount + " " + minPopulation + " " + maxPopulation, clientZone, turnaroundTime, executionTime, waitingTime, serverZone);
            cache.put(cacheKey, result);
            return List.of(result);
        } catch (Exception e) {
            throw new RemoteException("Error retrieving countries", e);
        }
    }
    
    @Override
    public int getQueueSize() throws RemoteException {
        return taskQueue.size();
    }
}
