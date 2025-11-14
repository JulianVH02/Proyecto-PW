package com.proyecto.sinergia;

import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.model.enums.RolUsuario;
import com.proyecto.sinergia.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; 
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration; 
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder; // Necesitas esta importación

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class, 
    UserDetailsServiceAutoConfiguration.class
})
public class SinergiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SinergiaApplication.class, args);
    }

    /**
     * Crea un usuario de prueba fijo al iniciar la aplicación si no existe.
     * Correo: prueba@test.com
     * Pass: 123456
     */
    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Asegurarse de que el usuario de prueba no existe antes de crearlo
            if (usuarioRepository.findByCorreo("prueba@test.com").isEmpty()) {
                Usuario testUser = new Usuario();
                testUser.setNombre("Test");
                testUser.setApellido("User");
                testUser.setCorreo("prueba@test.com");
                // La contraseña debe ser encriptada con el mismo encoder que usa el login
                testUser.setContraseña(passwordEncoder.encode("123456")); 
                testUser.setRol(RolUsuario.ESTUDIANTE);
                usuarioRepository.save(testUser);
                System.out.println("\n\n>>> USUARIO DE PRUEBA CREADO: prueba@test.com / 123456 <<<\n");
            }
        };
    }
}