package com.assessment.authentication.api.service;

import com.assessment.authentication.api.presentation.dto.AccountRegistrationRequest;

public interface AccountRegistrationService {
    void registerNewAccount(AccountRegistrationRequest accountRegistrationRequest);
}
