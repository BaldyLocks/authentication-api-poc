package com.assessment.authentication.api.presentation;

import com.assessment.authentication.api.exception.AccountLockedException;
import com.assessment.authentication.api.exception.BankingAccountNotFoundException;
import com.assessment.authentication.api.exception.FailedToGenerateJWTException;
import com.assessment.authentication.api.exception.IncorrectPasswordException;
import com.assessment.authentication.api.exception.IncorrectUsernameException;
import com.assessment.authentication.api.exception.UsernameAlreadyInUseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(UsernameAlreadyInUseException.class)
    protected ResponseEntity<Object> handleUsernameAlreadyInUseException(
        UsernameAlreadyInUseException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(BankingAccountNotFoundException.class)
    protected ResponseEntity<Object> handleBankingAccountNotFoundException(
        BankingAccountNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(AccountLockedException.class)
    protected ResponseEntity<Object> handleAccountLockedException(
        AccountLockedException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler({IncorrectPasswordException.class, IncorrectUsernameException.class})
    protected ResponseEntity<Object> handleBadCredentialsExceptions(
        RuntimeException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user credentials provided");
    }

    @ExceptionHandler(FailedToGenerateJWTException.class)
    protected ResponseEntity<Object> handleFailedToGenerateJWTException(
        FailedToGenerateJWTException ex) {

        LOGGER.error("Access token generation failed for a valid login request, please analyse the stacktrace.", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Access token generation failed. Please contact support.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex) {
        LOGGER.error("Unhandled exception was thrown, please analyse the stacktrace", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Unknown server error");
    }
}
