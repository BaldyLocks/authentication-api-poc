package com.assessment.authentication.api.presentation;

import com.assessment.authentication.api.presentation.dto.AccountRegistrationRequest;
import com.assessment.authentication.api.service.AccountRegistrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * New API account registration controller.
 */
@Api(value = "New account registration API")
@RestController
public class AccountRegistrationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRegistrationController.class);


    private final AccountRegistrationService accountRegistrationService;

    public AccountRegistrationController(
        AccountRegistrationService accountRegistrationService) {
        this.accountRegistrationService = accountRegistrationService;
    }

    /**
     * Controller method for registering new API account.
     *
     * @param accountRegistrationRequest
     * @return HTTP code 200 if successful
     */
    @ApiOperation("New account registration")
    @ApiResponses(value = {
            @ApiResponse(code= 200, message = "New account successfully registered")
    })
    @PostMapping(value = "/register-account", consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registerUserAccount(
        @ApiParam(value = "New account registration request", required = true)
        @Valid @RequestBody AccountRegistrationRequest accountRegistrationRequest) {

        LOGGER.info("New account registration requested for banking account number:{} with username:{}",
            accountRegistrationRequest.getAccountNumber(),
            accountRegistrationRequest.getUsername());

        accountRegistrationService.registerNewAccount(accountRegistrationRequest);

        LOGGER.info("New account registered for banking account number:{} with username:{}",
            accountRegistrationRequest.getAccountNumber(),
            accountRegistrationRequest.getUsername());

        return ResponseEntity.ok().build();
    }
}
