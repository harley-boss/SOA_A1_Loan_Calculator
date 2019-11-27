package com.boss.soaa1.carloan;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static String ipAddress;
    public static String teamName;
    public static int registryPort;
    public static int clientPort;

    public static void main(String[] args) {
        Logger.init();
        Logger.debug("Starting main activity");
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

        // TODO:
        // 1. Start up the server
        // 2. Register team and get team id
        // 3. Register service
        // 4. Start listening for clients


        Server server = new Server(ipAddress, registryPort, clientPort);
        if (server.registerTeamAndService(teamName)) {  // TODO: default to true for testing
            Logger.debug("Server registered team, starting listening for client connections");
            server.startServer();
        } else {
            // TODO: logging
            return;
        }
        // TODO: stop server?
    }
}
