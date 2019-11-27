package com.boss.soaa1.carloan;

import java.sql.Timestamp;

public class Logger {
    private static boolean logStarted = false;

    public static void log(String message) {
        if (!logStarted) {
            startLog();
        }
        System.out.println(formatMessage(message));
    }

    private static void startLog() {
        logStarted = true;
        System.out.println(formatMessage("===================================================================="));
        System.out.println(formatMessage("Team:      BOSS (Harley B., Spencer B., Justin S., Nathan D."));
        System.out.println(formatMessage("Tag-name:  CAR-LOAN"));
        System.out.println(formatMessage("Service:   CarLoanCalculator"));
        System.out.println(formatMessage("===================================================================="));
    }

    private static String formatMessage(String message) {
        return new Timestamp(System.currentTimeMillis()).toString() + "\t" + message;
    }
}
