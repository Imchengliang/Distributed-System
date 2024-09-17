package com.example;

public class City {

    int geoNameID;
    String cityName;
    String countryCode;
    String countryNameEN;
    int population;
    String timezone;
    double coordinateX;
    double coordinateY;

    public City(int geoNameID, String cityName, String countryCode, String countryNameEN, int population, String timezone, double coordinateX, double coordinateY) {
        this.geoNameID = geoNameID;
        this.cityName = cityName;
        this.countryCode = countryCode;
        this.countryNameEN = countryNameEN;
        this.population = population;
        this.timezone = timezone;
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    public int getGeoNameID() {
        return geoNameID;
    }

    public void setGeoNameID(int geoNameID) {
        this.geoNameID = geoNameID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryNameEN() {
        return countryNameEN;
    }

    public void setCountryNameEN(String countryNameEN) {
        this.countryNameEN = countryNameEN;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    @Override
    public String toString() {
        return "City{" +
                "geoNameID=" + geoNameID +
                ", countryName='" + cityName + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", countryNameEN='" + countryNameEN + '\'' +
                ", population=" + population +
                ", timezone='" + timezone + '\'' +
                ", coordinateX=" + coordinateX +
                ", coordinateY=" + coordinateY +
                '}';
    }
}
