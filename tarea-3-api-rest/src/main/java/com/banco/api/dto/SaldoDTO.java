package com.banco.api.dto;

import java.math.BigDecimal;

public record SaldoDTO(String numeroCuenta, BigDecimal saldo, String moneda) {
}