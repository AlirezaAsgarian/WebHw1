package ir.aa.weatherprediction.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import ir.aa.dto.user.UserDao;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;

import static ir.aa.weatherprediction.config.UserDetailsFactory.toUserEntity;

@Component
public class ApiTokenAuthenticationFilter extends OncePerRequestFilter  {
    private String API_TOKENS_BASE_URL;

    @Autowired
    private RestTemplate restTemplate;
    @Value("${authentication.url}")
    public void setBaseUrl(String baseUrl) {
        this.API_TOKENS_BASE_URL = "http://" + baseUrl + "/user/api-tokens/validate";
    }
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("maw request i get it hehe !!!!!!!");
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith(TokenType.API_TOKEN.getName() + " ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String token = authHeader.substring(TokenType.API_TOKEN.getName().length() + 1);
        ResponseEntity<String> exchange = null;
        try {
            exchange = restTemplate.exchange(API_TOKENS_BASE_URL, HttpMethod.POST, new HttpEntity<>(token, new HttpHeaders()), String.class);
            if (exchange.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "api token is not valid");
            }
            if (exchange.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user with this api token not found");
            }
        } catch (HttpClientErrorException ex) {
            throw new ResponseStatusException(ex.getStatusCode(), ex.getMessage());
        }

        UserDetails userEntity = toUserEntity(Objects.requireNonNull(new ObjectMapper().readValue(exchange.getBody(), UserDao.class)));
        UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(
                userEntity, null, userEntity.getAuthorities()
        );
        userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(userToken);
        filterChain.doFilter(request, response);
    }

}
