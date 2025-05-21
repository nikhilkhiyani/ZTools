package com.zasya.ZTools.utils;

import com.zasya.ZTools.enums.App;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final String KEY = "ThisIsASecretKeyThatIsAtLeast32BytesLong!";
    private static final SecretKey secret = Keys.hmacShaKeyFor(KEY.getBytes());

    private final long expiration = 86400000;

    public String generateToken(String username, String role, Set<App> apps){
        Map<String, Object> claims = new HashMap<>();
        claims.put("apps", apps);
        claims.put("roles", role);

        return Jwts.builder()
                .setSubject(username)
                .claim("claims", claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(secret, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token){
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String extractRole(String token) {
        return Jwts.parser().setSigningKey(secret)
                .parseClaimsJws(token).getBody().get("role", String.class);
    }

    public Set<App> extractApps(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        List<String> appList = (List<String>) claims.get("apps");
        return appList.stream()
                .map(App::valueOf)
                .collect(Collectors.toSet());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getAppsFromToken(String token) {
        Claims claims = extractAllClaims(token);
        Object appsObject = claims.get("apps");
        if (appsObject instanceof List<?>) {
            return ((List<?>) appsObject)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

}
