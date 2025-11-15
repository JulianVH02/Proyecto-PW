package com.proyecto.sinergia.config;

import com.proyecto.sinergia.security.JwtAuthFilter;
import com.proyecto.sinergia.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod; // IMPORTANTE
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// --- IMPORTACIONES DE CORS ---
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
            // 1. VINCULACIÓN EXPLÍCITA DE CORS (Para evitar errores 403 en POST/PUT)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 2. DESACTIVAR CSRF (Obligatorio para APIs Stateless con Token)
            .csrf(csrf -> csrf.disable()) 
            
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            .authorizeHttpRequests(authz -> authz
                    // --- REGLA DE ORO PARA EL NAVEGADOR ---
                    // Permitir peticiones OPTIONS (preflight) globalmente para evitar bloqueos de CORS
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // 1. PERMISOS PÚBLICOS (HTMLs, Auth, Estáticos)
                    .requestMatchers("/", "/index.html", "/login.html", "/registro.html", "/403.html").permitAll()
                    .requestMatchers("/static/**", "/images/**", "/css/**", "/js/**").permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                    // Dashboards protegidos por rol
                    .requestMatchers("/dashboard-estudiante.html").hasAnyAuthority("ESTUDIANTE")
                    .requestMatchers("/dashboard-tutor.html").hasAnyAuthority("TUTOR")
                    .requestMatchers("/dashboard-admin.html").hasAnyAuthority("ADMIN")

                    // Endpoints de autenticación y públicos
                    .requestMatchers("/api/public/**").permitAll() 
                    .requestMatchers("/api/auth/**").permitAll() 
                    
                    // 2. ENDPOINTS PROTEGIDOS (Usuario Logueado)
                    // Aquí agregamos TODAS tus nuevas funcionalidades
                    .requestMatchers("/api/quizzes/**").authenticated() 
                    .requestMatchers("/api/flashcards/**").authenticated()
                    .requestMatchers("/api/tareas/**").authenticated()
                    .requestMatchers("/api/recursos/**").authenticated()
                    .requestMatchers("/api/marketplace/**").authenticated()
                    .requestMatchers("/api/ratings/**").authenticated()
                    
                    // Regla específica del tutor
                    .requestMatchers("/api/tutor/**").authenticated() 
                    
                    // 3. PROTECCIÓN DE API ADMIN (SOLO ADMIN)
                    .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                    // 4. CUALQUIER OTRA PETICIÓN debe estar autenticada
                    .anyRequest().authenticated() 
                )
            
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/403.html")
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuración Global de CORS
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // En desarrollo es seguro usar *, pero asegúrate de permitir credenciales si lo necesitas
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080")); // O usar "*" si no usas cookies
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true); // Importante para algunos navegadores
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
}