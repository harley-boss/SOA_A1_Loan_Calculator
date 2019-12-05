/**
 * File:        Main.java
 * Project:     SOA_A1
 * Date:        December 4th 2019
 * Programmer:  Harley Boss
 * Description: This class is the launching point for the application
 */


package com.boss.soaa1.carloan;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static String ipAddress;
    public static String teamName;
    public static int registryPort;
    public static int clientPort;
    public static RegistryTimer registryTimer;
    public static Server server;


    /**
     * Method: main
     * @param args
     * Description: Launching point of the application
     */
    public static void main(String[] args) {
        Logger.init();
        Logger.debug("Starting main");
        Properties prop = new Properties();
        try {
            FileInputStream ip = new FileInputStream("./config.properties");
            prop.load(ip);
            ipAddress = prop.getProperty("registry_ip");
            registryPort = Integer.parseInt(prop.getProperty("registry_port"));
            clientPort = Integer.parseInt(prop.getProperty("client_port"));
            teamName = prop.getProperty("team_name");
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
            return;
        }
        startServer();
    }




    /**
     * Method: startServer
     * Description: Starts up the server and calls a method to register the team and service.
     */
    private static void startServer() {
        server = new Server(ipAddress, registryPort, clientPort);
        if (server.registerTeamAndService(teamName)) {
            Logger.debug("Server registered team, starting listening for client connections");
            startTimer();
            server.startServer();
        } else {
            Logger.error("Failed to register team and or service");
            return;
        }
    }




    /**
     * Method: startTimer
     * Description: Starts a timer for periodic reminders to check for a connection to the registry
     */
    private static void startTimer() {
        registryTimer = new RegistryTimer();
        registryTimer.startTimer(() -> {
            if (!server.testRegistryConnection()) {
                Logger.error("Lost connection to registry, restarting");
                stopTimer();
                server.stopServer();
                startServer();
            } else {
                Logger.debug("Still connected to registry");
            }
        });
    }




    /**
     * Method: stopTimer
     * Description: stops the timer
     */
    private static void stopTimer() {
        registryTimer.stopTimer();
    }
}
