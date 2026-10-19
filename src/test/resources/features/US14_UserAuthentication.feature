@auth @iam
Feature: US14 - User Authentication and Secure JWT Issuance
  As a registered SmartDrop user
  I want to authenticate using my email and encrypted password
  So that I can obtain a signed JWT token to securely access microservices according to my RBAC role.

  Background:
    Given the SmartDrop IAM database is initialized
    And the user "valeria.brewer@artesanal.pe" exists with hashed password "BeerPro2026!" and role "ROLE_USER"

  Scenario: Successful user login with valid credentials
    When the client sends a POST request to "/api/iam/auth/login" with payload:
      | email                      | password     |
      | valeria.brewer@artesanal.pe | BeerPro2026! |
    Then the response status code should be 200 OK
    And the response body should contain a valid JWT access token
    And the token claims should contain username "valeria.brewer@artesanal.pe" and role "ROLE_USER"

  Scenario: Failed user login with invalid password
    When the client sends a POST request to "/api/iam/auth/login" with payload:
      | email                      | password        |
      | valeria.brewer@artesanal.pe | WrongPassword!  |
    Then the response status code should be 401 Unauthorized
    And the response message should indicate "Invalid credentials"

  Scenario: Successful user registration with role assignment
    When the client sends a POST request to "/api/iam/auth/register" with payload:
      | fullName        | email                    | password     |
      | Carlos Mendoza  | carlos.mendoza@gmail.com | Password123! |
    Then the response status code should be 201 Created
    And the user should be persisted in the IAM database
