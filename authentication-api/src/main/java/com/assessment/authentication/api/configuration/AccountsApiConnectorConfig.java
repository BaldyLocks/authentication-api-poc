package com.assessment.authentication.api.configuration;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AccountsApiConnectorConfig {

    @Value("${accounts-api.keystore.path}")
    private String keystorePath;
    @Value("${accounts-api.keystore.password}")
    private String keystorePassword;
    @Value("${accounts-api.key.password}")
    private String keyPassword;
    @Value("${accounts-api.truststore.path}")
    private String truststorePath;
    @Value("${accounts-api.truststore.password}")
    private String truststorePassword;

    @Bean
    public RestTemplate restTemplate() throws IOException, GeneralSecurityException{

        File keystoreFile = ResourceUtils.getFile(keystorePath);
        File truststoreFile = ResourceUtils.getFile(truststorePath);

        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(keystoreFile, keystorePassword.toCharArray(), keyPassword.toCharArray())
                .loadTrustMaterial(truststoreFile, truststorePassword.toCharArray())
                .build();

        CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }
}
