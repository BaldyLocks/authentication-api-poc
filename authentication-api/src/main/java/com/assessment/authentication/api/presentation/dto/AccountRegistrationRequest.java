package com.assessment.authentication.api.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@ApiModel(description = "New account registration request")
public class AccountRegistrationRequest{

    @NotEmpty
    private String username;
    @NotEmpty
    @Size(min = 6, message = "Minimal password length is 6 characters")
    private String password;
    @NotNull
    private Integer accountNumber;

    public AccountRegistrationRequest(
        @JsonProperty(value = "username", required = true) String username,
        @JsonProperty(value = "password", required = true) String password,
        @JsonProperty(value = "accountNumber", required = true) Integer accountNumber) {

        this.username = username;
        this.password = password;
        this.accountNumber = accountNumber;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }
}
