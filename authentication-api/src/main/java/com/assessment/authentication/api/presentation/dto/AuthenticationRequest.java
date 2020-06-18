package com.assessment.authentication.api.presentation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import java.io.Serializable;

@ApiModel(description = "User credentials")
public class AuthenticationRequest implements Serializable {
    private String username;
    private String password;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public AuthenticationRequest(
        @JsonProperty(value = "username", required = true) String username,
        @JsonProperty(value = "password", required = true) String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
