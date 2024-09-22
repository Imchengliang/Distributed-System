package com.example;

import java.io.File;
import java.util.Scanner;

public class RunDistributedClients {
    DistributedClients client;
    String filename = "src/main/resources/dataset/input.txt";
    Scanner scanner = null;

    public void sendQuery() {
        while (scanner.hasNextLine()) {
            String query = scanner.nextLine();
            String[] zone = query.split(":");
            int clientZone = Integer.parseInt(zone[1]);
            client.sendQueryToServer(query, clientZone);
        }
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
