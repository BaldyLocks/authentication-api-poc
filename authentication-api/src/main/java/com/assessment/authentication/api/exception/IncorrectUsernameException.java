package com.assessment.authentication.api.exception;

public class IncorrectUsernameException extends RuntimeException {

    public IncorrectUsernameException() {
        super("Entered username not recognized");
    }
}
