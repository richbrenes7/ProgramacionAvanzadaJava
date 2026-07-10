/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
package com.banco.core.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UsuarioDataSeeder implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final String adminUser;
    private final String adminPass;

    public UsuarioDataSeeder(
            UsuarioService usuarioService,
            @Value("${app.default.admin:user}") String adminUser,
            @Value("${app.default.admin-pass:admin}") String adminPass) {
        this.usuarioService = usuarioService;
        this.adminUser = adminUser;
        this.adminPass = adminPass;
    }

    @Override
    public void run(String... args) {
        usuarioService.asegurarAdminDemo(adminUser, adminPass);
    }
}
