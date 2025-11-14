package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.LoginRequest;
import com.proyecto.sinergia.dto.LoginResponse;
import com.proyecto.sinergia.dto.RegistroRequest;
import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.security.JwtService;
import com.proyecto.sinergia.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<Usuario> registrar(@RequestBody RegistroRequest request) {
        // Usamos .usuario() y .tutor() porque es un 'record'
        Usuario usuarioRegistrado = usuarioService.registrarUsuario(
            request.usuario(), 
            request.tutor()
        );
        return ResponseEntity.ok(usuarioRegistrado);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.correo(), request.contraseña())
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        
        return ResponseEntity.ok(new LoginResponse(token));
    }
    
    @GetMapping("/mi-perfil")
    public ResponseEntity<Usuario> getMiPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        // userDetails.getUsername() nos da el correo extraído del Token
        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Por seguridad, no devolvemos la contraseña en el JSON
        usuario.setContraseña(null); 
        
        return ResponseEntity.ok(usuario);
    }
}