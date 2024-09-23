package com.example;

public class TimedTask implements Runnable {
    private final Runnable task;
    private long executionTime;
    private final long enqueueTime; // Time when the task was added to the queue
    private final int clientZone;
    private long delayTime; // To store the delay caused by latency

    public TimedTask(Runnable task, int clientZone) {
        this.task = task;
        this.clientZone = clientZone;
        this.enqueueTime = System.currentTimeMillis(); // Capture enqueue time
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            task.run();
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
}
