package com.example;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

public class CityService implements CountryInterface{

    //maybe make cities final
    private List<City> cities;

    public CityService(String csvFilePath) throws IOException {
        // Use CSVReader to load the cities from the CSV file
        CSVDatabase csvDatabase = new CSVDatabase();
        this.cities = csvDatabase.readCitiesFromCSV(csvFilePath);
    }


    public void printFirstFiveCities() {
        System.out.println("First 5 cities from the CSV:");
        for (int i = 0; i < Math.min(5, cities.size()); i++) {
            System.out.println(cities.get(i));
        }
    }

    @Override
    public int getPopulationOfCountry(String countryName) throws RemoteException {
        return 0;
    }

    @Override
    public int getNumberOfCities(String countryName, int min) throws RemoteException {
        return 0;
    }

    @Override
    public int getNumberOfCountries(int cityCount, int minPopulation) throws RemoteException {
        return 0;
    }

    @Override
    public int getNumberOfCountries2(int cityCount, int minPopulation, int maxPopulation) throws RemoteException {
        return 0;
    }
}
