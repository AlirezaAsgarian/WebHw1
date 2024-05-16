package ir.aa.weatherprediction.login.user;

import ir.aa.weatherprediction.login.user.domain.AuthResponse;
import ir.aa.weatherprediction.login.user.domain.LoginRequest;
import ir.aa.weatherprediction.login.user.domain.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request, Role.USER));
    }
    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
