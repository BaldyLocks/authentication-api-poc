package com.assessment.authentication.api.repository.entity;

import com.google.common.base.Objects;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class Account {
    private final String username;
    private final String passwordHash;
    private Instant lastTimeOfAccess;
    private Integer numberOfFailedAttempts;

    public Account(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.numberOfFailedAttempts = 0;
        this.lastTimeOfAccess = OffsetDateTime.now(ZoneOffset.UTC).toInstant();
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getLastTimeOfAccess() {
        return lastTimeOfAccess;
    }

    public void setLastTimeOfAccess(Instant lastTimeOfAccess) {
        this.lastTimeOfAccess = lastTimeOfAccess;
    }

    public Integer getNumberOfFailedAttempts() {
        return numberOfFailedAttempts;
    }

    public void setNumberOfFailedAttempts(Integer numberOfFailedAttempts) {
        this.numberOfFailedAttempts = numberOfFailedAttempts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return Objects.equal(username, account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(username);
    }
}
