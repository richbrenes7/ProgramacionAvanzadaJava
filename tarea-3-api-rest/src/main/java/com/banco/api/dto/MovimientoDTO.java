package com.banco.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoDTO(Long id, LocalDateTime fecha, BigDecimal monto, String tipoMovimiento, String descripcion, String referencia) {
}