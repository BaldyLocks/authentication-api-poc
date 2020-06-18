package com.assessment.authentication.api.exception;

public class BankingAccountNotFoundException extends RuntimeException {

    public BankingAccountNotFoundException() {
        super("Banking account number can not be verified");
    }
}
