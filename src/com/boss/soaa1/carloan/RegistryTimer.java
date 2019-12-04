/**
 * File:        RegistryTimer.java
 * Project:     SOA_A1
 * Date:        December 4th 2019
 * Programmer:  Harley Boss
 * Description: This class acts as a periodic timer that sends an alert every 30 seconds
 * to whoever implemented it
 */


package com.boss.soaa1.carloan;

import java.util.Timer;
import java.util.TimerTask;

public class RegistryTimer {
    private Timer timer;
    private OnReminder reminder;


    /**
     * Method: RegistryTimer
     * Description: Constructor
     */
    public RegistryTimer() {
        timer = new Timer();
    }


    /**
     * Method: startTimer
     * @param reminder Class implementing callback
     * Description: sends a reminder every 30 seconds
     */
    public void startTimer(OnReminder reminder) {
        this.reminder = reminder;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                reminder.timesUp();
            }
        }, 30*1000, 30*1000);
    }



    /**
     * Method: stopTimer
     * Description: stops the timer and discards resources
     */
    public void stopTimer() {
        timer.cancel();
        timer.purge();
    }



    /**
     * Interface: OnReminder
     * Description: Interface for handling timer callbacks
     */
    public interface OnReminder {
        void timesUp();
    }
}
