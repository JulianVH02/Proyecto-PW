package com.proyecto.sinergia.dto;

import com.proyecto.sinergia.model.Usuario;
import com.proyecto.sinergia.model.enums.RolUsuario;

import java.time.LocalDateTime;

public class UsuarioPerfilDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private RolUsuario rol;
    private LocalDateTime fechaRegistro;
    private String fotoPerfil;
    private String descripcionPerfil;

    public UsuarioPerfilDto() {}

    public UsuarioPerfilDto(Usuario usuario) {
        this.id = usuario.getId();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.correo = usuario.getCorreo();
        this.rol = usuario.getRol();
        this.fechaRegistro = usuario.getFechaRegistro();
        this.fotoPerfil = usuario.getFotoPerfil();
        this.descripcionPerfil = usuario.getDescripcionPerfil();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCorreo() { return correo; }
    public RolUsuario getRol() { return rol; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public String getFotoPerfil() { return fotoPerfil; }
    public String getDescripcionPerfil() { return descripcionPerfil; }
}
