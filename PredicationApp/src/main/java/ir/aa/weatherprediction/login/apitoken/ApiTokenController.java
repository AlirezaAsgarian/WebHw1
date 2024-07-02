package ir.aa.weatherprediction.login.apitoken;

import ir.aa.dto.apitoken.ApiTokenRequest;
import ir.aa.dto.apitoken.ApiTokenResponse;
import ir.aa.dto.apitoken.ApiTokenRetrieveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;



@RestController
@RequestMapping("/user/api-tokens")
public class ApiTokenController {

    @Autowired
    private RestTemplate restTemplate;
    private String API_TOKENS_BASE_URL;

    @Value("${authentication.url}")
    public void setBaseUrl(String baseUrl) {
        this.API_TOKENS_BASE_URL = "http://" + baseUrl + "/user/api-tokens";
    }

    @PostMapping
    public ResponseEntity<ApiTokenResponse> createApiToken(@RequestBody ApiTokenRequest request) {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_TOKENS_BASE_URL)
                .queryParam("username", SecurityContextHolder.getContext().getAuthentication().getName())
                .build()
                .toUri();
        return restTemplate.exchange(uri, HttpMethod.POST, new HttpEntity<>(request, new HttpHeaders()), ApiTokenResponse.class);
    }

    @GetMapping
    public ResponseEntity<ApiTokenRetrieveResponse> getApiTokens() {
        URI uri = UriComponentsBuilder.fromHttpUrl(API_TOKENS_BASE_URL)
                .queryParam("username", SecurityContextHolder.getContext().getAuthentication().getName())
                .build()
                .toUri();
        return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders()), ApiTokenRetrieveResponse.class);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Boolean>> invalidateApiToken(@RequestHeader("Authorization") String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorizationHeader);
        return restTemplate.exchange(API_TOKENS_BASE_URL, HttpMethod.DELETE, new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<Map<String, Boolean>>() {
                });
    }
}

