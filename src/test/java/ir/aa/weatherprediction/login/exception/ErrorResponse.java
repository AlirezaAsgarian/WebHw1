package ir.aa.weatherprediction.login.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ErrorResponse {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
}

