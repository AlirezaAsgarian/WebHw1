package ir.aa.weatherprediction.login.apitoken;

import lombok.Builder;
import lombok.Data;

// Request model
@Data
@Builder
public class ApiTokenRequest {
    private String name;
    private String expireDate;
}

