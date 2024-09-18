package com.example;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends CityService{
    public Server () {
    }

    public static void main(String[] args) {
        try {
            CityService obj = new CityService();
            CityInterface stub = (CityInterface) UnicastRemoteObject.exportObject(obj,0);
            Registry registry = LocateRegistry.getRegistry();

            registry.bind("CityInterface", stub);
            System.err.println("Server ready");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
