package com.unistay.housing_management_system.security;

import com.unistay.housing_management_system.exceptions.UnauthorizedAccessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter
{
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal
            (@NonNull HttpServletRequest request,
             @NonNull HttpServletResponse response,
             @NonNull FilterChain filterChain) throws ServletException, IOException
    {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        String path = request.getServletPath();

        if (
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/logout") ||
                path.startsWith("/swagger-ui/") ||
                path.equals("/swagger-ui.html") ||
                path.startsWith("/v3/api-docs") ||
                path.equals("/api/hello")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        /*if (authHeader == null || !authHeader.startsWith("Bearer "))
        {
            log.debug("No JWT token found in request to: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            throw new UnauthorizedAccessException("No JWT token found in request to: " + request.getRequestURI());
        }*/
        if (authHeader == null || !authHeader.startsWith("Bearer "))
        {
            log.debug("No JWT token found in request to: {}", request.getRequestURI());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write("""
             {
                "status": 401,
                "error": "Unauthorized",
                "message": "No JWT token found"
                }
             """);

            return;
        }

        try
        {
            jwt = authHeader.substring(7);

            if (tokenBlacklistService.isBlacklisted(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token is blacklisted");
                throw new UnauthorizedAccessException("Token is blacklisted");
            }

            userEmail = jwtUtil.extractUsername(jwt);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null)
            {
                log.debug("Processing JWT token for user: {} on endpoint: {}", userEmail, request.getRequestURI());

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtUtil.isTokenValid(jwt, userDetails))
                {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("JWT authentication successful for user: {} on endpoint: {}", userEmail, request.getRequestURI());
                } else
                {
                    log.warn("JWT token validation failed for user: {} on endpoint: {}", userEmail, request.getRequestURI());
                    throw new UnauthorizedAccessException(" JWT token validation failed for user: " + userEmail);
                }
            }
        } catch (Exception e)
        {
            log.warn("JWT processing error on endpoint: {} - Error: {}", request.getRequestURI(), e.getMessage());
            //throw new UnauthorizedAccessException("JWT processing error on endpoint: " + request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            response.getWriter().write("""
             {
                "status": 401,
                "error": "Unauthorized",
                "message": "Invalid JWT token"
                }
             """);
        }

        filterChain.doFilter(request, response);
    }
}
