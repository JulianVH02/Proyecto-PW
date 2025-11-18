package com.proyecto.sinergia.controller;

import com.proyecto.sinergia.dto.LoginRequest;
import com.proyecto.sinergia.dto.LoginResponse;
import com.proyecto.sinergia.dto.RegistroRequest;
import com.proyecto.sinergia.dto.UsuarioPerfilDto;
import com.proyecto.sinergia.dto.ActualizarPerfilRequest;
import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.security.JwtService;
import com.proyecto.sinergia.service.UsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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

        // Enviar también el token en una cookie HttpOnly para que el navegador lo
        // incluya automáticamente al navegar a los HTML estáticos.
        ResponseCookie jwtCookie = ResponseCookie.from("JWT", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(7 * 24 * 60 * 60) // 7 días
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new LoginResponse(token));
    }
    
    @GetMapping("/mi-perfil")
    public ResponseEntity<UsuarioPerfilDto> getMiPerfil(@AuthenticationPrincipal UserDetails userDetails) {
        // userDetails.getUsername() nos da el correo extraído del Token
        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(new UsuarioPerfilDto(usuario));
    }

    @PutMapping("/mi-perfil")
    public ResponseEntity<UsuarioPerfilDto> actualizarMiPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ActualizarPerfilRequest request) {

        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Solo actualizar la foto si viene explícitamente en el request
        if (request.fotoPerfil() != null) {
            usuario.setFotoPerfil(request.fotoPerfil());
        }

        usuario.setDescripcionPerfil(request.descripcionPerfil());

        Usuario guardado = usuarioService.actualizar(usuario);

        return ResponseEntity.ok(new UsuarioPerfilDto(guardado));
    }

    @PostMapping("/mi-perfil/foto")
    public ResponseEntity<UsuarioPerfilDto> actualizarFotoPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("imagen") MultipartFile imagen) throws IOException {

        if (imagen == null || imagen.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuario = usuarioService.buscarPorCorreo(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Carpeta dentro de resources/static para servir las imágenes como /uploads/perfiles/...
        Path uploadDir = Paths.get("src/main/resources/static/uploads/perfiles");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String extension = "";
        String original = imagen.getOriginalFilename();
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf('.'));
        }
        String fileName = "perfil_" + usuario.getId() + "_" + UUID.randomUUID() + extension;
        Path destino = uploadDir.resolve(fileName);

        Files.copy(imagen.getInputStream(), destino);

        // Ruta accesible desde el navegador
        String urlPublica = "/uploads/perfiles/" + fileName;
        usuario.setFotoPerfil(urlPublica);

        Usuario guardado = usuarioService.actualizar(usuario);

        return ResponseEntity.ok(new UsuarioPerfilDto(guardado));
    }
}