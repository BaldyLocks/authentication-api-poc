package com.assessment.authentication.api.exception;

import com.nimbusds.jose.JOSEException;

public class FailedToGenerateJWTException extends RuntimeException {

    public FailedToGenerateJWTException(JOSEException e) {
        super(e);
    }
}
