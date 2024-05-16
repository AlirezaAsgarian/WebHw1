package ir.aa.weatherprediction;

import ir.aa.weatherprediction.user.Role;
import ir.aa.weatherprediction.user.UserEntity;
import ir.aa.weatherprediction.user.UserService;
import ir.aa.weatherprediction.user.domain.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class WeatherPredictionApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherPredictionApplication.class, args);
    }

}
