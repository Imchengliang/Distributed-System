package com.example;

import java.util.concurrent.FutureTask;

public class TimedTask implements Runnable {
    private final FutureTask<?> futureTask; // Use FutureTask directly
    private long executionTime;
    private final long enqueueTime; // Time when the task was added to the queue
    private final int clientZone;
    private long delayTime; // To store the delay caused by latency

    public TimedTask(FutureTask<?> futureTask, int clientZone) {
        this.futureTask = futureTask;
        this.clientZone = clientZone;
        this.enqueueTime = System.currentTimeMillis(); // Capture enqueue time
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            futureTask.run(); // Execute the future task
        } finally {
            executionTime = System.currentTimeMillis() - startTime - delayTime; // Adjust execution time for delay
        }
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime; // Set the delay time caused by latency
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public long getWaitingTime() {
        return System.currentTimeMillis() - enqueueTime - (executionTime + delayTime); // Calculate waiting time
    }

    public int getClientZone() {
        return clientZone;
    }

    public FutureTask<?> getFutureTask() {
        return futureTask; // Add getter for FutureTask
    }
}
