package com.assessment.authentication.api.connector;

import com.assessment.authentication.api.connector.dto.BankingAccountDto;

public interface AccountsApiConnector {
    BankingAccountDto retrieveBankingAccount(Integer accountNumber);
}
