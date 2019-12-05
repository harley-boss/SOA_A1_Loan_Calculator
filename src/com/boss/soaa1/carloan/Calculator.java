/**
 * File:        Calculator.java
 * Project:     SOA_A1
 * Date:        December 4th 2019
 * Programmer:  Harley Boss
 * Description: This class handles the logic of determining monthly payments based on
 * purchase amount and interest rate
 */



package com.boss.soaa1.carloan;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Calculator {
    private static final int TERM_DURATION_36 = 36;
    private static final int TERM_DURATION_48 = 48;
    private static final int TERM_DURATION_60 = 60;

    public static HashMap<String, String> calculateMonthlyPayments(String purchaseAmount, String interestRate) {
        HashMap<String, String> payments = new HashMap<>();
        try {
            double fAmount = Float.parseFloat(purchaseAmount);
            double fInterest = Float.parseFloat(interestRate);

            if (fAmount <= 0 || fInterest <= 0) {
                Logger.error("Invalid purchase amount or interest");
                return null;
            }

            double intRate = fInterest / 1200;
            double thirtySix = intRate / ((Math.pow((1 + intRate), TERM_DURATION_36)) - 1);
            thirtySix = intRate + thirtySix * fAmount;
            double fortyEight = intRate / ((Math.pow((1 + intRate), TERM_DURATION_48)) - 1);
            fortyEight = intRate + fortyEight * fAmount;
            double sixty = intRate / ((Math.pow((1 + intRate), TERM_DURATION_60)) - 1);
            sixty = intRate + sixty * fAmount;
            DecimalFormat df = new DecimalFormat("#.00");
            payments.put("TERM_36", df.format(thirtySix));
            payments.put("TERM_48", df.format(fortyEight));
            payments.put("TERM_60", df.format(sixty));
        } catch (Exception ex) {
            Logger.error(ex.getMessage());
            payments = null;
        }
        return payments;
    }
}
