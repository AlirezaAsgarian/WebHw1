package ir.aa.weatherprediction.login.apitoken;

import lombok.Data;

// Request model
@Data
public class ApiTokenRequest {
    private String name;
    private String expireDate;
}

