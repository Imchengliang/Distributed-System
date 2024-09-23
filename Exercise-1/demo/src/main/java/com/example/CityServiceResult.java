package com.example;

public record CityServiceResult(int result, String methodName, String inputQuery, int clientZone, long turnaroundTime,
                                long executionTime, long waitingTime, int serverZone) {
}