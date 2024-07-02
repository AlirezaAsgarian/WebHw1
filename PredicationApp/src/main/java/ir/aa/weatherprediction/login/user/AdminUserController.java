package ir.aa.weatherprediction.login.user;

import ir.aa.dto.user.UserListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private RestTemplate restTemplate;
    private String ADMIN_BASE_URL;

    @Value("${authentication.url}")
    public void setBaseUrl(String baseUrl) {
        this.ADMIN_BASE_URL = "http://" + baseUrl + "/admin/users";
    }

    @PutMapping
    public ResponseEntity<String> updateUserStatus(@RequestParam String username, @RequestParam boolean active) {
        URI uri = UriComponentsBuilder.fromHttpUrl(ADMIN_BASE_URL)
                .queryParam("username", username)
                .queryParam("active", active)
                .build()
                .toUri();
        return restTemplate.exchange(uri, HttpMethod.PUT, new HttpEntity<>(null, new HttpHeaders()), String.class);
    }

    @GetMapping
    public ResponseEntity<UserListResponse> getUsers() {
        return restTemplate.exchange(ADMIN_BASE_URL, HttpMethod.GET, new HttpEntity<>(null, new HttpHeaders()), UserListResponse.class);
    }

}
