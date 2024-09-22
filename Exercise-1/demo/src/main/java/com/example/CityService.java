package com.example;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.FutureTask;

public class CityService implements CityInterface {

    //maybe make cities final


    private List<City> cities;
    private final ConcurrentLinkedQueue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();
    private final int serverZone;
    private volatile boolean executing = false;

    private final Map<String, Integer> cache = new HashMap<>();

    public CityService(String csvFilePath, int serverZone) throws IOException {
        CSVDatabase csvDatabase = new CSVDatabase();
        this.cities = csvDatabase.readCitiesFromCSV(csvFilePath);
        this.serverZone = serverZone;

        // Start the task acceptance and processing threads
        new Thread(this::processTasks).start();
    }


private void checkZoneLatency(Request request) throws InterruptedException {
    // Check client zone and add latency
    if (request.getClientZone() == this.serverZone) {
        Thread.sleep(80); // Same zone, lower latency
    } else {
        Thread.sleep(170); // Different zones, higher latency
    }
}

    public void addTask(Runnable task) {
        taskQueue.add(task);
        synchronized (taskQueue) {
            taskQueue.notify(); // Notify the processing thread
        }
    }

    // Processes tasks from the queue
    private void processTasks() {
        while (true) {
            Runnable task = taskQueue.poll(); // Fetch the next task
            if (task != null) {
                System.out.println("processing new task");
                executing = true; // Indicate that we're in execution mode
                try {
                    System.out.println("starting execution of task");
                    task.run(); // Execute the task
                    System.out.println("task finished");
                } catch (Exception e) {
                    System.out.println("Error during task execution: " + e.getMessage());
                } finally {
                    executing = false; // Reset the execution flag
                }
            } else {
                // Wait if no tasks are available
                System.out.println("waiting for task");
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
    public int getPopulationOfCountry(String countryName) throws RemoteException {
        String cacheKey = "population:" + countryName;
        if (cache.containsKey(cacheKey)) {
            System.out.println("Fetching from cache for country: " + countryName);
            return cache.get(cacheKey);
        }
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            int totalPopulation = 0;
            for (City city : cities) {
                if (city.getCountryNameEN().equalsIgnoreCase(countryName)) {
                    totalPopulation += city.getPopulation();
                }
            }
            //might remove the sout later
            System.out.println(totalPopulation);

            return totalPopulation;
        });

        addTask(futureTask);

        try {
            // This will block until the task is completed
            int population = futureTask.get();
            // Store result in cache
            cache.put(cacheKey, population);
            return population;
        } catch (Exception e) {
            throw new RemoteException("Error retrieving population", e);
        }

    }

    @Override
    public int getNumberOfCities(String countryName, int min) throws RemoteException {
        String cacheKey = "cities:" + countryName + ":" + min;
        if (cache.containsKey(cacheKey)) {
            System.out.println("Fetching from cache for cities in " + countryName + " with min " + min);
            return cache.get(cacheKey);
        }
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            int numberOfCities = 0;
            for (City city : cities) {
                if (city.countryNameEN.equalsIgnoreCase(countryName) && city.getPopulation() >= min) {
                    numberOfCities += 1;
                }
            }
            System.out.println(numberOfCities);
            return numberOfCities;
        });

        addTask(futureTask);

        try {
            int numberOfCities = futureTask.get();
            cache.put(cacheKey, numberOfCities); // Cache the result
            return numberOfCities;

        } catch (Exception e) {
            throw new RemoteException("Error retrieving Cities", e);
        }

    }

    @Override
    public int getNumberOfCountries(int cityCount, int minPopulation) throws RemoteException {
        String cacheKey = "countries:" + cityCount + ":" + minPopulation;
        if (cache.containsKey(cacheKey)) {
            System.out.println("Fetching from cache for countries with cityCount " + cityCount + " and minPopulation " + minPopulation);
            return cache.get(cacheKey);
        }
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            Map<String, Integer> countryCityCount = new HashMap<>();
            for (City city : cities) {
                if (city.getPopulation() >= minPopulation) {
                    String countryName = city.getCountryNameEN();

                    //if countryCityCount already exists add 1 if not initialize it with the value 1
                    if (countryCityCount.containsKey(countryName)) {
                        countryCityCount.put(countryName, countryCityCount.get(countryName) + 1);
                    } else {
                        countryCityCount.put(countryName, 1);
                    }
                }
            }

            int numberOfCountries = 0;

            //traverse hashmap to return values if they are above cityCount
            for (int count : countryCityCount.values()) {
                if (count >= cityCount) {
                    numberOfCountries++;
                }
            }

            System.out.println(numberOfCountries);
            return numberOfCountries;
        });

        addTask(futureTask);

        try {
            int numberOfCountries = futureTask.get();
            cache.put(cacheKey, numberOfCountries); // Cache the result
            return numberOfCountries;
        } catch (Exception e) {
            throw new RemoteException("Error retrieving countries", e);
        }

    }

    @Override
    public int getNumberOfCountries(int cityCount, int minPopulation, int maxPopulation) throws RemoteException {
        String cacheKey = "countries:" + cityCount + ":" + minPopulation + ":" + maxPopulation;
        if (cache.containsKey(cacheKey)) {
            System.out.println("Fetching from cache for countries with cityCount " + cityCount + ", minPopulation " + minPopulation + " and maxPopulation " + maxPopulation);
            return cache.get(cacheKey);
        }
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            Map<String, Integer> countryCityCount = new HashMap<>();
            for (City city : cities) {
                if (city.getPopulation() >= minPopulation && city.getPopulation() <= maxPopulation) {
                    String countryName = city.getCountryNameEN();

                    //if countryCityCount already exists add 1 if not initialize it with the value 1
                    if (countryCityCount.containsKey(countryName)) {
                        countryCityCount.put(countryName, countryCityCount.get(countryName) + 1);
                    } else {
                        countryCityCount.put(countryName, 1);
                    }
                }
            }

            int numberOfCountries = 0;

            //traverse hashmap to return values if they are above cityCount
            for (int count : countryCityCount.values()) {
                if (count >= cityCount) {
                    numberOfCountries++;
                }
            }

            System.out.println(numberOfCountries);
            return numberOfCountries;
        });
        addTask(futureTask);

        try {
            int numberOfCountries = futureTask.get();
            cache.put(cacheKey, numberOfCountries); // Cache the result
            return numberOfCountries;
        } catch (Exception e) {
            throw new RemoteException("Error retrieving countries", e);
        }
    }
}
