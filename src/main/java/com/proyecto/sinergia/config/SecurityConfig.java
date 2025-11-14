package com.proyecto.sinergia.config;

import com.proyecto.sinergia.security.JwtAuthFilter;
import com.proyecto.sinergia.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Primary;

// --- IMPORTACIONES DE CORS (NECESARIAS) ---
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailsService)
                   .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    @Primary
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. HABILITAR CORS (Evita errores de red en el navegador)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. DESHABILITAR CSRF (¡ESTO ES LO QUE ARREGLA EL ERROR 403!)
            .csrf(csrf -> csrf.disable()) 
            
            // 3. Sesiones sin estado (Stateless) para JWT
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 4. Configurar permisos de rutas
            .authorizeHttpRequests(authz -> authz
                // Archivos estáticos
                .requestMatchers("/", "/index.html", "/*.html", "/static/**", "/images/**").permitAll()
                // Swagger UI
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                // APIs Públicas
                .requestMatchers("/api/public/**").permitAll() 
                // Login y Registro (¡IMPORTANTE!)
                .requestMatchers("/api/auth/**").permitAll() 
                // Cualquier otra cosa requiere Token
                .anyRequest().authenticated()
            )
            
            // 5. Filtro JWT antes del filtro de autenticación
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuración Global de CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Permitir cualquier origen (localhost:8080, etc.)
        configuration.setAllowedOrigins(Arrays.asList("*")); 
        // Permitir todos los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permitir todos los encabezados
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
}