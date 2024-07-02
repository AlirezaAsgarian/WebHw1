package ir.aa.weatherprediction.rest.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WeatherResponse {
    private String id;
    private JsonNode jsonNode;
}
