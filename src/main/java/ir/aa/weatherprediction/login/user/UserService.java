package ir.aa.weatherprediction.login.user;

import ir.aa.weatherprediction.config.JwtService;
import ir.aa.weatherprediction.login.user.domain.AuthResponse;
import ir.aa.weatherprediction.login.user.domain.LoginRequest;
import ir.aa.weatherprediction.login.user.domain.RegisterRequest;
import ir.aa.weatherprediction.login.user.domain.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    public static final String DUPLICATE_USERNAME_ERROR_MSG = "duplicate username";
    public static final String WRONG_USERNAME_OR_PASSWORD_ERROR_MSG = "wrong username or password";
    public static final String USER_IS_INACTIVE_ERROR_MSG = "user is inactive";
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                       @Value("${app.admin.password}") String adminPass,
                       @Value("${app.admin.username}") String adminUsername) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        addAdminUser(adminPass, adminUsername);
    }

    public UserEntity getUserByUsername(String username) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid token");
        }
        return optionalUser.get();
    }

    public String register(RegisterRequest request, Role role) throws ResponseStatusException {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, DUPLICATE_USERNAME_ERROR_MSG);
        }
        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .role(role)
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        user.setDateJoined(new Date());
        user.setLastLogin(new Date());
        userRepository.save(user);
        String msg = "User with id " + user.getId() + " and username " + user.getUsername() + " registered";
        log.info(msg);
        return msg;
    }

    public ResponseEntity<AuthResponse> login(LoginRequest request) throws ResponseStatusException {
        String username = request.getUsername();
        String password = request.getPassword();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, WRONG_USERNAME_OR_PASSWORD_ERROR_MSG);
        }

        UserEntity user = optionalUser.get();

        if (!optionalUser.get().isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, USER_IS_INACTIVE_ERROR_MSG);
        }

        if (!passwordEncoder.matches(password, optionalUser.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, WRONG_USERNAME_OR_PASSWORD_ERROR_MSG);
        }

        user.setLastLogin(new Date());
        userRepository.save(user);
        log.info("User with id " + user.getId() + " and username " + user.getUsername() + " logged in");
        return ResponseEntity.ok(AuthResponse.builder()
                        .token(jwtService.generateToken(user))
                        .build());
    }

    public void updateUserStatus(String username, boolean active) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "username not found"));
        if (userEntity.isEnabled() && active) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user already has been active");
        }

        if (!userEntity.isEnabled() && !active) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "user already has been inactive");
        }

        userEntity.setActive(active);
        userRepository.saveAndFlush(userEntity);
    }

    public List<UserDao> getUsers() {
        return userRepository.findAll().stream().map(UserDao::fromUserEntity).toList();
    }

    private void addAdminUser(String adminPass, String adminUsername) {
        UserEntity user = UserEntity.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminPass))
                .role(Role.ADMIN)
                .isActive(true)
                .build();
        if (userRepository.findByUsername(adminUsername).isEmpty())
            userRepository.save(user);
    }
}
