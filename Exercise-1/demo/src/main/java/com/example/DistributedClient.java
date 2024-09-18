package com.example.rmi;

//import java.rmi.registry.LocateRegistry;
//import java.rmi.registry.Registry;
//import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.util.Scanner;
//import java.rmi.*;

public class DistributedClient {
    public void runClient(int clientZone) {
        try {
            LoadBalancerInterface loadBalancer = (LoadBalancerInterface) Naming.lookup("rmi://localhost/LoadBalancer");
            String availableServerUrl = loadBalancer.getAvailableServer(clientZone);

            if (availableServerUrl != null) {
                System.out.println("Available server found: " + availableServerUrl);

                // Now connect to the available server
                ServerInterface server = (ServerInterface) Naming.lookup(availableServerUrl);

                Scanner scanner = new Scanner(System.in);

                while (true) {
                    System.out.println("Client " + clientZone + ": Choose an option:");
                    System.out.println("1. Get population of a country");
                    System.out.println("2. Get number of cities with population greater than a given value in a country");
                    System.out.println("3. Get number of countries with at least a given number of cities having a minimum population");
                    System.out.println("4. Get number of countries with at least a given number of cities having population between two values");
                    System.out.println("5. Exit");

                    // Read user choice
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                    switch (choice) {
                        case 1:
                            // Get population of a country
                            System.out.print("Enter the country name: ");
                            String countryName = scanner.nextLine();
                            long population = server.getPopulationOfCountry(countryName);
                            System.out.println("Population of " + countryName + ": " + population);
                            break;

                        case 2:
                            // Get number of cities with population greater than a given value in a country
                            System.out.print("Enter the country name: ");
                            String country = scanner.nextLine();
                            System.out.print("Enter the minimum population: ");
                            long minPopulation = scanner.nextLong();
                            int citiesCount = server.getNumberOfCities(country, minPopulation);
                            System.out.println("Number of cities in " + country + " with population >= " + minPopulation + ": " + citiesCount);
                            scanner.nextLine(); // Consume newline
                            break;

                        case 3:
                            // Get number of countries with at least a given number of cities having a minimum population
                            System.out.print("Enter the city count: ");
                            int cityCount = scanner.nextInt();
                            System.out.print("Enter the minimum population: ");
                            long minCityPopulation = scanner.nextLong();
                            int countriesCount = server.getNumberOfCountries(cityCount, minCityPopulation);
                            System.out.println("Number of countries with at least " + cityCount + " cities having population >= " + minCityPopulation + ": " + countriesCount);
                            scanner.nextLine(); // Consume newline
                            break;

                        case 4:
                            // Get number of countries with at least a given number of cities having population between two values
                            System.out.print("Enter the city count: ");
                            int cityCountRange = scanner.nextInt();
                            System.out.print("Enter the minimum population: ");
                            long minPopulationRange = scanner.nextLong();
                            System.out.print("Enter the maximum population: ");
                            long maxPopulation = scanner.nextLong();
                            int countriesInRange = server.getNumberOfCountries(cityCountRange, minPopulationRange, maxPopulation);
                            System.out.println("Number of countries with at least " + cityCountRange + " cities having population between " + minPopulationRange + " and " + maxPopulation + ": " + countriesInRange);
                            scanner.nextLine(); // Consume newline
                            break;

                        case 5:
                            // Exit the application
                            System.out.println("Client " + clientZone + " exiting...");
                            scanner.close();
                            return;

                        default:
                            System.out.println("Invalid choice. Please try again.");
                            break;
                    }
                }

            } else {
                System.out.println("No available server found for client " + clientZone + ".");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Main method can still be used for running a single client manually
    public static void main(String[] args) {
        int clientZone = 1; // Default to zone 1 if not provided
        if (args.length > 0) {
            clientZone = Integer.parseInt(args[0]);
        }
        new DistributedClient().runClient(clientZone);
    }
}

