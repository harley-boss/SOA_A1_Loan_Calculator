package com.boss.soaa1.carloan;

import java.util.ArrayList;
import java.util.HashMap;

public class HL7MessageFormatter {
    private static final char BOM = '\013';
    private static final char EOS1 = '\r';
    private static final char EOS2 = '\n';
    private static final char EOM = '\034';

    public static String buildRegisterTeamMessage(String teamName) {
        return BOM + "DRC|REG_TEAM|||" + EOS1 + EOS2
                + "INF|" + teamName + "|||" + EOM + EOS2;
    }

    public static String buildUnregisterTeamMessage(String teamName, String teamId) {
        return BOM + "DRC|UNREG-TEAM|" + teamName + "|" + teamId + "|" + EOM + EOS2;
    }

    public static String buildRegisterServiceMessage(String teamName, String teamId, String ip, String port) {
        ArrayList<String> message = new ArrayList<>();
        String finalMessage = null;
        message.add(BOM + "DRC|PUB-SERVICE|" + teamName + "|" + teamId + EOS1 + EOS2);
        message.add("SRV|CAR-LOAN|CarLoanCalculator|3|2|3|Service to calculate monthly loan payments" + EOS1 + EOS2);
        message.add("ARG|1|principalCarAmount|double|mandatory||" + EOS1 + EOS2);
        message.add("ARG|2|interestRate|double|mandatory||" + EOS1 + EOS2);
        message.add("RSP|1|payment36Month|double||" + EOS1 + EOS2);
        message.add("RSP|2|payment48Month|double||" + EOS1 + EOS2);
        message.add("RSP|3|payment60Month|double||" + EOS1 + EOS2);
        message.add("MCH|" + ip + "|" + port + "|" + EOM + EOS2);
        Logger.logSoa("Calling SOA-Registry with message : ");
        for (String s : message) {
            Logger.logSoa("\t>> " + s);
            finalMessage += s;
        }
        return finalMessage;
    }

    public static String buildLoanResponse(HashMap<String, String> values) {
        if (values == null) {
            return buildErrorMessage(ErrorCodes.INVALID_MESSAGE);  // TODO: error condition
        } else {
            String purchaseAmount = values.get("arg1Value");
            String interestRate = values.get("arg2Value");
            HashMap<String, String> payments = Calculator.calculateMonthlyPayments(purchaseAmount, interestRate);


            // TODO: construct a message with the values


            return "";
        }
    }

    // TODO: fluffing and match error codes to document
    public static String buildErrorMessage(ErrorCodes errorCode) {
        String errorResponse = BOM + "SOA|NOT-OK|" + errorCode.ordinal() + "|";
        switch (errorCode) {
            case INVALID_MESSAGE:
                errorResponse = errorResponse.concat("Message from client was invalid");
                break;
            case TEAM_DOES_NOT_EXIST:
                errorResponse += errorResponse.concat("Team does not exist in registry");
                break;
        }
        errorResponse += EOM + EOS2;
        Logger.logSoa(errorResponse);
        return errorResponse;
    }
}
