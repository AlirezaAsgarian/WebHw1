package ir.aa.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
public class UserDao {

    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String phoneNumber;
    private String address;
    private boolean isActive;
    private Role role;
    private Date dateJoined;
    private Date lastLogin;
    public UserDao() {
    }


}
