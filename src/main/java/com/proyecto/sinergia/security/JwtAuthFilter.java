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

        // Si no es ninguna de las rutas públicas de arriba, el filtro se ejecuta.
        return false; 
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si shouldNotFilter devuelve true, el código de abajo no se ejecuta.
        // Si llegamos aquí, es porque la ruta requiere verificación de token.
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        
        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Si el token no es válido o está expirado, devuelve 401
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token JWT inválido");
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}