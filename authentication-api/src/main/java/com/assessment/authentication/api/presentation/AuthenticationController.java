package com.assessment.authentication.api.presentation;

import com.assessment.authentication.api.presentation.dto.AuthenticationRequest;
import com.assessment.authentication.api.service.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller.
 */
@Api(value="Authentication API")
@RestController
public class AuthenticationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);


    private final AuthenticationService authenticationService;

    public AuthenticationController(
        AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Controller method for user authentication.
     *
     * @param authenticationRequest
     * @return JWT if successful
     */
    @ApiOperation("User authentication")
    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_JSON_VALUE , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> authenticate(
        @ApiParam(value = "User credentials", required = true)
        @Valid @RequestBody AuthenticationRequest authenticationRequest){

        LOGGER.info("Authentication requested for username:{}", authenticationRequest.getUsername());

        String token = authenticationService.authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        LOGGER.info("Authentication successful for username:{}", authenticationRequest.getUsername());

        return ResponseEntity.ok(token);
    }

}
