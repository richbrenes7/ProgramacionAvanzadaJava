package com.banco.core.controller;

import java.time.LocalDateTime;

public record ApiUsageLog(
        LocalDateTime fecha,
        String metodo,
        String ruta,
        int estado,
        long duracionMs,
        String usuario,
        String origen) {
}
