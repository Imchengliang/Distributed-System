package com.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CityInterface extends Remote {
    int getPopulationOfCountry(int clientZone, String countryName) throws RemoteException;
    int getNumberOfCities(int clientZone, String countryName, int min) throws RemoteException;
    int getNumberOfCountries(int clientZone, int cityCount, int minPopulation) throws RemoteException;
    int getNumberOfCountries(int clientZone, int cityCount, int minPopulation, int maxPopulation) throws RemoteException;
}
