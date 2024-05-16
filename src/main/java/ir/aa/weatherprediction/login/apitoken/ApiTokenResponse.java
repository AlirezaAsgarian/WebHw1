package ir.aa.weatherprediction.login.apitoken;

import lombok.Data;

// Response model
@Data
public class ApiTokenResponse {
    private String name;
    private String expireDate; // Use String or OffsetDateTime if needed
    private String token;
}
