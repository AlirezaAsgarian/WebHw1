package ir.aa.weatherprediction.login.apitoken;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiTokenDto {
    private String name;
    private String expireDate;
    private boolean isExpired;
    private boolean isActive;
    private String token;
}

