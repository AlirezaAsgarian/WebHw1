package ir.aa.authentication.login.apitoken;



import ir.aa.dto.apitoken.ApiTokenDto;
import ir.aa.dto.apitoken.ApiTokenRequest;
import ir.aa.dto.apitoken.ApiTokenResponse;
import ir.aa.dto.apitoken.ApiTokenRetrieveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiTokenService {

    @Autowired
    ApiTokenRepository apiTokenRepository;

    public String findUserNameByApiToken(String apiToken) {
        if(isValidApiToken(apiToken)) {
            return apiTokenRepository.findByToken(apiToken).map(ApiTokenEntity::getUsername).orElse(null);
        }
        return null;
    }
    public ApiTokenResponse createApiToken(ApiTokenRequest request, String username) {
        // Create the response object
        ApiTokenResponse response = new ApiTokenResponse();
        if (!request.getName().isEmpty()) {
            response.setName(request.getName());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing name for token");
        }

        if (apiTokenRepository.findByName(request.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate token name");
        }

        if (!request.getExpireDate().isEmpty() && isValidDate(request.getExpireDate())) {
            response.setExpireDate(request.getExpireDate());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing name for expire date");
        }

        String token = UUID.randomUUID().toString();
        ApiTokenEntity entity = new ApiTokenEntity(request.getName(), username,  request.getExpireDate(), token);
        apiTokenRepository.save(entity);

        response.setToken(token);
        // Return the response
        return response;
    }

    public ApiTokenRetrieveResponse retriveApiTokens(String username) {
        // Fetch all API tokens from the repository
        List<ApiTokenEntity> apiTokenEntities = apiTokenRepository.findByUsername(username);

        // Map entities to the response model and mask tokens
        List<ApiTokenDto> tokens = apiTokenEntities.stream()
                .map(entity -> {
                    ApiTokenDto token = new ApiTokenDto();
                    token.setName(entity.getName());
                    token.setExpireDate(entity.getExpireDate());
                    token.setExpired(this.isExpired(entity.getExpireDate()));
                    token.setActive(entity.isActive());
                    // Mask the token: Replace the middle portion with "***"
                    String maskedToken = "API ***";
                    token.setToken(maskedToken);
                    return token;
                })
                .toList();

        // Create the response object
        ApiTokenRetrieveResponse response = new ApiTokenRetrieveResponse();
        response.setTokens(tokens);
        response.setCount(tokens.size());
        return response;
    }

    public boolean isValidApiToken(String apiToken) {
        if (!apiToken.isEmpty()) {
            Optional<ApiTokenEntity> apiTokenEntity = apiTokenRepository.findByToken(apiToken);
            if (apiTokenEntity.isPresent()){
                if (!apiTokenEntity.get().isActive()) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Api token is not active");
                }
                if (isExpired(apiTokenEntity.get().getExpireDate())) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Api token is expired");
                }
                return true;
            }
        }
        return false;
    }

    public boolean inActiveToken(String token) {

        // Find the API token entity in the repository
        Optional<ApiTokenEntity> apiTokenEntity = apiTokenRepository.findByToken(token);

        if (apiTokenEntity.isEmpty() || !apiTokenEntity.get().isActive()) {
            // Token not found
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token not found or already invalidated");
        }

        // Invalidate the token
        apiTokenEntity.get().setActive(false);
        apiTokenRepository.save(apiTokenEntity.get()); // Save changes to the database
        return true;
    }

    public boolean inActiveTokenByName(String tokenName) {

        // Find the API token entity in the repository
        Optional<ApiTokenEntity> apiTokenEntity = apiTokenRepository.findByName(tokenName);

        if (apiTokenEntity.isEmpty() || !apiTokenEntity.get().isActive()) {
            // Token not found
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token not found or already invalidated");
        }

        // Invalidate the token
        apiTokenEntity.get().setActive(false);
        apiTokenRepository.save(apiTokenEntity.get()); // Save changes to the database
        return true;
    }

    private boolean isValidDate(String expireDate) {
        try {
            LocalDateTime.parse(expireDate);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    // Todo: check time zone
    private boolean isExpired(String expireDate) {
        if (expireDate.isEmpty() || !isValidDate(expireDate))
            return false;

        LocalDateTime targetDate = LocalDateTime.parse(expireDate);
        // Get the current date and time
        LocalDateTime currentDate = LocalDateTime.now();
        return targetDate.isBefore(currentDate);
    }
}

