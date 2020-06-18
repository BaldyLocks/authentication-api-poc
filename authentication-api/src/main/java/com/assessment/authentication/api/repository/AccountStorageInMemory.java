package com.assessment.authentication.api.repository;

import com.assessment.authentication.api.repository.entity.Account;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class AccountStorageInMemory implements AccountStorage {
    private static Map<String, Account> accountsByUsernameMap = new ConcurrentHashMap<>();

    @Override
    public void createAccount(Account account) {

        final Account newAccount = new Account(
            account.getUsername(),
            account.getPasswordHash()
        );

        accountsByUsernameMap.put(account.getUsername(), newAccount);
    }

    @Override
    public Optional<Account> findByUsername(String username) {
        return Optional.ofNullable(accountsByUsernameMap.get(username));
    }
}
