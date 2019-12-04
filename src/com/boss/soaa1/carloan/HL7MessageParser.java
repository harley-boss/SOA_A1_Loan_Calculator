/**
 * File:        HL7MessageParser.java
 * Project:     SOA_A1
 * Date:        December 4th 2019
 * Programmer:  Harley Boss
 * Description: This class parses/inspects all incoming HL7 messages
 */


package com.boss.soaa1.carloan;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class HL7MessageParser {


    /**
     * Method: isValid
     * @param message string to inspect
     * @return true if message doesn't contain NOT-OK or a -4
     * Description: returns whether or not string is valid
     */
    public static boolean isValid(String message) {
        if (message != null) {
            if (message.contains("NOT-OK") && !message.contains("-4")) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }


    /**
     * Method: readLine
     * @param reader
     * @return formatted string without '\r'
     */
    public static String readLine(BufferedReader reader) {
        String line = "";
        try {
            int c = 0;
            while(c != '\u001c') {
                c = reader.read();
                if((char) c == '\r')
                    continue;
                else if((char) c == '\n')
                    continue;
                line += (char) c;
            }
        } catch (IOException ex) {
            Logger.error(ex.getMessage());
        }
        return line;
    }


    /**
     * Method: parseTeamId
     * @param message string to parse
     * @return teamID if available
     * Description: parses out a team id from a HL7 message
     */
    public static String parseTeamId(String message) {
        String teamId = null;
        if (message != null) {
            if (message.contains("OK")) {
                String[] data = message.split("\\|");
                if (data.length >= 4) {
                    teamId = data[2];
                }
            }
        }
        return teamId;
    }


    /**
     * Method: parseMessage
     * @param message the entire message string
     * @return a hashmap of extracted values
     * Description: Parses out all the relevant data from an incoming service request
     */
    public static HashMap<String, String> parseMessage(String message) {
        HashMap<String, String> values = new HashMap<>();
        if (message == null) {
            values = null;
        } else {
            String[] data = message.split("\\|");
            if (!isValidData(data)) {
                return null;
            }
            int counter = 0;
            while (counter < data.length) {
                switch (counter) {
                    case 2:  // Team name
                        values.put("teamName", data[counter]);
                        break;
                    case 3:
                        values.put("teamId", data[counter]);
                        break;
                    case 6:  // Method name
                        values.put("methodName", data[counter]);
                        break;
                    case 8:  // Security level
                        values.put("securityLevel", data[counter]);
                        break;
                    case 11: // Argument 1
                        break;
                    case 13: // Variable name
                        values.put("arg1Name", data[counter]);
                        break;
                    case 14: // Data type
                        values.put("arg1DataType", data[counter]);
                        break;
                    case 16: // Variable value
                        values.put("arg1Value", data[counter]);
                        break;
                    case 17: // Argument 2
                        break;
                    case 19: // Variable name
                        values.put("arg2Name", data[counter]);
                        break;
                    case 20: // Variable data type
                        values.put("arg2DataType", data[counter]);
                        break;
                    case 22: // Variable value
                        values.put("arg2Value", data[counter]);
                        break;
                }
                counter++;
            }
        }
        return values;
    }


    /**
     * Method: isValidData
     * @param data string[] of data
     * @return true if the data is of a valid length
     */
    private static boolean isValidData(String[] data) {
        // Data needs to have a length of 22
        // Need to ensure parameters are valid doubles
        // Have a team name
        return data.length == 24;
    }
}
