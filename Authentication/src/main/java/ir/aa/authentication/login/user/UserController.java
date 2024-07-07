package ir.aa.authentication.login.user;


import ir.aa.dto.user.AuthResponse;
import ir.aa.dto.user.LoginRequest;
import ir.aa.dto.user.RegisterRequest;
import ir.aa.dto.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping(path = "/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping(path = "/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request, Role.USER));
    }
    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) { return userService.login(request); }

}
