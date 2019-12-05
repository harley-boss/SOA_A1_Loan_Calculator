/**
 * File:        Logger.java
 * Project:     SOA_A1
 * Date:        December 4th 2019
 * Programmer:  Harley Boss
 * Description: This class handles writing messages to console and file
 */


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


    /**
     * Method: init
     * Description: initializes class variables and sets up directory
     */
    public static void init() {
        if (!filesSetup) {
            setupDirectory();
        }
        if (!logStarted) {
            startLog();
        }
    }


    /**
     * Method: logSoa
     * @param message
     * Description: Logs a formatted soa message
     */
    public static void logSoa(String message) {
        System.out.println(formatMessage(message));
        debug(message);
    }



    /**
     * Method: debug
     * @param message
     * Description: logs debug level messages
     */
    public static void debug(String message) {
        String logMessage = "[DEBUG]\t" + formatMessage(message);
        if (filesSetup) {
            writeToFile(logMessage);
        }
        System.out.println(logMessage);
    }


    /**
     * Method: error
     * @param message
     * Description: logs error level messages
     */
    public static void error(String message) {
        String logMessage = "[ERROR]\t" + formatMessage(message);
        if (filesSetup) {
            writeToFile(logMessage);
        }
        System.out.println(logMessage);
    }


    /**
     * Method: writeToFile
     * @param message
     * Description: appends the current message to the open file
     */
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


    /**
     * Method: setupDirectory
     * Description: Attempts to setup a logging folder
     */
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


    /**
     * Method: setupLogsFile
     * Description: creates a log file if it doesn't exists
     */
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


    /**
     * Method: startLog
     * Description: Formats the log file header
     */
    private static void startLog() {
        logStarted = true;
        System.out.println(formatMessage("===================================================================="));
        System.out.println(formatMessage("Team:      BOSS (Harley B., Spencer B., Justin S., Nathan D.)"));
        System.out.println(formatMessage("Tag-name:  CAR-LOAN"));
        System.out.println(formatMessage("Service:   CarLoanCalculator"));
        System.out.println(formatMessage("===================================================================="));
    }


    /**
     * Method: formatMessage
     * @param message
     * @return formatted message
     * Description: adds a times stamp to the message
     */
    private static String formatMessage(String message) {
        return new Timestamp(System.currentTimeMillis()).toString() + "\t" + message;
    }
}
