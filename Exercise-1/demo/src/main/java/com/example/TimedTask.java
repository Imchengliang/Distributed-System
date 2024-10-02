package com.example;

import java.util.concurrent.FutureTask;

public class TimedTask implements Runnable {
    private final Runnable task; // Use FutureTask directly
    private long executionTime;
    private final long enqueueTime; // Time when the task was added to the queue
    private final int clientZone;
    private long delayTime; // To store the delay caused by latency

    public TimedTask(Runnable task, int clientZone) {
        this.task = task;
        this.clientZone = clientZone;
        this.enqueueTime = System.currentTimeMillis(); // Capture enqueue time
    }

    public Runnable getTask() {
        return task;  // Expose the underlying task (could be FutureTask)
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            task.run(); // Execute the future task
        } finally {
            //executionTime = System.currentTimeMillis() - startTime - delayTime; // Adjust execution time for delay
            executionTime = System.currentTimeMillis() - startTime;
        }
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime; // Set the delay time caused by latency
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public long getWaitingTime(long delayTime) {
        return System.currentTimeMillis() - enqueueTime - (executionTime + delayTime); // Calculate waiting time
    }

    public int getClientZone() {
        return clientZone;
    }

}
