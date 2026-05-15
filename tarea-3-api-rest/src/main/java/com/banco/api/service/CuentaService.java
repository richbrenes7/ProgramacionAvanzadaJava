package com.banco.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.banco.api.dto.MovimientoDTO;
import com.banco.api.dto.SaldoDTO;
import com.banco.api.entity.Cuenta;
import com.banco.api.entity.Movimiento;
import com.banco.api.exception.ResourceNotFoundException;
import com.banco.api.repository.CuentaRepository;
import com.banco.api.repository.MovimientoRepository;

@Service
public class CuentaService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    public CuentaService(CuentaRepository cuentaRepository, MovimientoRepository movimientoRepository) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    public SaldoDTO obtenerSaldo(String numeroCuenta) {
        Cuenta cuenta = findByNumero(numeroCuenta);
        return new SaldoDTO(cuenta.getNumeroCuenta(), cuenta.getSaldo(), cuenta.getMoneda());
    }

    public List<MovimientoDTO> listarMovimientos(String numeroCuenta, LocalDate desde, LocalDate hasta, int page, int size) {
        findByNumero(numeroCuenta);
        LocalDateTime desdeDate = desde != null ? desde.atStartOfDay() : LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime hastaDate = hasta != null ? hasta.atTime(LocalTime.MAX) : LocalDateTime.now();
        return movimientoRepository.findByCuentaNumeroCuentaAndFechaBetweenOrderByFechaDesc(numeroCuenta, desdeDate, hastaDate)
                .stream().skip((long) page * size).limit(size)
                .map(this::toDto)
                .toList();
    }

    private Cuenta findByNumero(String numeroCuenta) {
        return cuentaRepository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
    }

    private MovimientoDTO toDto(Movimiento movimiento) {
        return new MovimientoDTO(movimiento.getId(), movimiento.getFecha(), movimiento.getMonto(), movimiento.getTipoMovimiento(), movimiento.getDescripcion(), movimiento.getReferencia());
    }
}