package ir.aa.authentication.login.user;


import ir.aa.dto.user.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "user_tb")
@AllArgsConstructor
@Setter
@Getter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String phoneNumber;
    private String address;
    private boolean isActive;
    @Enumerated(value = EnumType.STRING)
    private Role role;
    private Date dateJoined;
    private Date lastLogin;

    public UserEntity() {
        isActive = false;
    }
    public boolean isEnabled() {
        return isActive;
    }
}
