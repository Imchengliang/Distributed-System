package com.example;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface CityInterface extends Remote {
    List<CityServiceResult> getPopulationOfCountry(int clientZone, String countryName) throws RemoteException;
    List<CityServiceResult> getNumberOfCities(int clientZone, String countryName, int min) throws RemoteException;
    List<CityServiceResult> getNumberOfCountries(int clientZone, int cityCount, int minPopulation) throws RemoteException;
    List<CityServiceResult> getNumberOfCountries(int clientZone, int cityCount, int minPopulation, int maxPopulation) throws RemoteException;
    int getQueueSize() throws RemoteException;
}
