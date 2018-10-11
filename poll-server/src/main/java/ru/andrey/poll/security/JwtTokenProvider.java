package ru.andrey.poll.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationInMs;

    public String createToken(Authentication authentication) {
        UserPrincipal user = (UserPrincipal) authentication.getPrincipal();
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(user.getId()))
                .setIssuedAt(now)
                .setExpiration(expiresAt)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Long getUserId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            logger.debug("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.debug("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.debug("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.debug("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.debug("JWT claims string is empty.");
        }
        return false;
    }
}
