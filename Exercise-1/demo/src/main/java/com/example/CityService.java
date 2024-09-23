package com.example;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;
import java.util.ArrayList;

public class CityService implements CityInterface {

    private List<City> cities;
    private final ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
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

    private void checkZoneLatency(int clientZone) throws InterruptedException {
        if (clientZone == this.serverZone) {
            Thread.sleep(80); // Same zone, lower latency
        } else {
            Thread.sleep(170); // Different zones, higher latency
        }
    }

    public void addTask(Runnable task, int clientZone) {
        taskQueue.add(new TimedTask(task, clientZone));
        synchronized (taskQueue) {
            taskQueue.notify();
        }
    }

    private void processTasks() {
        while (true) {
            TimedTask timedTask = (TimedTask) taskQueue.poll(); // Fetch the next task
            if (timedTask != null) {
                executing = true;
                try {
                    long delayStartTime = System.currentTimeMillis();
                    checkZoneLatency(timedTask.getClientZone());
                    long delayEndTime = System.currentTimeMillis();

                    // Calculate the delay
                    long delayTime = delayEndTime - delayStartTime;

                    // Set the delay time in the timedTask
                    timedTask.setDelayTime(delayTime);

                    timedTask.run(); // Execute the task

                    long executionTime = timedTask.getExecutionTime(); // Get adjusted execution time
                    long waitingTime = timedTask.getWaitingTime(); // Get waiting time

                    // You can log or process execution and waiting times as needed
                } catch (Exception e) {
                    System.out.println("Error during task execution: " + e.getMessage());
                } finally {
                    executing = false;
                }
            } else {
                synchronized (taskQueue) {
                    try {
                        taskQueue.wait(); // Wait for a new task
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
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

        long startTime = System.currentTimeMillis();
        TimedTask timedTask = new TimedTask(futureTask, clientZone);
        addTask(timedTask, clientZone); // Add to queue

        try {
            int population = futureTask.get();
            long endTime = System.currentTimeMillis();
            long turnaroundTime = endTime - startTime;
            long executionTime = timedTask.getExecutionTime();
            long waitingTime = timedTask.getWaitingTime();

            CityServiceResult result = new CityServiceResult(population, "getPopulationOfCountry", countryName, clientZone, turnaroundTime, executionTime, waitingTime, serverZone);
            cache.put(cacheKey, result);
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
                if (city.countryNameEN.equalsIgnoreCase(countryName) && city.getPopulation() >= min) {
                    numberOfCities += 1;
                }
            }
            return numberOfCities;
        });

        long startTime = System.currentTimeMillis();
        TimedTask timedTask = new TimedTask(futureTask, clientZone);
        addTask(timedTask, clientZone);

        try {
            int numberOfCities = futureTask.get();
            long endTime = System.currentTimeMillis();
            long turnaroundTime = endTime - startTime;
            long executionTime = timedTask.getExecutionTime();
            long waitingTime = timedTask.getWaitingTime();

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
        addTask(timedTask, clientZone);

        try {
            int numberOfCountries = futureTask.get();
            long endTime = System.currentTimeMillis();
            long turnaroundTime = endTime - startTime;
            long executionTime = timedTask.getExecutionTime();
            long waitingTime = timedTask.getWaitingTime();

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
        addTask(timedTask, clientZone);

        try {
            int numberOfCountries = futureTask.get();
            long endTime = System.currentTimeMillis();
            long turnaroundTime = endTime - startTime;
            long executionTime = timedTask.getExecutionTime();
            long waitingTime = timedTask.getWaitingTime();

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
