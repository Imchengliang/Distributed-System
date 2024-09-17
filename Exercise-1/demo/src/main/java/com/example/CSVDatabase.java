package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDatabase {
    public List<City> readCitiesFromCSV(String csvFilePath) throws IOException {
        List<City> cities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                // Skip the header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Split by ';'
                String[] values = line.split(";");

                // Parse the fields from the split data
                int geoNameID = Integer.parseInt(values[0]);
                String name = values[1];
                String countryCode = values[2];
                String countryNameEN = values[3];
                int population = Integer.parseInt(values[4]);
                String timezone = values[5];
                String[] coordinates = values[6].split(",");
                double coordinateX = Double.parseDouble(coordinates[0]);
                double coordinateY = Double.parseDouble(coordinates[1]);

                // Create a City object and add it to the list
                City city = new City(geoNameID, name, countryCode, countryNameEN, population, timezone, coordinateX, coordinateY);
                cities.add(city);
            }
        }
        return cities;
    }
}
