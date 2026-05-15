package com.banco.api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banco.api.entity.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    List<Movimiento> findByCuentaNumeroCuentaAndFechaBetweenOrderByFechaDesc(String numeroCuenta, LocalDateTime desde, LocalDateTime hasta);
}