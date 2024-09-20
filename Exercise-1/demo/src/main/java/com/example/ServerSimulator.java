package com.example;

public class ServerSimulator {

    public static void main(String[] args) {
        // Number of servers (or zones) to create
        int numServers = 5;

        try{
        LoadBalancer2 loadBalancer = new LoadBalancer2(numServers);

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

                    //register server at loadbalancer
                    loadBalancer.registerServer(serverZone,new ServerInfo("127.0.0.1",port, serverZone));

                    Server.startServer(serverName, port); // Start server logic

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
