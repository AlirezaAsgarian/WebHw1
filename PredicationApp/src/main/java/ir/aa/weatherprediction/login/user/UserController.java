package ir.aa.weatherprediction.login.user;


import ir.aa.dto.user.AuthResponse;
import ir.aa.dto.user.LoginRequest;
import ir.aa.dto.user.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(path = "/users/")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private RestTemplate restTemplate;
    private String USERS_BASE_URL;
    @Value("${authentication.url}")
    public void setBaseUrl(String baseUrl) {
        this.USERS_BASE_URL = "http://" + baseUrl + "/users";
    }
    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
       return restTemplate.exchange(USERS_BASE_URL + "/register", HttpMethod.POST, new HttpEntity<>(request, new HttpHeaders()), String.class);
    }
    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return restTemplate.exchange(USERS_BASE_URL + "/login", HttpMethod.POST, new HttpEntity<>(request, new HttpHeaders()), AuthResponse.class); }
}
