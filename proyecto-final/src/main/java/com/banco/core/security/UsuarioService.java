/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
package com.banco.core.security;

import java.util.List;
import java.util.Locale;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = buscarPorUsername(username);
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .roles(normalizeRole(usuario.getRole()))
                .disabled(!"ACTIVO".equalsIgnoreCase(usuario.getEstado()))
                .build();
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public Usuario crear(String username, String password, String role, String nombre) {
        return crear(username, password, role, nombre, null);
    }

    public Usuario crear(String username, String password, String role, String nombre, Long clienteId) {
        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El usuario ya existe");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRole(normalizeRole(role));
        usuario.setNombre(nombre);
        usuario.setClienteId(clienteId);
        usuario.setEstado("ACTIVO");
        return usuarioRepository.save(usuario);
    }

    public Usuario asignarCliente(Long id, Long clienteId) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setClienteId(clienteId);
        return usuarioRepository.save(usuario);
    }

    public Usuario resetPassword(Long id, String newPassword) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow();
        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        return usuarioRepository.save(usuario);
    }

    public Usuario resetPasswordByUsername(String username, String newPassword) {
        Usuario usuario = buscarPorUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(newPassword));
        return usuarioRepository.save(usuario);
    }

    public Usuario asegurarAdminDemo(String username, String password) {
        return usuarioRepository.findByUsername(username)
                .orElseGet(() -> crear(username, password, "ADMIN", "Administrador demo"));
    }

    private String normalizeRole(String role) {
        if (role == null || role.isBlank()) return "USER";
        String value = role.toUpperCase(Locale.ROOT).replace("ROLE_", "");
        return "ADMIN".equals(value) ? "ADMIN" : "USER";
    }
}
