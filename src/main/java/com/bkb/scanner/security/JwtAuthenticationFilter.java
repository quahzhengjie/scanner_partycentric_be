package com.bkb.scanner.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Value("${app.demo-mode:false}")
    private boolean demoMode;

    // Define public endpoints that don't need authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/health",
            "/api/health",
            "/actuator/**",
            "/api/auth/**",
            "/error",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/api/demo/**"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getServletPath();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(requestPath)) {
            log.debug("Skipping authentication for public endpoint: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = getJwtFromRequest(request);

            // Check for demo mode header
            if (demoMode) {
                String demoUserId = request.getHeader("X-Demo-User");
                if (StringUtils.hasText(demoUserId)) {
                    log.debug("Demo mode: Using user ID from header: {}", demoUserId);
                    authenticateDemoUser(demoUserId, request);
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                String userEmail = jwtUtil.getUserEmailFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authenticated user: {}", userEmail);
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestPath) {
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void authenticateDemoUser(String userId, HttpServletRequest request) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUserId(userId);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Demo mode: Authenticated user with ID: {}", userId);
        } catch (Exception e) {
            log.error("Failed to authenticate demo user: {}", userId, e);
        }
    }
}