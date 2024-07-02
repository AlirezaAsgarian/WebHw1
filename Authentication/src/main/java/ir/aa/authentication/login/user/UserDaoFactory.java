package ir.aa.authentication.login.user;


import ir.aa.dto.user.UserDao;

public class UserDaoFactory {
    public static UserDao fromUserEntity(UserEntity userEntity) {
        return UserDao.builder()
                .firstName(userEntity.getFirstName() != null ? userEntity.getFirstName() : null)
                .lastName(userEntity.getLastName() != null ? userEntity.getLastName() :  null)
                .username(userEntity.getUsername() != null ? userEntity.getUsername() :  null)
                .password(userEntity.getPassword() != null ? userEntity.getPassword() :  null)
                .phoneNumber(userEntity.getPhoneNumber() != null ? userEntity.getPhoneNumber() :  null)
                .address(userEntity.getAddress() != null ? userEntity.getAddress() :  null)
                .isActive(userEntity.isActive())
                .role(userEntity.getRole() != null ? userEntity.getRole() :  null)
                .dateJoined(userEntity.getDateJoined() != null ? userEntity.getDateJoined() :  null)
                .lastLogin(userEntity.getLastLogin() != null ? userEntity.getLastLogin() :  null)
                .build();
    }
}
