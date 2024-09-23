package com.example;

public class CityServiceResult {
    private final int result;
    private final String methodName;
    private final String inputQuery;
    private final int clientZone;
    private final long turnaroundTime;
    private final long executionTime;
    private final long waitingTime;
    private final int serverZone;


    public CityServiceResult(int result, String methodName, String inputQuery, int clientZone, long turnaroundTime, long executionTime, long waitingTime, int serverZone) {
        this.result = result;
        this.methodName = methodName;
        this.inputQuery = inputQuery;
        this.clientZone = clientZone;
        this.turnaroundTime = turnaroundTime;
        this.executionTime = executionTime;
        this.waitingTime = waitingTime;
        this.serverZone = serverZone;
    }

    public int getResult() {
        return result;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getInputQuery() {
        return inputQuery;
    }

    public int getClientZone() {
        return clientZone;
    }

    public long getTurnaroundTime() {
        return turnaroundTime;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public long getWaitingTime() {
        return waitingTime;
    }

    public int getServerZone() {
        return serverZone;
    }

    @Override
    public String toString() {
        return "CityServiceResult{" +
                "result=" + result +
                ", methodName='" + methodName + '\'' +
                ", inputQuery='" + inputQuery + '\'' +
                ", clientZone=" + clientZone +
                ", turnaroundTime=" + turnaroundTime +
                ", executionTime=" + executionTime +
                ", waitingTime=" + waitingTime +
                ", serverZone=" + serverZone +
                '}';
    }
}