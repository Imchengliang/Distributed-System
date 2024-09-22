package com.example;

public class ServerSimulator {
    private static LoadBalancer loadBalancer;

    public static void main(String[] args) {
        // Number of servers (or zones) to create
        int numServers = 5;
        int loadBalancerPort = 1096;

        try{
        for (int i = 1; i <= numServers; i++) {
            final int serverZone = i;  // serverZone

            // Create and start a server in a new thread
            new Thread(() -> {
                try {
                    // Each server runs the logic in ServerThread class
                    //might need servername and port
                    String serverName = "server_" + serverZone;
                    //can also autoassign port, using 0 as port
                    int port = 1099+ serverZone; // assign port + i

                    Server.startServer(serverName, port, serverZone); // Start server logic

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
            // Wait for a moment to ensure servers are ready
            System.out.println("waiting a second, maybe more");
            Thread.sleep(5000);  // Adjust as necessary for your system
            System.out.println("starting loadbalancer");
            // Start the load balancer
            if (loadBalancer == null) {
                loadBalancer = new LoadBalancer(numServers, loadBalancerPort);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
