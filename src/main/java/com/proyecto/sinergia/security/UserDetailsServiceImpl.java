package com.proyecto.sinergia.security;

import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority; // Importar
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Importar
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collection; // Importar
import java.util.Collections; // Importar
import java.util.List; // Importar

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        // --- CORRECCIÓN CLAVE ---
        // 1. Obtener la autoridad (ej: "ADMIN", "ESTUDIANTE"). Usamos .name() o .toString()
        //    para obtener el nombre del Enum, que coincide con el valor de la DB.
        String rolAsString = usuario.getRol().toString(); 
        
        // 2. Crear una colección de GrantedAuthority (permisos)
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(rolAsString)
        );
        // ------------------------

        return new User(
            usuario.getCorreo(), 
            usuario.getContraseña(), 
            authorities // <-- ¡YA NO ESTÁ VACÍA!
        );
    }
}