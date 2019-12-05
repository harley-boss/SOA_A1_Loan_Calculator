/**
 * File:        Server.java
 * Project:     SOA_A1
 * Date:        December 4th 2019
 * Programmer:  Harley Boss
 * Description: This file handles all the network requests. It acts a a server most of the time while occasionally
 *              issuing requests to the registry as a client
 */


package com.boss.soaa1.carloan;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {

    private String ip;
    private int registryPort;
    private int clientPort;
    private ServerSocket serverSocket;
    private String teamName;
    private String teamId;


    /**
     * Method: Constructor
     * @param ip of the registry machine
     * @param registryPort part the registry is operating on
     * @param clientPort port which accepts client connections
     */
    public Server(String ip, int registryPort, int clientPort) {
        this.ip = ip;
        this.registryPort = registryPort;
        this.clientPort = clientPort;
    }




    /**
     * Method: startServer
     * Description: creates a server socket and starts listening for incoming connections on the client
     * port
     */
    public void startServer() {
        try {
            serverSocket = new ServerSocket(clientPort);
            while (true) {
                Socket socket = serverSocket.accept();
                handleClient(socket);
            }
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
        }
    }




    /**
     * Method: stopServer
     * Description: closes the server socket
     */
    public void stopServer() {
        try {
            String message = HL7MessageFormatter.buildUnregisterTeamMessage(teamName, teamId);
            Socket registrySocket = new Socket(ip, registryPort);
            sendMessage(registrySocket, message, new MessageSent() {
                @Override
                public void onMessageSent(boolean sent) {
                    Logger.debug("Disconnected from server");
                }
            });
            serverSocket.close();
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
        }
    }




    /**
     * Method: registerTeamAndService
     * @param teamName team name with which to register the service ass
     * @return boolean if method was successful
     * Description: Attempts to register the team name with the registry and if that is successful (or team already
     * exists) will attempt to register the service
     */
    public boolean registerTeamAndService(String teamName) {
        this.teamName = teamName;
        boolean success = false;
        try {
            String regTeam = HL7MessageFormatter.buildRegisterTeamMessage(teamName);
            Logger.debug("Opening a socket for registry request");
            Socket registrySocket = new Socket(ip, registryPort);
            if (sendRegistryMessage(registrySocket, regTeam, Query.REGISTER_TEAM)) {
                registrySocket.close();
                registrySocket = new Socket(ip, registryPort);
                String myIp = InetAddress.getLocalHost().getHostAddress();
                String regService = HL7MessageFormatter.buildRegisterServiceMessage(teamName, teamId, myIp, String.valueOf(clientPort));
                if (sendRegistryMessage(registrySocket, regService, Query.REGISTER_SERVICE)) {
                    success = true;
                }
                Logger.debug("Closing socket for registry request");
                registrySocket.close();
            }
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
        }
        return success;
    }




    /**
     * Method: testRegistryConnection
     * @return true if connected
     * Description: Calls the registry every 30 seconds to ensure there is a connection
     */
    public boolean testRegistryConnection() {
        return registerTeamAndService(teamName);
    }




    /**
     * Method: sendRegistryMessage
     * @param socket registry socket
     * @param message message to send
     * @param query enum to use when parsing response
     * @return true if successful
     * Description: sends a message to the registry
     */
    private boolean sendRegistryMessage(Socket socket, String message, Query query) {
        OutputStream oos;
        byte[] responseMsg = new byte[512];
        String output="";
        try {
            boolean isReachable = socket.getInetAddress().isReachable(3);
            Logger.debug("Client is reachable? " + isReachable);
            Logger.debug("Starting socket for connection to registry");
            Logger.debug("Socket created");
            oos = socket.getOutputStream();
            Logger.debug("Created an output stream to write to server");
            oos.write(message.getBytes());
            Logger.debug("Wrote message " + message);
            oos.flush();
            Logger.debug("Creating an input to read response from server");
            InputStream is = socket.getInputStream();
            while(is.read(responseMsg) != -1) {
                output = new String(responseMsg);
            };
            output = output.replaceAll("\\013", "");
            output = output.replaceAll("\\034", "");
            output = output.replaceAll("\\r", "");

            System.out.println("response: ");
            System.out.print(output);
            System.out.println("closing socket here");
            Logger.logSoa(output);

            // Not valid if the server returned SOA NOT-OK
            if (!HL7MessageParser.isValid(output)) {
                Logger.error("Invalid message from registry: " + output);
                return false;
            }
            boolean wasSuccessful = true;
            switch (query) {
                case REGISTER_SERVICE:
                    break;
                case REGISTER_TEAM:
                    wasSuccessful = true;
                    break;
                case CHECK_FOR_TEAM_EXISTENCE:
                    break;
            }
            teamId = HL7MessageParser.parseTeamId(output);
            if (teamId == null) {
                Logger.error("Failed to register team, must restart service");
                wasSuccessful = false;
            }
            return wasSuccessful;
        } catch (IOException e) {
            Logger.error(e.getMessage());
            return false;
        }
    }




    /**
     * Method: sendMessage
     * @param socket
     * @param message
     * @param callback
     * Description: writes a message to the specified socket
     */
    private void sendMessage(Socket socket, String message, MessageSent callback) {
        OutputStream dOut;
        try {
            dOut = socket.getOutputStream();
            dOut.write(message.getBytes());
            dOut.flush();
            callback.onMessageSent(true);
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
            callback.onMessageSent(false);
        }
    }




    /**
     * Method: teamExists
     * @param values
     * @return true if the team exists
     * Description: sends a message to the registery, checking to see if the team exists
     */
    private boolean doesTeamExist(HashMap<String, String> values) {
        boolean retValue = false;
        try {
            Socket registrySocket = new Socket(ip, registryPort);
            String theirId = values.get("teamId");
            String theirName = values.get("teamName");
            String ourId = teamId;
            String ourName = teamName;
            String query = HL7MessageFormatter.buildTeamExists(ourName, ourId, theirName, theirId);
            if (sendRegistryMessage(registrySocket, query, Query.CHECK_FOR_TEAM_EXISTENCE)) {
                retValue = true;
            }
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
        }
        return retValue;
    }




    /**
     * Method: handleClient
     * @param socket
     * Description: handles client connections on a separate thread
     */
    private void handleClient(Socket socket) {
        final Socket tempSocket = socket;
        Runnable runnable = () -> {
            try {
                InputStream input = tempSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                while (true) {
                    String line = HL7MessageParser.readLine(reader);
                    Logger.debug(line);
                    HashMap<String, String> values = HL7MessageParser.parseMessage(line);
                    String response;
                    if (values == null) {
                        Logger.error("Team does not exists");
                        response = HL7MessageFormatter.buildErrorMessage(ErrorCodes.INVALID_MESSAGE);
                    } else if (!doesTeamExist(values)) {
                        String team = values.get("teamName");
                        Logger.error("Team " + team + " does not exists");
                        response = HL7MessageFormatter.buildErrorMessage(ErrorCodes.TEAM_DOESNT_EXIST);
                    } else {
                        Logger.debug("Client exists, fulfilling request");
                        response = HL7MessageFormatter.buildLoanResponse(values);
                    }
                    sendMessage(tempSocket, response, sent -> {
                        try {
                            Logger.debug("Closing socket for client: " + values.get("teamName"));
                            tempSocket.close();
                        } catch (IOException ex) {
                            Logger.error(ex.getMessage());
                        }
                    });
                    break;
                }
            } catch (IOException ex) {
                Logger.error(ex.getMessage());
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }




    /**
     * Interface: MessageSent
     * Description: Callback for handling registry responses
     */
    private interface MessageSent {
        void onMessageSent(boolean sent);
    }
}
