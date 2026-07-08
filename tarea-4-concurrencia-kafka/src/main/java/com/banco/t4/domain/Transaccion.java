package com.banco.t4.domain;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaccion {
    private String id = UUID.randomUUID().toString();
    private String cuenta;
    private BigDecimal monto;

    public Transaccion() {}

    public Transaccion(String cuenta, BigDecimal monto) {
        this.cuenta = cuenta;
        this.monto = monto;
    }

    public String getId() { return id; }
    public String getCuenta() { return cuenta; }
    public BigDecimal getMonto() { return monto; }

    public void setId(String id) { this.id = id; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
}
