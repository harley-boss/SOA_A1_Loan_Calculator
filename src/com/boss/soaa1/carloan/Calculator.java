package com.boss.soaa1.carloan;

import java.text.DecimalFormat;
import java.util.HashMap;

public class Calculator {
    private static final int TERM_DURATION_36 = 36;
    private static final int TERM_DURATION_48 = 48;
    private static final int TERM_DURATION_60 = 60;

    public static HashMap<String, String> calculateMonthlyPayments(String purchaseAmount, String interestRate) {
        double fAmount = Float.parseFloat(purchaseAmount);
        double fInterest = Float.parseFloat(interestRate);
        double intRate = fInterest / 1200;
        double thirtySix = intRate / ((Math.pow((1 + intRate), TERM_DURATION_36)) - 1);
        thirtySix = intRate + thirtySix * fAmount;
        double fortyEight = intRate / ((Math.pow((1 + intRate), TERM_DURATION_48)) - 1);
        fortyEight = intRate + fortyEight * fAmount;
        double sixty = intRate / ((Math.pow((1 + intRate), TERM_DURATION_60)) - 1);
        sixty = intRate + sixty * fAmount;
        DecimalFormat df = new DecimalFormat("#.00");
        HashMap<String, String> payments = new HashMap<>();
        payments.put("TERM_36", df.format(thirtySix));
        payments.put("TERM_48", df.format(fortyEight));
        payments.put("TERM_60", df.format(sixty));
        return payments;
    }
}
