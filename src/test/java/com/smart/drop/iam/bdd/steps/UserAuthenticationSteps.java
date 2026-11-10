package com.smart.drop.iam.bdd.steps;

import com.smart.drop.iam.application.services.AuthService;
import com.smart.drop.iam.domain.model.Role;
import com.smart.drop.iam.domain.model.User;
import com.smart.drop.iam.domain.model.repositories.RoleRepository;
import com.smart.drop.iam.domain.model.repositories.UserRepository;
import com.smart.drop.iam.infrastructure.security.jwt.JwtTokenService;
import com.smart.drop.iam.interfaces.rest.dto.ValidateTokenResponseDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class UserAuthenticationSteps {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private User currentUser;
    private User registeredUser;
    private String jwtToken;
    private Exception authException;
    private ValidateTokenResponseDto introspectionResult;

    @Given("the SmartDrop IAM database is initialized")
    public void theSmartDropIamDatabaseIsInitialized() {
        assertThat(authService).isNotNull();
        assertThat(jwtTokenService).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @Given("the user {string} exists with password {string} and role {string}")
    public void theUserExistsWithPasswordAndRole(String email, String password, String roleName) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            User created = authService.registerUser("Valeria Brewer", email, password);
            Optional<Role> roleOpt = roleRepository.findByName(roleName);
            if (roleOpt.isEmpty()) {
                Role newRole = Role.create(roleName, "Role for " + roleName);
                roleRepository.save(newRole);
            }
            authService.assignRoleToUser(created.getId(), roleName);
        }
    }

    @When("the client registers a new user with name {string}, email {string}, and password {string}")
    public void theClientRegistersANewUserWithNameEmailAndPassword(String name, String email, String password) {
        try {
            userRepository.findByEmail(email).ifPresent(u -> userRepository.deleteById(u.getId()));
            registeredUser = authService.registerUser(name, email, password);
        } catch (Exception e) {
            authException = e;
        }
    }

    @Then("the user registration should be successful")
    public void theUserRegistrationShouldBeSuccessful() {
        assertThat(authException).isNull();
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getId()).isNotNull();
    }

    @And("the registered user should be persisted with email {string}")
    public void theRegisteredUserShouldBePersistedWithEmail(String email) {
        Optional<User> persisted = userRepository.findByEmail(email);
        assertThat(persisted).isPresent();
        assertThat(persisted.get().getEmail()).isEqualTo(email);
    }

    @When("the user {string} logs in with password {string}")
    public void theUserLogsInWithPassword(String email, String password) {
        try {
            authException = null;
            currentUser = authService.loginUser(email, password);
            jwtToken = jwtTokenService.generateToken(currentUser);
        } catch (Exception e) {
            authException = e;
        }
    }

    @Then("the authentication should be successful")
    public void theAuthenticationShouldBeSuccessful() {
        assertThat(authException).isNull();
        assertThat(currentUser).isNotNull();
    }

    @And("the response should contain a signed JWT access token")
    public void theResponseShouldContainASignedJwtAccessToken() {
        assertThat(jwtToken).isNotNull().isNotBlank();
        assertThat(jwtToken.split("\\.")).hasSize(3);
    }

    @And("the token claims should contain username {string} and role {string}")
    public void theTokenClaimsShouldContainUsernameAndRole(String username, String role) {
        ValidateTokenResponseDto validation = jwtTokenService.validateToken(jwtToken);
        assertThat(validation.valid()).isTrue();
        assertThat(validation.username()).isEqualTo(username);
        assertThat(validation.roles()).contains(role);
    }

    @When("the user {string} attempts to log in with password {string}")
    public void theUserAttemptsToLogInWithPassword(String email, String password) {
        try {
            authException = null;
            currentUser = authService.loginUser(email, password);
        } catch (Exception e) {
            authException = e;
        }
    }

    @Then("the authentication should fail with an invalid credentials error")
    public void theAuthenticationShouldFailWithAnInvalidCredentialsError() {
        assertThat(authException).isNotNull();
        assertThat(authException).isInstanceOf(IllegalArgumentException.class);
        assertThat(authException.getMessage()).containsIgnoringCase("Invalid credentials");
    }

    @Given("a signed JWT access token is issued for user {string} with role {string}")
    public void aSignedJwtAccessTokenIsIssuedForUserWithRole(String email, String role) {
        theUserExistsWithPasswordAndRole(email, "BeerPro2026!", role);
        User user = userRepository.findByEmail(email).orElseThrow();
        jwtToken = jwtTokenService.generateToken(user);
        assertThat(jwtToken).isNotBlank();
    }

    @When("the client introspects the token")
    public void theClientIntrospectsTheToken() {
        introspectionResult = jwtTokenService.validateToken(jwtToken);
    }

    @Then("the introspection result should be valid")
    public void theIntrospectionResultShouldBeValid() {
        assertThat(introspectionResult).isNotNull();
        assertThat(introspectionResult.valid()).isTrue();
    }

    @And("the introspection username should be {string}")
    public void theIntrospectionUsernameShouldBe(String username) {
        assertThat(introspectionResult.username()).isEqualTo(username);
    }

    @And("the introspection roles should contain {string}")
    public void theIntrospectionRolesShouldContain(String role) {
        assertThat(introspectionResult.roles()).contains(role);
    }

    @Given("a malformed JWT token {string}")
    public void aMalformedJwtToken(String malformedToken) {
        jwtToken = malformedToken;
    }

    @Then("the introspection result should be invalid")
    public void theIntrospectionResultShouldBeInvalid() {
        assertThat(introspectionResult).isNotNull();
        assertThat(introspectionResult.valid()).isFalse();
    }
}
