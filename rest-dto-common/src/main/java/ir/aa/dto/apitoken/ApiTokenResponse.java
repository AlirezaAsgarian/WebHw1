package ir.aa.dto.apitoken;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Response model
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiTokenResponse {
    private String name;
    private String expireDate; // Use String or OffsetDateTime if needed
    private String token;
}
