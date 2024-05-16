package ir.aa.weatherprediction.login.apitoken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static ir.aa.weatherprediction.config.ApiTokenAuthenticationFilter.API_TOKEN_PREFIX_HEADER;


@RestController
@RequestMapping("/user/api-tokens")
public class ApiTokenController {

    @Autowired
    private ApiTokenService apiTokenService;

    @PostMapping
    public ResponseEntity<ApiTokenResponse> createApiToken(@RequestBody ApiTokenRequest request) {
        // Call the service to create the token
        ApiTokenResponse response = apiTokenService.createApiToken(request);

        // Return the response with HTTP 201 Created status
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiTokenRetrieveResponse> getApiTokens() {
        ApiTokenRetrieveResponse apiTokenRetrieveResponse = apiTokenService.retriveApiTokens();
        // Return the response
        return ResponseEntity.ok(apiTokenRetrieveResponse);
    }

    @DeleteMapping
    public ResponseEntity<?> invalidateApiToken(@RequestHeader("Authorization") String authorizationHeader) {
        apiTokenService.inActiveToken(authorizationHeader.substring(API_TOKEN_PREFIX_HEADER.length()));
        // Return a response indicating the token was successfully deleted
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}

