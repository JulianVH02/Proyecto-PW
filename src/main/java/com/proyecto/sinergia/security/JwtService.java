package com.proyecto.sinergia.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority; // Importar
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap; // Importar
import java.util.Map; // Importar
import java.util.function.Function;

@Service
public class JwtService {

    // (Tu variable JWT_SECRET debe estar aquí, usualmente cargada con @Value)
    // Asegúrate de tener esta variable inicializada
 
	private static final String JWT_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // --- ¡ESTE ES EL MÉTODO QUE CAMBIAMOS! ---
    // (Si tu método se llama diferente, como createToken, aplica la misma lógica)
    public String generateToken(UserDetails userDetails) {
        // 1. Crear el Map de "claims" (datos extra)
        Map<String, Object> claims = new HashMap<>();
        
        // 2. Obtener el rol del usuario y agregarlo al Map
        // (Asumimos que cada usuario tiene solo UN rol en este sistema)
        String role = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(GrantedAuthority::getAuthority)
                        .orElse("ESTUDIANTE"); // Un rol por defecto si no se encuentra
        
        claims.put("role", role); // <-- ¡AQUÍ INCLUIMOS EL ROL!

        // 3. Construir el token CON los claims
        return Jwts.builder()
                .setClaims(claims) // <-- AÑADIMOS LOS CLAIMS
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // (Si tienes un método sobrecargado "generateToken(Map<String, Object> extraClaims, UserDetails userDetails)"
    //  asegúrate de que el 'UsuarioController' llame a este método de un solo argumento,
    //  o adapta el map de claims en el controlador.)

    // --- MÉTODOS EXISTENTES (NO CAMBIAN) ---

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}