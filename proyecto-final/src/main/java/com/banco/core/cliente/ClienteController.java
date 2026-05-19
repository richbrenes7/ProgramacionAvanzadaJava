/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
package com.banco.core.cliente;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteRepository repo;

    public ClienteController(ClienteRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@RequestBody Cliente c) {
        Cliente saved = repo.save(c);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listar() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id, @RequestBody Cliente c) {
        return repo.findById(id).map(existing -> {
            existing.setNombre(c.getNombre());
            existing.setDocumento(c.getDocumento());
            existing.setEmail(c.getEmail());
            existing.setTelefono(c.getTelefono());
            existing.setEstado(c.getEstado());
            repo.save(existing);
            return ResponseEntity.ok(existing);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
