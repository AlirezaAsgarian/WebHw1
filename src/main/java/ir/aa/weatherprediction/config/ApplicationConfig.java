package ir.aa.weatherprediction.config;

import ir.aa.weatherprediction.login.user.UserRepository;
import ir.aa.weatherprediction.rest.domain.WeatherRequest;
import ir.aa.weatherprediction.rest.domain.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> (UserDetails) userRepository.findByUsername(username).orElse(null);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ArrayBlockingQueue<WeatherRequest> getWeatherRequestQueue() {
        return new ArrayBlockingQueue<>(100);
    }

    @Bean
    public ArrayBlockingQueue<WeatherResponse> getWeatherResponseQueue() {
        return new ArrayBlockingQueue<>(100);
    }
}
