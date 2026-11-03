package com.billio.billio_api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Verification;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

/**
 * Filter for validating JWT tokens issued by Supabase.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${supabase.jwt.secret}")
    private String jwtSecret;

    @Value("${supabase.jwt.issuer}")
    private String jwtIssuer;

    @Value("${supabase.jwt.audience}")
    private String jwtAudience;
    
    @Value("${supabase.jwt.skip-expiration-check:false}")
    private boolean skipExpirationCheck;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                   @NonNull HttpServletResponse response, 
                                   @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = getJwtFromRequest(request);
        
        if (StringUtils.hasText(token)) {
            try {
                // First decode token without verification for debugging
                DecodedJWT decodedJwt = JWT.decode(token);
                String tokenIssuer = decodedJwt.getIssuer();
                String tokenAudience = decodedJwt.getAudience().size() > 0 ? decodedJwt.getAudience().get(0) : "none";
                
                logger.info("Token claims - Issuer: '{}', Audience: '{}'", tokenIssuer, tokenAudience);
                logger.info("Expected values - Issuer: '{}', Audience: '{}'", jwtIssuer, jwtAudience);
                
                // Verify the token
                DecodedJWT jwt = verifyToken(token);
                
                // Get user ID from subject claim
                String userId = jwt.getSubject();
                
                // Create authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                );
                
                // Set authentication in context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Successfully authenticated user: {}", userId);
                
            } catch (JWTVerificationException e) {
                logger.error("Could not validate JWT token: {}", e.getMessage());
                
                // For debugging purposes, print token details
                try {
                    String[] parts = token.split("\\.");
                    if (parts.length == 3) {
                        String header = new String(Base64.getUrlDecoder().decode(parts[0]));
                        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
                        logger.info("JWT header: {}", header);
                        logger.info("JWT payload: {}", payload);
                    }
                } catch (Exception ex) {
                    logger.error("Error decoding token for debugging", ex);
                }
                
                // For development, if we're skipping expiration check, try to authenticate anyway
                if (skipExpirationCheck && e.getMessage().contains("expired")) {
                    try {
                        // Just decode the token without verification
                        DecodedJWT jwt = JWT.decode(token);
                        String userId = jwt.getSubject();
                        
                        // Create authentication object
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userId, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                        
                        // Set authentication in context
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("Authenticated user with expired token (development mode): {}", userId);
                    } catch (Exception ex) {
                        logger.error("Error authenticating with expired token", ex);
                    }
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private DecodedJWT verifyToken(String token) {
        logger.info("Attempting to verify token with secret length: {}", jwtSecret.length());
        
        // Build the verification
        Verification verification = JWT.require(Algorithm.HMAC256(jwtSecret))
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience);
        
        // If we're skipping expiration check in development, we'll handle it after verification fails
        
        return verification.build().verify(token);
    }
}
