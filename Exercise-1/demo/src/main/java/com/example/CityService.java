package com.example;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityService implements CityInterface {

    //maybe make cities final
    private List<City> cities;

    public CityService(String csvFilePath) throws IOException {
        // Use CSVReader to load the cities from the CSV file
        // might move this to its own method for caching later
        CSVDatabase csvDatabase = new CSVDatabase();
        this.cities = csvDatabase.readCitiesFromCSV(csvFilePath);
    }


    //test method.
    public void printFirstFiveCities() {
        System.out.println("First 5 cities from the CSV:");
        for (int i = 0; i < Math.min(5, cities.size()); i++) {
            System.out.println(cities.get(i));
        }
    }

    @Override
    public int getPopulationOfCountry(String countryName) throws RemoteException {
        int totalPopulation = 0;
        for (City city : cities) {
            if (city.getCountryNameEN().equalsIgnoreCase(countryName)){
                totalPopulation += city.getPopulation();
            }
        }
        //might remove the sout later
        System.out.println(totalPopulation);
        return totalPopulation;
    }

    @Override
    public int getNumberOfCities(String countryName, int min) throws RemoteException {
        int numberOfCities = 0;
        for (City city : cities) {
            if (city.countryNameEN.equalsIgnoreCase(countryName) && city.getPopulation() >= min){
                numberOfCities += 1;
            }
        }
        System.out.println(numberOfCities);
        return numberOfCities;
    }

    @Override
    public int getNumberOfCountries(int cityCount, int minPopulation) throws RemoteException {
        Map<String, Integer> countryCityCount = new HashMap<>();
        for (City city : cities){
            if (city.getPopulation() >= minPopulation){
                String countryName = city.getCountryNameEN();

                //if countryCityCount already exists add 1 if not initialize it with the value 1
                if (countryCityCount.containsKey(countryName)){
                    countryCityCount.put(countryName, countryCityCount.get(countryName) +1);
                }else {
                    countryCityCount.put(countryName,1);
                }
            }
        }

        int numberOfCountries = 0;

        //traverse hashmap to return values if they are above cityCount
        for (int count : countryCityCount.values()){
            if (count >= cityCount) {
                numberOfCountries++;
            }
        }

        System.out.println(numberOfCountries);
        return numberOfCountries;
    }

    @Override
    public int getNumberOfCountries(int cityCount, int minPopulation, int maxPopulation) throws RemoteException {
        Map<String, Integer> countryCityCount = new HashMap<>();
        for (City city : cities){
            if (city.getPopulation() >= minPopulation && city.getPopulation() <= maxPopulation){
                String countryName = city.getCountryNameEN();

                //if countryCityCount already exists add 1 if not initialize it with the value 1
                if (countryCityCount.containsKey(countryName)){
                    countryCityCount.put(countryName, countryCityCount.get(countryName) +1);
                }else {
                    countryCityCount.put(countryName,1);
                }
            }
        }

        int numberOfCountries = 0;

        //traverse hashmap to return values if they are above cityCount
        for (int count : countryCityCount.values()){
            if (count >= cityCount) {
                numberOfCountries++;
            }
        }

        System.out.println(numberOfCountries);
        return numberOfCountries;
    }
}
