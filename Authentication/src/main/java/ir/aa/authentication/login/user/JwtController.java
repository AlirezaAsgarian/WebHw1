package ir.aa.authentication.login.user;

import ir.aa.dto.user.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/jwt")
public class JwtController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @PostMapping
    public ResponseEntity<UserDao> validateToken(@RequestBody String jwt) {
        try {
            jwtService.extractAllClaims(jwt);
        } catch (Exception ignore) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jwt is not invalid");
        }
        final String username = jwtService.extractUsername(jwt);
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user name not found");
        }
        UserEntity user = userRepository.findByUsername(username).orElse(null);
        if (!jwtService.isTokenValid(user, jwt)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Jwt is not invalid");
        }
        return ResponseEntity.ok(UserDaoFactory.fromUserEntity(user));
    }
}
