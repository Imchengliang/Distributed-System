package com.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CityInterface extends Remote {
    int getPopulationOfCountry(String countryName) throws RemoteException;
    int getNumberOfCities(String countryName, int min) throws RemoteException;
    int getNumberOfCountries(int cityCount, int minPopulation) throws RemoteException;
    int getNumberOfCountries(int cityCount, int minPopulation, int maxPopulation) throws RemoteException;
}
