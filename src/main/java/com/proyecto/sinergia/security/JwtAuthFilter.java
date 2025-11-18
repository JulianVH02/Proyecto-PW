package com.proyecto.sinergia.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // --- RUTAS PÚBLICAS EXCLUIDAS DEL FILTRO JWT ---
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/register"
    );
    
    /**
     * Este método le dice a Spring que NO ejecute el filtro JWT 
     * para las rutas definidas en EXCLUDED_PATHS.
     * Esto soluciona el problema de interferencia que causa el 403 en el login.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1. Excluir específicamente las peticiones POST de autenticación
        if (method.equals("POST")) {
             return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
        }
        
        // 2. Excluir peticiones GET a /api/public (para cargar materias)
        if (method.equals("GET") && path.startsWith("/api/public/")) {
             return true;
        }

        // 3. Excluir peticiones GET a /api/recursos (biblioteca pública)
        if (method.equals("GET") && path.startsWith("/api/recursos")) {
            return true;
        }

        // Si no es ninguna de las rutas públicas de arriba, el filtro se ejecuta.
        return false; 
    }

 // ... imports y resto de la clase ...

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String userEmail = null;

        String tokenFromHeader = null;
        if (authHeader != null) {
            String lowerAuth = authHeader.toLowerCase();
            if (lowerAuth.startsWith("bearer ")) {
                tokenFromHeader = authHeader.substring(7);
            }
        }

        String tokenFromCookie = null;
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie c : request.getCookies()) {
                if ("JWT".equals(c.getName())) {
                    tokenFromCookie = c.getValue();
                    break;
                }
            }
        }

        // Elegir el token disponible
        if (tokenFromHeader != null) {
            jwt = tokenFromHeader;
        } else if (tokenFromCookie != null) {
            jwt = tokenFromCookie;
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido");
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.validateToken(jwt, userDetails)) {
                // --- DEBUG ---
                System.out.println(">>> INTENTO DE ACCESO A: " + request.getRequestURI());
                System.out.println(">>> USUARIO: " + userDetails.getUsername());
                System.out.println(">>> AUTORIDADES (ROLES) CARGADOS: " + userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}