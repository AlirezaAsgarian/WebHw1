package ir.aa.weatherprediction.user.domain;

import ir.aa.weatherprediction.user.Role;
import ir.aa.weatherprediction.user.UserEntity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
public class UserDao {

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

    public static UserDao fromUserEntity(UserEntity userEntity) {
        return UserDao.builder()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .phoneNumber(userEntity.getPhoneNumber())
                .address(userEntity.getAddress())
                .isActive(userEntity.isActive())
                .role(userEntity.getRole())
                .dateJoined(userEntity.getDateJoined())
                .lastLogin(userEntity.getLastLogin())
                .build();
    }
}
