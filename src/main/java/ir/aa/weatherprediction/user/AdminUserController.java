package ir.aa.weatherprediction.user;

import ir.aa.weatherprediction.user.domain.UserListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping
    public ResponseEntity<String> updateUserStatus(@RequestParam String username, @RequestParam boolean active) {
        userService.updateUserStatus(username, active);
        return ResponseEntity.ok("user " + username + " isActive status updated to " + (active ? "activate" : "inactive"));
    }

    @GetMapping
    public ResponseEntity<UserListResponse> getUsers() {
        return ResponseEntity.ok(UserListResponse.builder().users(userService.getUsers()).build());
    }

}
