package ir.aa.weatherprediction.login.apitoken;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ApiTokenRetrieveResponse {
    private List<ApiTokenDto> tokens;
    private int count;
}

