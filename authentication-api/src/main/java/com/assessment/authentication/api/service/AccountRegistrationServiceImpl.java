package com.assessment.authentication.api.service;

import com.assessment.authentication.api.connector.AccountsApiConnector;
import com.assessment.authentication.api.connector.dto.BankingAccountDto;
import com.assessment.authentication.api.exception.BankingAccountNotFoundException;
import com.assessment.authentication.api.exception.UsernameAlreadyInUseException;
import com.assessment.authentication.api.presentation.dto.AccountRegistrationRequest;
import com.assessment.authentication.api.repository.AccountStorage;
import com.assessment.authentication.api.repository.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountRegistrationServiceImpl implements AccountRegistrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRegistrationServiceImpl.class);


    private final AccountStorage accountStorage;
    private final AccountsApiConnector accountsApiConnector;
    private final PasswordEncoder passwordEncoder;

    public AccountRegistrationServiceImpl(
        AccountStorage accountStorage,
        AccountsApiConnector accountsApiConnector,
        PasswordEncoder passwordEncoder) {

        this.accountStorage = accountStorage;
        this.accountsApiConnector = accountsApiConnector;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void registerNewAccount(AccountRegistrationRequest accountRegistrationRequest) {
        BankingAccountDto bankingAccountDto = accountsApiConnector
            .retrieveBankingAccount(accountRegistrationRequest.getAccountNumber());

        //TODO: check how AccountsAPI responds when account not found
        if (bankingAccountDto == null) {
            LOGGER.info("Banking account not found for account number:{}", accountRegistrationRequest.getAccountNumber());
            throw new BankingAccountNotFoundException();
        }

        if (accountStorage.findByUsername(accountRegistrationRequest.getUsername()).isPresent()) {
            LOGGER.info("Username: {} requested for account number:{} is already in use by another user",
                accountRegistrationRequest.getUsername(),
                accountRegistrationRequest.getAccountNumber());
            throw new UsernameAlreadyInUseException();
        }

        String passwordHash = passwordEncoder.encode(accountRegistrationRequest.getPassword());
        Account newAccount = new Account(accountRegistrationRequest.getUsername(), passwordHash);
        accountStorage.createAccount(newAccount);

        LOGGER.debug("Account with username:{} registered for account number: {}",
            accountRegistrationRequest.getUsername(),
            accountRegistrationRequest.getAccountNumber());
    }
}
