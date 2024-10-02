package com.example;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RunDistributedClients {
    DistributedClients client;
    String filename = "dataset/exercise_1_input.txt";
    Scanner scanner = null;
    private boolean toggle = true;
    //private ExecutorService executor;

    public void sendQuery() {
        ExecutorService executor = Executors.newFixedThreadPool(80);
        while (scanner.hasNextLine()) {
            String query = scanner.nextLine();
            String[] zone = query.split(":");
            int clientZone = Integer.parseInt(zone[1]);
            //client.sendQueryToServer(query, clientZone);
            executor.submit(() -> {
                client.sendQueryToServer(query, clientZone);
            });

            // trigger delay
            //int delay = toggle ? 50 : 20;
            int delay = 10;
            try {
                System.out.println("Client sleeps " + delay);
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                System.out.println("Sleep interrupted: " + e.getMessage());
                Thread.currentThread().interrupt(); // Restore the interrupted status
            }
            toggle = !toggle;
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {  // Wait for all tasks to finish or timeout
                executor.shutdownNow();  // Force shutdown if tasks did not finish within the timeout
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        client.writeResultToTxt(client.getResults()); 
    }
    
    public void runClient(int startPort) {
        System.out.println("Starting client");

        // Create client object
        client = new DistributedClients(0, startPort+7);

        // Create scanner object
        try {
            scanner = new Scanner(new File(filename));
        } catch (Exception e) {
            System.out.println("Error on input");
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        int startPort = 3197;

        RunDistributedClients client = new RunDistributedClients();
        client.runClient(startPort);
        client.sendQuery();
    }
}
