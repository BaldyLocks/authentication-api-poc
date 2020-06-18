package com.assessment.authentication.api.repository;

import com.assessment.authentication.api.repository.entity.Account;
import java.util.Optional;

public interface AccountStorage {
    void createAccount(Account accountRegistrationRequest);
    Optional<Account> findByUsername(String username);
}
