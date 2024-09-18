package com.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            CityService cityService = new CityService("Exercise-1/demo/src/main/resources/dataset/exercise_1_dataset.csv");

            // Call the method to print the first 5 cities to make sure it runs
            //cityService.printFirstFiveCities();
            //testing getPopulationOfCountry
            cityService.getPopulationOfCountry("Norway");
            cityService.getPopulationOfCountry("Sweden");
            //testing getNumberOfCities
            cityService.getNumberOfCities("Norway", 100000);
            //testing cityCount and minValue
            cityService.getNumberOfCountries(2, 5000000);
            //testing cityCount with min and maxValue
            cityService.getNumberOfCountries(30, 100000, 800000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}