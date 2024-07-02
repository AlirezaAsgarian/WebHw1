package ir.aa.weatherprediction.rest.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherRequest {
    private String id;
    private String countryName;
}
