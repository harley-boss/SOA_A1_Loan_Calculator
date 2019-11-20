package com.boss.soaa1.carloan;

public class CarLoanCalculator {

    public static void main(String[] args) {
        Calculator calculator = new Calculator("30000", "10");
        calculator.calculateMonthlyPayments();
    }
}
