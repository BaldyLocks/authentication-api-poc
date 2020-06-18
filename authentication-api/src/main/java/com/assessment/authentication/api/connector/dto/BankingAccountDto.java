package com.assessment.authentication.api.connector.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class BankingAccountDto {
    private final String iban;
    private final UUID ownerId;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public BankingAccountDto(
        @JsonProperty("iban") String iban,
        @JsonProperty("ownerId") UUID ownerId) {
        this.iban = iban;
        this.ownerId = ownerId;
    }

    public String getIban() {
        return iban;
    }

    public UUID getOwnerId() {
        return ownerId;
    }
}
