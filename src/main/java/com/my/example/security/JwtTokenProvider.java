package com.my.example.security;

import com.my.example.domain.AccountCredentials;
import com.my.example.service.UserDetailsServiceImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    public static final String JWT_AUDIENCE_AUTH_URI = "/auth";
    public static final String JWT_AUDIENCE_AUTH_TOKEN = "authToken";
    public static final String JWT_AUDIENCE_LOGIN_TOKEN = "loginToken";

    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public JwtTokenProvider(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long tokenOriginMillisecond = 1000L * 60 * 60;

    private final long tokenFirstMillisecond = 1000L * 60 * 60;

    static final String PREFIX = "Bearer";

    public String generateToken(String subject, String audience, List<String> roles) {

        Claims claims = Jwts.claims();

        claims.setSubject(subject);
        claims.setAudience(audience);
        claims.put("roles", roles);

        Date now = new Date();
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + tokenOriginMillisecond))
                .signWith(secretKey)
                .compact();

        log.info("generateToken : " + token);

        return token;
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token.replace(PREFIX,"")).getBody();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    public Authentication getAuthentication(String username){
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Authentication getAuthentication(AccountCredentials credentials){
        UserDetails userDetails = userDetailsService.loadUserByUsername(credentials.getUsername());
        return new UsernamePasswordAuthenticationToken(userDetails, credentials.getPassword());
    }

    public boolean validateToken(String token) {

        try{
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token.replace(PREFIX,""));
            return true;
        } catch(ExpiredJwtException e) {
            throw new IllegalStateException(e.getMessage());
        } catch(JwtException e) {
            throw new RuntimeException(e.getMessage());
        }

    }
}