package com.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            CityService cityService = new CityService("Exercise-1/demo/src/main/resources/dataset/exercise_1_dataset.csv", 1);
            cityService.getNumberOfCountries(30, 100000, 800000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}