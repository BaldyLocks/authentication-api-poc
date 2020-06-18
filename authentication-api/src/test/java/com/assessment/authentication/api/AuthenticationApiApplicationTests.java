package com.assessment.authentication.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.assessment.authentication.api.connector.AccountsApiConnector;
import com.assessment.authentication.api.connector.dto.BankingAccountDto;
import com.assessment.authentication.api.presentation.dto.AccountRegistrationRequest;
import com.assessment.authentication.api.presentation.dto.AuthenticationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import java.util.Base64;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
class AuthenticationApiApplicationTests {

	private MockMvc mockMvc;
	private ObjectMapper objectMapper = new ObjectMapper();
	private JWSVerifier jwsVerifier;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Value("${jws.secret}")
	private String encodedSecret;

	@MockBean
	private AccountsApiConnector accountsApiConnector;

	@PostConstruct
	public void init() throws JOSEException {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
		this.jwsVerifier = new MACVerifier(Base64.getUrlDecoder().decode(encodedSecret));
	}

	@Test
	public void registerValidAccountTest() throws Exception {

		AccountRegistrationRequest accountRegistrationRequest =
			new AccountRegistrationRequest(
				"testUser1",
				"testPassword",
				77853449);

		BankingAccountDto bankingAccountDto = new BankingAccountDto("NL24INGB7785344909", UUID.randomUUID());

		given(accountsApiConnector.retrieveBankingAccount(77853449)).willReturn(bankingAccountDto);

		mockMvc.perform(post("/register-account")
			.content(objectMapper.writeValueAsString(accountRegistrationRequest))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());
	}

	@Test
	public void registerInvalidAccountTest_shortPassword() throws Exception {

		AccountRegistrationRequest accountRegistrationRequest =
			new AccountRegistrationRequest(
				UUID.randomUUID().toString(),
				"short",
				77853449);

		BankingAccountDto bankingAccountDto = new BankingAccountDto("NL24INGB7785344909", UUID.randomUUID());

		given(accountsApiConnector.retrieveBankingAccount(77853449)).willReturn(bankingAccountDto);

		mockMvc.perform(post("/register-account")
			.content(objectMapper.writeValueAsString(accountRegistrationRequest))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void registerInalidAccountTest_usernameInUse() throws Exception {

		AccountRegistrationRequest accountRegistrationRequest =
			new AccountRegistrationRequest(
				"repeatedUsername",
				"testPassword",
				77853449);

		BankingAccountDto bankingAccountDto = new BankingAccountDto("NL24INGB7785344909", UUID.randomUUID());

		given(accountsApiConnector.retrieveBankingAccount(77853449)).willReturn(bankingAccountDto);

		mockMvc.perform(post("/register-account")
			.content(objectMapper.writeValueAsString(accountRegistrationRequest))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		mockMvc.perform(post("/register-account")
			.content(objectMapper.writeValueAsString(accountRegistrationRequest))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isConflict());
	}

	@Test
	public void registerInvalidAccountTest_wrongAccountNumber() throws Exception {

		AccountRegistrationRequest accountRegistrationRequest =
			new AccountRegistrationRequest(
				UUID.randomUUID().toString(),
				"testPassword",
				77853449);

		given(accountsApiConnector.retrieveBankingAccount(77853449)).willReturn(null);

		mockMvc.perform(post("/register-account")
			.content(objectMapper.writeValueAsString(accountRegistrationRequest))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest());
	}

	@Test
	public void authenticateValidAccountTest() throws Exception {

		AccountRegistrationRequest accountRegistrationRequest =
			new AccountRegistrationRequest(
				"testUser2",
				"testPassword",
				77853449);

		BankingAccountDto bankingAccountDto = new BankingAccountDto("NL24INGB7785344909", UUID.randomUUID());

		given(accountsApiConnector.retrieveBankingAccount(77853449)).willReturn(bankingAccountDto);

		mockMvc.perform(post("/register-account")
			.content(objectMapper.writeValueAsString(accountRegistrationRequest))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		AuthenticationRequest authenticationRequest =
			new AuthenticationRequest(
				"testUser2",
				"testPassword");

		String token = mockMvc.perform(post("/authenticate")
			.content(objectMapper.writeValueAsString(authenticationRequest))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();

		JWSObject parsedToken = assertDoesNotThrow(() -> JWSObject.parse(token));
		assertTrue(parsedToken.verify(jwsVerifier));
	}

	@Test
	public void authenticateInvalidAccountTest() throws Exception {
		AuthenticationRequest authenticationRequest =
			new AuthenticationRequest(
				"unregisteredUsername",
				"testPassword");

		mockMvc.perform(post("/authenticate")
			.content(objectMapper.writeValueAsString(authenticationRequest))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

	@Test
	public void authenticateValidAccountTest_wrongPasswordTooManyTimes() throws Exception {

		AccountRegistrationRequest accountRegistrationRequest =
			new AccountRegistrationRequest(
				"testUser3",
				"goodPassword",
				77853449);

		BankingAccountDto bankingAccountDto = new BankingAccountDto("NL24INGB7785344909", UUID.randomUUID());

		given(accountsApiConnector.retrieveBankingAccount(77853449)).willReturn(bankingAccountDto);

		mockMvc.perform(post("/register-account")
			.content(objectMapper.writeValueAsString(accountRegistrationRequest))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk());

		AuthenticationRequest authenticationRequestBadPassword =
			new AuthenticationRequest(
				"testUser3",
				"wrongPassword");

		for (int i = 0; i < 3; i++) {
			mockMvc.perform(post("/authenticate")
				.content(objectMapper.writeValueAsString(authenticationRequestBadPassword))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden());
		}

		AuthenticationRequest authenticationRequestGoodPassword =
			new AuthenticationRequest(
				"testUser3",
				"goodPassword");

		mockMvc.perform(post("/authenticate")
			.content(objectMapper.writeValueAsString(authenticationRequestGoodPassword))
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isForbidden());
	}

}
