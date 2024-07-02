package ir.aa.weatherprediction.config;

import ir.aa.dto.user.UserDao;
import ir.aa.weatherprediction.login.user.UserDetailsImp;

public class UserDetailsFactory {
    public static UserDetailsImp toUserEntity(UserDao userDao) {
        return UserDetailsImp.builder()
                .firstName(userDao.getFirstName())
                .lastName(userDao.getLastName())
                .username(userDao.getUsername())
                .password(userDao.getPassword())
                .phoneNumber(userDao.getPhoneNumber())
                .address(userDao.getAddress())
                .isActive(userDao.isActive())
                .role(userDao.getRole())
                .dateJoined(userDao.getDateJoined())
                .lastLogin(userDao.getLastLogin())
                .build();
    }
}
