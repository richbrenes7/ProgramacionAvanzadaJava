package com.banco.api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

@Entity
public class Movimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_id")
    private Cuenta cuenta;

    private LocalDateTime fecha;
    private BigDecimal monto;
    private String tipoMovimiento;
    private String descripcion;
    private String referencia;

    public Movimiento() {
    }

    public Movimiento(Cuenta cuenta, BigDecimal monto, String tipoMovimiento, String descripcion, String referencia) {
        this.cuenta = cuenta;
        this.monto = monto;
        this.tipoMovimiento = tipoMovimiento;
        this.descripcion = descripcion;
        this.referencia = referencia;
    }

    @PrePersist
    void prePersist() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public Cuenta getCuenta() { return cuenta; }
    public LocalDateTime getFecha() { return fecha; }
    public BigDecimal getMonto() { return monto; }
    public String getTipoMovimiento() { return tipoMovimiento; }
    public String getDescripcion() { return descripcion; }
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
}