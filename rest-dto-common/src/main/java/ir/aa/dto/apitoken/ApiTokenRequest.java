package ir.aa.dto.apitoken;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request model
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiTokenRequest {
    private String name;
    private String expireDate;
}

