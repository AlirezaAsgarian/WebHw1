package ir.aa.weatherprediction.config;

import ir.aa.weatherprediction.login.apitoken.ApiTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.module.ResolutionException;

@Component
public class ApiTokenAuthenticationFilter extends OncePerRequestFilter  {

    final ApiTokenService apiTokenService;
    final UserDetailsService userDetailsService;

    public ApiTokenAuthenticationFilter(ApiTokenService apiTokenService, UserDetailsService userDetailsService) {
        this.apiTokenService = apiTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith(TokenType.API_TOKEN.getName() + " ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String token = authHeader.substring(TokenType.API_TOKEN.getName().length() + 1);
        try {
            if (!apiTokenService.isValidApiToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }
        } catch (ResponseStatusException ex) {
            logger.error("error occurs during valiate api token : " + ex.getMessage());
            throw ex;
        } catch (Exception ignore) {
            logger.error("error occurs during valiate api token : " + ignore.getMessage());
            filterChain.doFilter(request, response);
            return;
        }
        String username  = apiTokenService.findUserNameByApiToken(token);
        if(username == null) {
            filterChain.doFilter(request, response);
            return;
        }
        UserDetails user = userDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken userToken =new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );
        userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(userToken);
        filterChain.doFilter(request, response);
    }

}
