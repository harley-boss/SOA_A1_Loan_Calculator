package com.boss.soaa1.carloan;

import java.io.*;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    private String ip;
    private int registryPort;
    private int clientPort;
    private ServerSocket serverSocket;
    private ServerSocket registrySocket;
    private Socket socket;
    private String teamName;
    private String teamId;

    public Server(String ip, int registryPort, int clientPort) {
        this.ip = ip;
        this.registryPort = registryPort;
        this.clientPort = clientPort;
    }

    // Listens for incoming connections from clients
    public void startServer() {
        try {
            serverSocket = new ServerSocket(clientPort);
            registrySocket = new ServerSocket(registryPort);
            while (true) {
                socket = serverSocket.accept();
                handleClient(socket);
            }
        } catch (IOException ex) {
            // TODO: logging
        }
    }

    public void stopServer() {
        try {
            serverSocket.close();
        } catch (IOException ex) {
            // TODO logging
        }
    }

    public boolean registerTeamAndService(String teamName) {
        this.teamName = teamName;
        String regTeam = HL7MessageFormatter.buildRegisterTeamMessage(teamName);
        if (sendClientMessage(regTeam, Query.REGISTER_TEAM)) {
            String regService = HL7MessageFormatter.buildRegisterServiceMessage(teamName, teamId, ip, String.valueOf(registryPort));
            if (sendClientMessage(regService, Query.REGISTER_SERVICE)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // Send message as a client (i.e. register our team & service)
    private boolean sendClientMessage(String message, Query query) {
        Socket client;
        ObjectOutputStream oos;
        ObjectInputStream ois;
        try {
            client = new Socket(ip, registryPort);
            oos = new ObjectOutputStream(client.getOutputStream());
            ois = new ObjectInputStream(client.getInputStream());
            oos.writeUTF(message);
            oos.flush();
            BufferedReader reader = new BufferedReader(new InputStreamReader(ois));
            String response = reader.readLine();
            if (!HL7MessageParser.isValid(response)) {
                // TODO: log here
                return false;
            }
            switch (query) {
                case REGISTER_TEAM:
                    teamId = HL7MessageParser.parseTeamId(response);
                    // TODO:
                    if (teamId == null) {
                        // Failed to register teamId -- manually restart
                    }
                    break;
                case REGISTER_SERVICE:

                    // TODO: check for SOA_OK
                    // TODO: log here
                    break;
                case CHECK_FOR_TEAM_EXISTENCE:
                    // TODO: check for SOA_OK
                    // TODO: log here
                    break;
            }
            return true;
        } catch (IOException e) {
            // TODO: logging
            return false;
        }
    }

    private void sendMessage(String message, MessageSent callback) {
        DataOutputStream dOut;
        try {
            dOut = new DataOutputStream(socket.getOutputStream());
            dOut.writeByte(1);
            dOut.writeUTF(message);
            dOut.flush();
            callback.onMessageSent(true);
        } catch (IOException ex) {
            // TODO: logging
            callback.onMessageSent(false);
        }
    }

    // TODO: fluff this up
    private boolean teamExists(String teamId) {
        return true;
    }

    // Handle each client connection in a separate thread
    private void handleClient(Socket socket) {
        Runnable runnable = () -> {
            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    // TODO: parse message and generate response into a string
                    HashMap<String, String> values = HL7MessageParser.parseMessage(line);

                    // TODO: parse values for team id and ensure team exists in registry
                    String response;
                    if (values == null) {
                        // TODO- START TEST BLOCK: test method - move to if team exists
/*                        HashMap<String, String> test = new HashMap<>();
                        test.put("Key", line);
                        response = HL7MessageFormatter.buildLoanResponse(test);
                        response = HL7MessageFormatter.buildRegisterServiceMessage("BOSS", "6969", ip, String.valueOf(port));*/
                        // TODO- END TEST BLOCK






                        response = HL7MessageFormatter.buildErrorMessage(ErrorCodes.INVALID_MESSAGE);
                    } else if (!teamExists(values.get(0))) {
                        // TODO: logging
                        response = HL7MessageFormatter.buildErrorMessage(ErrorCodes.TEAM_DOES_NOT_EXIST);
                    } else {
                        response = HL7MessageFormatter.buildLoanResponse(values);
                    }

                    // TODO: send message and close socket
                    sendMessage(response, new MessageSent() {
                        @Override
                        public void onMessageSent(boolean sent) {
                            try {
                                socket.close();
                            } catch (IOException ex) {
                                // TODO: logging
                            }
                        }
                    });

                }
            } catch (IOException ex) {
                // TODO: logging
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private interface MessageSent {
        void onMessageSent(boolean sent);
    }

    private interface MessageReceived {
        void onMessageReceived(boolean received);
    }
}
