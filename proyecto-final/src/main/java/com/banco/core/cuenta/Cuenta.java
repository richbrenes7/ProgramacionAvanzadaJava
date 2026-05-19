/*
 * Nelson Ricardo Brenes Lemus
 * Carnet: 092-20-3971
 * Curso: Programacion Avanzada en Java
 * Tarea: Proyecto Final
 * Guatemala, 2026 - NRBL.
 */
package com.banco.core.cuenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cuentas")
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroCuenta;
    private String tipoCuenta;
    private String moneda;
    private BigDecimal saldo = BigDecimal.ZERO;
    private String estado;
    private Long clienteId;
    private LocalDateTime fechaApertura = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }
    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public java.math.BigDecimal getSaldo() { return saldo; }
    public void setSaldo(java.math.BigDecimal saldo) { this.saldo = saldo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public java.time.LocalDateTime getFechaApertura() { return fechaApertura; }
}
