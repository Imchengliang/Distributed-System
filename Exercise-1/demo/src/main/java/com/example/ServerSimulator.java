package com.example;

public class ServerSimulator {
    private static LoadBalancer loadBalancer;

    public static void main(String[] args) {
        // Number of servers (or zones) to create
        int numServers = 5;
        int loadBalancerPort = 1096;
        boolean enableCache = false; // Default: no cache

        // Parse command-line arguments to check for cache flag
        for (String arg : args) {
            if (arg.equals("--enable-cache")) {
                enableCache = true; // Enable cache if the flag is passed
                break;
            }
        }


        try{
        for (int i = 1; i <= numServers; i++) {
            final int serverZone = i;  // serverZone

            // Create and start a server in a new thread
            boolean finalEnableCache = enableCache;
            new Thread(() -> {
                try {
                    // Each server runs the logic in ServerThread class
                    //might need servername and port
                    String serverName = "server_" + serverZone;
                    //can also autoassign port, using 0 as port
                    int port = 1099+ serverZone; // assign port + i

                    String logFilePath = "queue_log_" + serverZone + ".txt";

                    Server.startServer(serverName, port, serverZone, finalEnableCache, logFilePath); // Start server logic

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
            // Wait for a moment to ensure servers are ready
            System.out.println("waiting a second, maybe more");
            Thread.sleep(2000);  // Adjust as necessary for your system
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
