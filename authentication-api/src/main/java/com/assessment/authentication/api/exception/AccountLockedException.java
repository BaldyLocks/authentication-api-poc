package com.assessment.authentication.api.exception;

public class AccountLockedException extends RuntimeException {

    public AccountLockedException() {
        super("Account is locked, wait for 24 hours after 3 failed attempts");
    }
}
