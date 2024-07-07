package ir.aa.weatherprediction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.CrossOrigin;

@EnableCaching
@EnableScheduling
@CrossOrigin(origins = "*")
@SpringBootApplication
public class WeatherPredictionApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherPredictionApplication.class, args);
    }

}