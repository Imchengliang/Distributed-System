package com.example;

public class ServerSimulator {
    private static LoadBalancer loadBalancer;

    public static void main(String[] args) {
        // Number of servers (or zones) to create
        int numServers = 5;
        int loadBalancerPort = 1096;
        boolean enableCache = false; // Default: no cache
        //Thread[] serverThreads = new Thread[numServers];

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
                boolean finalEnableCache = enableCache;

                // Create and start a server in a new thread
                /* 
                serverThreads[i - 1] = new Thread(() -> {
                    try {
                        String serverName = "server_" + serverZone;
                        int port = 1099 + serverZone; // assign port + i
                        String logFilePath = "dataset/queue_log_" + serverZone + ".txt";
                        System.out.println("Port: " + port);
                        Server.startServer(serverName, port, serverZone, finalEnableCache, logFilePath);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                serverThreads[i - 1].start();
            }
            for (Thread serverThread : serverThreads) {
                serverThread.join(); // Ensure each server thread completes
            }*/


                new Thread(() -> {
                    try {
                        // Each server runs the logic in ServerThread class
                        //might need servername and port
                        String serverName = "server_" + serverZone;
                        //can also autoassign port, using 0 as port
                        int port = 1099+ serverZone; // assign port + i

                        String logFilePath = "queue_log_" + serverZone + ".txt";

                        Server.startServer(serverName, port, serverZone, finalEnableCache, logFilePath); // Start server logic
                        System.out.println("Port: " + port);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }

            // Wait for a moment to ensure servers are ready
            System.out.println("waiting a second, maybe more");
            Thread.sleep(800);  // Adjust as necessary for your system
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
