package com.example;

public class ServerSimulator {

    public static void main(String[] args) {
        // Number of servers (or zones) to create
        int numServers = 5;

        for (int i = 1; i <= numServers; i++) {
            final int serverZone = i;  // serverZone

            // Create and start a server in a new thread
            new Thread(() -> {
                try {
                    // Each server runs the logic in ServerThread class
                    //might need servername and port
                    String serverName = "CityServer" + serverZone;
                    //can also autoassign port, using 0 as port
                    int port = 1099+ serverZone; // assign port + i
                    Server.startServer(serverName, port); // Start server logic

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
