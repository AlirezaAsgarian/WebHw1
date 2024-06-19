package ir.aa.dto.apitoken;

import ir.aa.dto.apitoken.ApiTokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiTokenRetrieveResponse {
    private List<ApiTokenDto> tokens;
    private int count;
}

