package com.example.rmi;

public class ClientSimulator {
    public static void main(String[] args) {
        // Number of clients to create
        int numClients = 5;

        for (int i = 1; i <= numClients; i++) {
            final int clientZone = i;

            // Create and start a client in a new thread
            new Thread(() -> {
                try {
                    // Each client runs the DistributedClient logic
                    DistributedClient client = new DistributedClient();
                    client.runClient(clientZone); // Use a method in DistributedClient for execution
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
