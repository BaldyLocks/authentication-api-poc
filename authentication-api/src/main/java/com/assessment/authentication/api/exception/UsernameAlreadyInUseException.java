package com.assessment.authentication.api.exception;

public class UsernameAlreadyInUseException extends RuntimeException {

    public UsernameAlreadyInUseException() {
        super("Requested username already in use");
    }
}
