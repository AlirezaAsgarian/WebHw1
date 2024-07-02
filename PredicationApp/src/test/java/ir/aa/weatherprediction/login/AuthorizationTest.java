package ir.aa.weatherprediction.login;

import ir.aa.weatherprediction.config.TokenType;
import ir.aa.weatherprediction.login.apitoken.ApiTokenRequest;
import ir.aa.weatherprediction.login.apitoken.ApiTokenResponse;
import ir.aa.weatherprediction.login.exception.ErrorResponse;
import ir.aa.weatherprediction.login.user.domain.AuthResponse;
import ir.aa.weatherprediction.login.user.domain.LoginRequest;
import ir.aa.weatherprediction.login.user.domain.RegisterRequest;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthorizationTest {
    public static final String USER_PASS = "pass";
    public static final String USER_NAME = "johnDoe";

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPass;
    @Autowired
    private TestRestTemplate restTemplate;
    @Test
    void testRegisterUserEndpoint() {
        ResponseEntity<String> response = registerUser();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        // We can not register one user twice
        ResponseEntity<ErrorResponse> errorResponse = registerUserWithException();
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
        Assertions.assertEquals(UserService.DUPLICATE_USERNAME_ERROR_MSG, errorResponse.getBody().getDetail());

        // We can not log in with inactivated user
        errorResponse = loginUserWithException();
        Assertions.assertEquals(HttpStatus.FORBIDDEN, errorResponse.getStatusCode());
        Assertions.assertEquals(UserService.USER_IS_INACTIVE_ERROR_MSG, errorResponse.getBody().getDetail());
    }

    @Test
    void testEnabledUserToLoginAndDisable() {
        registerUser();
        ResponseEntity<AuthResponse> adminLoginResponse = loginAdmin();
        enableUserByAdmin(adminLoginResponse.getBody().getToken());

        // We can now log in with activated user
        ResponseEntity<AuthResponse> authResponseResponseEntity = loginUser();
        Assertions.assertEquals(HttpStatus.OK, authResponseResponseEntity.getStatusCode());

        disableUserByAdmin(adminLoginResponse.getBody().getToken());

        // We can not log in with inactivated user
        ResponseEntity<ErrorResponse> errorResponse = loginUserWithException();
        Assertions.assertEquals(HttpStatus.FORBIDDEN, errorResponse.getStatusCode());
        Assertions.assertEquals(UserService.USER_IS_INACTIVE_ERROR_MSG, errorResponse.getBody().getDetail());
    }

    @Test
    void testEnabledUserDoenNotHaveAccessToAdminEndpoits() {
        registerUser();
        ResponseEntity<AuthResponse> adminLoginResponse = loginAdmin();
        enableUserByAdmin(adminLoginResponse.getBody().getToken());
        ResponseEntity<AuthResponse> authResponseResponseEntity = loginUser();

        HttpEntity<String> requestEntity = getHttpEntityWithJustAuthorizationHeader(authResponseResponseEntity.getBody().getToken(), TokenType.BEARER);

        ResponseEntity<String> response = restTemplate.exchange("/admin/users" + "?username={username}&active={active}",
                HttpMethod.PUT, requestEntity, String.class, USER_NAME, true);
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void createApiTokenAndLoginWithThat() {
        registerUser();
        ResponseEntity<AuthResponse> adminLoginResponse = loginAdmin();
        enableUserByAdmin(adminLoginResponse.getBody().getToken());
        ResponseEntity<AuthResponse> authResponse = loginUser();

        ApiTokenRequest apiTokenRequest = ApiTokenRequest.builder()
                .name("token1")
                .expireDate(LocalDateTime.now().plusHours(1L).toString())
                .build();

        // Create api token using jwt method
        ResponseEntity<ApiTokenResponse> apiTokenResponseWithValidToken =
                createApiToken(authResponse.getBody().getToken(), TokenType.BEARER, apiTokenRequest);

        Assertions.assertEquals(HttpStatus.CREATED, apiTokenResponseWithValidToken.getStatusCode());

        // Create api token using api token method
        apiTokenRequest = ApiTokenRequest.builder()
                .name("token2")
                // Expired api token
                .expireDate(LocalDateTime.now().minusMinutes(1L).toString())
                .build();

        final ResponseEntity<ApiTokenResponse> apiTokenResponseWithExpiredToken =
                createApiToken(apiTokenResponseWithValidToken.getBody().getToken(), TokenType.API_TOKEN, apiTokenRequest);

        assertEquals(HttpStatus.CREATED, apiTokenResponseWithExpiredToken.getStatusCode());

        apiTokenRequest = ApiTokenRequest.builder()
                .name("token3")
                // Expired api token
                .expireDate(LocalDateTime.now().plusHours(1L).toString())
                .build();

        // Create token using expired token is not allowed
        ApiTokenRequest finalApiTokenRequest = apiTokenRequest;
        assertThrows(Exception.class,
                () -> createApiTokenWithException(apiTokenResponseWithExpiredToken.getBody().getToken(), TokenType.API_TOKEN, finalApiTokenRequest));


        ResponseEntity<Map<String, Boolean>> revokeResponseEntity =
                revokeToken(apiTokenResponseWithValidToken.getBody().getToken());

        assertEquals(HttpStatus.OK, revokeResponseEntity.getStatusCode());
        assertEquals(true, revokeResponseEntity.getBody().get("deleted"));

        // Create token using revoked token is not allowed
        assertThrows(Exception.class,
                () -> createApiTokenWithException(apiTokenResponseWithValidToken.getBody().getToken(), TokenType.API_TOKEN, finalApiTokenRequest));
    }

    private ResponseEntity<Map<String, Boolean>> revokeToken(String token) {
        HttpHeaders headers = getAuthorizationHeader(token, TokenType.API_TOKEN);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        return restTemplate.exchange("/user/api-tokens", HttpMethod.DELETE, requestEntity, new ParameterizedTypeReference<>() {});
    }

    private ResponseEntity<ErrorResponse> createApiTokenWithException(String token, TokenType tokenType, ApiTokenRequest apiTokenRequest) {
        HttpHeaders headers = getAuthorizationHeader(token, tokenType);
        headers.set("Content-Type", "application/json");
        HttpEntity<ApiTokenRequest> requestEntity = new HttpEntity<>(apiTokenRequest, headers);
        return restTemplate.exchange("/user/api-tokens", HttpMethod.POST, requestEntity, ErrorResponse.class);
    }
    private ResponseEntity<ApiTokenResponse> createApiToken(String token, TokenType tokenType, ApiTokenRequest apiTokenRequest) {
         HttpHeaders headers = getAuthorizationHeader(token, tokenType);
         headers.set("Content-Type", "application/json");
        HttpEntity<ApiTokenRequest> requestEntity = new HttpEntity<>(apiTokenRequest, headers);
        return restTemplate.exchange("/user/api-tokens", HttpMethod.POST, requestEntity, ApiTokenResponse.class);
    }

    private ResponseEntity<String> enableUserByAdmin(String token) {
        boolean active = true;
        return activateUser(token, active);
    }

    private ResponseEntity<String> disableUserByAdmin(String token) {
        boolean active = false;
        return activateUser(token, active);
    }

    private ResponseEntity<String> activateUser(String token, boolean active) {
        HttpEntity<String> requestEntity = getHttpEntityWithJustAuthorizationHeader(token, TokenType.BEARER);

        // Send PUT request
        return restTemplate.exchange("/admin/users" + "?username={username}&active={active}",
                HttpMethod.PUT, requestEntity, String.class, USER_NAME, active);
    }

    private static HttpEntity<String> getHttpEntityWithJustAuthorizationHeader(String token, TokenType tokenType) {
        // Prepare headers
        HttpHeaders headers = getAuthorizationHeader(token, tokenType);

        // Create HttpEntity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        return requestEntity;
    }

    private static HttpHeaders getAuthorizationHeader(String token, TokenType tokenType) {
        String authorizationHeader = tokenType.getName() + " " + token;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);
        return headers;
    }

    private ResponseEntity<AuthResponse> loginAdmin() {
        LoginRequest loginRequest = LoginRequest
                .builder()
                .password(adminPass)
                .username(adminUsername)
                .build();

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create HttpEntity with headers and body
        HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(loginRequest, headers);
        return restTemplate.exchange("/users/login", HttpMethod.POST, requestEntity, AuthResponse.class);
    }

    @NonNull
    private ResponseEntity<ErrorResponse> registerUserWithException() {
        HttpEntity<RegisterRequest> requestEntity = getRegisterRequestHttpEntity();
        // Send POST request
        return restTemplate.exchange("/users/register", HttpMethod.POST, requestEntity, ErrorResponse.class);
    }

    @NonNull
    private ResponseEntity<ErrorResponse> loginUserWithException() {
        HttpEntity<LoginRequest> requestEntity = getLoginRequestHttpEntity();
        // Send POST request
        return restTemplate.exchange("/users/login", HttpMethod.POST, requestEntity, ErrorResponse.class);
    }
    private ResponseEntity<AuthResponse> loginUser() {
        HttpEntity<LoginRequest> requestEntity = getLoginRequestHttpEntity();
        // Send POST request
        return restTemplate.exchange("/users/login", HttpMethod.POST, requestEntity, AuthResponse.class);
    }

    private static HttpEntity<LoginRequest> getLoginRequestHttpEntity() {
        LoginRequest loginRequest = LoginRequest
                .builder()
                .password(USER_PASS)
                .username(USER_NAME)
                .build();

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create HttpEntity with headers and body
        HttpEntity<LoginRequest> requestEntity = new HttpEntity<>(loginRequest, headers);
        return requestEntity;
    }

    private ResponseEntity<String> registerUser() {
        HttpEntity<RegisterRequest> requestEntity = getRegisterRequestHttpEntity();
        // Send POST request
        return restTemplate.exchange("/users/register", HttpMethod.POST, requestEntity, String.class);
    }

    private static HttpEntity<RegisterRequest> getRegisterRequestHttpEntity() {
        RegisterRequest registerRequest = RegisterRequest
                .builder()
                .firstName("john")
                .lastName("doe")
                .phoneNumber("phonenumber")
                .password("pass")
                .address("addr")
                .username("johnDoe")
                .build();

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Create HttpEntity with headers and body
        HttpEntity<RegisterRequest> requestEntity = new HttpEntity<>(registerRequest, headers);
        return requestEntity;
    }
}

