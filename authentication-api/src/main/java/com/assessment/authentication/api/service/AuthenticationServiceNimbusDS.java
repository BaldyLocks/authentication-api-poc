package com.assessment.authentication.api.service;

import com.assessment.authentication.api.exception.AccountLockedException;
import com.assessment.authentication.api.exception.FailedToGenerateJWTException;
import com.assessment.authentication.api.exception.IncorrectPasswordException;
import com.assessment.authentication.api.exception.IncorrectUsernameException;
import com.assessment.authentication.api.repository.AccountStorage;
import com.assessment.authentication.api.repository.entity.Account;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceNimbusDS implements AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceNimbusDS.class);

    private final AccountStorage accountStorage;
    private final PasswordEncoder passwordEncoder;
    private final JWSSigner signer;

    public AuthenticationServiceNimbusDS(
        AccountStorage accountStorage,
        PasswordEncoder passwordEncoder,
        @Value("${jws.secret}")String secretEncoded) throws JOSEException {

        this.accountStorage = accountStorage;
        this.passwordEncoder = passwordEncoder;

        this.signer = new MACSigner(Base64.getUrlDecoder().decode(secretEncoded));
    }

    //TODO: Make retry count and account locking period configurable
    //TODO: Refactor into smaller methods
    @Override
    public String authenticate(String username, String password) {

        Optional<Account> optionalOfAccount = accountStorage.findByUsername(username);

        if (optionalOfAccount.isPresent()){
            LOGGER.debug("Account retrieved for username:{}", username);

            Account account = optionalOfAccount.get();
            Instant now = OffsetDateTime.now(ZoneOffset.UTC).toInstant();

            long hoursSinceLastAccess = hoursSince(now, account.getLastTimeOfAccess());

            if (account.getNumberOfFailedAttempts()>=3
                && hoursSinceLastAccess < 24 ) {
                LOGGER.warn("Attempt to access a locked account with username:{} detected", account.getUsername());
                throw new AccountLockedException();
            }

            if (hoursSinceLastAccess >=24) {
                LOGGER.warn("Account unlocked for username:{}. Locking period expired.", username);
                account.setNumberOfFailedAttempts(0);
            }

            account.setLastTimeOfAccess(now);

            boolean isCorrectPassword = passwordEncoder.matches(password, account.getPasswordHash());

            if (isCorrectPassword) {
                LOGGER.debug("Successfully verified password for username:{}", username);
                try {
                    account.setNumberOfFailedAttempts(0);
                    String jwt = createJwt(account);
                    LOGGER.debug("Successfully generated access token for username:{}", username);
                    return jwt;
                } catch (JOSEException e) {
                    throw new FailedToGenerateJWTException(e);
                }
            } else {
                account.setNumberOfFailedAttempts(account.getNumberOfFailedAttempts() + 1);

                LOGGER.info("Incorrect password submitted for an existing account with username:{}", username);
                throw new IncorrectPasswordException(username);
            }
        } else {
            LOGGER.info("Unregistered username:{} submitted, account not found.", username);
            throw new IncorrectUsernameException();
        }
    }

    private long hoursSince(Instant later, Instant previous) {
        LocalDateTime timeNow = LocalDateTime.ofInstant(later, ZoneOffset.UTC);
        LocalDateTime timeThen = LocalDateTime.ofInstant(previous, ZoneOffset.UTC);
        return timeThen.until(timeNow, ChronoUnit.HOURS);
    }

    private String createJwt(Account account) throws JOSEException {

        //TODO make expiration, issuer and audience configurable
        JWTClaimsSet claimsSet = new Builder()
            .issuer("me")
            .audience("you")
            .subject(account.getUsername())
            .expirationTime(Date.from(Instant.now().plusSeconds(30*60)))
            .build();

        JWSObject jwsObject = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.HS512).build(),
            claimsSet);

        jwsObject.sign(signer);

        return jwsObject.serialize();
    }

}
