@auth @iam
Feature: US14 - User Authentication, Registration, and Secure JWT Introspection
  As a registered or new SmartDrop user
  I want to register, authenticate using my credentials, and introspect signed JWT tokens
  So that I can securely access microservices according to my RBAC role permissions.

  Background:
    Given the SmartDrop IAM database is initialized
    And the user "valeria.brewer@artesanal.pe" exists with password "BeerPro2026!" and role "ROLE_USER"

  Scenario: Successful user registration with unique email and password
    When the client registers a new user with name "Carlos Mendoza", email "carlos.mendoza@smartdrop.pe", and password "Password123!"
    Then the user registration should be successful
    And the registered user should be persisted with email "carlos.mendoza@smartdrop.pe"

  Scenario: Successful user login with valid credentials returning signed JWT access token
    When the user "valeria.brewer@artesanal.pe" logs in with password "BeerPro2026!"
    Then the authentication should be successful
    And the response should contain a signed JWT access token
    And the token claims should contain username "valeria.brewer@artesanal.pe" and role "ROLE_USER"

  Scenario: Failed user login with invalid password
    When the user "valeria.brewer@artesanal.pe" attempts to log in with password "WrongPassword999!"
    Then the authentication should fail with an invalid credentials error

  Scenario: Token introspection and validation for an active JWT token
    Given a signed JWT access token is issued for user "valeria.brewer@artesanal.pe" with role "ROLE_USER"
    When the client introspects the token
    Then the introspection result should be valid
    And the introspection username should be "valeria.brewer@artesanal.pe"
    And the introspection roles should contain "ROLE_USER"

  Scenario: Token introspection for a malformed or invalid token
    Given a malformed JWT token "invalid.jwt.token.payload"
    When the client introspects the token
    Then the introspection result should be invalid
