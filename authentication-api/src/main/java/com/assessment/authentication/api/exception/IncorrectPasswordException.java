package com.assessment.authentication.api.exception;

public class IncorrectPasswordException extends RuntimeException {

    public IncorrectPasswordException(String username) {
        super("Entered password for username:" + username + " is invalid");
    }
}
