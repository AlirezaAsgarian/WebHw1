package ir.aa.weatherprediction.login.apitoken;

import ir.aa.weatherprediction.config.TokenType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;



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
    public ResponseEntity<Map<String, Boolean>> invalidateApiToken(@RequestHeader("Authorization") String authorizationHeader) {
        apiTokenService.inActiveToken(authorizationHeader.substring(TokenType.API_TOKEN.getName().length() + 1));
        // Return a response indicating the token was successfully deleted
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}

