package com.boss.soaa1.carloan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Logger {
    private static boolean logStarted = false;
    private static boolean filesSetup = false;
    private static File logsDirectory;
    private static File logsFile;
    private static String fileName;

    public static void init() {
        if (!filesSetup) {
            setupDirectory();
        }
        if (!logStarted) {
            startLog();
        }
    }
    public static void logSoa(String message) {
        System.out.println(formatMessage(message));
    }

    public static void debug(String message) {
        String logMessage = "[DEBUG]\t" + formatMessage(message);
        if (filesSetup) {
            writeToFile(logMessage);
        }
        System.out.println(logMessage);
    }

    public static void error(String message) {
        String logMessage = "[ERROR]\t" + formatMessage(message);
        if (filesSetup) {
            writeToFile(logMessage);
        }
        System.out.println(logMessage);
    }

    private static void writeToFile(String message) {
        try {
            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(logsFile, true)  //Set true for append mode
            );
            writer.write(message);
            writer.newLine();   //Add new line
            writer.close();
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

    private static void setupDirectory() {
        logsDirectory = new File("/Logs");
        if (!logsDirectory.exists()) {
            if (!logsDirectory.mkdir()) {
                error("Failed to create directory for logs");
                return;
            } else {
                debug("Successfully created logging directory");
            }
        } else {
            debug("Logs directory already created");
        }
        filesSetup = true;
        setupLogsFile();
    }

    // Logs files are arranged by date. Only create a new file if the date doesn't match
    private static void setupLogsFile() {
        LocalDateTime now = LocalDateTime.now();
        fileName = now.toLocalDate().toString();
        logsFile = new File(logsDirectory, fileName + ".log");
        if (!logsFile.exists()) {
            try {
                if (logsFile.createNewFile()) {
                    System.out.println("Created a new logs file for today");
                } else {
                    System.out.println("Failed to create a new logs file");
                }
            } catch (IOException ex) {
                System.out.println("Failed to create a new logs file");
            }
        } else {
            System.out.println("Log file for today already exists");
        }
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
