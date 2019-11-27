package com.boss.soaa1.carloan;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class HL7MessageParser {

    public static boolean isValid(String message) {
        if (message != null) {
            if (message.contains("NOT-OK")) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static String parseTeamId(String message) {
        String teamId = null;
        if (message != null) {
            if (message.contains("OK")) {
                String[] data = message.split("\\|");
                if (data.length >= 4) {
                    teamId = data[2];

                    // TODO logging expiration @data[3]
                }
            }
        }
        return teamId;
    }

    // Should only be messages requesting the service calculation
    public static HashMap<String, String> parseMessage(String message) {
        HashMap<String, String> values = new HashMap<>();
        if (message == null) {
            values = null;
        } else {
            String[] data = message.split("\\|");
            if (!isValidData(data)) {
                // TODO: log here and return an error message?
                return null;
            }
            int counter = 0;
            while (counter < data.length) {
                switch (counter) {
                    case 2:  // Team name
                        values.put("teamName", data[counter]);
                        break;
                    case 5:  // Method name
                        values.put("methodName", data[counter]);
                        break;
                    case 7:  // Security level
                        values.put("securityLevel", data[counter]);
                        break;
                    case 11: // Argument 1
                        break;
                    case 12: // Variable name
                        values.put("arg1Name", data[counter]);
                        break;
                    case 13: // Data type
                        values.put("arg1DataType", data[counter]);
                        break;
                    case 15: // Variable value
                        values.put("arg1Value", data[counter]);
                        break;
                    case 17: // Argument 2
                        break;
                    case 18: // Variable name
                        values.put("arg2Name", data[counter]);
                        break;
                    case 19: // Variable data type
                        values.put("arg2DataType", data[counter]);
                        break;
                    case 21: // Variable value
                        values.put("arg2Value", data[counter]);
                        break;
                }
                counter++;
            }
        }
        return values;
    }

    private static boolean isValidData(String[] data) {
        // Data needs to have a length of 22
        // Need to ensure parameters are valid doubles
        // Have a team name
        return data.length == 22;
    }
}
