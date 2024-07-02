package ir.aa.authentication.login.apitoken;

import ir.aa.authentication.login.user.UserDaoFactory;
import ir.aa.authentication.login.user.UserEntity;
import ir.aa.authentication.login.user.UserRepository;
import ir.aa.dto.apitoken.ApiTokenRequest;
import ir.aa.dto.apitoken.ApiTokenResponse;
import ir.aa.dto.apitoken.ApiTokenRetrieveResponse;
import ir.aa.dto.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;



@RestController
@RequestMapping("/user/api-tokens")
public class ApiTokenController {

    @Autowired
    private ApiTokenService apiTokenService;

    @Autowired
    private UserRepository userDetailsService;

    @PostMapping
    public ResponseEntity<ApiTokenResponse> createApiToken(@RequestBody ApiTokenRequest request, @RequestParam String username) {
        // Call the service to create the token
        ApiTokenResponse response = apiTokenService.createApiToken(request, username);

        // Return the response with HTTP 201 Created status
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiTokenRetrieveResponse> getApiTokens(@RequestParam String username) {
        ApiTokenRetrieveResponse apiTokenRetrieveResponse = apiTokenService.retriveApiTokens(username);
        // Return the response
        return ResponseEntity.ok(apiTokenRetrieveResponse);
    }

    @PostMapping("validate")
    public ResponseEntity<UserDao> validateToken(@RequestBody String token) {
        if(apiTokenService.isValidApiToken(token)) {
            String username = apiTokenService.findUserNameByApiToken(token);
            UserEntity user = userDetailsService.findByUsername(username).orElse(null);
            if (user != null) {
                return ResponseEntity.ok(UserDaoFactory.fromUserEntity(user));
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found");
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "api token is not valid");
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Boolean>> invalidateApiToken(@RequestHeader("Authorization") String authorizationHeader) {
        apiTokenService.inActiveToken(authorizationHeader.substring(TokenType.API_TOKEN.getName().length() + 1));
        // Return a response indicating the token was successfully deleted
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}

