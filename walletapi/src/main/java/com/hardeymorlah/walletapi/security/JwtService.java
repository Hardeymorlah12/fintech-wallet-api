package com.hardeymorlah.walletapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

    @Service
    public class JwtService {

        // Secret key (later move to application.properties)
        private static final String SECRET_KEY =
                "mysecretkeymysecretkeymysecretkeymysecretkey";

        // Generate token
        public String generateToken(String email, String role) {

            return Jwts.builder()
                    .setSubject(email)
                    .claim("role", role)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        }

        // Extract email
        public String extractEmail(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        // Extract claims
        public <T> T extractClaim(
                String token,
                Function<Claims, T> claimsResolver
        ) {

            final Claims claims = extractAllClaims(token);

            return claimsResolver.apply(claims);
        }

        // Extract all claims
        private Claims extractAllClaims(String token) {

            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }

        // Signing key


        private Key getSigningKey() {

            return Keys.hmacShaKeyFor(
                    SECRET_KEY.getBytes()
            );
        }
//        private Key getSigningKey() {
//
//            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
//
//            return Keys.hmacShaKeyFor(keyBytes);
//        }

        // Validate token
        public boolean isTokenValid(String token, String email) {

            final String extractedEmail = extractEmail(token);

            return extractedEmail.equals(email)
                    && !isTokenExpired(token);
        }

        // Check expiration
        private boolean isTokenExpired(String token) {

            return extractExpiration(token).before(new Date());
        }

        // Extract expiration
        private Date extractExpiration(String token) {

            return extractClaim(token, Claims::getExpiration);
        }
    }
