package com.banco.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String documento;
    private String email;
    private boolean estado = true;

    public Cliente() {
    }

    public Cliente(String nombre, String documento, String email) {
        this.nombre = nombre;
        this.documento = documento;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDocumento() { return documento; }
    public String getEmail() { return email; }
    public boolean isEstado() { return estado; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDocumento(String documento) { this.documento = documento; }
    public void setEmail(String email) { this.email = email; }
    public void setEstado(boolean estado) { this.estado = estado; }
}