package com.assessment.authentication.api.connector;

import com.assessment.authentication.api.connector.dto.BankingAccountDto;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AccountsApiConnectorImpl implements AccountsApiConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsApiConnectorImpl.class);

    private final String accountsApiUriTemplate;
    private final RestTemplate restTemplate;

    public AccountsApiConnectorImpl(
        @Value("${accounts-api.uriTemplate}")String accountsApiUriTemplate,
        RestTemplate restTemplate) {

        this.accountsApiUriTemplate = accountsApiUriTemplate;
        this.restTemplate = restTemplate;
    }

    /**
     * Attempts to verify the banking account number using the Accounts API.
     *
     * @param accountNumber
     *
     * @return banking account information if found
     */
    //TODO: throws various connections and API Runtime exceptions, analysis and explicit handling needed.
    //TODO: Possibly better to replace with RestTemplate#exchange method for more control and clarity
    @Override
    public BankingAccountDto retrieveBankingAccount(Integer accountNumber) {
        LOGGER.debug("Attempting to fetch banking account data for account number:{}", accountNumber);

        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("accountNumber", accountNumber.toString());

        return restTemplate.getForObject(accountsApiUriTemplate, BankingAccountDto.class, uriParams);
    }
}
