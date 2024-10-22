package com.blogsite.auth.service.security.jwttokenutil;



import com.blogsite.auth.service.security.login.BlogUserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import org.springframework.security.core.GrantedAuthority;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtils.class);
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private final Key jwtSecret = getSignKey();//Keys.secretKeyFor(SignatureAlgorithm.HS256); // Generate a key or use your own

//    @Value("${pineapple.app.jwtExpirationMs}")
//    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        BlogUserDetailsImpl userPrincipal = (BlogUserDetailsImpl) authentication.getPrincipal();
        List<String> roles=userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)  // Extract the authority name
                .filter(role -> role.startsWith("ROLE_"))  // Optional: filter by 'ROLE_' prefix
                .collect(Collectors.toList());
        return Jwts.builder()
                .setSubject(userPrincipal.getEmail())
                .claim("roles",roles.stream().toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(jwtSecret,SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, String username) {
        final String extractedUsername = getUserNameFromJwtToken(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }



    public boolean validateJwtToken(String authToken) {

        try {
            validateToken(authToken, getUserNameFromJwtToken(authToken));
            //Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}