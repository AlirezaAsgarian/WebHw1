package ir.aa.weatherprediction.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ir.aa.dto.user.UserDao;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Objects;

import static ir.aa.weatherprediction.config.UserDetailsFactory.toUserEntity;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private String JWT_BASE_URL;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${authentication.url}")
    public void setBaseUrl(String baseUrl) {
        this.JWT_BASE_URL = "http://" + baseUrl + "/jwt";
    }
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("maw request i get it hehe !!!!!!!");
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(TokenType.BEARER.getName() + " ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String jwt = authHeader.substring(TokenType.BEARER.getName().length() + 1);

        ResponseEntity<String> exchange = restTemplate.exchange(JWT_BASE_URL, HttpMethod.POST, new HttpEntity<>(jwt, new HttpHeaders()), String.class);
        if (exchange.getStatusCode() == HttpStatus.BAD_REQUEST) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exchange.getBody());
        }
        if (exchange.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exchange.getBody());
        }
        UserDetails user = toUserEntity(Objects.requireNonNull(new ObjectMapper().readValue(exchange.getBody(), UserDao.class)));
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);
        filterChain.doFilter(request, response);
    }
}
