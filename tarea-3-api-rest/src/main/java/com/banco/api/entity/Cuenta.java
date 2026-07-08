package com.banco.api.entity;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroCuenta;
    private String tipoCuenta;
    private BigDecimal saldo;
    private String moneda;
    private boolean estado = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public Cuenta() {
    }

    public Cuenta(String numeroCuenta, String tipoCuenta, BigDecimal saldo, String moneda, Cliente cliente) {
        this.numeroCuenta = numeroCuenta;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldo;
        this.moneda = moneda;
        this.cliente = cliente;
    }

    public Long getId() { return id; }
    public String getNumeroCuenta() { return numeroCuenta; }
    public String getTipoCuenta() { return tipoCuenta; }
    public BigDecimal getSaldo() { return saldo; }
    public String getMoneda() { return moneda; }
    public boolean isEstado() { return estado; }
    public Cliente getCliente() { return cliente; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public void setEstado(boolean estado) { this.estado = estado; }
}