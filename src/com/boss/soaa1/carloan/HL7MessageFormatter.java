/**
 * File:        HL7MessageFormatter.java
 * Project:     SOA_A1
 * Date:        December 4th 2019
 * Programmer:  Harley Boss
 * Description: This class formats all messages into an HL7 string
 */



package com.boss.soaa1.carloan;

import java.util.ArrayList;
import java.util.HashMap;

public class HL7MessageFormatter {
    private static final char START_OF_MESSAGE = '\u000b';
    private static final char END_OF_MESSAGE = '\u001c';
    private static final char END_OF_SEGMENT = '\r';


    /**
     * Method: buildRegisterTeamMessage
     * @param teamName team name to register
     * @return HL7 formatted string
     * Description: Builds the string to register a team by name
     */
    public static String buildRegisterTeamMessage(String teamName) {
        return START_OF_MESSAGE + "DRC|REG-TEAM|||" + END_OF_SEGMENT
                + "INF|" + teamName + "|||" + END_OF_SEGMENT + END_OF_MESSAGE + END_OF_SEGMENT;
    }


    /**
     * Method: buildUnregisterTeamMessage
     * @param teamName teamName to unregister
     * @param teamId teamId to unregister
     * @return HL7 formatted string
     * Description: Builds the string to unregister a team by name
     */
    public static String buildUnregisterTeamMessage(String teamName, String teamId) {
        return START_OF_MESSAGE + "DRC|UNREG-TEAM|" + teamName + "|" + teamId + "|" + END_OF_SEGMENT + END_OF_MESSAGE + END_OF_SEGMENT;
    }


    /**
     * Method: buildRegisterServiceMessage
     * @param teamName team name that's registering the service
     * @param teamId team id that's registering the service
     * @param ip ip of the machine running the service
     * @param port port of the machine running the service
     * @return HL7 formatted string
     */
    public static String buildRegisterServiceMessage(String teamName, String teamId, String ip, String port) {
        ArrayList<String> message = new ArrayList<>();
        String finalMessage = "";
        message.add(START_OF_MESSAGE + "DRC|PUB-SERVICE|" + teamName + "|" + teamId + "|" + END_OF_SEGMENT);
        message.add("SRV|CAR-LOAN|CarLoanCalculator|3|2|3|Service to calculate monthly loan payments|" + END_OF_SEGMENT);
        message.add("ARG|1|principalCarAmount|double|mandatory||" + END_OF_SEGMENT);
        message.add("ARG|2|interestRate|double|mandatory||" + END_OF_SEGMENT);
        message.add("RSP|1|payment36Month|double||" + END_OF_SEGMENT);
        message.add("RSP|2|payment48Month|double||" + END_OF_SEGMENT);
        message.add("RSP|3|payment60Month|double||" + END_OF_SEGMENT);
        message.add("MCH|" + ip + "|" + port + "|" + END_OF_SEGMENT + END_OF_MESSAGE + END_OF_SEGMENT);
        Logger.logSoa("Calling SOA-Registry with message : ");
        for (String s : message) {
            finalMessage += s;
        }
        return finalMessage;
    }


    /**
     * Method: buildLoanResponse
     * @param values calculated values
     * @return HL7 formatted string
     * Description: creates the hl7 formatted string with values for the monthly payments
     */
    public static String buildLoanResponse(HashMap<String, String> values) {
        if (values == null) {
            Logger.error("Invalid values, bad message");
            return buildErrorMessage(ErrorCodes.INVALID_MESSAGE);
        } else {
            String purchaseAmount = values.get("arg1Value");
            String interestRate = values.get("arg2Value");
            HashMap<String, String> payments = Calculator.calculateMonthlyPayments(purchaseAmount, interestRate);
            ArrayList<String> message = new ArrayList<>();
            String finalMessage = "";

            if (payments == null) {
                finalMessage = buildErrorMessage(ErrorCodes.INVALID_MESSAGE);
                return finalMessage;
            }

            String header = "PUB|OK|||3|";
            String line1 = "RSP|1|payment36Month|double|" + payments.get("TERM_36") + "|";
            String line2 = "RSP|2|payment48Month|double|" + payments.get("TERM_48") + "|";
            String line3 = "RSP|3|payment60Month|double|" + payments.get("TERM_60") + "|";

            message.add(START_OF_MESSAGE + header + END_OF_SEGMENT);
            message.add(line1 + END_OF_SEGMENT);
            message.add(line2 + END_OF_SEGMENT);
            message.add(line3 + END_OF_SEGMENT + END_OF_MESSAGE + END_OF_SEGMENT);
            for (String s : message) {
                finalMessage += s;
            }
            Logger.logSoa(header);
            Logger.logSoa("\t>>" + line1);
            Logger.logSoa("\t>>" + line2);
            Logger.logSoa("\t>>" + line3);

            return finalMessage;
        }
    }


    /**
     * Method: buildTeamExists
     * @param myTeam our team name
     * @param myId our team id
     * @param theirName their team name
     * @param theirId their team id
     * @return HL7 formatted string
     * Description: Returns an HL7 message to check and see if team exists in the registry
     */
    public static String buildTeamExists(String myTeam, String myId, String theirName, String theirId) {
        return "DRC|QUERY-TEAM|" + myTeam + "|" + myId + "|" + END_OF_SEGMENT
        + "INF|" + theirName + "|" + theirId + " |CarLoanCalculator|" + END_OF_SEGMENT + END_OF_MESSAGE + END_OF_SEGMENT;
    }


    /**
     * Method: buildErrorMessage
     * @param errorCode
     * @return HL7 formatted string
     * Description: returns an hl7 string indicating what error occurred
     */
    public static String buildErrorMessage(int errorCode) {
        String errorResponse = START_OF_MESSAGE + "SOA|NOT-OK|" + errorCode + "|";
        switch (errorCode) {
            case -1:
                errorResponse += errorResponse.concat("Team does not exist in registry");
                break;
            case -2:
                errorResponse = errorResponse.concat("Invalid parameters in request.\n\nPlease try again and thank you for your patronage :)");
                break;
        }
        errorResponse = errorResponse.concat(String.valueOf(END_OF_SEGMENT) + String.valueOf(END_OF_MESSAGE) + String.valueOf(END_OF_SEGMENT));
        Logger.logSoa(errorResponse);
        return errorResponse;
    }
}
